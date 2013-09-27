/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.server;

import junit.framework.Assert;

import org.junit.Test;

import com.weizoom.apiserver.api.DummyApi;
import com.weizoom.apiserver.api.DuplicateApiKeyException;

/**
 * 
 * @author chuter
 *
 */
public class ApiManagerTest {
	@Test public void testApiget() {
		//测试在还没有注册情况下获取Api
		Assert.assertNull(ApiManager.get().get("not_exitst"));
		
		//注册Api
		ApiManager.get().register(new DummyApi(), true);
		Assert.assertTrue(ApiManager.get().get("dummy").getClass() == DummyApi.class);
		
		//重复注册Api(不允许覆盖已有Api)
		try {
			ApiManager.get().register(new DummyApi(), false);
			Assert.assertTrue(false);
		} catch (DuplicateApiKeyException e) {
			Assert.assertTrue(true);
		}
		
		//重复注册Api(允许覆盖已有Api)
		ApiManager.get().register(new DummyApi(), true);
		
		//取消注册
		ApiManager.get().unRegister("dummy");
		Assert.assertNull(ApiManager.get().get("not_exitst"));
		
		//再次取消
		ApiManager.get().unRegister(new DummyApi());
		
	}
}
