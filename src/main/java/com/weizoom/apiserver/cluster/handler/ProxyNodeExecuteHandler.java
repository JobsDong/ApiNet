/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-15
 */

package com.weizoom.apiserver.cluster.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.TaskResult;
import com.wintim.common.util.LogFactory;

/**
 * 用于{@link com.weizoom.apiserver.cluster.ProxyNode}的pipeline中.<br />
 * 主要负责接收{@link com.weizoom.apiserver.cluster.local.LocalNode}的taskResult.<br />
 * 并将{@link com.weizoom.apiserver.cluster.TaskResult}设置到{@link com.weizoom.apiserver.cluster.TaskResultFuture}<br />
 * 中，并设置该任务已完成
 * 
 * @author wuyadong
 *
 */
public class ProxyNodeExecuteHandler extends SimpleChannelUpstreamHandler {
	final static private Logger LOG = LogFactory.getLogger(ProxyNodeExecuteHandler.class);
	
	final private ClusterState clusterState;
	
	public ProxyNodeExecuteHandler(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		LOG.error("proxy node execute handler get an exception.", e.getCause());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (! (e.getMessage() instanceof TaskResult)) {
			ctx.sendUpstream(e);
			return ;
		}
		assert (e.getMessage() instanceof TaskResult);
		TaskResult result = (TaskResult) e.getMessage();
		LOG.info("proxy node execute handler get a result." + result.toJson());
		//1. 获得对应的ProxyNode
		ProxyNode proxyNode = clusterState.getProxyNode(e.getChannel());
		assert (proxyNode != null);
		
		//2. 设置为完成状态
		proxyNode.setDone(result.getTaskId(), result);
	}
}
