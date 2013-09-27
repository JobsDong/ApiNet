/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-19
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.cluster.constant.ResultCode;


/**
 * 
 * @author chuter
 *
 */
public class IllegalApiOperateActionException extends ApiException {

	public IllegalApiOperateActionException(Api api, ApiOperateAction operateAction) {
		super(ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY.getCode(), api, "不支持操作:"+operateAction.name());
	}
	
	private static final long serialVersionUID = 1780276410745370309L;

}
