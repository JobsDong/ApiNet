/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.exception;

/**
 * <code>NoNodeException</code>描述的是没有可用的节点<br />
 * @author wuyadong
 *
 */
public class NoNodeException extends Exception {
	private static final long serialVersionUID = 7363018856549100698L;
	
	NoNodeException() {
		super();
	}
	
	public NoNodeException(String error) {
		super(error);
	}
}
