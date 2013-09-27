/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-19
 */

package com.weizoom.apiserver.cluster.balance;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.exception.NoNodeException;
/**
 * ����{@link com.weizoom.apiserver.cluster.NodeWeight}��ѡ����ʵ�
 * {@link com.weizoom.apiserver.cluster.Node}<br />
 * ע����ѡ��Ȩ������<code>Node</code>
 * 
 * ���̰߳�ȫ��
 * @author wuyadong
 */
class NodeWeightStrategy implements IBalancingStrategy {
	
	private ClusterState clusterState;
	
	/**
	 * �п��ܷ���ProxyNode,�п��ܷ���noNodeException���п��ܷ���Null
	 * �����Ҫ����Null�����
	 */
	final public ProxyNode selectNode(Task task) throws NoNodeException {
		if (null == task) {
			throw new NullPointerException("task");
		}
		if (clusterState.getActiveSize() <= 0) {
			throw new NoNodeException("no avaliabel node");
		}

		int maxWeight = -1;
		ProxyNode node = null;
		
		for (int i = 0; i < clusterState.getActiveSize(); i++) {
			ProxyNode proxyNode = clusterState.getProxyNode(i);
			if (proxyNode != null) {
				int weight = proxyNode.getNodeWeight().getWeight();
				if (weight >= maxWeight) {
					maxWeight = weight;
					node = proxyNode;
				} 
			}
		}
		return node;
	}
	
	final public void setClusterState(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
	}
}
