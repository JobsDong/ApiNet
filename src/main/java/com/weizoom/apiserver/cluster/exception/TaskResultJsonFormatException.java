/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-12
 */

package com.weizoom.apiserver.cluster.exception;

import com.weizoom.apiserver.cluster.Task;

import net.sf.json.JSONObject;

public class TaskResultJsonFormatException extends RuntimeException {
	private static final long serialVersionUID = -7901194508663292013L;

	TaskResultJsonFormatException(Task task, String errorMesssage) {
		super(String.format("Failed to format task(%s) to json(%s)", task, errorMesssage));
	}
	
	public TaskResultJsonFormatException(JSONObject taskJson, String errorMesssage) {
		super(String.format("Failed to build task from json format(%s)(%s)", taskJson, errorMesssage));
	}
}
