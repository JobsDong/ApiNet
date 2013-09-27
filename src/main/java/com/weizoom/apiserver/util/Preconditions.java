/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-5
 */
package com.weizoom.apiserver.util;

/**
 * 条件判定类，目前只支持一个对象是否为Null的判定
 * @author chuter
 *
 */
public final class Preconditions {
	private Preconditions() {
	}
	
    public static <T> T checkNotNull(T reference) {
        if (null == reference) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (null == reference) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
