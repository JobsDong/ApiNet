/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-29
 */
package com.weizoom.apiserver.cluster;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


/**
 * <code>BootstrapForNodeRequest</code>是专门用于处理<code>Node</code>请求的接口
 * 注：暂时只支持注册<br />
 * @author wuyadong
 *
 */
class BootstrapForNodeRequest {
	final static private Logger LOG = Logger.getLogger(BootstrapForNodeRequest.class);
	
	/**
	 * 这里可以考虑使用ClusterSettings来处理
	 */
	final static private int DEFAULT_BOOTSTRAP_BOSS_NUM = 1;
	final static private int DEFAULT_BOOTSTRAP_WORKER_NUM = 5;
	
	private ServerBootstrap serverBootstrap;
	final private ChannelGroup channelGroup;
	private boolean isStarted = false;
	
	final private NodeRequestPipelineFactory nodeRequestPipelineFactory;
	
	public BootstrapForNodeRequest(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		channelGroup = new DefaultChannelGroup("groupForCommand");
		nodeRequestPipelineFactory = new NodeRequestPipelineFactory(clusterState, channelGroup);
	}
	
	void start() {
		if (! isStarted) {
			LOG.info("bootstrap for node request is starting...");
			serverBootstrap = buildBootstrap();
			serverBootstrap.bind();
			LOG.info("bootstrap for node request started!");
		}
	}
	
	void stop() {
		if (isStarted) {
			LOG.info("bootstrap for node request is stopping...");
			channelGroup.close().awaitUninterruptibly();
			serverBootstrap.releaseExternalResources();
			LOG.info("bootstrap for node request stopped!");
		}
	}
	
	private ServerBootstrap buildBootstrap() {
		//创建serverbootstrap
		ServerBootstrap bootstrap =  new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), DEFAULT_BOOTSTRAP_BOSS_NUM,
                Executors.newCachedThreadPool(), DEFAULT_BOOTSTRAP_WORKER_NUM));
		bootstrap.setPipelineFactory(nodeRequestPipelineFactory);
		//设置基本属性
		bootstrap.setOption("tcpNoDelay", false);
		bootstrap.setOption("localAddress", ClusterSettings.getClusterRegisterAddress());
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("soLinger", 1);
		return bootstrap;
	}
}
