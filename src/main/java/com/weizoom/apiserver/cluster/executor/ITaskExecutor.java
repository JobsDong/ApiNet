/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-8
 */
package com.weizoom.apiserver.cluster.executor;

import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.exception.TaskExecuteException;

/**
 * 
 * @author chuter
 *
 */
public interface ITaskExecutor {
	public TaskResult execute(Task task) throws TaskExecuteException;
}
