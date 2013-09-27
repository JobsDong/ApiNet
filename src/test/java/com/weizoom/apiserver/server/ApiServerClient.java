/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-20
 */
package com.weizoom.apiserver.server;

import java.net.URL;
import java.util.Map;

import com.weizoom.apiserver.api.ApiOperateAction;
import com.weizoom.apiserver.server.LightHttpClient.Method;

public class ApiServerClient {
	private static final LightHttpClient HTTP_CLIENT = new LightHttpClient();
	
	static {
		HTTP_CLIENT.setToOnlyReadHeader(false);
	}
	
	private String serverHost;
	private int serverPort;
	
	public ApiServerClient(String host, int port) {
		serverHost = host;
		this.serverPort = port;
	}

	public String buildApiRequestUrl(String apiName) {
		return String.format("http://%s:%d/api/%s", serverHost, serverPort, apiName);
	}
	
	public String buildApiRequestUrl(String apiName, ApiOperateAction apiOperateAction) {
		return String.format("http://%s:%d/api/%s/%s", serverHost, serverPort, apiName, apiOperateAction.name().toLowerCase());
	}
	
	String buildApiSettingRequestUrl(String apiName) {
		return String.format("http://%s:%d/api/settings/?apiname=%s", serverHost, serverPort, apiName);
	}
	
	public ApiResponse processGetRequest(String url, Map<String, String> paramMap) throws Exception {
		HttpResponse response = HTTP_CLIENT.execute(new URL(url), paramMap, Method.GET);
		return new ApiResponse(response.getContentStr("utf-8"));
	}
	
	public ApiResponse processPostRequest(String url, Map<String, String> paramMap) throws Exception {
		HttpResponse response = HTTP_CLIENT.execute(new URL(url), paramMap, Method.POST);
		return new ApiResponse(response.getContentStr("utf-8"));
	}
}
