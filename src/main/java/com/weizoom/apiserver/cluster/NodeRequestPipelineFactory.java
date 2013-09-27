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

import com.weizoom.apiserver.cluster.command.ClusterCommandHandler;
import com.weizoom.apiserver.cluster.command.NodeRegisterCommander;
import com.weizoom.apiserver.cluster.handler.ChannelManageHandler;
import com.weizoom.apiserver.cluster.handler.codec.TaskDecoder;
import com.weizoom.apiserver.cluster.handler.codec.TaskResultEncoder;

/**
 * <code>NodeRegisterPipelineFactory</code>������<code>BootstrapForRegister</code>��handler
 * ע������ֻ��ע��Ĺ��ܣ����Լ���������
 * ������Handler<br />
 * <li>{@link com.weizoom.apiserver.cluster.handler.ChannelManageHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskDecoder}</li>
 * <li>{@link com.weizoom.apiserver.cluster.command.ClusterCommandHandler}</li>
 * <li>{@link com.weizoom.apiserver.cluster.handler.codec.TaskResultEncoder}</li>
 * @author wuyadong
 *
 */
public class NodeRequestPipelineFactory implements ChannelPipelineFactory {
	final private ChannelGroup channelGroup;
	final private ClusterState clusterState;
	
	public NodeRequestPipelineFactory(ClusterState clusterState, ChannelGroup channelGroup) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		if (channelGroup == null) {
			throw new NullPointerException("channelGroup");
		}
		this.clusterState = clusterState;
		this.channelGroup = channelGroup;
	}
	
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline channelPipeline = Channels.pipeline();
		//����channel-manage-handler
		channelPipeline.addLast("system-channel-manage-handler", new ChannelManageHandler(channelGroup));
		//����task-decoder
		channelPipeline.addLast("system-task-decoder", new TaskDecoder(ClusterSettings.getClusterChannelCharset()));
		//����command-handler
		channelPipeline.addLast("command-handler", buildCommandHandler());
		//����task-result-encoder
		channelPipeline.addLast("system-task-result-encoder", new TaskResultEncoder(ClusterSettings.getClusterChannelCharset()));
		return channelPipeline;
	}
	
	private ClusterCommandHandler buildCommandHandler() {
		ClusterCommandHandler commandHandler = new ClusterCommandHandler(clusterState);
		commandHandler.registerCommander(new NodeRegisterCommander(), true);
		return commandHandler;
	}
}
