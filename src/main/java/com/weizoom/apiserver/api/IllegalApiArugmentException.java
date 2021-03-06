package com.weizoom.apiserver.api;

import com.weizoom.apiserver.cluster.constant.ResultCode;

public class IllegalApiArugmentException extends ApiException {

	
	private static final long serialVersionUID = -3757979932193580578L;

	public IllegalApiArugmentException(String errorMsg) {
		super(ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY.getCode(), errorMsg);
	}
	
}
