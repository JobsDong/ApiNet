/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-17
 */

package com.weizoom.apiserver.cluster.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.executor.ITaskExecutor;
import com.wintim.common.util.LogFactory;

/**
 * <code>LocalNodeExecuteHandler</code>是用于{@link com.weizoom.apiserver.cluster.local.LocalNode}的
 * 负责处理的<code>handler</code>
 * 
 * @author wuyadong
 *
 */
public class LocalNodeExecuteHandler extends SimpleChannelUpstreamHandler {
	final static private Logger LOG = LogFactory.getLogger(LocalNodeExecuteHandler.class);
	
	final private ITaskExecutor taskExecutor;
	
	public LocalNodeExecuteHandler(ITaskExecutor executor) {
		if (executor == null) {
			throw new NullPointerException("executor");
		}
		this.taskExecutor = executor;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		LOG.error("local node execute handler get an exception.", e.getCause());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		//必须有handler在此handler之前把接收的数据解码为Task
		if (! (e.getMessage() instanceof Task)) {
			ctx.sendUpstream(e);
			return;
		}
	
		Task task = (Task) e.getMessage();
		LOG.info(String.format("LocalNode execute a task,task id: %s, param: %s",task.getId(), task.getParamJson()));
		
		//把task attach到ChannelHandlerContext中，便于后续的handler通过ChannelHandlerContext
		//获取当前获取的task
//		ctx.setAttachment(task);
		TaskResult taskExecuteResult = taskExecutor.execute(task);
		
		Channel channel = e.getChannel();
		if (channel != null && channel.isWritable()) {
			channel.write(taskExecuteResult);
		}
	}
}
