/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.balance;

import java.util.concurrent.atomic.AtomicInteger;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.exception.NoNodeException;

/**
 * 采用RoundRobin算法进行选择<br />
 * 注：RoundRobinStrategy，不是绝对的轮训
 * @author chuter & wuyadong
 */
class RoundRobinStrategy implements IBalancingStrategy {
	private ClusterState clusterState;
	
	private AtomicInteger nextIndex = new AtomicInteger(0);
	
	/**
	 * 线程安全的<br />
	 * 不会导致线程安全的问题<br />
	 * 但是不一定能够获取到可用的<code>ProxyNode</code>
	 * 有可能返回ProxyNode的对象，有可能返回Null，有可能爆出NoNodeException
	 */
	final public ProxyNode selectNode(Task task) throws NoNodeException {
		if (clusterState == null) {
			throw new IllegalStateException("cluster state can't be null!");
		}
		if (task == null) {
			throw new NullPointerException("task");
		}
		int size = clusterState.getActiveSize();
		if (size <= 0) {
			throw new NoNodeException("no avaliable node!");
		}
		nextIndex.set(nextIndex.get() % size);
		int index = nextIndex.get();
		ProxyNode proxyNode = clusterState.getProxyNode(index);
		nextIndex.incrementAndGet();
		return proxyNode;
	}
	
	/**
	 * 此函数不可用在多线程的环境下
	 */
	final public void setClusterState(ClusterState clusterState) {
		nextIndex.set(0);
		this.clusterState = clusterState;
	}
}
