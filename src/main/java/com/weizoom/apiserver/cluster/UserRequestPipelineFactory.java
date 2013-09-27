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
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.handler.ChannelManageHandler;
import com.weizoom.apiserver.cluster.handler.ExecuteHandlerForUser;
import com.weizoom.apiserver.cluster.handler.codec.HttpTaskDecoder;
import com.weizoom.apiserver.cluster.handler.codec.HttpTaskResultEncoder;
import com.weizoom.apiserver.cluster.operation.ClusterOperationHandler;

/**
 * <code>UserRequestPipelineFactory</code>��Ҫ������<code>BootstrapForUserRequest</code>��channelFactory<br />
 * ��Ҫ��������Handler<br />
 * <li>{@link com.weizoom.apiserver.cluster.handler.ChannelManageHandler}</li>���ڹ���<code>channel</code>��<code>handler</code>
 * <li>{@link HttpRequestDecoder}</li>����<code>Http</code>�����<code>handler</code>
 * <li>{@link HttpChunkAggregator}</li>����<code>Http</code>chunk�ĺϲ�
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.HttpTaskDecoder}</li>���ڴ�<code>http</code>�н�����<code>Task</code>
 * <li>{@link ExecutionHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.operation.ClusterOperationHandler}</li>���ڴӴ���<code>Operation</code>����
 * <li>{@link com.weizoom.apiserver.cluster.handler.ExecuteHandlerForUser}</li>���ڴ�������
 * <li>{@link HttpResponseEncoder}</li>���ڼ����
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.HttpTaskResultEncoder}</li>���ڼ����
 * @author wuyadong
 *
 */
public class UserRequestPipelineFactory implements ChannelPipelineFactory {
	final private static int DEFAULT_HTTP_AGGREGATOR_BUFFER_SIZE = 1024 * 10;
	final private ChannelGroup channelGroup;
	final private ExecutionHandler executionHandler;
	final private ClusterState clusterState;
	final private MasterNode masterNode;
	
	UserRequestPipelineFactory(ChannelGroup channelGroup, ExecutionHandler executionHandler, ClusterState clusterState, MasterNode masterNode) {
		if (channelGroup == null) {
			throw new NullPointerException("channelGroup");
		}
		if (executionHandler == null) {
			throw new NullPointerException("executionhandler");
		}
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		if (masterNode == null) {
			throw new NullPointerException("masterNode");
		}
		this.channelGroup = channelGroup;
		this.executionHandler = executionHandler;
		this.clusterState = clusterState;
		this.masterNode = masterNode;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline channelPipeline = Channels.pipeline();
		channelPipeline.addLast("system-channel-manage-handler", new ChannelManageHandler(channelGroup));
		channelPipeline.addLast("system-http-request-decoder", new HttpRequestDecoder());
		channelPipeline.addLast("system-http-chunk-aggregator", new HttpChunkAggregator(DEFAULT_HTTP_AGGREGATOR_BUFFER_SIZE));
		channelPipeline.addLast("system-task-decoder", new HttpTaskDecoder(ClusterSettings.getClusterChannelCharset()));
		channelPipeline.addLast("execution-handler", executionHandler);
		channelPipeline.addLast("operation-handler", new ClusterOperationHandler(clusterState));
		channelPipeline.addLast("master-execute-handler", new ExecuteHandlerForUser(masterNode));
		channelPipeline.addLast("system-http-response", new HttpResponseEncoder());
		channelPipeline.addLast("system-http-task-result-encoder", new HttpTaskResultEncoder());
		return channelPipeline;
	}
}
