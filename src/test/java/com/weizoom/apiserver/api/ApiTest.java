/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-24
 */
package com.weizoom.apiserver.api;

import junit.framework.Assert;

import org.junit.Test;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.DefaultApiResponsePropertyNameProcessor;
import com.weizoom.apiserver.api.DummyApiResponsePropertyNameProcessor;
import com.weizoom.apiserver.api.IApiResponsePropertyNameProcessor;
import com.weizoom.apiserver.settings.Settings;
import com.weizoom.apiserver.settings.Settings.Builder;
import com.weizoom.apiserver.settings.SettingsException;

/**
 * 
 * @author chuter
 *
 */
public class ApiTest {
	
	private static final Api DUMM_API = new DummyApi();
	
	/**
	 * 测试对Api调用返回结果的Json的域名处理器配置的处理<br>
	 * 需要能够正确解析出配置的处理器
	 */
	@Test public void testResponsePropertyNameProcessorConstructing() {
		//在没有配置时，正确结果为返回DummyApiResponsePropertyNameProcessor
		Assert.assertEquals(DummyApiResponsePropertyNameProcessor.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//在配置正确时（配置的解析器类存在，且具有静态的get方法或者public的无参数的构造器），正确结果为能够正确解析出解析器实例
		//1. 配置的解析器具有静态的get方法
		Builder settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, DefaultApiResponsePropertyNameProcessor.class.getName());
		DUMM_API.resetSettings(settingsBuilder.build());
		
		Assert.assertEquals(DefaultApiResponsePropertyNameProcessor.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//2. 配置的解析器具有public的无参数的构造器
		settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, ApiResponsePropertyNameProcessorForTest.class.getName());
		DUMM_API.resetSettings(settingsBuilder.build());
		Assert.assertEquals(ApiResponsePropertyNameProcessorForTest.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//在配置错误时，会抛出配置异常
		//1. 配置的解析器不满足条件（不可见，没有静态的get方法获取实例，或者没有public的且无参的构造器）
		settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, InvalidApiResponsePropertyNameProcessorForTest.class.getName());
		DUMM_API.resetSettings(settingsBuilder.build());
		
		try {
			DUMM_API.getResponsePropertyNameProcessor();
			Assert.assertTrue(false);
		} catch (SettingsException e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Can not create the property name processor instance by class: class com.weizoom.apiserver.api.InvalidApiResponsePropertyNameProcessorForTest"));
		}
		
		//2. 配置的解析器名错误
		settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, "any.way");
		DUMM_API.resetSettings(settingsBuilder.build());
		
		try {
			DUMM_API.getResponsePropertyNameProcessor();
			Assert.assertTrue(false);
		} catch (SettingsException e) {
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().contains("Failed to load class setting [response.fields.processor.class] with value [any.way]"));
		}
		
	}
}

class InvalidApiResponsePropertyNameProcessorForTest implements IApiResponsePropertyNameProcessor {
	
	InvalidApiResponsePropertyNameProcessorForTest(String name) {}
	
	public String process(String name) {
		return null;
	}
}