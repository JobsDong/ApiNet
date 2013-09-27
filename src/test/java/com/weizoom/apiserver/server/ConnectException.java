/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-1-9
 */
package com.weizoom.apiserver.server;

import java.net.URL;

/**
 * Õ¯¬Á¡¨Ω”“Ï≥£ 
 * @author chuter
 *
 */
public class ConnectException extends Exception {

	private static final long serialVersionUID = 504907031079802277L;

	public ConnectException(URL url) {
		super("Failed to connect to " + url.toString());
	}
	
	public ConnectException(URL url, String message) {
		super(String.format("Failed to connect to '%s' because of %s", url.toString(), message));
	}
	
	public ConnectException(URL url, Throwable cause) {
		super("Failed to connect to " + url.toString(), cause);
	}
	
	public ConnectException(URL url, String message, Throwable cause) {
		super(String.format("Failed to connect to '%s' because of %s", url.toString(), message), cause);
	}
	
}
