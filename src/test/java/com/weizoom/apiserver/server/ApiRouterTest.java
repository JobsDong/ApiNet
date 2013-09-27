/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.server;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.ApiCallMetadata;
import com.weizoom.apiserver.api.ApiOperateAction;
import com.weizoom.apiserver.api.ApiException;
import com.weizoom.apiserver.api.IllegalApiArugmentException;

/**
 * API路由单元测试
 * @author chuter
 *
 */
public class ApiRouterTest {
	
	/**
	 * 测试对一个没有注册的API的路由
	 */
	@Test public void testNotExistApiRouting() {
		Assert.assertNull(ApiRouter.route("/not_exist_api/"));
	}
	
	/**
	 * 测试对非法路径的路由
	 */
	@Test public void testInvalidPathRouting() {
		Assert.assertNull(ApiRouter.route(null));
		Assert.assertNull(ApiRouter.route("invalid_path"));
		Assert.assertNull(ApiRouter.route("/"));
		Assert.assertNull(ApiRouter.route("//"));
	}
	
	/**
	 * 测试对一个已经注册的API的路由
	 */
	@Test public void testExistApiRouting() {
		ApiManager.get().register(new Api() {
			@Override
			public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
				return null;
			}
			
			@Override public String getApiName() {
				return "exist_api";
			}
			
			@Override
			public void doStart() throws ApiException {
			}
			
			@Override
			public void doClose() throws ApiException {
			}

			@Override
			public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
			}
		});
		
		Api api = ApiRouter.route("/exist_api/?key1=value1");
		Assert.assertNotNull(api);
		Assert.assertEquals("exist_api", api.getApiName());
		
		api = ApiRouter.route("/exist_api?key1=value1");
		Assert.assertNotNull(api);
		Assert.assertEquals("exist_api", api.getApiName());
		
		//注册Api名称为ExistApi, 访问路径为exist_api
		ApiManager.get().unRegister("exist_api");
		ApiManager.get().register(new ExistApi());
		api = ApiRouter.route("/exist_api/?key1=value1");
		Assert.assertNull(api);
	}
	
	class ExistApi extends Api {
		@Override
		public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
			return null;
		}
		
		@Override
		public void doStart() throws ApiException {
		}

		@Override
		public void doClose() throws ApiException {
		}

		@Override
		public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
		}
	}
	
}
