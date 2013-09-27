/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster.local;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.ClusterSettings;
import com.weizoom.apiserver.cluster.Node;
import com.weizoom.apiserver.cluster.NodeGroup;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.exception.RegisterException;
import com.weizoom.apiserver.cluster.executor.ITaskExecutor;
import com.wintim.common.util.LogFactory;

/**
 * <code>LocalNode</code>是一个节点的描述，主要负责逻辑上的处理请求。<br />
 * <code>LocalNode</code>包括如下组件：
 * <li>LocalNodeBoostrap用于接收数据请求，和返回结果的功能组件</li>
 * 注：LocalNode的所有属性，都是从NodeSetting中获取的，默认不能传送参数
 * 操作Node,如下就行：
 * <pre>
 * LocalNode localNode = new LocalNode(taskExecutor);
 * LocalDaemonThread localDaemonThread = new LocalDaemonThread(localNode);
 * localNode.start();
 * localDaemonThread.start();
 * ..
 * ...
 * </pre>
 * 注：首先假定只实现集群模式/不提供单节点模式,localNode要嫁接一个守护进程
 * 如果没有守护线程，也可以正常运行，但是LocalNode发送错误时，不能自动重启
 * @author wuyadong
 */
//TODO 这里是executionHandler是可能有问题的，还有这个LocalNode的启动也是有问题的
public class LocalNode extends Node {
	final static private Logger LOG = LogFactory.getLogger(LocalNode.class);
	final static private int DEFAULT_BEATHEART_CORE_SIZE = 2;
	
	final private ITaskExecutor taskExecutor;//任务执行器
	private LocalNodeBootstrap localNodeBootstrap;//连接器
	private ExecutionHandler executionHandler;//任务执行的线程池
	private ScheduledExecutorService scheduledExecutorService;//心跳线程池
	
	private NodeGroup group = NodeGroup.GROUP_GENERAL;//node的组属性
	private boolean isStarted = false;//node是否开启
	private volatile boolean beatHeartError = false;//node的心跳是否出问题
	
	public LocalNode(ITaskExecutor taskExecutor) {
		super(NodeSettings.getLocalNodeAddress());
		if (null == taskExecutor) {
			throw new NullPointerException("task executor");
		}
		this.taskExecutor = taskExecutor;
	}

	@Override
	public TaskResult execute(Task task) {
		return taskExecutor.execute(task);
	}
	
	/**
	 * 启动LocalNode的过程,主要分为这几步:<br />
	 * <li>初始化</li>
	 * <li>注册</li>
	 * <li>连接</li>
	 */
	public void start() {
		if (!isStarted) {
			//初始化
			LOG.info("local node is starting...");
			this.scheduledExecutorService = Executors.newScheduledThreadPool(DEFAULT_BEATHEART_CORE_SIZE);
			this.executionHandler = new ExecutionHandler(Executors.newCachedThreadPool());
			localNodeBootstrap = new LocalNodeBootstrap(executionHandler, scheduledExecutorService, this, taskExecutor);
			localNodeBootstrap.setNodeAddress(nodeAddress);
			
			//TODO 这里可能有问题
			InetSocketAddress proxyNodeAddress = null;
			//注册
			if (null == (proxyNodeAddress = registerToCluster())) {
				throw new RegisterException(ClusterSettings.getClusterRegisterAddress());
			}
			//连接
			connectToProxy(proxyNodeAddress);

			LOG.info("local node started!");
			isStarted = true;
		} 
	}
	
	/**
	 * stop后可以进行start
	 */
	public void stop() {
		if (isStarted) {
			LOG.info("local node is stopping...");
			localNodeBootstrap.stop();
			if (! scheduledExecutorService.isShutdown()) {
				this.scheduledExecutorService.shutdown();
				this.scheduledExecutorService.shutdownNow();
			}
			this.executionHandler.releaseExternalResources();
			LOG.info("local node stopped!");
			isStarted = false;
		}
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	boolean isBeatHeartError() {
		return beatHeartError;
	}
	
	public void setBeatHeartError(boolean isError) {
		this.beatHeartError = isError;
	}
	
	void setGroup(NodeGroup group) {
		this.group = group;
	}
	
	private InetSocketAddress registerToCluster() {
		return RegisterChannel.registerToCluster(nodeAddress, group);
	}
	
	private void connectToProxy(InetSocketAddress address) {
		assert (null != address);
		localNodeBootstrap.setProxyAddress(address);
		localNodeBootstrap.start();
	}
}
