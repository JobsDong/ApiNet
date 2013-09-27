/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-31
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.cluster.constant.ResultCode;

/**
 * 
 * @author chuter
 *
 */
public class IllegalApiArugmentException extends ApiException {

	
	private static final long serialVersionUID = -3757979932193580578L;

	public IllegalApiArugmentException(String errorMsg) {
		super(ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY.getCode(), errorMsg);
	}
	
}
