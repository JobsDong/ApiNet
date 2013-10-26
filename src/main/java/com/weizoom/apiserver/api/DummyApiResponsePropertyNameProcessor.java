package com.weizoom.apiserver.api;

public class DummyApiResponsePropertyNameProcessor implements IApiResponsePropertyNameProcessor {
	private static final IApiResponsePropertyNameProcessor INSTANCE = new DummyApiResponsePropertyNameProcessor();
	
	public static IApiResponsePropertyNameProcessor get() {
		return INSTANCE;
	}
	
	public String process(String name) {
		return name;
	}
	
	private DummyApiResponsePropertyNameProcessor() {	}
}
