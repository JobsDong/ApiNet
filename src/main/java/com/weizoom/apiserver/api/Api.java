/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;

import java.util.concurrent.atomic.AtomicBoolean;

import com.weizoom.apiserver.api.ApiLifecycle.State;
import com.weizoom.apiserver.settings.Settings;
import com.weizoom.apiserver.settings.SettingsException;
import com.weizoom.apiserver.util.Strings;
import com.weizoom.apiserver.util.ObjectsFactory;

import net.sf.json.JSONObject;

/**
 * <i>Api</i>����<br>
 * 
 * ÿ��<i>Api</i>����{@link ApiLifecycle ��������}}��֧�����²���:<br>
 * <ul>
 * <li>{@link #start() ����}</li>
 * <li>{@link #close() �ر�}</li>
 * <li>{@link #run(JSONObject) ִ��}</li>
 * </ul>
 * 
 * ��ͨ��{@link #lifecycleState()}��ѯ<i>Api</i>��ǰ״̬
 * 
 * notice: �����Ҫ���ԣ��׳����쳣������<code>ResultCode.SYSTEM_ERROR_NEED_RETRY</code>
 * @see com.weizoom.apiserver.cluster.constant.ResultCode#SYSTEM_ERROR_NEED_RETRY
 * ���򱨳���ApiException��Code������<code>ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY</code>
 * 
 * @author chuter
 *
 */
//TODO ʵ��ͨ�������ļ���ȡ�Է��ؽ������������Ĵ�����
public abstract class Api {
	final static public String REMOTE_HOST_ATTR = "remote";  
	final static public String RESPONSE_FIELDS_PROPERTY = "response.fields"; //ÿ��Api���õ�������Ϣ����Ҫ�������ֶ�, ����Ը����Խ�
                                                                             //�������ã���ô���ؽ��ֻ�������õ��ֶ���Ϣ���������ȫ�� 
	//�Է��ؽ���������ƵĴ�����������������������
	final static public String RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY = "response.fields.processor.class"; 
	
	final protected ApiLifecycle lifecycle = new ApiLifecycle();
	
	protected Settings settings = Settings.EMPTY_SETTINGS; 
	
	final private AtomicBoolean hasBuildSettings = new AtomicBoolean(false);
	
	/**
	 * ִ��һ�β���
	 * @param paramJson json��ʽ�Ĳ�����Ϣ
	 * @param apiCallMetadata api����Ԫ��Ϣ
	 * @return json��ʽ��ִ�н��
	 */
	public abstract JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException;
	
	public synchronized void start() throws ApiException {
		synchronized (lifecycle) {
			if (lifecycle.canMoveToStarted()) {
				//�����û�й���������Ϣ�����Ƚ���������Ϣ�Ĺ���
				if (! hasBuildSettings.get()) {
					getSettings();
				}
				
				doStart();
				lifecycle.moveToStarted();
			} else if (State.STARTED == lifecycleState()) { //�Ѿ��������������κβ���
			} else {
				throw new ApiException(this, "Can not move to be started");
			}
		}
	}
	
	public synchronized void close() throws ApiException {
		synchronized (lifecycle) {
			if (lifecycle.canMoveToClosed()) {
				doClose();
				lifecycle.moveToClosed();
			} else {
				throw new ApiException(this, "Can not moved to be closed");
			}
		}
	}
	
	/**
	 * ��ȡ<i>Api</i>��key��һ��<i>Api</i>��Ψһ��ʶ�������ں�<i>Api</i>���й���<br>
	 * Ĭ��ʹ��������Сд��Ϊһ��<i>Api</i>��Ψһ��ʶ<br>
	 * ͨ����д�÷��������ض��ı�ʶ
	 * 
	 * @return
	 */
	public String getApiName() {
		String apiName = this.getClass().getSimpleName();
		if (apiName.toLowerCase().endsWith("api")) {
			apiName = apiName.substring(0, apiName.length()-"api".length());
		}
		return Strings.toUnderscoreCase(apiName);
	}
	
	public Settings getSettings() {
		if (! hasBuildSettings.get()) { //�����û�й������������Ƚ��й���
			synchronized (this) {
				if (! hasBuildSettings.get()) { //�ٽ���һ�μ��
					buildSettings();
					hasBuildSettings.set(true);
				}
			}
		}
		
		return settings;
	}
	
	public void resetSettings(Settings settings) {
		synchronized (this) {
			this.settings = settings;
			apiResponsePropertyNameProcessor = null;
		}
	}
	
	/**
	 * ��ȡ����������Ҫ���ص��ֶ���Ϣ
	 * @return
	 */
	public String[] getResponseFields() {
		synchronized (settings) {
			return settings.getAsArray(RESPONSE_FIELDS_PROPERTY);
		}
	}
	
	public State lifecycleState() {
		synchronized (lifecycle) {
			return lifecycle.state();
		}
	}
	
	public ApiLifecycle lifecycle() {
		synchronized (lifecycle) {
			return lifecycle.clone();
		}
	}
	
	private IApiResponsePropertyNameProcessor apiResponsePropertyNameProcessor = null;
	
	public IApiResponsePropertyNameProcessor getResponsePropertyNameProcessor() {
		synchronized (this) {
			if (null == apiResponsePropertyNameProcessor) {
				Class<IApiResponsePropertyNameProcessor> processorClass = (Class<IApiResponsePropertyNameProcessor>) settings.getAsClass(RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY, null);
				
				if (null == processorClass) {
					apiResponsePropertyNameProcessor = DummyApiResponsePropertyNameProcessor.get();
				} else {
					try {
						apiResponsePropertyNameProcessor = ObjectsFactory.createObject(processorClass);
					} catch (Exception e) {
						throw new SettingsException("Can not create the property name processor instance by class: " + processorClass , e);
					}
				}
			}
		}
		
		return apiResponsePropertyNameProcessor;
	}
	
	/**
	 * �������ö���Ĺ��������Api��Ҫ�������ã���ôͨ����д�÷��������Api������, 
	 * ��Api������ʱ��Ż���ô˷���
	 */
	protected synchronized void buildSettings() {
	}
	
	/**
	 * ������������
	 * @throws ApiException
	 */
	protected abstract void doStart() throws ApiException;
	
	/**
	 * ���йرղ���
	 * @throws ApiException
	 */
	protected abstract void doClose() throws ApiException;
	
	/**
	 * �����������Ƿ�Ϸ�
	 * @param paramJson
	 * @return
	 */
	public abstract void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException;
	
}
