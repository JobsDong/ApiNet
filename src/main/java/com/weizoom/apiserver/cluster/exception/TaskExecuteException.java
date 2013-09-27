/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-8
 */
package com.weizoom.apiserver.cluster.exception;

import com.weizoom.apiserver.cluster.Task;

public class TaskExecuteException extends ClusterException {
	private static final long serialVersionUID = -292417687623507111L;

	public TaskExecuteException(Task task, Throwable cause) {
		super("Failed to exeute task: " + task, cause);
	}
	
	public TaskExecuteException(Task task, String errorMessage) {
		super(String.format("Failed to execute the task('%s') cause to %s", 
				task, errorMessage));
	}
}
