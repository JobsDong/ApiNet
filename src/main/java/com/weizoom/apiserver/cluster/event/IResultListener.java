/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-30
 */
package com.weizoom.apiserver.cluster.event;

import java.util.EventListener;

import com.weizoom.apiserver.cluster.TaskResultFuture;

/**
 * <code>IResultListener</code>是事件模型中的监听器<br />
 * @author wuyadong
 *
 */
public interface IResultListener extends EventListener {
	
	void operationComplete(TaskResultFuture future) throws Exception;
}
