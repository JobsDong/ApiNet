/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.balance;

import org.junit.Test;

/**
 * 没有测试多线程的情况下会出现什么情况<br />
 * 但是可以预想到，多线程的情况下有可能会出现如下情况：
 * <li>NoNodeException</li>
 * <li>Null</li>
 * <li>ProxyNode</li>
 * 
 * @author wuyadong
 *
 */
public class RoundRobinStrategyTest {
	
	/**
	 * 测试ClusterState没有变化的情况下的RoundRobinStrategy
	 */
	@Test
	public void testWithNoChange() {
//		ClusterState clusterState = ClusterState.getInstance();
//		RoundRobinStrategy roundRobinStrategy = new RoundRobinStrategy();
//		roundRobinStrategy.setClusterState(clusterState);
//		
//		//1. activeNodes为空的时候
//		try {
//			roundRobinStrategy.selectNode(Task.EMPTY_TASK);
////			Assert.assertTrue(false);
//		} catch (NoNodeException e) {
//			e.printStackTrace();
//			Assert.assertTrue(true);
//		}
//		
//		//2. 当activeNodes为两个的时候
//		clusterState.addToUnAuthen(new NodeAddress("127.0.0.1", 0), NodeGroup.GROUP_GENERAL);
//		clusterState.addToUnAuthen(new NodeAddress("127.0.0.1", 1), NodeGroup.GROUP_GENERAL);
//		clusterState.authenticate(new NodeAddress("127.0.0.1", 0), null);
//		clusterState.authenticate(new NodeAddress("127.0.0.1", 1), null);
//		
//		for (int i = 0; i < clusterState.getActiveSize() * 3; i++) {
//			try {
//				ProxyNode proxyNode = roundRobinStrategy.selectNode(Task.EMPTY_TASK);
//				Assert.assertNotNull(proxyNode);
//				Assert.assertEquals(i % clusterState.getActiveSize(), proxyNode.getAddress().getPort());
//				Assert.assertTrue(true);
//			} catch (NoNodeException e) {
//				e.printStackTrace();
//				Assert.assertFalse(false);
//			}
//		}
	}
}
