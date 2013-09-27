/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.api;

import net.sf.json.JSONObject;

/**
 * 
 * @author chuter
 *
 */
public class FakeApi extends Api {

	private ApiOperateAction apiOperateAction = ApiOperateAction.GET;
	
	public synchronized void setSurpportOperateAction(ApiOperateAction apiOperateAction) {
		this.apiOperateAction = apiOperateAction;
	}
	
	@Override public String getApiName() {
		return "fake";
	}
	
	@Override
	public void doStart() throws ApiException {
		System.out.println("i am starting...");
	}

	@Override
	public void doClose() throws ApiException {
		System.out.println("i am closing...");
	}

	@Override
	public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
		//需要id参数
		if (! paramJson.has("id")) {
			throw new IllegalApiArugmentException("id can not be null");
		}
	}

	private boolean isToFailed = false;
	public void setToFailed(boolean isToFailed) {
		this.isToFailed = isToFailed;
	}
	
	private String failedMsg = "";
	public void setFailedMsg(String failedMsg) {
		this.failedMsg = failedMsg;
	}
	
	@Override
	synchronized public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
		if (apiOperateAction != apiCallMetadata.getOperateAction()) {
			throw new IllegalApiOperateActionException(this, apiCallMetadata.getOperateAction());
		}
		
		if (isToFailed) {
			throw new ApiException(failedMsg);
		}
		return paramJson;
	}
}