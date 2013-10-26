/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.cluster.balance.Balancing;
import com.weizoom.apiserver.cluster.balance.IBalancingStrategy;
import com.weizoom.apiserver.cluster.common.NodeAddress;
import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.exception.NoNodeException;

/**
 * <code>MasterNode</code>����<code>Cluster</code>�е����ڵ�<br />
 * ��Ҫ�����<code>ProxyNode</code>��ѡ������
 * 
 * ע�⣺
 * Ĭ��MasterNode���ܴ�������<br />
 * ����û�п�ѡ��<code>ProxyNode</code>�׳��쳣
 * @author wuyadong
 *
 */
//TODO �������Ի��ƣ��������
public class MasterNode extends Node {
	final static private Logger LOG = Logger.getLogger(MasterNode.class);
	
	protected Balancing balancing = null;
	
	public MasterNode(ClusterState clusterState) {
		super(NodeAddress.FAKE_ADDRESS);
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.balancing = new Balancing(clusterState);
	}
	
	void setBalanceStrategy(IBalancingStrategy strategy) {
		if (strategy == null) {
			throw new NullPointerException("strategy");
		}
		this.balancing.setBanlanceStrategy(strategy);
	}
	
	@Override
	boolean isMaster() {
		return true;
	}
	
	@Override
	public TaskResultFuture execute(Task task) {
		if (task == null) {
			throw new NullPointerException("task");
		}
		LOG.info("master node executing a task!id: " + task.getId() + ", param: "+ task.getParamJson() + ", time: " + task.getStartTime());
		try {
			ProxyNode proxyNode = balancing.selectProxyNode(task);
			LOG.debug("master node select one proxyNode to execute this task.node: " + proxyNode);
			TaskResultFuture resultFuture = proxyNode.execute(task);
			return resultFuture;
		} catch (NoNodeException e) {
			LOG.error("no avaliable node to use!", e);
			return createNoNodeResult(task.getId(), e);
		}
	}
	
	private TaskResultFuture createNoNodeResult(String taskId, NoNodeException e) {
		assert (taskId != null);
		TaskResultFuture resultFuture = new TaskResultFuture(taskId, ResultCode.CLUSTER_ERROR_NOT_NEED_RETRY);
		resultFuture.setDone();
		return resultFuture;
	}
}
