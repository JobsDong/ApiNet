/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.balance;

import org.junit.Test;

/**
 * û�в��Զ��̵߳�����»����ʲô���<br />
 * ���ǿ���Ԥ�뵽�����̵߳�������п��ܻ�������������
 * <li>NoNodeException</li>
 * <li>Null</li>
 * <li>ProxyNode</li>
 * 
 * @author wuyadong
 *
 */
public class RoundRobinStrategyTest {
	
	/**
	 * ����ClusterStateû�б仯������µ�RoundRobinStrategy
	 */
	@Test
	public void testWithNoChange() {
//		ClusterState clusterState = ClusterState.getInstance();
//		RoundRobinStrategy roundRobinStrategy = new RoundRobinStrategy();
//		roundRobinStrategy.setClusterState(clusterState);
//		
//		//1. activeNodesΪ�յ�ʱ��
//		try {
//			roundRobinStrategy.selectNode(Task.EMPTY_TASK);
////			Assert.assertTrue(false);
//		} catch (NoNodeException e) {
//			e.printStackTrace();
//			Assert.assertTrue(true);
//		}
//		
//		//2. ��activeNodesΪ������ʱ��
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
