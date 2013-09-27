/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.beatheart;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;

/**
 * <code>ClusterPingTaskResult</code>�������������������е�ping��Ϣ<br />
 * ʵ���Ͼ�����result�м�����һ�����λ<br />
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
