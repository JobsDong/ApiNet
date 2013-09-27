/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-6
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.util.Strings;

/**
 * 默认的Api返回的Json结果的属性名称处理实现，该实现完成如下转换:<br>
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
	 * 对所有处理结果的字段名做如下处理:<br>
	 * "UndercoreCase" -> "undercore_case"
	 * 
	 */
	public String process(String name) {
		return Strings.toUnderscoreCase(name);
	}

}
