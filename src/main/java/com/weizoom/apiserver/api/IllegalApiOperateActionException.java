package com.weizoom.apiserver.api;

import com.weizoom.apiserver.cluster.constant.ResultCode;


public class IllegalApiOperateActionException extends ApiException {

	public IllegalApiOperateActionException(Api api, ApiOperateAction operateAction) {
		super(ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY.getCode(), api, "��֧�ֲ���:"+operateAction.name());
	}
	
	private static final long serialVersionUID = 1780276410745370309L;

}
