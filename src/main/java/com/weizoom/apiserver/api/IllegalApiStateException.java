/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.cluster.constant.ResultCode;



/**
 * 
 * @author chuter
 *
 */
public class IllegalApiStateException extends ApiException {

	private static final long serialVersionUID = 6422394540739686340L;

	public IllegalApiStateException(String errorMsg) {
		super(ResultCode.SYSTEM_ERROR_NEED_RETRY.getCode(), errorMsg);
	}
	
	public IllegalApiStateException(Api api, String errorMsg) {
		super(ResultCode.SYSTEM_ERROR_NEED_RETRY.getCode(), api, errorMsg);
	}
	
	public IllegalApiStateException(Api api, Throwable cause) {
		super(ResultCode.SYSTEM_ERROR_NEED_RETRY.getCode(), api, cause);
	}

}
