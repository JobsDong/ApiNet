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
		//�����ڻ�û��ע������»�ȡApi
		Assert.assertNull(ApiManager.get().get("not_exitst"));
		
		//ע��Api
		ApiManager.get().register(new DummyApi(), true);
		Assert.assertTrue(ApiManager.get().get("dummy").getClass() == DummyApi.class);
		
		//�ظ�ע��Api(������������Api)
		try {
			ApiManager.get().register(new DummyApi(), false);
			Assert.assertTrue(false);
		} catch (DuplicateApiKeyException e) {
			Assert.assertTrue(true);
		}
		
		//�ظ�ע��Api(����������Api)
		ApiManager.get().register(new DummyApi(), true);
		
		//ȡ��ע��
		ApiManager.get().unRegister("dummy");
		Assert.assertNull(ApiManager.get().get("not_exitst"));
		
		//�ٴ�ȡ��
		ApiManager.get().unRegister(new DummyApi());
		
	}
}
