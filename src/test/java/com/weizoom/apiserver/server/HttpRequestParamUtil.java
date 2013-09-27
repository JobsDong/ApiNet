/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-1-12
 */
package com.weizoom.apiserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wintim.common.util.WintimConfigurer;

/**
 * 发送Http请求的参数处理方法集合
 * 
 * @author chuter
 *
 */
public class HttpRequestParamUtil {
	
	//基本的报文头，即一般的http请求都需要的报文头
	private static final String COMMON_HTTPREQUEST_HEADERS_STR = WintimConfigurer.get().get(
			"common.http.request.headers",
			"User-Agent:=Mozilla/5.0 (Windows NT 5.1; rv:9.0.1) Gecko/20100101 Firefox/9.0.1|" +
			"Accept:=text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8|" +
			"Accept-Language:=en-us,en;q=0.5|" +
			"Accept-Encoding:=gzip, deflate|" +
			"Accept-Charset:=ISO-8859-1,utf-8;q=0.7,*;q=0.7|" +
			"Connection:=keep-alive|" +
			"Content-Type:=application/x-www-form-urlencoded; charset=UTF-8|"
		);
	
	private static Map<String, String> COMMON_REQUEST_HEADER_MAP = null;
	
	private HttpRequestParamUtil() {	}
	
	public static Map<String, String> buildParamMap(String paramsConfLine) {
		Map<String, String> paramMap = new HashMap<String, String>();
		
		//参数配置行的格式为'name1:=value1|name2:=value2|...'
		String[] nameValuePairs = paramsConfLine.split("\\s*\\|\\s*");
		for (String nameValuePair : nameValuePairs) {
			nameValuePair = nameValuePair.trim();
			if (nameValuePair.length() <= ":=".length()) {
				continue;
			}
			
			if (nameValuePair.startsWith(":=")) {
				System.err.println("no key for header : " + nameValuePair);
				continue;
			}
			
			String[] nameAndValue = nameValuePair.split(":=");
			if (nameAndValue.length > 2) {
				System.err.println("Invalid header conf : " + nameValuePair);
				continue;
			}
			
			if (1 == nameAndValue.length) {//value为空
				paramMap.put(nameAndValue[0].trim(), "");
			} else {
				paramMap.put(nameAndValue[0].trim(), nameAndValue[1].trim());
			}
		}
		
		return paramMap;
	}
	
	public static final Map<String, String> getCommonHttpRequestHeaders() {
		Map<String, String> commonParamMap = new HashMap<String, String>();
		
		if (null == COMMON_REQUEST_HEADER_MAP) {
			COMMON_REQUEST_HEADER_MAP = buildParamMap(COMMON_HTTPREQUEST_HEADERS_STR);
		}
		
		for (Entry<String, String> entry : COMMON_REQUEST_HEADER_MAP.entrySet()) {
			commonParamMap.put(entry.getKey(), entry.getValue());
		}
		
		return commonParamMap;
	}
	
	public static final Map<String, String> addRequestHeaders(Map<String, String> extraRequestHeaders) {
		if (null == extraRequestHeaders || extraRequestHeaders.size() == 0) {
			return getCommonHttpRequestHeaders();
		}
		
		Map<String, String> newHeadersMap = new HashMap<String, String>();
		
		for (Entry<String, String> entry : getCommonHttpRequestHeaders().entrySet()) {
			newHeadersMap.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, String> entry : extraRequestHeaders.entrySet()) {
			newHeadersMap.put(entry.getKey(), entry.getValue());
		}
		
		return newHeadersMap;
	}
	
}
