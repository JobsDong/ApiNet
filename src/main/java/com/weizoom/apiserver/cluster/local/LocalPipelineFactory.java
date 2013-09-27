/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-9
 */
package com.weizoom.apiserver.cluster.local;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.beatheart.PingHeartBeatHandler;
import com.weizoom.apiserver.cluster.executor.ITaskExecutor;
import com.weizoom.apiserver.cluster.handler.LocalNodeExecuteHandler;
import com.weizoom.apiserver.cluster.handler.codec.CompressEncoder;
import com.weizoom.apiserver.cluster.handler.codec.TaskDecoder;
import com.weizoom.apiserver.cluster.handler.codec.TaskResultEncoder;
import com.wintim.common.util.LogFactory;

/**
 * <code>LocalPipelineFactory</code>是用于<code>LocalNode</code>的<code>Bootstrap</code>中。
 * 从上向下依次加入的<code>handler</code>为:<br/>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskDecoder}</li>
 * <li>{@link ExecutionHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.beatheart.PingHeartBeatHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.LocalNodeExecuteHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.CompressEncoder}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskResultEncoder}</li>
 * <li></li>
 * 
 * @author wuyadong
 *
 */
public class LocalPipelineFactory implements ChannelPipelineFactory {
	final static private long DEFAULT_PING_DELAY_TIME = 5000;
	
	final static private Logger LOG = LogFactory.getLogger(LocalPipelineFactory.class);
	final private ExecutionHandler executionHandler;
	final private ScheduledExecutorService scheduledExecutorService;
	final private LocalNode localNode;
	final private ITaskExecutor taskExecutor;
	
	public LocalPipelineFactory(ExecutionHandler executionHandler, ScheduledExecutorService scheduledExecutorService, LocalNode localNode, ITaskExecutor taskExecutor) {
		if (executionHandler == null) {
			throw new NullPointerException("executionHandler");
		}
		if (scheduledExecutorService == null) {
			throw new NullPointerException("scheduledExecutorService");
		}
		if (localNode == null) {
			throw new NullPointerException("localNode");
		}
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor");
		}
		this.localNode = localNode;
		this.executionHandler = executionHandler;
		this.scheduledExecutorService = scheduledExecutorService;
		this.taskExecutor = taskExecutor;
	}
	
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline channelPipeline = Channels.pipeline();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("start building local node bootstrap...");
		}
		//增加task-decoder
		channelPipeline.addLast("system-task-decoder", new TaskDecoder(NodeSettings.getLocalNodeChannelCharset()));
		//增加execution-handler
		channelPipeline.addLast("system-execution-handler", executionHandler);
		//增加ping-pong-actor
		channelPipeline.addLast("system-ping-pong-actor-handler", new PingHeartBeatHandler(scheduledExecutorService, localNode, DEFAULT_PING_DELAY_TIME, DEFAULT_PING_DELAY_TIME * 3, TimeUnit.MILLISECONDS));
		//增加local-execute-handler
		channelPipeline.addLast("local-execute-handler", new LocalNodeExecuteHandler(taskExecutor));
		//增加compress-handler
		channelPipeline.addLast("system-compress-encoder", new CompressEncoder());
		//增加task-result-encoder
		channelPipeline.addLast("system-task-result-encoder", new TaskResultEncoder(NodeSettings.getLocalNodeChannelCharset()));
		if (LOG.isDebugEnabled()) {
			LOG.debug("building local node bootstrap finished!");
		}
		return channelPipeline;
	}
}
