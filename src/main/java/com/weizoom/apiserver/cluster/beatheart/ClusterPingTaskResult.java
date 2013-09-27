/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.beatheart;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;

/**
 * <code>ClusterPingTaskResult</code>是用于描述心跳机制中的ping信息<br />
 * 实际上就是在result中加入了一个标记位<br />
 * 
 * @author wuyadong
 *
 */
class ClusterPingTaskResult extends TaskResult {
	final static public String PING_TASK_RESULT_FLAG_ATTRI = "is.ping";
	
	ClusterPingTaskResult() {
		super(Task.FAKE_TASK_ID);
		result.put(PING_TASK_RESULT_FLAG_ATTRI, true);
	}
}
