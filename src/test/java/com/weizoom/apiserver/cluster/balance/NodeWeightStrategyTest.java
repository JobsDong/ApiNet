/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.balance;

import org.junit.Test;

/**
 * ֻ������һ��������û�в��Զ��̵߳����<br />
 * �ܹ�Ԥ�뵽���ܷ���ProxyNode,Null,NoNodeException
 * @author wuyadong
 *
 */
public class NodeWeightStrategyTest {
	
	@Test
	public void testWithNoChange() {
//		//û��Node�����
//		ClusterState clusterState = ClusterState.getInstance();
//
//		NodeWeightStrategy nodeWeightStrategy = new NodeWeightStrategy();
//		nodeWeightStrategy.setClusterState(clusterState);
//		try {
//			nodeWeightStrategy.selectNode(Task.EMPTY_TASK);
//			Assert.assertTrue(false);
//		} catch (NoNodeException e) {
//			e.printStackTrace();
//			Assert.assertTrue(true);
//		}
//		
//		//������Node
//		clusterState.addToUnAuthen(new NodeAddress("127.0.0.1", 0), NodeGroup.GROUP_GENERAL);
//		clusterState.addToUnAuthen(new NodeAddress("127.0.0.1", 1), NodeGroup.GROUP_GENERAL);
//		clusterState.authenticate(new NodeAddress("127.0.0.1", 0), null);
//		clusterState.authenticate(new NodeAddress("127.0.0.1", 1), null);
//		
//		System.out.println(clusterState.getProxyNode(0));
//		clusterState.getProxyNode(0).getNodeWeight().increaseInProgress();
//		clusterState.getProxyNode(0).getNodeWeight().increaseSystemError();
//		clusterState.getProxyNode(1).getNodeWeight().increaseInProgress();
//		clusterState.getProxyNode(1).getNodeWeight().increaseSuccess(100);
//		
//		try {
//			ProxyNode proxyNode = nodeWeightStrategy.selectNode(Task.EMPTY_TASK);
//			Assert.assertTrue(true);
//			Assert.assertEquals(1, proxyNode.getAddress().getPort());
//		} catch (NoNodeException e) {
//			e.printStackTrace();
//			Assert.assertTrue(false);
//		}
	}
}
