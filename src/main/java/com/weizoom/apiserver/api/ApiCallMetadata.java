package com.weizoom.apiserver.api;

import java.util.HashMap;
public class ApiCallMetadata extends HashMap<String, Object> {

	public static final String OPERATE_ACTION = "op";
	
	private static final long serialVersionUID = 6009176624961990648L;
	
	public ApiOperateAction getOperateAction() {
		return ApiOperateAction.parse(getString(OPERATE_ACTION));
	}
	
	private String getString(String key) {
		Object value = get(key);
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}
	
}
