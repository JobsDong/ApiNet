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
 * <code>AuthenticationHandler</code>是用于认证连接是否合法<br />
 * 每个LocalNode注册的时候，如果注册成功，就会获得一个唯一id号。<br />
 * 通过这个Id号，找到对应的ProxyNode，同时用于进行认证服务
 * 
 * @author wuyadong
 *
 */
//TODO 认证未通过的处理
public class AuthenticationHandler extends SimpleChannelUpstreamHandler {
	final static public String AUTHEN_TYPE = "authen_type";//是否是认证包
	final static public String AUTHEN_HOST = "authen_host";//认证包的host
	final static public String AUTHEN_PORT = "authen_port";//认证包的port
	final static public String AUTHEN_PATH = "authen_path";//认证包的path
	
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
		
		//1. 检查是否是认证包
		TaskResult result = (TaskResult)e.getMessage();
		if (checkAuthenResult(result)) {
			LOG.info("receive an authentication message:" + result.getResult());
			//如果是认证包，就进行认证
			JSONObject param = result.getResult();
			String host = param.getString(AUTHEN_HOST);
			int port = param.getInt(AUTHEN_PORT);
			String path = param.getString(AUTHEN_PATH);
			
			//进行认证
			boolean isAuthenSuccess = clusterState.authenticate(new NodeAddress(host, port, path), e.getChannel());
			if (isAuthenSuccess) {
				LOG.info("authenticate success!");
				//如果认证成功
				return ;
			} else {
				//如果未认证成功
				LOG.info("authenticate failed!");
				e.getChannel().close();
			}
		} else {
			//如果不是认证包，就放行
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
