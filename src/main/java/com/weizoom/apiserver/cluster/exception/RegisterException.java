/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster.exception;

import java.net.InetSocketAddress;

/**
 * 
 * @author wuyadong
 */
public class RegisterException extends ClusterException {
	private static final long serialVersionUID = 6352665509088045983L;

	public RegisterException(InetSocketAddress address) {
		super(String.format("failed to register to cluster:%s", address.toString()));
	}
}
