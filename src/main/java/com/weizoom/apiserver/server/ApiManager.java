package com.weizoom.apiserver.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.DuplicateApiKeyException;

/**
 * 
 * @author chuter
 *
 */
public class ApiManager {

	private static final Logger LOG = Logger.getLogger(ApiManager.class);
	
	private static final Map<String, Api> name2api = new HashMap<String, Api>();
	
	private static final ApiManager SINGLETON = new ApiManager();
	
	public static ApiManager get() {
		return SINGLETON;
	}
	
	private ApiManager() {	}
	
	boolean contains(String apiName) {
		synchronized (name2api) {
			return name2api.containsKey(apiName);
		}
	}
	
	public Api get(String apiName) {
		synchronized (name2api) {
			return name2api.get(apiName);
		}
	}
	
	public void register(Api api) {
		register(api, false);
	}
	
	public void register(Api api, boolean isOverWrite) {
		if (null == api) {
			throw new IllegalArgumentException("The api can not be null");
		}
		
		if (! isOverWrite) {
			synchronized (name2api) {
				if (name2api.containsKey(api.getApiName())) {
					throw new DuplicateApiKeyException(api.getApiName());
				}
			}
		}
		
		LOG.info(String.format("Register api:'%s' as '%s'", api.getClass().getName(), api.getApiName()));
		
		synchronized (name2api) {
			name2api.put(api.getApiName(), api);
		}
	}
	
	void unRegister(Api api) {
		if (null == api) {
			return;
		}
		
		synchronized (name2api) {
			if (name2api.containsKey(api.getApiName())) {
				name2api.remove(api.getApiName());
			}
		}
	}
	
	void unRegister(String apiName) {
		if (null == apiName) {
			return;
		}
		
		synchronized (name2api) {
			if (name2api.containsKey(apiName)) {
				name2api.remove(apiName);
			}
		}
	}
}
