/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-9
 */
package com.weizoom.apiserver.cluster;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.beatheart.PongHeartBeatHandler;
import com.weizoom.apiserver.cluster.handler.AuthenticationHandler;
import com.weizoom.apiserver.cluster.handler.ChannelManageHandler;
import com.weizoom.apiserver.cluster.handler.NodeManageHandler;
import com.weizoom.apiserver.cluster.handler.ProxyNodeExecuteHandler;
import com.weizoom.apiserver.cluster.handler.codec.CompressDecoder;
import com.weizoom.apiserver.cluster.handler.codec.TaskEncoder;
import com.weizoom.apiserver.cluster.handler.codec.TaskResultDecoder;

/**
 * <code>NodeTaskPipelineFactory</code>��{@link com.weizoom.apiserver.cluster.BootstrapForDataExchange}�е�channelFactory
 * ��Ҫ���������handler:<br />
 * <li>{@link com.weizoom.apiserver.cluster.handler.ChannelManageHandler}</li>���ڹ���<code>channel</code>��<code>handler</code>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.CompressDecoder}</li>���ڽ��н�ѹ����<code>handler</code>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskResultDecoder}</li>���ڽ���<code>TaskResult</code>�Ľ���
 * <li>{@link com.weizoom.apiserver.cluster.handler.NodeManageHandler}</li>�ر����ڴ���<code>Node</code>�ر��¼�
 * <li>{@link com.weizoom.apiserver.cluster.beatheart.PongHeartBeatHandler}</li>�ر����ڴ��������¼���<code>handler</code>
 * <li>{@link ExecutionHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.AuthenticationHandler}</li>������Ȩ�����<code>Handler</code>
 * <li>{@link com.weizoom.apiserver.cluster.handler.ProxyNodeExecuteHandler}</li>���ڴ��������<code>Handler</code>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskEncoder}</li>���ڶ�<code>Task</code>���м����<code>Handler</code>
 * 
 * @author wuyadong
 *
 */
public class DataExchangePipelineFactory implements ChannelPipelineFactory {
	final private ChannelGroup channelGroup;
	final private ClusterState clusterState;
	final private ExecutionHandler executionHandler;
	
	public DataExchangePipelineFactory(ChannelGroup channelGroup, ClusterState clusterState, ExecutionHandler executionHandler) {
		if (channelGroup == null) {
			throw new NullPointerException("channelGroup");
		}
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		if (executionHandler == null) {
			throw new NullPointerException("execution handler");
		}
		this.channelGroup = channelGroup;
		this.clusterState = clusterState;
		this.executionHandler = executionHandler;
	}
	
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline channelPipeline = Channels.pipeline();
		channelPipeline.addLast("system-channel-manage-handler", new ChannelManageHandler(channelGroup));
		channelPipeline.addLast("system-task-result-compress-decoder", new CompressDecoder());
		channelPipeline.addLast("system-task-result-decoder", new TaskResultDecoder(ClusterSettings.getClusterChannelCharset()));
		channelPipeline.addLast("node-stop-handler", new NodeManageHandler(clusterState));
		channelPipeline.addLast("ping-pong-reactor-handler", new PongHeartBeatHandler());
		channelPipeline.addLast("execution-handler", executionHandler);
		channelPipeline.addLast("authenticate-handler", new AuthenticationHandler(clusterState));
		channelPipeline.addLast("proxy-execute-handler", new ProxyNodeExecuteHandler(clusterState));
		channelPipeline.addLast("system-task-encoder", new TaskEncoder(ClusterSettings.getClusterChannelCharset()));
		return channelPipeline;
	}
}
