/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-10-10
 */
package com.weizoom.apiserver.server;

import net.sf.json.JSONObject;

public class ApiResponse {
	private JSONObject apiRetJson = null;
	
	private static final String CODE_ATTR_NAME = com.weizoom.apiserver.api.ApiResponse.RESPONSE_CODE_FIELD;
	private static final String ERRORMSG_ATTR_NAME = com.weizoom.apiserver.api.ApiResponse.RESPONSE_ERRMSG_FIELD;
	private static final String INNERERRMSG_ATTR_NAME = com.weizoom.apiserver.api.ApiResponse.RESPONSE_ERRMSG_DETAIL_FIELD;
	private static final String API_PROCESS_RESULT_ATTR_NAME = com.weizoom.apiserver.api.ApiResponse.RESPONSE_DATA_FIELD;
	private static final String IS_API_PROCESS_SUCCEED_ATTR_NAME = com.weizoom.apiserver.api.ApiResponse.RESPONSE_ISSUCCESS_FIELD;
	
	public ApiResponse(JSONObject apiRetJson) {
		if (null == apiRetJson) {
			throw new IllegalArgumentException("The api return json can not be null.");
		}
		
		this.apiRetJson = apiRetJson;
		check();
	}
	
	ApiResponse(String retJsonStr) {
		if (null == retJsonStr) {
			throw new IllegalArgumentException("The api return json string can not be null.");
		}
		
		try {
			this.apiRetJson = JSONObject.fromObject(retJsonStr);
			check();
		} catch (Exception e) {
			com.weizoom.apiserver.api.ApiResponse response = new com.weizoom.apiserver.api.ApiResponse(com.weizoom.apiserver.api.ApiResponse.SystemError.getCode(), retJsonStr);
			response = response.setException(e);
			apiRetJson = response.toJsonObject();
		}
	}
	
	private void check() {
		if (
				(! apiRetJson.containsKey(CODE_ATTR_NAME)) 
				|| (! apiRetJson.containsKey(ERRORMSG_ATTR_NAME))
				|| (! apiRetJson.containsKey(INNERERRMSG_ATTR_NAME))
			) {
			throw new IllegalArgumentException("The api return json is invalid!");
		}
	}
	
	public int getCode() {
		return apiRetJson.getInt(CODE_ATTR_NAME);
	}
	
	public String getErrorMessage() {
		return apiRetJson.getString(ERRORMSG_ATTR_NAME);
	}
	
	public String getErrorMessageDetail() {
		return apiRetJson.getString(INNERERRMSG_ATTR_NAME);
	}
	
	public JSONObject getApiRrocessResultJson() {
		if (! apiRetJson.containsKey(API_PROCESS_RESULT_ATTR_NAME)) {
			return new JSONObject();
		}
		
		return apiRetJson.getJSONObject(API_PROCESS_RESULT_ATTR_NAME);
	}
	
	public boolean isApiProcessSucceed() {
		return apiRetJson.getBoolean(IS_API_PROCESS_SUCCEED_ATTR_NAME);
	}
	
	public JSONObject toJson() {
		return apiRetJson;
	}
	
	@Override
	public String toString() {
		return apiRetJson.toString();
	}
}
