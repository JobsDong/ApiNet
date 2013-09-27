/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-7
 */
package com.weizoom.apiserver.cluster.beatheart;

import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.Task;

/**
 * <code>ClusterPongTask</code>是用于描述心跳机制中的Pong包<br />
 * 其中只包含一个标志位
 * @author wuyadong
 *
 */
class ClusterPongTask extends Task {
	final static public String PONG_TASK_FLAG_ATTR = "is.pong";
	final static private JSONObject json;
	static {
		json = new JSONObject();
		json.put(PONG_TASK_FLAG_ATTR, true);
	}
	
	public ClusterPongTask() {
		this(Task.FAKE_TASK_ID, json, System.currentTimeMillis());
	}
	
	private ClusterPongTask(String id, JSONObject param, long startTime) {
		super(id, param, startTime);
	}
}
