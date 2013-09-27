/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-13
 */
package com.weizoom.apiserver.cluster.constant;

/**
 * <code>TaskParamNames</code>�Ƕ�����{@link com.weizoom.apiserver.cluster.Task}�еĲ���
 * ������<br />
 * �������£�<br />
 * <li>task_uri��ʾ������������uri</li>
 * <li>task_method��ʾ������������method</li>
 * <li>task_remote��ʾ�������ķ����ߵĵ�ַ</li>
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
