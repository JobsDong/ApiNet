/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-6
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.util.Strings;

/**
 * Ĭ�ϵ�Api���ص�Json������������ƴ���ʵ�֣���ʵ���������ת��:<br>
 * "UndercoreCase" -> "undercore_case"
 * 
 * @author chuter
 *
 */
public class DefaultApiResponsePropertyNameProcessor implements IApiResponsePropertyNameProcessor {

	public static DefaultApiResponsePropertyNameProcessor get() {
		if (null == PROCESSOR) {
			PROCESSOR = new DefaultApiResponsePropertyNameProcessor();
		}
		return PROCESSOR;
	}
	
	private static DefaultApiResponsePropertyNameProcessor PROCESSOR = null;
	
	private DefaultApiResponsePropertyNameProcessor() {	}
	
	/**
	 * �����д��������ֶ��������´���:<br>
	 * "UndercoreCase" -> "undercore_case"
	 * 
	 */
	public String process(String name) {
		return Strings.toUnderscoreCase(name);
	}

}
