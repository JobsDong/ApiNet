/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.executor;

import net.sf.json.JSONObject;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.exception.TaskExecuteException;

/**
 * 用于测试的一个执行器.
 * 将{@link com.weizoom.apiserver.cluster.Task}的参数返回去
 * @author chuter & wuyadong
 *
 */
public class DummyTaskExecutor implements ITaskExecutor {
	public TaskResult execute(Task task) throws TaskExecuteException {
		JSONObject result = task.getParamJson();
		TaskResult executeResult = new TaskResult(task.getId());
		executeResult.setResult(result);
		return executeResult;
	}
}
