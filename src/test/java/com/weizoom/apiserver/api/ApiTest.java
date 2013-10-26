/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-24
 */
package com.weizoom.apiserver.api;

import junit.framework.Assert;

import org.junit.Test;

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
	 * ���Զ�Api���÷��ؽ���Json�������������õĴ���<br>
	 * ��Ҫ�ܹ���ȷ���������õĴ�����
	 */
	@Test public void testResponsePropertyNameProcessorConstructing() {
		//��û������ʱ����ȷ���Ϊ����DummyApiResponsePropertyNameProcessor
		Assert.assertEquals(DummyApiResponsePropertyNameProcessor.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//��������ȷʱ�����õĽ���������ڣ��Ҿ��о�̬��get��������public���޲���Ĺ�����������ȷ���Ϊ�ܹ���ȷ������������ʵ��
		//1. ���õĽ��������о�̬��get����
		Builder settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, DefaultApiResponsePropertyNameProcessor.class.getName());
		DUMM_API.resetSettings(settingsBuilder.build());
		
		Assert.assertEquals(DefaultApiResponsePropertyNameProcessor.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//2. ���õĽ���������public���޲���Ĺ�����
		settingsBuilder = Settings.settingsBuilder().put(DUMM_API.getSettings());
		settingsBuilder.put(Api.RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, ApiResponsePropertyNameProcessorForTest.class.getName());
		DUMM_API.resetSettings(settingsBuilder.build());
		Assert.assertEquals(ApiResponsePropertyNameProcessorForTest.class, DUMM_API.getResponsePropertyNameProcessor().getClass());
		
		//�����ô���ʱ�����׳������쳣
		//1. ���õĽ��������������������ɼ�û�о�̬��get������ȡʵ�����û��public�����޲εĹ�������
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
		
		//2. ���õĽ����������
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