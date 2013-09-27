/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-30
 */
package com.weizoom.apiserver.cluster.event;

import java.util.EventListener;

import com.weizoom.apiserver.cluster.TaskResultFuture;

/**
 * <code>IResultListener</code>���¼�ģ���еļ�����<br />
 * @author wuyadong
 *
 */
public interface IResultListener extends EventListener {
	
	void operationComplete(TaskResultFuture future) throws Exception;
}
