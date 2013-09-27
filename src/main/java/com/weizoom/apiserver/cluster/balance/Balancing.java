/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.balance;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.Node;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.exception.NoNodeException;

/**
 * ���ؾ��⣬ͨ��{@link #getNodeToExute(Task, TaskExecutingContext)}��ȡ
 * һ��{@link Node}ִ������<br>
 * 
 * @author chuter
 *
 */
public class Balancing {
	private IBalancingStrategy balancingStrategy;
	private ClusterState clusterState;
	
	public Balancing(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.balancingStrategy = new RoundRobinStrategy();
		this.clusterState = clusterState;
		this.balancingStrategy.setClusterState(clusterState);
	}
	
	public void setBanlanceStrategy(IBalancingStrategy strategy) {
		if (strategy == null) {
			throw new NullPointerException("strategy");
		}
		this.balancingStrategy = strategy;
		this.balancingStrategy.setClusterState(clusterState);
	}
	
	public ProxyNode selectProxyNode(Task task) throws NoNodeException {
		return this.balancingStrategy.selectNode(task);
	}
}
