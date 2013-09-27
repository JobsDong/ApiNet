/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-24
 */
package com.weizoom.apiserver.api;

/**
 * ��<i>Api</i>�������������������κδ���
 * @author chuter
 *
 */
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
