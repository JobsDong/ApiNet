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
 * ����RoundRobin�㷨����ѡ��<br />
 * ע��RoundRobinStrategy�����Ǿ��Ե���ѵ
 * @author chuter & wuyadong
 */
class RoundRobinStrategy implements IBalancingStrategy {
	private ClusterState clusterState;
	
	private AtomicInteger nextIndex = new AtomicInteger(0);
	
	/**
	 * �̰߳�ȫ��<br />
	 * ���ᵼ���̰߳�ȫ������<br />
	 * ���ǲ�һ���ܹ���ȡ�����õ�<code>ProxyNode</code>
	 * �п��ܷ���ProxyNode�Ķ����п��ܷ���Null���п��ܱ���NoNodeException
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
	 * �˺����������ڶ��̵߳Ļ�����
	 */
	final public void setClusterState(ClusterState clusterState) {
		nextIndex.set(0);
		this.clusterState = clusterState;
	}
}
