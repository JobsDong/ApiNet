/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-19
 */
package com.weizoom.apiserver.api;

/**
 * 
 * @author chuter
 */
public enum ApiOperateAction {
	GET(null), //��ȡ��Դ
	CREATE("POST"), //������Դ
	DELETE(null), //ɾ����Դ
	MODIFY("PUT"), //�޸���Դ
	NOT_SURPPORT(null); //��֧�ֵĲ���
	
	private String alias = null;
	
	ApiOperateAction(String alias) {
		this.alias = alias;
	}
	
	public static ApiOperateAction parse(String opStr) {
		if (null == opStr) {
			return NOT_SURPPORT;
		}
		
		for (ApiOperateAction action : values()) {
			if (action.name().equalsIgnoreCase(opStr) || opStr.equalsIgnoreCase(action.alias)) {
				return action;
			}
		}
		
		return NOT_SURPPORT;
	}
	
}

