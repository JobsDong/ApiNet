/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-16
 */

package com.weizoom.apiserver.cluster.exception;

/**
 * 关于连接的<i>exception</i>
 * @author wuyadong
 *
 */
public class ConnectException extends ClusterException {
	private static final long serialVersionUID = 5982046261051824145L;
	public ConnectException(String message) {
		super(message);
	}
	
	public ConnectException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
