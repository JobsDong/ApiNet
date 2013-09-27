/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-17
 */

package com.weizoom.apiserver.cluster.constant;

/**
 * ���ڽ�{@link com.weizoom.apiserver.cluster.TaskResult}���л�ʱ������Ӧ��
 * field����
 * ��������field:
 * <li>TASK_FIELD:result_task������������Ӧ��task��field</li>
 * <li>CODE_FIELD:result_code������������Ӧ��code��field</li>
 * <li>JSON_FIELD:result_json������������Ӧ��json��field</li>
 * <li>MESSAGE_FIELD:result_message������������Ӧ��messge��field</li>
 * @author wuyadong
 *
 */
public enum ResultField {
	TASK_FIELD("result_task"),
	CODE_FIELD("result_code"),
	MESSAGE_FIELD("result_message"),
	JSON_FIELD("result_json");
	
	final private String content;
	private ResultField(String content) {
		assert(content != null);
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
}
