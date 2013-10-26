/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-8
 */
package com.weizoom.apiserver.cluster.local;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.common.NodeAddress;
import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.exception.ConnectException;
import com.weizoom.apiserver.cluster.executor.ITaskExecutor;
import com.weizoom.apiserver.cluster.handler.AuthenticationHandler;

/**
 * <code>local node boostrap</code>是用于数据交换的组件<br />
 * 主要交换的数据格式有<code>Task</code>和<code>TaskResult</code>
 * 注：用于<code>LocalNode</code>
 * 
 * @author wuyadong
 */
class LocalNodeBootstrap {
	final static private Logger LOG = Logger.getLogger(LocalNodeBootstrap.class);
	protected ClientBootstrap requestAcceptBossBootstrap;
	private boolean isStarted = false;
	private Channel channel;
	final private LocalPipelineFactory localPipelineFactory;
	private InetSocketAddress proxyAddress;
	private NodeAddress nodeAddress;
	
	LocalNodeBootstrap(ExecutionHandler executionHandler, ScheduledExecutorService scheduledExecutorService, LocalNode localNode, ITaskExecutor taskExecutor) {
		localPipelineFactory = new LocalPipelineFactory(executionHandler, scheduledExecutorService, localNode, taskExecutor);
	}
	
	void setNodeAddress(NodeAddress nodeAddress) {
		if (nodeAddress == null) {
			throw new NullPointerException("nodeAddress");
		}
		this.nodeAddress = nodeAddress;
	}
	
	/**
	 * 注：这里的注册是有问题的，但是不影响大局
	 * 写入授权信息，应该等待回复
	 */
	void start() {
		if (! isStarted) {
			requestAcceptBossBootstrap = buildBootstrap();
			LOG.info("local node bootstrap is starting....");
			ChannelFuture future = requestAcceptBossBootstrap.connect(proxyAddress);
			future.awaitUninterruptibly();
			if (future.isSuccess()) {
				isStarted = true;
				channel = future.getChannel();
				channel.write(createAuthenResult()).awaitUninterruptibly();
				LOG.info("local node bootstrap start successfully!");
			} else {
				isStarted = false;
				LOG.error("local node bootstrap started failed!");
				throw new ConnectException(String.format("can't connect to proxyNode:%s",proxyAddress.toString()));
			}
		}
	}
	
	/**
	 * stop之后，是不能start的
	 */
	void stop() {
		if (isStarted) {
			LOG.info("local node bootstrap is stopping...");
			if (channel != null) {
				channel.close().awaitUninterruptibly();
			}
			if (requestAcceptBossBootstrap != null) {
				requestAcceptBossBootstrap.shutdown();
				requestAcceptBossBootstrap.releaseExternalResources();
			}
			isStarted = false;
			LOG.info("local node bootstrap stopped!");
		}
	}
	
	void setProxyAddress(InetSocketAddress address) {
		if (address == null) {
			throw new NullPointerException("address");
		}
		this.proxyAddress = address;
	}
	
	private ClientBootstrap buildBootstrap() {
		ClientBootstrap clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool(), 
				NodeSettings.getLocalNodeWorkersCount()));
		clientBootstrap.setPipelineFactory(localPipelineFactory);
		clientBootstrap.setOption("localAddress", NodeSettings.getLocalNodeInetAddress());
		clientBootstrap.setOption("tcpNoDelay", false);
		clientBootstrap.setOption("keepAlive", true);
		clientBootstrap.setOption("soLinger", 0);
		return clientBootstrap;
	}
	
	private TaskResult createAuthenResult() {
		JSONObject json = new JSONObject();
		json.put(AuthenticationHandler.AUTHEN_HOST, nodeAddress.getHost());
		json.put(AuthenticationHandler.AUTHEN_PORT, nodeAddress.getPort());
		json.put(AuthenticationHandler.AUTHEN_PATH, nodeAddress.getPath());
		json.put(AuthenticationHandler.AUTHEN_TYPE, true);
		TaskResult result = new TaskResult(Task.FAKE_TASK_ID, ResultCode.SUCCESS, json);
		return result;
	}
}
