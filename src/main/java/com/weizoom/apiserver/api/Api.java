package com.weizoom.apiserver.api;

import java.util.concurrent.atomic.AtomicBoolean;
import com.weizoom.apiserver.api.ApiLifecycle.State;
import com.weizoom.apiserver.settings.Settings;
import com.weizoom.apiserver.settings.SettingsException;
import com.weizoom.apiserver.util.Strings;
import com.weizoom.apiserver.util.ObjectsFactory;

import net.sf.json.JSONObject;

public abstract class Api {
	final static public String REMOTE_HOST_ATTR = "remote";  
	final static public String RESPONSE_FIELDS_PROPERTY = "response.fields"; 
	final static public String RESPONSE_FIELD_NAME_PROCESSOR_CLASS_PROPERTY = "response.fields.processor.class"; 
	
	final protected ApiLifecycle lifecycle = new ApiLifecycle();
	
	protected Settings settings = Settings.EMPTY_SETTINGS; 
	
	final private AtomicBoolean hasBuildSettings = new AtomicBoolean(false);
	
	public abstract JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException;
	
	public synchronized void start() throws ApiException {
		synchronized (lifecycle) {
			
			if (lifecycle.canMoveToStarted()) {
					getSettings();
					doStart();
					lifecycle.moveToStarted();
				}else if(ApiLifecycle.State.STARTED == lifecycle.state()) { 
					
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
	
	public String getApiName() {
		String apiName = this.getClass().getSimpleName();
		if (apiName.toLowerCase().endsWith("api")) {
			apiName = apiName.substring(0, apiName.length()-"api".length());
		}
		return Strings.toUnderscoreCase(apiName);
	}
	
	public Settings getSettings() {
		if (! hasBuildSettings.get()) { 
				if (! hasBuildSettings.get()) { 
					hasBuildSettings.set(true);
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
	
	protected synchronized void buildSettings() {
	}
	
	protected abstract void doStart() throws ApiException;
	
	protected abstract void doClose() throws ApiException;
	
	public abstract void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException;
	
}
