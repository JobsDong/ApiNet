/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.beatheart;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.local.LocalNode;

/**
 * <code>PingHeartBeatHandler</code>是专门用于客户端发起心跳<code>ping</code>的Handler<br />
 * 同时，也固定时间检测是否有接收到<code>pong</code>的信息。
 * 
 * 具体如下：<br />
 * 1. 每个x秒，都会发出一个Ping
 * 2. 检测pong，如果在n秒中没有收到，就执行不能连接的代码
 * 
 * @author wuyadong
 */
public class PingHeartBeatHandler extends SimpleChannelUpstreamHandler {
	final static private Logger LOG = Logger.getLogger(PingHeartBeatHandler.class);
	
	final private ScheduledExecutorService scheduledExecutorService;
	final private long pingTime;
	final private long timeOut;
	final private TimeUnit timeUnit;
	final private LocalNode localNode;
	private Channel channel;
	private boolean isResponsed;
	private volatile boolean isGoOn = true;
	
	/**
	 * @param pingTime 是ping的间隔时间
	 * @param timeOut 是得到pong的最短间隔时间
	 * @param timeUnit 是时间的单位
	 */
	public PingHeartBeatHandler(ScheduledExecutorService scheduledExecutorService, LocalNode localNode, long pingTime, long timeOut, TimeUnit timeUnit) {
		if (scheduledExecutorService == null) {
			throw new NullPointerException("scheduledExecutorService");
		}
		this.scheduledExecutorService = scheduledExecutorService;
		this.pingTime = pingTime;
		this.timeOut = timeOut;
		this.timeUnit = timeUnit;
		this.isResponsed = false;
		this.localNode = localNode;
	}
	
	/**
	 * 当channel连接上了，就开启心跳线程，和检测心跳的线程
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		this.channel = e.getChannel();
		
		//启动ping任务
		scheduledExecutorService.scheduleWithFixedDelay(buildPingActor(), pingTime, pingTime, timeUnit);
		//启动pongActor任务
		scheduledExecutorService.scheduleWithFixedDelay(buildTimeOutMonitor(), timeOut, timeOut, timeUnit);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (! (e.getMessage() instanceof Task)) {
			ctx.sendUpstream(e);
			return ;
		}
		
		assert (e.getMessage() instanceof Task);
		Task task = (Task) e.getMessage();
		
		//检查是否是PongTask
		if (! isPongTask(task)) {
			ctx.sendUpstream(e);
			return ;
		} 
		LOG.debug("ping heart beat handler get a pong response!");
		isResponsed = true;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		LOG.error("ping heart beat handler get an exception.", e.getCause());
	}

	/**
	 * 创建心跳线程
	 * @return
	 */
	private Runnable buildPingActor() {
		Runnable pingActor = new Runnable() {
			public void run() {
				if (isGoOn && channel != null && channel.isWritable()) {
					channel.write(new ClusterPingTaskResult());
					LOG.debug("ping heart beat handler send a ping task!");
				}
			}
		};
		return pingActor;
	}
	
	/**
	 * 创建检测心跳回复线程
	 * @return
	 */
	private Runnable buildTimeOutMonitor() {
		Runnable timeOutMonitor = new Runnable() {
			public void run() {
				if (isGoOn) {
					if (isResponsed) {
						//心跳正常
						isResponsed = false;
						return;
					} else {
						//心跳不正常
						LOG.info("local node haven't got a pong response!");
						//设置心跳不正常
						localNode.setBeatHeartError(true);
						//关闭ping,pong
						isGoOn = false;
					}
				}
			}
		};
		return timeOutMonitor;
	}
	
	private boolean isPongTask(Task task) {
		assert (task != null);
		JSONObject json = task.getParamJson();
		if (json.containsKey(ClusterPongTask.PONG_TASK_FLAG_ATTR)) {
			return true;
		} else {
			return false;
		}
	}
}
