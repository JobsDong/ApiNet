/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.balance;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.exception.NoNodeException;

/**
 * {@link com.weizoom.apiserver.cluster.balance.Balancing}负载均衡使用的策略的接口<br />
 * 
 * @author wuyadong
 */
public interface IBalancingStrategy {
	/**
	 * 设置<code>strategy</code>中所使用的{@link com.weizoom.apiserver.cluster.ClusterState}
	 * @param clusterState
	 */
	void setClusterState(ClusterState clusterState);
	/**
	 * 根据<code>Task</code>选择一个合适的<code>ProxyNode</code>
	 * 如果没有可用的<code>ProxyNode</code>就会抛出<code>NoNodeException</code>
	 * 有可能返回一个Null，这是因为多线程的缘故
	 * @param task
	 * @return
	 * @throws NoNodeException
	 */
	ProxyNode selectNode(Task task) throws NoNodeException;
}
