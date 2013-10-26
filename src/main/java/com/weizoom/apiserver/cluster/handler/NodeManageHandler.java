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
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.constant.ResultCode;

public class NodeManageHandler extends SimpleChannelUpstreamHandler {
	final private static Logger LOG = Logger.getLogger(NodeManageHandler.class);
	final private ClusterState clusterState;
	public NodeManageHandler(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		ProxyNode proxyNode = clusterState.getProxyNode(e.getChannel());
		LOG.info("one proxyNode disconnected!:" + proxyNode);
		proxyNode.setDoneToAllTask(ResultCode.CLUSTER_ERROR_NEED_RETRY, new JSONObject(), "node is closed");
		e.getChannel().close();
		clusterState.deleteProxyNode(proxyNode);
	}
}
