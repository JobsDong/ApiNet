/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-30
 */
package com.weizoom.apiserver.cluster.exception;

/**
 * <code>NoExecutorException</code>当，<code>masternode</code>
 * 没有找到合适的ProxyNode去做的时候，同时<code>MasterNode</code>又没有设定
 * <code>ITaskExecutor</code>抛出的错误。
 * 
 * @author wuyadong
 */
public class NoExecutorException extends ClusterException {
	private static final long serialVersionUID = 4614280675689057874L;
	
	public NoExecutorException(String message) {
		super(message);
	}
}
