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
 * <code>PingHeartBeatHandler</code>��ר�����ڿͻ��˷�������<code>ping</code>��Handler<br />
 * ͬʱ��Ҳ�̶�ʱ�����Ƿ��н��յ�<code>pong</code>����Ϣ��
 * 
 * �������£�<br />
 * 1. ÿ��x�룬���ᷢ��һ��Ping
 * 2. ���pong�������n����û���յ�����ִ�в������ӵĴ���
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
	 * @param pingTime ��ping�ļ��ʱ��
	 * @param timeOut �ǵõ�pong����̼��ʱ��
	 * @param timeUnit ��ʱ��ĵ�λ
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
	 * ��channel�������ˣ��Ϳ��������̣߳��ͼ���������߳�
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		this.channel = e.getChannel();
		
		//����ping����
		scheduledExecutorService.scheduleWithFixedDelay(buildPingActor(), pingTime, pingTime, timeUnit);
		//����pongActor����
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
		
		//����Ƿ���PongTask
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
	 * ���������߳�
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
	 * ������������ظ��߳�
	 * @return
	 */
	private Runnable buildTimeOutMonitor() {
		Runnable timeOutMonitor = new Runnable() {
			public void run() {
				if (isGoOn) {
					if (isResponsed) {
						//��������
						isResponsed = false;
						return;
					} else {
						//����������
						LOG.info("local node haven't got a pong response!");
						//��������������
						localNode.setBeatHeartError(true);
						//�ر�ping,pong
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
