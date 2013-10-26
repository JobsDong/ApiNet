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
 * {@link Api}��������״̬ת����Ԫ����<br>
 * ֧�ֵ�״̬ת�ƣ�<br>
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
	 * ����ÿ��Api�����ɹ���״̬ת��
	 */
	@Test public void testApiLifecycleStateTransferingWithoutFailedOperation() {
		DummyApi dummyApi = new DummyApi();
		
		//��ʼ״̬ΪINITIALIZED
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
	
		//����״̬ת�ƣ�INITIALIZED->STARTED,CLOSED
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		dummyApi = new DummyApi();
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//����״̬ת�ƣ�STARTED->CLOSED
		dummyApi = new DummyApi();
		dummyApi.start();
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//������CLOSED״̬ʱ��״̬ת��
		Assert.assertFalse(dummyApi.lifecycle().moveToClosed());
	}
	
	/**
	 * ����Api����ʧ�ܺ��״̬ת��
	 */
	@Test public void testApiLifecycleStateTransferingWithFailedOperation() {
		DummyApi dummyApi = new DummyApi();
		
		//��ʼ״̬ΪINITIALIZED
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
		dummyApi.setStartFailed(true); //��������ʧ��
		try {
			dummyApi.start();
			Assert.assertTrue(false);
		} catch (ApiException e) {
			Assert.assertEquals("Been told to be failed.", e.getMessage());
		}
		//����ʧ�ܺ󻹴���ԭ״̬
		Assert.assertEquals(State.INITIALIZED, dummyApi.lifecycleState());
		
		//�����ɹ���
		dummyApi.setStartFailed(false);
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		//�ٴ���������������κβ�����״̬���ᷢ��仯
		dummyApi.start();
		Assert.assertEquals(State.STARTED, dummyApi.lifecycleState());
		
		//�ر�ʧ�ܺ�
		dummyApi.setCloseFailed(true);
		try {
			dummyApi.close();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertEquals("Been told to be failed.", e.getMessage());
		}
		
		//�رճɹ���
		dummyApi.setCloseFailed(false);
		dummyApi.close();
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
		
		//�ٴιرջᵼ���쳣
		try {
			dummyApi.close();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(State.CLOSED, dummyApi.lifecycleState());
	}
	
	
}
