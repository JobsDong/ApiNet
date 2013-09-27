/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.operation;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.Node;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.constant.ResultCode;

/**
 * {@link com.weizoom.apiserver.cluster.Cluster}状态概况的运维实现<br>
 * 该实现中只支持获取{@link com.weizoom.apiserver.cluster.Cluster}的概况信息<br>
 * 包括如下信息:<br>
 * <ul>
 * <li>截止当前<i>Cluster</i>中总共处理的<i>Task</i>个数</li>
 * <li>所有有效<i>Node</i>信息</li>
 * <li>所有已失效<i>Node</i>信息</li>
 * </ul>
 * 
 * <b>其中每个<i>Node</i>信息中只包含了<i>Node</i>的地址信息</b>
 * 
 * @author chuter
 *
 */
class ClusterSimpleStatusOperator implements IClusterOperator {
	final static private String OPERATION_NAME = "simple_status";
	
	public String getOperationName() {
		return OPERATION_NAME;
	}

	public TaskResult operate(ClusterOperationTask operationTask,
			ClusterState clusterState) {
		if (null == operationTask || null == clusterState) {
			throw new NullPointerException("operation task or cluster");
		}
		
		if (operationTask.getOperateType() != OperateType.GET) {
			throw new IllegalArgumentException(getOperationName() + " 只支持获取操作");
		}
		
		TaskResult executeResult = new TaskResult(operationTask.getId(),ResultCode.SUCCESS, buildCluterSimpleStatusInfo(clusterState));
		return executeResult;
	}

	private JSONObject buildCluterSimpleStatusInfo(ClusterState clusterState) {
		assert (clusterState != null);
		JSONObject statusInfoJson = new JSONObject();
		statusInfoJson.put("executing_tasks_count", clusterState.getExecutingTasksCount());
		statusInfoJson.put("isactive_nodes", buildNodesInfo(clusterState));
		
		return statusInfoJson;
	}
	
	private JSONArray buildNodesInfo(ClusterState clusterState) {
		assert (clusterState != null);
		JSONArray nodesInfoArray = new JSONArray();
		
		List<ProxyNode> activeNodes = clusterState.getActiveNodes();
		List<ProxyNode> inactiveNodes = clusterState.getInactiveNodes();
		if (activeNodes != null) {
			for (Node node : activeNodes) {
				nodesInfoArray.add(buildNodeInfo(node, true));
			}
		}
		
		if (inactiveNodes != null) {
			for (Node node : inactiveNodes) {
				nodesInfoArray.add(buildNodeInfo(node, false));
			}
		}
		
		return nodesInfoArray;
	}
	
	private JSONObject buildNodeInfo(Node node, boolean isActive) {
		assert (node != null);
		
		JSONObject nodeInfo = new JSONObject();
		nodeInfo.put("host", node.getAddress().getHost());
		nodeInfo.put("port", node.getAddress().getPort());
		nodeInfo.put("path", node.getAddress().getPath());
		nodeInfo.put("is_active", isActive ? 1 : 0);
		return nodeInfo;
	}
}
