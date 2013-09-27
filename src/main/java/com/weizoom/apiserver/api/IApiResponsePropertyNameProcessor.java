/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-6
 */
package com.weizoom.apiserver.api;

/**
 * Api返回结果的Json属性名称的处理类接口
 * @author chuter
 *
 */
public interface IApiResponsePropertyNameProcessor {
	public String process(String name);
}
