/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-6
 */
package com.weizoom.apiserver.cluster.handler;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.group.ChannelGroup;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.constant.ResultCode;

/**
 * <code>TaskChannelManageHandler</code>是用于在BootstrapForTask中控制channelGroup<br />
 * 同时，清除已断开连接的所有Task
 * @author wuyadong
 *
 */
public class TaskChannelManageHandler extends ChannelManageHandler {
	final static Logger LOG = Logger.getLogger(TaskChannelManageHandler.class);
	final private ClusterState clusterState;
	
	public TaskChannelManageHandler(ChannelGroup channelGroup, ClusterState clusterState) {
		super(channelGroup);
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		ProxyNode proxyNode = clusterState.getProxyNode(e.getChannel());
		proxyNode.setDoneToAllTask(ResultCode.CLUSTER_ERROR_NEED_RETRY, new JSONObject(), "node is closed");
		e.getChannel().close();
		clusterState.deleteProxyNode(proxyNode);
		super.channelDisconnected(ctx, e);
	}
}
