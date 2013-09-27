/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-13
 */
package com.weizoom.apiserver.cluster.constant;

/**
 * <code>TaskParamNames</code>是定义了{@link com.weizoom.apiserver.cluster.Task}中的参数
 * 的名字<br />
 * 包括如下：<br />
 * <li>task_uri表示这个任务的请求uri</li>
 * <li>task_method表示这个任务的请求method</li>
 * <li>task_remote表示这个任务的发起者的地址</li>
 * @author wuyadong
 *
 */
public enum TaskParamNames {
	TASK_PARAM_URI("task_param_uri"),
	TASK_PARAM_METHOD("task_param_method"),
	TASK_PARAM_REMOTE("task_param_remote");
	
	final private String content;
	
	private TaskParamNames(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
}
