/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster.exception;

public class ClusterException extends RuntimeException {
	private static final long serialVersionUID = 2426125903479606841L;

	public ClusterException() {
		super();
	}
	
	protected ClusterException(String error) {
		super(error);
	}
	
	ClusterException(String error, Throwable throwable) {
		super(error, throwable);
	}
}

