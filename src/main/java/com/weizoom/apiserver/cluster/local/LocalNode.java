/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster.local;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.weizoom.apiserver.cluster.ClusterSettings;
import com.weizoom.apiserver.cluster.Node;
import com.weizoom.apiserver.cluster.NodeGroup;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.exception.RegisterException;
import com.weizoom.apiserver.cluster.executor.ITaskExecutor;
import com.wintim.common.util.LogFactory;

/**
 * <code>LocalNode</code>��һ���ڵ����������Ҫ�����߼��ϵĴ�������<br />
 * <code>LocalNode</code>�������������
 * <li>LocalNodeBoostrap���ڽ����������󣬺ͷ��ؽ���Ĺ������</li>
 * ע��LocalNode���������ԣ����Ǵ�NodeSetting�л�ȡ�ģ�Ĭ�ϲ��ܴ��Ͳ���
 * ����Node,���¾��У�
 * <pre>
 * LocalNode localNode = new LocalNode(taskExecutor);
 * LocalDaemonThread localDaemonThread = new LocalDaemonThread(localNode);
 * localNode.start();
 * localDaemonThread.start();
 * ..
 * ...
 * </pre>
 * ע�����ȼٶ�ֻʵ�ּ�Ⱥģʽ/���ṩ���ڵ�ģʽ,localNodeҪ�޽�һ���ػ�����
 * ���û���ػ��̣߳�Ҳ�����������У�����LocalNode���ʹ���ʱ�������Զ�����
 * @author wuyadong
 */
//TODO ������executionHandler�ǿ���������ģ��������LocalNode������Ҳ���������
public class LocalNode extends Node {
	final static private Logger LOG = LogFactory.getLogger(LocalNode.class);
	final static private int DEFAULT_BEATHEART_CORE_SIZE = 2;
	
	final private ITaskExecutor taskExecutor;//����ִ����
	private LocalNodeBootstrap localNodeBootstrap;//������
	private ExecutionHandler executionHandler;//����ִ�е��̳߳�
	private ScheduledExecutorService scheduledExecutorService;//�����̳߳�
	
	private NodeGroup group = NodeGroup.GROUP_GENERAL;//node��������
	private boolean isStarted = false;//node�Ƿ���
	private volatile boolean beatHeartError = false;//node�������Ƿ������
	
	public LocalNode(ITaskExecutor taskExecutor) {
		super(NodeSettings.getLocalNodeAddress());
		if (null == taskExecutor) {
			throw new NullPointerException("task executor");
		}
		this.taskExecutor = taskExecutor;
	}

	@Override
	public TaskResult execute(Task task) {
		return taskExecutor.execute(task);
	}
	
	/**
	 * ����LocalNode�Ĺ���,��Ҫ��Ϊ�⼸��:<br />
	 * <li>��ʼ��</li>
	 * <li>ע��</li>
	 * <li>����</li>
	 */
	public void start() {
		if (!isStarted) {
			//��ʼ��
			LOG.info("local node is starting...");
			this.scheduledExecutorService = Executors.newScheduledThreadPool(DEFAULT_BEATHEART_CORE_SIZE);
			this.executionHandler = new ExecutionHandler(Executors.newCachedThreadPool());
			localNodeBootstrap = new LocalNodeBootstrap(executionHandler, scheduledExecutorService, this, taskExecutor);
			localNodeBootstrap.setNodeAddress(nodeAddress);
			
			//TODO �������������
			InetSocketAddress proxyNodeAddress = null;
			//ע��
			if (null == (proxyNodeAddress = registerToCluster())) {
				throw new RegisterException(ClusterSettings.getClusterRegisterAddress());
			}
			//����
			connectToProxy(proxyNodeAddress);

			LOG.info("local node started!");
			isStarted = true;
		} 
	}
	
	/**
	 * stop����Խ���start
	 */
	public void stop() {
		if (isStarted) {
			LOG.info("local node is stopping...");
			localNodeBootstrap.stop();
			if (! scheduledExecutorService.isShutdown()) {
				this.scheduledExecutorService.shutdown();
				this.scheduledExecutorService.shutdownNow();
			}
			this.executionHandler.releaseExternalResources();
			LOG.info("local node stopped!");
			isStarted = false;
		}
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	boolean isBeatHeartError() {
		return beatHeartError;
	}
	
	public void setBeatHeartError(boolean isError) {
		this.beatHeartError = isError;
	}
	
	void setGroup(NodeGroup group) {
		this.group = group;
	}
	
	private InetSocketAddress registerToCluster() {
		return RegisterChannel.registerToCluster(nodeAddress, group);
	}
	
	private void connectToProxy(InetSocketAddress address) {
		assert (null != address);
		localNodeBootstrap.setProxyAddress(address);
		localNodeBootstrap.start();
	}
}
