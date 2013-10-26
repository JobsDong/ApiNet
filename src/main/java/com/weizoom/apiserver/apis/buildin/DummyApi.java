package com.weizoom.apiserver.apis.buildin;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.ApiCallMetadata;
import com.weizoom.apiserver.api.ApiException;
import com.weizoom.apiserver.api.ApiOperateAction;
import com.weizoom.apiserver.api.IllegalApiArugmentException;

import net.sf.json.JSONObject;

public class DummyApi extends Api {

	@Override
	public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
		return paramJson;
	}

	@Override
	protected void doStart() throws ApiException {
		System.out.println("I am starting...");
	}

	@Override
	protected void doClose() throws ApiException {
		System.out.println("I am closing...");
	}

	@Override
	public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
	}

}
