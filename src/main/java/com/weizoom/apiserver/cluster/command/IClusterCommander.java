/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-28
 */
package com.weizoom.apiserver.cluster.command;

import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.TaskResult;

/**
 * 
 * @author wuyadong
 *
 */
public interface IClusterCommander {
	public String getCommandName();
	public TaskResult operateCommand(ClusterCommandTask task, ClusterState clusterState);
}
