/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.local;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <code>LocalDaemonThread</code>是用于<code>LocalNode</code>的守护进程<br />
 * 主要功能:<br />
 * 用于守护LocalNode，当LocalNode的channel被关闭的时候，重新进行注册，启动等功能<br />
 * @author wuyadong
 *
 */
public class LocalDaemonThread extends Thread {
	final static private int DEFAULT_CORE_SIZE = 1;
	final static private long DEFAULT_DELAY_MS = 5000;
	
	final private LocalNode localNode;
	final private ScheduledExecutorService scheduledExecutorService;
	
	
	public LocalDaemonThread(LocalNode localNode) {
		if (localNode == null) {
			throw new NullPointerException("localNode");
		}
		this.localNode = localNode;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(DEFAULT_CORE_SIZE);
	}
	
	@Override
	public void run() {
		if (localNode != null && localNode.isStarted()) {
			scheduledExecutorService.scheduleWithFixedDelay(buildRunnable(), DEFAULT_DELAY_MS, DEFAULT_DELAY_MS, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 关闭LocalNode的守护进程
	 */
	public void shutDown() {
		if (scheduledExecutorService != null && ! scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService.shutdownNow();
		}
	}
	/**
	 * 负责检查localNode的状态，重启
	 * @return
	 */
	private Runnable buildRunnable() {
		Runnable runnable = new Runnable() {
			
			public void run() {
				if (localNode != null && localNode.isStarted() && localNode.isBeatHeartError()) {
					localNode.stop();
					localNode.start();
				}
			}
		};
		
		return runnable;
	}
}
