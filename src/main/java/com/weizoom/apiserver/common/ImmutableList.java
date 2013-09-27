/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-5-17
 */
package com.weizoom.apiserver.common;

import java.util.List;

/**
 * 不可变List实现
 * @author chuter
 *
 */
public abstract class ImmutableList implements List {

	public static final ImmutableList EMPTY_LIST = new EmptyImmutableList();
	
}
