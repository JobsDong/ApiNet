/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.operation;

import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.Task;

/**
 * {@link com.weizoom.apiserver.cluster.Cluster}����ά��������
 * 
 * @author chuter
 *
 */
class ClusterOperationTask extends Task {
	//Task������ָ���Ƿ�Ϊ��ά���������������Ϣ
	final static public String OPERATION_TASK_FLAG_ATTR = "is.operation";
	final static public String OPERATION_NAME_ATTR = "operation.name";
	final static public String OPERATE_TYPE = "operate.type";
	
	private String operationName;
	private OperateType operateType;
	
	public ClusterOperationTask(OperateType operateType, String id, String operationName, JSONObject param) {
		super(id, param, System.currentTimeMillis());
		
		if (null == operateType) {
			throw new NullPointerException("operate type");
		}
		
		if (OperateType.UNKOWN == operateType) {
			throw new IllegalArgumentException("The operate type is UNKOWN");
		}
		
		if (null == operationName) {
			throw new NullPointerException("operation name");
		}
		
		this.operateType   = operateType;
		this.operationName = operationName;
	}

	public String getOperationId() {
		return super.getId();
	}
	
	public String getOperationName() {
		return operationName;
	}
	
	public JSONObject getOperationParam() {
		return param;
	}
	
	public OperateType getOperateType() {
		return operateType;
	}
}
