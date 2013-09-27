/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.server;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.util.Strings;
import com.wintim.common.util.LogFactory;

/**
 * API·��, ��������·�����Զ�·�ɵ���Ӧ��Api<br>
 * ����API·���淶Ϊ"/api/${api_name}/../?param1=value1&param2=value2..."
 * 
 * @author chuter
 *
 */
public class ApiRouter {
	
	private static final Logger LOG = LogFactory.getLogger(ApiRouter.class);
	
	private ApiRouter() {	}
	
	public static Api route(String path) {
		if (null == path) {
			LOG.warn("Can not route the path null");
			return null;
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Routing api by path " + path);
		}
		
		String apiName = extractApiName(path);
		if (null == apiName) {
			LOG.warn(String.format("Can not route the path '%s', because the api name extracted is null", path));
			return null;
		}
		Api api = ApiManager.get().get(apiName);
		if (null == api) {
			api = ApiManager.get().get(Strings.toUnderscoreCase(apiName));
		}
		return api;
	}
	
	/**
	 * ������·���н����������ʵ�<i>Api</i>����
	 * @param path
	 */
	public static final String extractApiName(String path) {
		if (! path.startsWith("/")) {
			return null;
		}
		
		int apiNameStartPos = "/".length();
		int apiNameEndPos = path.indexOf("/", apiNameStartPos);
		if (apiNameEndPos == -1) {
			apiNameEndPos = path.indexOf("?", apiNameStartPos);
			if (apiNameEndPos == -1) { //����"/api/apiname"������·��
				apiNameEndPos = path.length();
			} //��������"/api/apiname?key=value"������·��
		} 
		
		return path.substring(apiNameStartPos, apiNameEndPos);
	}
	
}
