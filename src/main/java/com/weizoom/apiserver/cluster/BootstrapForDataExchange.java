/**
 * Copyright    : Copyright (c) 2006. Wintim Coop. All rights reserved
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
import org.jboss.netty.handler.execution.ExecutionHandler;


/**
 * <code>BootstrapForDataExchange</code>是用于集群中节点与<code>Cluster</code>的通信<br />
 * 主要是用于{@link com.weizoom.apiserver.cluster.Task}和{@link com.weizoom.apiserver.cluster.TaskResult}的交换<br />
 * 
 * @author wuyadong
 *
 */
class BootstrapForDataExchange {
	final static private Logger LOG = Logger.getLogger(BootstrapForDataExchange.class);

	//TODO 使用统一配置管理模块，处理apinet集群的配置(这里可以考虑使用ClusterSettings)
	final static private int DEFAULT_BOOTSTRAP_BOSS_NUM = 1;
	final static private int DEFAULT_BOOTSTRAP_WORKER_NUM = 5;
	
	private ServerBootstrap serverBootstrap;
	private boolean isStarted = false;
	
	final private DataExchangePipelineFactory dataExchangePipelineFactory;
	final private ChannelGroup channelGroup;
	
	BootstrapForDataExchange(ClusterState clusterState, ExecutionHandler executionHandler) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		if (executionHandler == null) {
			throw new NullPointerException("executionHandler");
		}
		channelGroup = new DefaultChannelGroup("groupForTask");
		dataExchangePipelineFactory = new DataExchangePipelineFactory(channelGroup, clusterState, executionHandler);
	}
	
	void start() {
		if (! isStarted) {
			LOG.info("bootstrap for task is starting...");
			serverBootstrap = buildBootstrap();
			serverBootstrap.bind();
			LOG.info("bootstrap for task started!");
		}
	}
	
	/**
	 * stop之后，可以start
	 */
	void stop() {
		if (isStarted) {
			LOG.info("bootstrap for task is stopping...");
			channelGroup.close().awaitUninterruptibly();
			serverBootstrap.releaseExternalResources();
			LOG.info("bootstrap for task stopped!");
		}
	}
	
	private ServerBootstrap buildBootstrap() {
		//创建serverbootstrap
		ServerBootstrap bootstrap =  new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), DEFAULT_BOOTSTRAP_BOSS_NUM,
                Executors.newCachedThreadPool(), DEFAULT_BOOTSTRAP_WORKER_NUM));
		bootstrap.setPipelineFactory(dataExchangePipelineFactory);
		//设置基本属性
		bootstrap.setOption("tcpNoDelay", false);
		bootstrap.setOption("localAddress", ClusterSettings.getClusterDataExchangeAddress());
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("soLinger", 1);
		return bootstrap;
	}
}
