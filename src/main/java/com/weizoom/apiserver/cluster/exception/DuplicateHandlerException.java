/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-29
 */
package com.weizoom.apiserver.cluster.exception;


/**
 * ��simpleChannelPipelineFacotory���ظ�����Handler��ʱ�򣬱����˴���<br />
 * @see com.weizoom.apiserver.SimpleChannelPipelineFactory
 * 
 * @author wuyadong
 */
public class DuplicateHandlerException extends ClusterException {
	private static final long serialVersionUID = 2900975939592685383L;
	
	public DuplicateHandlerException(String error) {
		super(error);
	}
}
