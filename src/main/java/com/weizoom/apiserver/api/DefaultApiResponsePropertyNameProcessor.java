package com.weizoom.apiserver.api;

import com.weizoom.apiserver.util.Strings;

public class DefaultApiResponsePropertyNameProcessor implements IApiResponsePropertyNameProcessor {

	public static DefaultApiResponsePropertyNameProcessor get() {
		if (null == PROCESSOR) {
			PROCESSOR = new DefaultApiResponsePropertyNameProcessor();
		}
		return PROCESSOR;
	}
	
	private static DefaultApiResponsePropertyNameProcessor PROCESSOR = null;
	
	private DefaultApiResponsePropertyNameProcessor() {	}
	
	public String process(String name) {
		return Strings.toUnderscoreCase(name);
	}

}
