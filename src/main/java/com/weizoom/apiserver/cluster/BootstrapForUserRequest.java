/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-8
 */
package com.weizoom.apiserver.cluster;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.wintim.common.util.LogFactory;

/**
 * <code>BootstrapForUserRequest</code>是用于<code>cluster</code>和用户进行交互的接口<br />
 * 
 * @author wuyadong
 *
 */
class BootstrapForUserRequest {
	/**
	 * TODO
	 * 这些是用于描述BootstrapForUser所使用的常量，如果使用ClusterSetting去描述是最好的，但暂且不这么用<br />
	 * 等后面再看一下
	 */
	final static private int DEFAULT_BOOTSTRAP_BOSS_NUM = 1;
	
	final static private Logger LOG = LogFactory.getLogger(BootstrapForUserRequest.class);
		
	private ServerBootstrap serverBootstrap;
	private boolean isStarted = false;
	//创建pipelineFactory
	final private UserRequestPipelineFactory userRequestPipelineFactory;
	final private ChannelGroup channelGroup;
	
	BootstrapForUserRequest(ExecutionHandler executionHandler, ClusterState clusterState, MasterNode masterNode) {
		if (executionHandler == null) {
			throw new NullPointerException("executionHandler");
		}
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		if (masterNode == null) {
			throw new NullPointerException("masterNode");
		}
		channelGroup = new DefaultChannelGroup("groupForUser");
		userRequestPipelineFactory = new UserRequestPipelineFactory(channelGroup, executionHandler, clusterState, masterNode);
	}
	
	void start() {
		if (! isStarted) {
			//1. 初始化bootstrap
			serverBootstrap = buildBootstrap();
			//2. 启动bootstrap
			LOG.info("starting clusterBootstrap(serve for users)...");
			serverBootstrap.bind();
			LOG.info("clusterBootstrap(serve for users) started!");
			isStarted = true;
		}
	}

	void stop() {
		if (isStarted) {
			LOG.info("stopping clusterBootstrap(serve for users)...");
			channelGroup.close().awaitUninterruptibly();
			serverBootstrap.releaseExternalResources();
			LOG.info("clusterBootstrap(serve for users) is stopped!");
			isStarted = false;
		}
	}
	
	/**
	 * 创建Bootstrap
	 * @return
	 */
	private ServerBootstrap buildBootstrap() {
		//创建serverbootstrap
		ServerBootstrap bootstrap =  new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), DEFAULT_BOOTSTRAP_BOSS_NUM,
                Executors.newCachedThreadPool(), ClusterSettings.getClusterWorkersCount()));
		bootstrap.setPipelineFactory(userRequestPipelineFactory);
		//设置基本属性
		bootstrap.setOption("tcpNoDelay", false);
		bootstrap.setOption("localAddress", ClusterSettings.getClusterAddress());
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("soLinger", 1);
		return bootstrap;
	}
}
