/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-3-18
 */
package com.weizoom.apiserver.apis.buildin;

import net.sf.json.JSONObject;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.ApiCallMetadata;
import com.weizoom.apiserver.api.ApiException;
import com.weizoom.apiserver.api.ApiOperateAction;
import com.weizoom.apiserver.api.IllegalApiArugmentException;

public class HelloApi extends Api {

	@Override
	public void checkRequstParam(JSONObject paramJson,
			ApiOperateAction apiOperateAction)
			throws IllegalApiArugmentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doClose() throws ApiException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doStart() throws ApiException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata)
			throws ApiException {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		for (int i = 0; i < 100; i ++) {
			json.put("hello" + i, "world");
		}
		return json;
	}
}
