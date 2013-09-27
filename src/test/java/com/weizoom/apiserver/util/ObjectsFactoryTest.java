/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.util;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.DummyApi;
import com.weizoom.apiserver.api.FakeApi;
import com.weizoom.apiserver.api.FakeApiWithData;

/**
 * 
 * @author chuter
 *
 */
public class ObjectsFactoryTest {
	@Test public void testObjectCreateByClassType() throws Exception {
		Api api = ObjectsFactory.createObject(DummyApi.class);
		Assert.assertTrue(api.getClass() == DummyApi.class);
	}
	
	@Test public void testObjectCreateByClassStr() throws Exception {
		Api api = (Api) ObjectsFactory.createObject(DummyApi.class.getName());
		Assert.assertTrue(api.getClass() == DummyApi.class);
	}
	
	@Test public void testObjectsCreateByClassStrs() throws Exception {
		String[] apiClassStrs = new String[]{
			DummyApi.class.getName(),
			FakeApi.class.getName(),
			FakeApiWithData.class.getName()
		};
		
		Collection<Api> apis = (Collection<Api>) ObjectsFactory.createObjects(Arrays.asList(apiClassStrs), Api.class);
		Assert.assertEquals(3, apis.size());
		for (Api api : apis) {
			Assert.assertTrue("fake".equals(api.getApiName()) || "dummy".equals(api.getApiName()));
		}
	}
	
	@Test public void testObjectsCreateByClassTypes() throws Exception {
		Class<?>[] apiClassTypes = new Class<?>[] {
			DummyApi.class,
			FakeApi.class,
			FakeApiWithData.class
		};
		
		Collection<Api> apis = (Collection<Api>) ObjectsFactory.createObjects(Arrays.asList(apiClassTypes));
		Assert.assertEquals(3, apis.size());
		for (Api api : apis) {
			Assert.assertTrue("fake".equals(api.getApiName()) || "dummy".equals(api.getApiName()));
		}
	}
	
}
