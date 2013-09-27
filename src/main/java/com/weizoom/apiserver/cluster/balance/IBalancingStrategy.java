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
 * {@link com.weizoom.apiserver.cluster.balance.Balancing}���ؾ���ʹ�õĲ��ԵĽӿ�<br />
 * 
 * @author wuyadong
 */
public interface IBalancingStrategy {
	/**
	 * ����<code>strategy</code>����ʹ�õ�{@link com.weizoom.apiserver.cluster.ClusterState}
	 * @param clusterState
	 */
	void setClusterState(ClusterState clusterState);
	/**
	 * ����<code>Task</code>ѡ��һ�����ʵ�<code>ProxyNode</code>
	 * ���û�п��õ�<code>ProxyNode</code>�ͻ��׳�<code>NoNodeException</code>
	 * �п��ܷ���һ��Null��������Ϊ���̵߳�Ե��
	 * @param task
	 * @return
	 * @throws NoNodeException
	 */
	ProxyNode selectNode(Task task) throws NoNodeException;
}
