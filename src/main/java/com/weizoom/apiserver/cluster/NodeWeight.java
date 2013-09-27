/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-5
 */
package com.weizoom.apiserver.cluster;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <code>NodeWeight</code>是用于描述<code>ProxyNode</code>的权重的数据结构<br />
 * 主要取决于三个量:<br />
 * <li>成功比率</li>
 * <li>正在执行的任务数</li>
 * <li>平均花费的时间</li>
 * <li>上一次的执行结果</li>
 * 
 * <code>NodeWeight</code>分为两部分，一部分是一段时间内的数据，一部分是历史记录的数据<br />
 * 实际分数分为两大部分：<code>baseWeight</code>基本分，就是描述一段时间的权重
 * 
 * 注：一下所说的线程安全，只是相对的，就是不会出现中间状态，但是有可能应该加的，但是没有加。但是这种<br />
 * 状态不会影响系统的运行。也就是说相对的安全。因为我们对事务一致性要求没有这么高！
 * 
 * 
 * 
 * @author wuyadong
 */
public class NodeWeight {
	final static private long MAX_TIME_COST = 2500L;
	final static private int MAX_TASK_IN_PROGRESS = 500;
	final static private int RECENT_INTERVAL_NUM = 50;
	
	final static private int SUCCESS = 0x0;
	final static private int SYSTEM_ERROR = 0x1;
	final static private int CLUSTER_ERROR = 0x2;
	
	//当前数据区
	private AtomicLong taskInProgress;//当前正在运行的任务个数
	
	//最近数据区
	private AtomicLong recentFinished;//最近完成的任务总数
	private AtomicLong recentTimeCost;//最近的平均花费时间
	private AtomicLong recentSuccessNum;//最近的成功次数
	private AtomicInteger recentResult;//上一次的结果
	
	//历史数据区
	private AtomicLong historyTimeCost;//历史的平均花费时间
	private AtomicLong historyFinished;//历史完成的任务数
	private AtomicLong historySuccessNum;//历史的成功次数
	private AtomicLong historySystemErrNum;//历史的systemerror的次数
	private AtomicLong historyClusterErrNum;//历史的clustererror的次数

	public NodeWeight() {
		historyTimeCost = new AtomicLong(0);
		historySuccessNum = new AtomicLong(1);
		historySystemErrNum = new AtomicLong(0);
		historyClusterErrNum = new AtomicLong(0);
		historyFinished = new AtomicLong(1);

		recentTimeCost = new AtomicLong(0);
		recentSuccessNum = new AtomicLong(1L);
		recentResult = new AtomicInteger(SUCCESS);
		recentFinished = new AtomicLong(1);
		
		taskInProgress = new AtomicLong(0);
	}
	
	/**
	 * 最重要的weight计算函数
	 * @return
	 */
	private int countWeight() {
		/**
		 * 最近任务完成情况得分计算如下：
		 * 
		 */
		//1. 计算成功率
		double recentSuccessRate = (double)recentSuccessNum.get() / recentFinished.get();
		//2. 计算花费的时间
		double recentTimeRate = 1.0 - (double)(recentTimeCost.get() % MAX_TIME_COST) / (MAX_TIME_COST - 1);
		/**
		 * 历史任务完成情况得分计算如下：
		 */
		//1. 计算成功率
		double historySuccessRate = (double)historySuccessNum.get() / historyFinished.get();
		//2. 计算花费时间
		double historyTimeRate = 1.0 - (double) (historyTimeCost.get() % MAX_TIME_COST) / (MAX_TIME_COST - 1);
		
		/***
		 * 正在执行的任务得分情况如下：
		 */
		//1. 计算空闲率
		double progressRate = 1.0 - (double)(taskInProgress.get() % MAX_TASK_IN_PROGRESS) / (MAX_TASK_IN_PROGRESS - 1);
		
		//最近所占分数
		double w1 = 10 * recentSuccessRate + 20 * recentTimeRate;
		//历史所占分数
		double w2 = 5 * historySuccessRate + 5 * historyTimeRate;
		//上一次的结果所占分数
		double w3 = 10;
		switch (recentResult.get()) {
		case SUCCESS:w3 = 10;break;
		case SYSTEM_ERROR: w3 = 5;break;
		case CLUSTER_ERROR:w3 = 0;break;
		}
		recentResult.set(SUCCESS);//这里计算了一次，就把它清掉！！！
		
		//正在执行任务的个数所占分数
		double w4 = progressRate * 20;
		//空白分数（未使用）
		double w5 = 30;
		
		return (int) Math.round(w1 + w2 + w3 + w4 + w5);
	}
	
