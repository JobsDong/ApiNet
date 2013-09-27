/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-19
 */
package com.weizoom.apiserver.api;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author chuter
 *
 */
public class ApiOperateActionTest {
	@Test public void testApiOperateActionParsing() {
		Assert.assertTrue(ApiOperateAction.parse(null) == ApiOperateAction.NOT_SURPPORT);
		Assert.assertTrue(ApiOperateAction.parse("get") == ApiOperateAction.GET);
		Assert.assertTrue(ApiOperateAction.parse("GET") == ApiOperateAction.GET);
		Assert.assertTrue(ApiOperateAction.parse("create") == ApiOperateAction.CREATE);
		Assert.assertTrue(ApiOperateAction.parse("post") == ApiOperateAction.CREATE);
		Assert.assertTrue(ApiOperateAction.parse("delete") == ApiOperateAction.DELETE);
		Assert.assertTrue(ApiOperateAction.parse("DELETE") == ApiOperateAction.DELETE);
		Assert.assertTrue(ApiOperateAction.parse("put") == ApiOperateAction.MODIFY);
		Assert.assertTrue(ApiOperateAction.parse("modify") == ApiOperateAction.MODIFY);
		Assert.assertTrue(ApiOperateAction.parse("other") == ApiOperateAction.NOT_SURPPORT);
	}
}
