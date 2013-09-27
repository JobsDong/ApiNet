/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster;

import net.sf.json.JSONObject;

import org.jboss.netty.channel.Channel;

import com.weizoom.apiserver.cluster.common.NodeAddress;
import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.exception.ConnectException;

/**
 * <code>ProxyNode</code>作为<code>LocalNode</code>在<code>Cluster</code>端的虚拟连接器<br />
 * 对LocalNode的所有操作，都会通过ProxyNode来执行，同时ProxyNode保存所有与LocalNode有关的信息。
 * <code>ProxyNode</code>是LocalNode的镜像.
 * notice:<br />
 * 但是，实际上LocalNode与ProxyNode的连接都只是和cluster的端口连接
 * 
 * @author wuyadong
 *
 */
public class ProxyNode extends Node {
	private Channel channel;
	final private TaskResultFutureTable table;
	private NodeWeight nodeWeight;
	private NodeGroup group;
	
	public ProxyNode(NodeAddress nodeAddress) {
		super(nodeAddress); 
		table = new TaskResultFutureTable();
		nodeWeight = new NodeWeight();
	}
	
	/**
	 * 设置所有的task为已完成状态，并设置结果<br />
	 * 这个不影响Weight
	 */
	public void setDoneToAllTask(ResultCode resultCode, JSONObject result, String message) {
		if (resultCode == null) {
			throw new NullPointerException("result code");
		}
		if (result == null) {
			throw new NullPointerException("result");
		}
		if (message == null) {
			throw new NullPointerException("message");
		}
		
		table.clearAllTasks(resultCode, result, message);
	}
	
	/**
	 * 设置taskId对应的task，已完成，并设置result
	 * 如果这个task不存在，就不做任何处理
	 * @param taskId
	 * @param result
	 */
	public void setDone(String taskId, TaskResult result) {
		if (taskId == null) {
			throw new NullPointerException("task ID");
		}
		if (result == null) {
			throw new NullPointerException("result");
		}
		updateNodeWeight(result);
		this.table.setDone(taskId, result);
	}
	
	@Override
	public TaskResultFuture execute(Task task) throws ConnectException {
		if (task == null) {
			throw new NullPointerException("task");
		}
		assert (channel != null);
		//更新NodeWeight
		nodeWeight.increaseInProgress();
		
		channel.write(task);
		TaskResultFuture resultFuture = new TaskResultFuture(task.getId());
		table.putTaskResultFuture(task, resultFuture);
		return resultFuture;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void setGroup(NodeGroup group) {
		if (group == null) {
			throw new NullPointerException("group");
		}
		this.group = group;
	}
	
	public NodeGroup getGroup() {
		return this.group;
	}
	
	public NodeWeight getNodeWeight() {
		return nodeWeight;
	}
	
	public void resetNodeWeight() {
		this.nodeWeight = new NodeWeight();
	}
	
	/**
	 * 根据Result的结果，更新ProxyNode的权重信息,如果这个Task,不存在，就不做任何事
	 * @param result
	 */
	private void updateNodeWeight(TaskResult result) {
		assert (result != null);
		String taskId = result.getTaskId();
		
		switch (result.getCode()) {
		case SUCCESS:
			Task task = table.getTask(taskId);
			nodeWeight.increaseSuccess(System.currentTimeMillis() - task.getStartTime());
			break;
		case SYSTEM_ERROR_NEED_RETRY:
		case SYSTEM_ERROR_NOT_NEED_RETRY:
			nodeWeight.increaseSystemError();
			break;
		case CLUSTER_ERROR_NEED_RETRY:
		case CLUSTER_ERROR_NOT_NEED_RETRY:
			nodeWeight.increaseClusterError();
			break;
		default:
			break;
		}
	}
}