	/**
	 * 清除recent数据
	 */
	private void checkAndReset() {
		if (recentFinished.get() > RECENT_INTERVAL_NUM) {
			recentFinished.set(1);
			recentSuccessNum.set(1);
			recentTimeCost.set(0);
		}
	}
	
	
	/**
	 * 基本线程安全
	 * @param timeCost
	 */
	public void increaseSuccess(long taskTimeCost) {
		//更新history数据
		long historyTimeTemp = historyTimeCost.get();
		long historySuccessTemp = historySuccessNum.get();
		historyTimeCost.set((historyTimeTemp * historySuccessTemp + taskTimeCost) / (historySuccessTemp + 1));
		historySuccessNum.incrementAndGet();
		historyFinished.incrementAndGet();
		//更新recent数据
		long recentTimeTemp = recentTimeCost.get();
		long recentSuccessTemp = recentSuccessNum.get();
		recentTimeCost.set((recentTimeTemp * recentSuccessTemp + taskTimeCost) / (recentSuccessTemp + 1));
		recentSuccessNum.incrementAndGet();
		recentFinished.incrementAndGet();
		//更新taskInProgress
		taskInProgress.decrementAndGet();
		//更新recentResult
		recentResult.set(SUCCESS);
		//重置
		checkAndReset();
	}
	
	/**
	 * 基本线程安全
	 */
	public void increaseSystemError() {
		//更新history数据
		historySystemErrNum.incrementAndGet();
		historyFinished.incrementAndGet();
		//更新recent数据
		recentFinished.incrementAndGet();
		//更新taskInProgress
		taskInProgress.decrementAndGet();
		//更新recentResult
		recentResult.set(SYSTEM_ERROR);
		//重置
		checkAndReset();
	}

	/**
	 * 基本线程安全
	 */
	public void increaseClusterError() {
		//更新history数据
		historyClusterErrNum.incrementAndGet();
		historyFinished.incrementAndGet();
		//更新recent数据
		recentFinished.incrementAndGet();
		//更新taskInProgress
		taskInProgress.decrementAndGet();
		//更新recentResult
		recentResult.set(CLUSTER_ERROR);
		//重置
		checkAndReset();
	}
	
	/**
	 * 线程安全的
	 */
	public void increaseInProgress() {
		taskInProgress.incrementAndGet();
	}
	
	/**
	 * 权重分为:<br />
	 * 基础分：baseWeight（总分为50）
	 * 上一次结果的影响分：（总分为10）
	 * 历史记录的分数：（总分为10）-----暂且不使用
	 * 空白分：30
	 * 线程安全的
	 * @return
	 */
	public int getWeight() {
		return countWeight();
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getTaskInProgress() {
		return taskInProgress.get();
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getTaskHasFinished() {
		return historyFinished.get() - 1;//因为初始化的时候，都加了1
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getSuccessNumber() {
		return historySuccessNum.get() - 1;//因为初始化的时候，都嫁了1
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getSystemErrorNumber() {
		return historySystemErrNum.get();
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getClusterErrorNumber() {
		return historyClusterErrNum.get();
	}
	
	/**
	 * 线程安全
	 * @return
	 */
	public long getAverageTimeCost() {
		return historyTimeCost.get();
	}
}
