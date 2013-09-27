/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-17
 */

package com.weizoom.apiserver.cluster.constant;

/**
 * ��������{@link com.weizoom.apiserver.cluster.TaskResult}�ķ�����<br />
 * ��������:
 * <li>SUCCESS=200,��ʾִ�гɹ�</li>
 * <li>CLUSTER_ERROR,300-400,��ʾִ��ʧ�ܣ�ʧ�ܵ�ԭ����cluster����������</li>
 * <li>SYSTEM_ERROR,500-,��ʾִ��ʧ�ܣ�ʧ�ܵ�ԭ���Ǿ���Task����������</li>
 * 
 * <code>CLUSTER_ERROR</code>�ַ�Ϊ���£�<br />
 * <code>CLUSTER_ERROR_NEED_RETRY��Ҫ����</code>
 * <code>CLUSTER_ERROR_NO_NEED_RETRY����Ҫ����</code>
 * 
 * <code>SYSTEM_ERROR</code>�ַ�Ϊ����: <br />
 * <li>SYSTEM_ERROR_NEED_RETRY��Ҫ����</li>
 * <li>SYSTEM_ERROR_NOT_NEED_RETRY����Ҫ����</li>
 * @author wuyadong
 *
 */
public enum ResultCode {
	SUCCESS(200),
	SYSTEM_ERROR_NEED_RETRY(500),
	SYSTEM_ERROR_NOT_NEED_RETRY(501),
	CLUSTER_ERROR_NEED_RETRY(300),
	CLUSTER_ERROR_NOT_NEED_RETRY(301);
	
	final private int code;
	private ResultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static ResultCode toRESULT_CODE(int code, ResultCode defaultCode) {
		if (defaultCode == null) {
			throw new NullPointerException("defaultCode");
		}
		switch (code) {
		case 200:
			return SUCCESS;
		case 300:
			return CLUSTER_ERROR_NEED_RETRY;
		case 301:
			return CLUSTER_ERROR_NOT_NEED_RETRY;
		case 500:
			return SYSTEM_ERROR_NEED_RETRY;
		case 501:
			return SYSTEM_ERROR_NOT_NEED_RETRY;
		default:
			return defaultCode;
		}
	}
	
	public static ResultCode toRESULT_CODE(int code) {
		switch (code) {
		case 200:
			return SUCCESS;
		case 300:
			return CLUSTER_ERROR_NEED_RETRY;
		case 301:
			return CLUSTER_ERROR_NOT_NEED_RETRY;
		case 500:
			return SYSTEM_ERROR_NEED_RETRY;
		case 501:
			return SYSTEM_ERROR_NOT_NEED_RETRY;
		default:
			throw new IllegalArgumentException("code");
		}
	}
}
