/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.settings.Settings;

import net.sf.json.JSONObject;

/**
 * 
 * @author chuter
 *
 */
public class DummyApi extends Api {

	private boolean isStartFailed = false;
	private boolean isCloseFailed = false;

	public void setStartFailed(boolean isStartFailed) {
		this.isStartFailed = isStartFailed;
	}
	
	public void setCloseFailed(boolean isCloseFailed) {
		this.isCloseFailed = isCloseFailed;
	}
	
	@Override
	public void doStart() throws ApiException {
		if (isStartFailed) {
			throw new ApiException("Been told to be failed.");
		}
	}

	@Override
	public void doClose() throws ApiException {
		if (isCloseFailed) {
			throw new ApiException("Been told to be failed.");
		}
	}

	@Override
	public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
		
	}

	@Override
	public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
		return null;
	}

	@Override
	public Settings getSettings() {
		if (null == settings) {
			settings = Settings.EMPTY_SETTINGS;
		}
		
		return settings;
	}

}
