/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-23
 */
package com.weizoom.apiserver.cluster.handler;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.common.NodeAddress;

/**
 * 
 * <code>AuthenticationHandler</code>��������֤�����Ƿ�Ϸ�<br />
 * ÿ��LocalNodeע���ʱ�����ע��ɹ����ͻ���һ��Ψһid�š�<br />
 * ͨ�����Id�ţ��ҵ���Ӧ��ProxyNode��ͬʱ���ڽ�����֤����
 * 
 * @author wuyadong
 *
 */
//TODO ��֤δͨ���Ĵ���
public class AuthenticationHandler extends SimpleChannelUpstreamHandler {
	final static public String AUTHEN_TYPE = "authen_type";//�Ƿ�����֤��
	final static public String AUTHEN_HOST = "authen_host";//��֤����host
	final static public String AUTHEN_PORT = "authen_port";//��֤����port
	final static public String AUTHEN_PATH = "authen_path";//��֤����path
	
	final static private Logger LOG = Logger.getLogger(AuthenticationHandler.class);
	final private ClusterState clusterState;
	
	public AuthenticationHandler(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		LOG.error("authentication handler get an exception.", e.getCause());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (!(e.getMessage() instanceof TaskResult)) {
			ctx.sendUpstream(e);
			return ;
		}
		
		//1. ����Ƿ�����֤��
		TaskResult result = (TaskResult)e.getMessage();
		if (checkAuthenResult(result)) {
			LOG.info("receive an authentication message:" + result.getResult());
			//�������֤�����ͽ�����֤
			JSONObject param = result.getResult();
			String host = param.getString(AUTHEN_HOST);
			int port = param.getInt(AUTHEN_PORT);
			String path = param.getString(AUTHEN_PATH);
			
			//������֤
			boolean isAuthenSuccess = clusterState.authenticate(new NodeAddress(host, port, path), e.getChannel());
			if (isAuthenSuccess) {
				LOG.info("authenticate success!");
				//�����֤�ɹ�
				return ;
			} else {
				//���δ��֤�ɹ�
				LOG.info("authenticate failed!");
				e.getChannel().close();
			}
		} else {
			//���������֤�����ͷ���
			ctx.sendUpstream(e);
		}
	}
	
	private boolean checkAuthenResult(TaskResult result) {
		JSONObject param = result.getResult();
		if (param.containsKey(AUTHEN_TYPE)) {
			return true;
		} else {
			return false;
		}
	}
}
