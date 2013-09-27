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
 * ���ڲ��Ե�һ��ִ����.
 * ��{@link com.weizoom.apiserver.cluster.Task}�Ĳ�������ȥ
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
