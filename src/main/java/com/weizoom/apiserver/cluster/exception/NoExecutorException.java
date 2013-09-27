/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-30
 */
package com.weizoom.apiserver.cluster.exception;

/**
 * <code>NoExecutorException</code>����<code>masternode</code>
 * û���ҵ����ʵ�ProxyNodeȥ����ʱ��ͬʱ<code>MasterNode</code>��û���趨
 * <code>ITaskExecutor</code>�׳��Ĵ���
 * 
 * @author wuyadong
 */
public class NoExecutorException extends ClusterException {
	private static final long serialVersionUID = 4614280675689057874L;
	
	public NoExecutorException(String message) {
		super(message);
	}
}
