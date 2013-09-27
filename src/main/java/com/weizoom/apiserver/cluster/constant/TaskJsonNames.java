/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-24
 */
package com.weizoom.apiserver.cluster.constant;

/**
 * 用于描述{@link com.weizoom.apiserver.cluster.Task#toJson()}中的json的字段<br />
 * 包括如下字段:<br />
 * <li>TASK_ID, id描述的是task的id号</li>
 * <li>TASK_ATTRIBUTES, attrs描述的是task的参数表</li>
 * <li>TASK_START_TIME, startTime描述的是task的开始时间</li>
 * <li>TASK_RETRY_COUNT, retryCount描述的是task被重试的次数</li>
 * 
 * @author wuyadong
 */
public enum TaskJsonNames {
	TASK_ID("id"),
	TASK_ATTRIBUTES("attrs"),
	TASK_START_TIME("startTime"),
	TASK_RETRY_COUNT("retryCount");
	
	private String content;
	
	private TaskJsonNames(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
}
