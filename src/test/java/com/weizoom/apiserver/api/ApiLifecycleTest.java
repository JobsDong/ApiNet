/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;

import org.junit.Assert;
import org.junit.Test;

import com.weizoom.apiserver.api.ApiLifecycle.State;


/**
 * {@link Api}生命周期状态转换单元测试<br>
 * 支持的状态转移：<br>
 * <pre>
 * INITIALIZED->STARTED,STOPPED,CLOSED
 * STARTED->STOPPED
 * STOPPED->STARTED,CLOSED
 * CLOSED
 * </pre>
 * @author chuter
 *
 */
public class ApiLifecycleTest {
	
	/**
	 * 测试每次Api操作成功的状态转移
	 */
	@Test public void testApiLifecycleStateTransferingWithoutFailedOperation() {
		DummyApi dummyApi = new DummyApi();
		
		//初始状态为INITIALIZED
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
	
		//测试状态转移：INITIALIZED->STARTED,CLOSED
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		dummyApi = new DummyApi();
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//测试状态转移：STARTED->CLOSED
		dummyApi = new DummyApi();
		dummyApi.start();
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//测试在CLOSED状态时的状态转移
		Assert.assertFalse(dummyApi.lifecycle().moveToClosed());
	}
	
	/**
	 * 测试Api操作失败后的状态转移
	 */
	@Test public void testApiLifecycleStateTransferingWithFailedOperation() {
		DummyApi dummyApi = new DummyApi();
		
		//初始状态为INITIALIZED
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
		dummyApi.setStartFailed(true); //设置启动失败
		try {
			dummyApi.start();
			Assert.assertTrue(false);
		} catch (ApiException e) {
			Assert.assertEquals("Been told to be failed.", e.getMessage());
		}
		//启动失败后还处于原状态
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
		
		//启动成功后
		dummyApi.setStartFailed(false);
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		//再次启动，不会进行任何操作，状态不会发生变化
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		//关闭失败后
		dummyApi.setCloseFailed(true);
		try {
			dummyApi.close();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertEquals("Been told to be failed.", e.getMessage());
		}
		
		//关闭成功后
		dummyApi.setCloseFailed(false);
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//再次关闭会导致异常
		try {
			dummyApi.close();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
	}
	
	
}
