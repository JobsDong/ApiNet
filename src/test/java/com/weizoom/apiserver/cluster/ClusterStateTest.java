/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-8
 */
package com.weizoom.apiserver.cluster;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.weizoom.apiserver.cluster.common.NodeAddress;

public class ClusterStateTest {
	
	@Test
	public void testClusterState() {
		//≤‚ ‘authenticate
		ClusterState clusterState = ClusterState.getInstance();
		assertTrue(clusterState.addToUnAuthen(new NodeAddress("127.0.0.1", 0), NodeGroup.GROUP_GENERAL));
		assertTrue(clusterState.authenticate(new NodeAddress("127.0.0.1", 0), null));
		assertEquals(1, clusterState.getActiveSize());
		
		//≤‚ ‘…æ≥˝
		ProxyNode proxyNode = clusterState.getProxyNode(0);
		assertEquals(new NodeAddress("127.0.0.1", 0), proxyNode.getAddress());
		clusterState.deleteProxyNode(proxyNode);
		assertEquals(0, clusterState.getActiveSize());
		assertEquals(1,clusterState.getInactiveNodes().size());
	
	}
}
