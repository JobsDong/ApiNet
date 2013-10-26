package com.weizoom.apiserver.api;

public enum ApiOperateAction {
	GET(null), 
	CREATE("POST"), 
	DELETE(null), 
	MODIFY("PUT"), 
	NOT_SURPPORT(null);
	
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

