/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;



/**
 * 
 * @author chuter
 *
 */
public class DuplicateApiKeyException extends ApiException {

	private static final long serialVersionUID = -7345427027521982411L;

	public DuplicateApiKeyException(String apiKey) {
		super("Already contains the api with key " + apiKey);
	}
	
}
