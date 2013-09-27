/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster;

import com.weizoom.apiserver.cluster.common.NodeAddress;

/**
 * 集群中一个节点的描述
 * 
 * @author chuter
 *
 */
public abstract class Node {
	protected NodeAddress nodeAddress;
	
	public Node(NodeAddress nodeAddress) {
		if (null == nodeAddress) {
			throw new NullPointerException("node address");
		}
		
		this.nodeAddress = nodeAddress;
	}
	
	public NodeAddress getAddress() {
		return nodeAddress;
	}
	
	boolean isMaster() {
		return false;
	}
	
	boolean isLocal() {
		return false;
	}
	
	public String getId() {
		return nodeAddress.hashStr();
	}
	
	public abstract TaskResult execute(Task task) throws Exception;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return node.getAddress().equals(nodeAddress);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return String.format("host:%s,port:%d,path:%s", nodeAddress.getHost(), nodeAddress.getPort(), nodeAddress.getPath()).hashCode();
	}
}
