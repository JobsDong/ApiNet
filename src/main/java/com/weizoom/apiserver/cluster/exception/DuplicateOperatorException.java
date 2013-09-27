/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.exception;

import com.weizoom.apiserver.cluster.operation.IClusterOperator;

/**
 * 
 * @author chuter
 *
 */
public class DuplicateOperatorException extends RuntimeException {
	private static final long serialVersionUID = -7731199511098514975L;

	public DuplicateOperatorException(IClusterOperator operator) {
		super("Duplicate operator " + operator.getOperationName());
	}
}
