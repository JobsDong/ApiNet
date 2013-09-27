/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.exception;

/**
 * <code>NoNodeException</code>��������û�п��õĽڵ�<br />
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
