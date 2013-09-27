/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-15
 */

package com.weizoom.apiserver.cluster;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.constant.ResultCode;


/**
 * 用于保存{@link com.weizoom.apiserver.cluster.TaskResult}的列表，<br />
 * 主要用于{@link com.weizoom.apiserver.cluster.ProxyNode}中管理任务的列表
 * 
 * @author wuyadong
 *
 */
public class TaskResultFutureTable {
	final private ConcurrentHashMap<Task, TaskResultFuture> taskResultFutureTable;
	final private ConcurrentHashMap<String, Task> id2Task;
	final private ReentrantLock lock;
	
	public TaskResultFutureTable() {
		taskResultFutureTable = new ConcurrentHashMap<Task, TaskResultFuture>();
		id2Task = new ConcurrentHashMap<String, Task>();
		lock = new ReentrantLock();
	}
	
	/**
	 * 用于加入Task，以及他的Future，这些task是未完成的
	 * @param task
	 * @param future
	 */
	public void putTaskResultFuture(Task task, TaskResultFuture future) {
		if (null == future) {
			throw new NullPointerException("future");
		}
		if (task == null) {
			throw new NullPointerException("task");
		}
		
		if (id2Task.containsKey(task.getId()) || taskResultFutureTable.containsKey(task)) {
			throw new IllegalArgumentException("has same id!");
		}
		lock.lock();
		try {
			id2Task.put(task.getId(), task);
			taskResultFutureTable.put(task, future);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 填充taskFuture的结果，并将对应的taskExecuteFuture设置为完成状态.
	 * 如果这个task不存在，不做任何事情，返回false<br />
	 * 如果这个task存在，返回true
	 * @param taskId
	 * @param result
	 * @return
	 */
	public boolean setDone(String taskId, TaskResult result) {
		Task task = id2Task.get(taskId);
		if (task != null) {
			TaskResultFuture future = taskResultFutureTable.get(task);
			if (future != null) {
				future.setResult(result.getResult());
				future.setCode(result.getCode());
				future.setMessage(result.getMessage());
				future.setDone();
				
				lock.lock();
				try {
					id2Task.remove(taskId);
					taskResultFutureTable.remove(task);
				} finally {
					lock.unlock();
				}
				return true;
			} else {
				//TODO something
				throw new NullPointerException("fuck");
			}
		} else {
			//TODO something
			return false;
		}
	}
	
	/**
	 *清除所有tasks <br />
	 *一般用于清除一个ProxyNode的操作
	 *这里没有细想过，使用的时候请注意检查
	 */
	public void clearAllTasks(ResultCode resultCode, JSONObject result, String message) {
		if (resultCode == null) {
			throw new NullPointerException("resultCode");
		}
		if (result == null) {
			throw new NullPointerException("result");
		}
		if (message == null) {
			throw new NullPointerException("message");
		}
		for (TaskResultFuture future : taskResultFutureTable.values()) {
			future.setCode(resultCode);
			future.setResult(result);
			future.setMessage(message);
			future.setDone();
		}
		taskResultFutureTable.clear();
	}
	
	public Task getTask(String taskId) {
		if (taskId == null) {
			throw new NullPointerException("taskId");
		}
		return id2Task.get(taskId);
	}
	
	/**
	 * 返回正在执行的任务个数
	 * @return
	 */
	public int size() {
		return taskResultFutureTable.size();
	}
}
