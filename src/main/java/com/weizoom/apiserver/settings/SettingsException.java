/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-4-5
 */
package com.weizoom.apiserver.settings;

/**
 * 
 * @author chuter
 *
 */
public class SettingsException extends RuntimeException {

	private static final long serialVersionUID = 8202851569618039947L;

	public SettingsException(String message) {
        super(message);
    }

    public SettingsException(String message, Throwable cause) {
        super(message, cause);
    }
}
