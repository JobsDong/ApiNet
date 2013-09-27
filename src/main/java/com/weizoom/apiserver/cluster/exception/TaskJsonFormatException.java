/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-10
 */
package com.weizoom.apiserver.cluster.exception;

import com.weizoom.apiserver.cluster.Task;

import net.sf.json.JSONObject;

/**
 * 用于{@link com.weizoom.apiserver.cluster.TaskResult#fromJson(JSONObject)}中检查参数的情况所抛出的异常
 * 
 * @author chuter
 *
 */
public class TaskJsonFormatException extends RuntimeException {
	private static final long serialVersionUID = -1315692550522750026L;
	
	TaskJsonFormatException(Task task, String errorMesssage) {
		super(String.format("Failed to format task(%s) to json(%s)", task, errorMesssage));
	}
	
	public TaskJsonFormatException(JSONObject taskJson, String errorMesssage) {
		super(String.format("Failed to build task from json format(%s)(%s)", taskJson, errorMesssage));
	}
}
