/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-3
 */
package com.weizoom.apiserver.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jboss.netty.channel.Channel;

import com.weizoom.apiserver.cluster.common.NodeAddress;

/**
 * <code>ClusterState</code>是用于保存{@link com.weizoom.apiserver.cluster.Cluster}中的数据的结构<br />
 * <code>ClusterState</code>主要是负责保管{@link com.weizoom.apiserver.cluster.ProxyNode}的集合
 * 
 * @author wuyadong
 */
//TODO 修改ClusterState的数据结构，提高性能
public class ClusterState {
	
	//没有授权的Node集合
	final private CopyOnWriteArrayList<ProxyNode> unAuthentNodes;
	//正常工作的Nodes
	final private List<ProxyNode> activeNodes;
	final private ReadWriteLock lock;
	//失效的Nodes
	final private CopyOnWriteArrayList<ProxyNode> inactiveNodes;
	
	static private ClusterState single = null;

	final static public ClusterState getInstance() {
		if (single == null) {
			single = new ClusterState();
		}
		return single;
	}
	
	private ClusterState() {
		lock = new ReentrantReadWriteLock();
		activeNodes = new ArrayList<ProxyNode>();
		inactiveNodes = new CopyOnWriteArrayList<ProxyNode>();
		unAuthentNodes = new CopyOnWriteArrayList<ProxyNode>();
	}
	
	/**
	 * 创建ProxyNode
	 * 如果存在，就返回False<br />
	 * 否则返回True<br />
	 * 注：这个方法，调用不频繁
	 * 貌似线程安全的
	 * @param nodeAddress
	 * @return
	 */
	public boolean addToUnAuthen(NodeAddress nodeAddress, NodeGroup group) {
		if (nodeAddress == null) {
			throw new NullPointerException("nodeAddress");
		}
		if (group == null) {
			throw new NullPointerException("group");
		}
		ProxyNode proxyNode = new ProxyNode(nodeAddress);
		proxyNode.setGroup(group);
		synchronized (activeNodes) {
			if (activeNodes.contains(proxyNode) && unAuthentNodes.contains(proxyNode)) {
				return false;
			} else {
				unAuthentNodes.add(proxyNode);
				inactiveNodes.remove(proxyNode);
				return true;
			}
		}
	}
	
	/**
	 * 从activeNodes中删除ProxyNode<br />
	 * 注：这个方法，调用不频繁
	 * 貌似线程安全的
	 * @param proxyNode
	 */
	public void deleteProxyNode(ProxyNode proxyNode) {
		if (proxyNode == null) {
			throw new NullPointerException("proxyNode");
		}
		synchronized (activeNodes) {
			if (activeNodes.remove(proxyNode)) {
				proxyNode.resetNodeWeight();
				inactiveNodes.add(proxyNode);
			}
		}
	}
	
	/**
	 * 用于认证的，主要是，设定ProxyNode和ChannelId对应的关系<br />
	 * 如果没有这个ProxyNode，就返回false
	 * 如果有这个ProxyNode，并且未被认证过，就返回true
	 * 注：调用不频繁
	 * @param nodeAddress
	 * @param channelId
	 * @return
	 */
	public boolean authenticate(NodeAddress nodeAddress, final Channel channel) {
		if (nodeAddress == null) {
			throw new NullPointerException("nodeAddress");
		}
		for (int i = 0; i < unAuthentNodes.size(); i++) {
			ProxyNode proxyNode = unAuthentNodes.get(i);
			if (proxyNode.getAddress().equals(nodeAddress)) {
				proxyNode.setChannel(channel);
				unAuthentNodes.remove(i);
				addToActive(proxyNode);
				return true;
			}
		}
		return false;
	}
	
	private void addToActive(ProxyNode proxyNode) {
		assert (proxyNode != null);
		
		lock.writeLock().lock();
		try {
			activeNodes.add(proxyNode);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * 貌似线程安全
	 * @param channel
	 * @return
	 */
	public ProxyNode getProxyNode(Channel channel) {
		if (channel == null) {
			throw new NullPointerException("channel");
		}
		lock.readLock().lock();
		try {
			for (ProxyNode proxyNode : activeNodes) {
				if (proxyNode.getChannel().getId() == channel.getId()) {
					return proxyNode;
				}
			}
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}
	
	/**
	 * 调用频繁
	 * @return
	 */
	public int getActiveSize() {
		lock.readLock().lock();
		try {
			return activeNodes.size();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * 如果index错误就会返回Null<br />
	 * 否则就会返回ProxyNode对象
	 * 注：调用频繁
	 * @param index
	 * @return
	 */
	public ProxyNode getProxyNode(int index) {
		lock.readLock().lock();
		try {
			if (index < 0 || index >= activeNodes.size()) {
				return null;
			} else {
				return activeNodes.get(index);
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * 获得当前正在执行的<code>Task</code>个数
	 * 调用不频繁
	 * @return
	 */
	public long getExecutingTasksCount() {
		long sum = 0L;

		lock.readLock().lock();
		try {
			for (ProxyNode proxyNode : activeNodes) {
				sum += proxyNode.getNodeWeight().getTaskInProgress();
			}
		} finally {
			lock.readLock().unlock();
		}
		return sum;
	}
	
	/**
	 * 获取的是已激活的<code>Node</code>集合
	 * 返回的是不可修改的视图
	 * 注：不知道需要不需要进行同步
	 * @return
	 */
	public List<ProxyNode> getActiveNodes() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableList(activeNodes);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 获取的是未激活的<code>Node</code>集合
	 * 注：不知道需不需要进行同步
	 * @return
	 */
	public List<ProxyNode> getInactiveNodes() {
		return Collections.unmodifiableList(inactiveNodes);
	}
}
