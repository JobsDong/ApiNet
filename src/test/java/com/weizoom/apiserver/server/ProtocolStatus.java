/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2011-2-10
 */

package com.weizoom.apiserver.server;

import java.util.HashMap;

/**
 * The status of the Protocol
 * 
 * @author chuter
 */
@SuppressWarnings("unchecked")
public class ProtocolStatus {
	/**
	 * Unspecified exception occured. Further information may be provided in
	 * args.
	 */
	public static final int EXCEPTION = 16;

	public static final int TIMEOUT = 1;
	public static final int CONNECT_EXCEPTION = 2;
	
	/** Content was retrieved without errors. */
	public static final int SUCCESS = 200;
	
	/** The resource has been moved */
	public static final int MOVED = 301;
	public static final int TEMP_MOVED = 302;
	
	/** Resource was not found. */
	public static final int NOTFOUND = 404;
	/** Access denied - authorization required, but missing/incorrect. */
	public static final int ACCESS_DENIED = 401;
	public static final int REQUEST_ERROR_CODE = 4;
	
	public static final int SERVER_ERROR_CODE = 5;
	
	public static final ProtocolStatus CONNECT_TIMEOUT = new ProtocolStatus(TIMEOUT);
	public static final ProtocolStatus CONNECT_FAINLED = new ProtocolStatus(TIMEOUT);
	public static final ProtocolStatus STATUS_SUCCESS = new ProtocolStatus(SUCCESS);
	public static final ProtocolStatus STATUS_NOTFOUND = new ProtocolStatus(NOTFOUND);
	public static final ProtocolStatus STATUS_ACCESS_DENIED = new ProtocolStatus(ACCESS_DENIED);
	public static final ProtocolStatus STATUS_EXCEPTION = new ProtocolStatus(EXCEPTION);
	public static final ProtocolStatus REQUEST_ERROR = new ProtocolStatus(REQUEST_ERROR_CODE);
	public static final ProtocolStatus SERVER_ERROR = new ProtocolStatus(SERVER_ERROR_CODE);

	private int code;
	private long lastModified;
	private String message;

	private static final HashMap codeToName = new HashMap();
	static {
		codeToName.put(new Integer(CONNECT_EXCEPTION), "socket连接失败！");
		codeToName.put(new Integer(TIMEOUT), "socket连接超时！");
		codeToName.put(new Integer(EXCEPTION), "其他异常！");
		codeToName.put(new Integer(SUCCESS), "succeed");
		codeToName.put(new Integer(MOVED), "所访问内容被永久性迁移！");
		codeToName.put(new Integer(TEMP_MOVED), "所访问内容临时迁移！");
		codeToName.put(new Integer(NOTFOUND), "所访问的内容不存在！");
		codeToName.put(new Integer(ACCESS_DENIED), "没有权限访问该内容！");
	}

	public ProtocolStatus() {

	}

	public ProtocolStatus(int code, String message) {
		this(code, message, 0L);
	}

	public ProtocolStatus(int code, String message, long lastModified) {
		this.code = code;
		this.message = message;
		this.lastModified = lastModified;
	}

	public ProtocolStatus(int code) {
		this(code, "");
	}

	public ProtocolStatus(int code, long lastModified) {
		this(code, "", lastModified);
	}

	public ProtocolStatus(int code, Object message) {
		this(code, message, 0L);
	}

	public ProtocolStatus(int code, Object message, long lastModified) {
		this.code = code;
		this.message = String.valueOf(message);
		this.lastModified = lastModified;
	}

	public ProtocolStatus(Throwable t) {
		this(EXCEPTION, t);
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		if (null == message) {
			message = (String) codeToName.get(code);
		} else {
			if (message.length() == 0 && codeToName.containsKey(code)) {
				message = (String) codeToName.get(code);
			}
		}
		return message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isSuccess() {
		return code == SUCCESS;
	}

	public boolean isTransientFailure() {
		return code == ACCESS_DENIED || code == TEMP_MOVED 
				|| code == SERVER_ERROR_CODE
				|| code == TIMEOUT || code == CONNECT_EXCEPTION 
				|| code == REQUEST_ERROR_CODE;
	}

	public boolean isPermanentFailure() {
		return code == MOVED || code == NOTFOUND;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof ProtocolStatus)) {
			return false;
		}
		ProtocolStatus that = (ProtocolStatus) o;
		if (this.code != that.code || this.lastModified != that.lastModified) {
			return false;
		}
		if (!this.message.equals(that.message)) {
			return false;
		} 
		return true;
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(codeToName.get(new Integer(code)) + "(" + code + "), lastModified=" + lastModified);
		stringBuffer.append(": " + message);
		return stringBuffer.toString();
	}
	
}
