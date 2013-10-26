package com.weizoom.apiserver.server;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.util.Strings;

/**
 * 
 * @author chuter
 *
 */
public class ApiRouter {
	
	private static final Logger LOG = Logger.getLogger(ApiRouter.class);
	
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
	 */
	public static final String extractApiName(String path) {
		if (! path.startsWith("/")) {
			return null;
		}
		
		int apiNameStartPos = "/".length();
		int apiNameEndPos = path.indexOf("/", apiNameStartPos);
		if (apiNameEndPos == -1) {
			apiNameEndPos = path.indexOf("?", apiNameStartPos);
			if (apiNameEndPos == -1) { 		
				apiNameEndPos = path.length();
			}
			} 
		
		return path.substring(apiNameStartPos, apiNameEndPos);
	}
	
}
