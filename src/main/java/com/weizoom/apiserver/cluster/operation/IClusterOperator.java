/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.operation;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.TaskResult;

/**
 * 
 * @author chuter & wuyadong
 *
 */
public interface IClusterOperator {
	public String getOperationName();
	public TaskResult operate(ClusterOperationTask operationTask, ClusterState clusterState);
}
