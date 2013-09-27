/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-23
 */
package com.weizoom.apiserver.cluster.exception;

import com.weizoom.apiserver.cluster.TaskResult;

/**
 * ��û����Ȩ��channel����TaskBootstrap���ᱬ���������
 * @author wuyadong
 *
 */
public class NoAuthenticationException extends Exception {
	private static final long serialVersionUID = 1237172186749524573L;
	
	public NoAuthenticationException(TaskResult result) {
		super(String.format("taskId:%s, code:%s, message:%s, result:%s", result.getTaskId(), result.getCode(), result.getMessage(), result.getResult()));
	}
}
