/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import com.weizoom.apiserver.cluster.balance.IBalancingStrategy;

/**
 * <code>cluster</code>�Ƕ�������Ⱥ�ĳ���������<br />
 * <code>cluster</code>��������Ԫ�أ�<br />
 * <li>{@link com.weizoom.apiserver.cluster.BootstrapForUserRequest}�������û��Ľӿڣ�ֻ�����û�������</li>
 * <li>{@link com.weizoom.apiserver.cluster.BootstrapForDataExchange}������ڵ��master��ͨ�Žӿڣ�ֻ���ڽڵ�֮������ݽ���</li>
 * <li>{@link com.weizoom.apiserver.cluster.BootstrapForNodeRequest}������ڵ�Ľӿڣ�ֻ����ڵ�ע�������</li>
 * <li>{@link com.weizoom.apiserver.cluster.MasterNode}���߼��ϵ����ڵ�</li>
 * <li>{@link com.weizoom.apiserver.cluster.ClusterState}��������Ⱥ��״̬�Ľṹ</li>
 * <pre>
 * Cluster cluster = new Cluster();
 * cluster.start();
 * .....
 * cluster.stop();
 * </pre>
 * <code>Cluster</code>���������ã����Ǵ�ClusterSetting�ж�ȡ�ģ��������м䴫�ݱ����������������Ҫʹ��ʱ������<br />-->final Լ��
 * @author chuter & wuyadong
 */
public class Cluster {
	final static private Logger LOG = Logger.getLogger(Cluster.class);
	
	private boolean isStarted = false;
	private BootstrapForUserRequest bootstrapForUserRequest;
	private BootstrapForNodeRequest bootstrapForNodeRequest;
	private BootstrapForDataExchange bootstrapForDataExchange;
	private ExecutionHandler executionHandler;
	
	final protected ClusterState clusterState;
	final protected MasterNode masterNode;
	
	public Cluster() {
		clusterState = ClusterState.getInstance();
		masterNode = new MasterNode(clusterState);
	}
	
	public void start() {
		if (! isStarted) {
			LOG.info("cluster is starting...");
			executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(ClusterSettings.getClusterTaskExecutorsSize(),
					ClusterSettings.getClusterChannelMaxMemoryByteSize(), ClusterSettings.getClusterTotalMemoryByteSize()));
			bootstrapForNodeRequest = new BootstrapForNodeRequest(clusterState);
			bootstrapForUserRequest = new BootstrapForUserRequest(executionHandler, clusterState, masterNode);
			bootstrapForDataExchange = new BootstrapForDataExchange(clusterState, executionHandler);
			
			bootstrapForDataExchange.start();
			bootstrapForNodeRequest.start();
			bootstrapForUserRequest.start();
			LOG.info("cluster is started!");
			isStarted = true;
		}
	}
	
	/**
	 * stop֮�󣬿���start
	 */
	public void stop() {
		if (isStarted) {
			LOG.info("cluster is stopping...");
			bootstrapForDataExchange.stop();
			bootstrapForNodeRequest.stop();
			bootstrapForUserRequest.stop();
			executionHandler.releaseExternalResources();
			LOG.info("cluster is stopped!");
			isStarted = false;
		}
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	/**
	 * ����masterNodeѡ��Ĳ���
	 * @param strategy
	 */
	void setBalanceStrategy(IBalancingStrategy strategy) {
		if (strategy == null) {
			throw new NullPointerException("strategy");
		}
		masterNode.setBalanceStrategy(strategy);
	}
}
