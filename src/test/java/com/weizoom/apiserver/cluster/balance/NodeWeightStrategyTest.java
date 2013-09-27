/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.balance;

import org.junit.Test;

/**
 * 只测试了一个，并且没有测试多线程的情况<br />
 * 能够预想到可能返回ProxyNode,Null,NoNodeException
 * @author wuyadong
 *
 */
public class NodeWeightStrategyTest {
	
	@Test
	public void testWithNoChange() {
//		//没有Node的情况
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
//		//有两个Node
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
