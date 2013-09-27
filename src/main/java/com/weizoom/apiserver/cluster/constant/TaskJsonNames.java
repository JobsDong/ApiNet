/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-24
 */
package com.weizoom.apiserver.cluster.constant;

/**
 * ��������{@link com.weizoom.apiserver.cluster.Task#toJson()}�е�json���ֶ�<br />
 * ���������ֶ�:<br />
 * <li>TASK_ID, id��������task��id��</li>
 * <li>TASK_ATTRIBUTES, attrs��������task�Ĳ�����</li>
 * <li>TASK_START_TIME, startTime��������task�Ŀ�ʼʱ��</li>
 * <li>TASK_RETRY_COUNT, retryCount��������task�����ԵĴ���</li>
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
