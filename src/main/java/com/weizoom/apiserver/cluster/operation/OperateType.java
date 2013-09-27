/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-27
 */
package com.weizoom.apiserver.cluster.operation;

/**
 * <code>OperateType</code>定义了api提供的操作类型的枚举类型
 * 包括如下种类：<br />
 * <li>GET</li>
 * <li>MODIFY</li>
 * <li>DELETE</li>
 * <li>UNKNOWN</li>
 * 
 * @author wuyadong
 */
enum OperateType {
	GET,
	MODIFY,
	DELETE,
	UNKOWN;
	
	static OperateType parse(String type) {
		if (null == type) {
			return UNKOWN;
		}
		
		for (OperateType action : values()) {
			if (action.name().equalsIgnoreCase(type)) {
				return action;
			}
		}
		
		return UNKOWN;
	}
}