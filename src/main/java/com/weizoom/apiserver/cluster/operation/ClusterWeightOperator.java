/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-19
 */

package com.weizoom.apiserver.cluster.operation;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.ProxyNode;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.constant.ResultCode;

/***
 * 用于观察所有Node的权重
 * 输出的样式如下:<br />
 * <li>host</li>
 * <li>port</li>
 * <li>path</li>
 * <li>weight</li>
 * @author wuyadong
 *
 */
class ClusterWeightOperator implements IClusterOperator {
	final static private String OPERATION_NAME = "weight_status";
	
	public String getOperationName() {
		return OPERATION_NAME;
	}

	public TaskResult operate(ClusterOperationTask operationTask,
			ClusterState clusterState) {
		if (null == operationTask || null == clusterState) {
			throw new NullPointerException("operation task or clusterState");
		}
		
		if (operationTask.getOperateType() != OperateType.GET) {
			throw new IllegalArgumentException(getOperationName() + " 只支持获取操作");
		}

		TaskResult executeResult = new TaskResult(operationTask.getId(),ResultCode.SUCCESS, buildCluterWeightStatusInfo(clusterState));
		return executeResult;
	}

	private JSONObject buildCluterWeightStatusInfo(ClusterState clusterState) {
		JSONObject jsonObject = new JSONObject();
		
		assert (clusterState != null);
		JSONArray jsonArray = new JSONArray();
		
		List<ProxyNode> activeNodes = clusterState.getActiveNodes();
		for (ProxyNode proxyNode : activeNodes) {
			JSONObject json = new JSONObject();
			json.put("host", proxyNode.getAddress().getHost());
			json.put("port", proxyNode.getAddress().getPort());
			json.put("path", proxyNode.getAddress().getPath());
			json.put("weight", proxyNode.getNodeWeight().getWeight());
			jsonArray.add(json);
		}
		
		jsonObject.put("status", jsonArray);
		
		return jsonObject;
	}
}
