/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2011-12-29
 */
package com.weizoom.apiserver.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * 
 * @author chuter
 *
 */
public class HttpResponse {
	
	private byte[] content = null;
	private int code;
	private Properties headers = new Properties();
	private String contentStr = null;
	private Throwable cause;

	HttpResponse(int code, byte[] content, Properties headers) {
		this.code = code;
		this.content = content;
		this.headers = headers;
	}
	
	public Throwable getCause() {
		return cause;
	}
	
	/** Returns the response code. */
	public int getCode() {
		return code;
	}

	/** Returns the value of a named header. */
	public String getHeader(String name) {
		name = name.toLowerCase();
		return (String) headers.get(name);
	}

	public Properties getHeaders() {
		return headers;
	}

	public byte[] getContent() {
		return content;
	}
	
	public String getContentStr(String encode) {
		if (null != contentStr) {
			return contentStr;
		}
		
		String tempContent = null;
		if (null == content || 0 == content.length) {
			tempContent = "";
		} else {
			try {
				tempContent = new String(content, encode);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				tempContent = new String(content);
			}
		}
		
		contentStr = tempContent;
		return contentStr;
	}
	
	public String dump() {
		StringBuilder messageBuffer = new StringBuilder(String.format("code: %d\nheaders:\n", code));
		
		for (Entry<Object, Object> header : headers.entrySet()) {
			messageBuffer.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
		}
		
		messageBuffer.append("content:\n").append(getContentStr("gbk"));
		return messageBuffer.toString();
	}
	
	void setCause(Throwable cause) {
		this.cause = cause;
	}

}
