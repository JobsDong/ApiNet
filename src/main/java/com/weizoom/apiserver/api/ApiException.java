/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-3-30
 */
package com.weizoom.apiserver.api;

/**
 * Api操作异常
 * @author chuter
 *
 */
public class ApiException extends RuntimeException {

	private int errorCode = -1;
	private static final long serialVersionUID = 1352512434755510478L;
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public ApiException(String errorMsg) {
		this(-1, errorMsg);
	}
	
	public ApiException(int errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
	}
	
	public ApiException(int errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
	
	public ApiException(Throwable cause) {
		this(-1, cause);
	}
	
	public ApiException(String errorMsg, Throwable cause) {
		this(-1, errorMsg, cause);
	}
	
	public ApiException(int errorCode, String errorMsg, Throwable cause) {
		super(errorMsg, cause);
		this.errorCode = errorCode;
	}
	
	public ApiException(Api api, String errorMsg) {
		this(-1, api, errorMsg);
	}
	
	public ApiException(int errorCode, Api api, String errorMsg) {
		super(String.format("api:'%s' error_code:'%d' error:'%s', current state:'%s'", api.getClass().getName(), errorCode, errorMsg, api.lifecycleState().toString()));
		this.errorCode = errorCode;
	}
	
	public ApiException(Api api, Throwable cause) {
		this(-1, api, cause);
	}
	
	public ApiException(int errorCode, Api api, Throwable cause) {
		super(String.format("failed for api:'%s', error_code:'%d' the current state is:'%s'", api.getClass().getName(), errorCode, api.lifecycleState().toString()), cause);
	}
	
	/**
     * 获取异常的详细信息，包括整个异常栈的所有异常信息
     */
    public String getDetailedMessage() {
        if (getCause() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(toString()).append("; ");
            if (getCause() instanceof ApiException) {
                sb.append(((ApiException) getCause()).getDetailedMessage());
            } else {
                sb.append(getCause());
            }
            return sb.toString();
        } else {
            return super.toString();
        }
    }

    /**
     * 检索最里层的异常，如果没有返回当前异常
     */
    public Throwable getRootCause() {
        Throwable rootCause = this;
        Throwable cause = getCause();
        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

    /**
     * 从该异常堆栈中获取指定类型的异常，如果不存在返回null
     * 
     * @param exType 需要寻找的异常类型
     * @return 指定类型的异常，如果不存在则返回null
     */
    public Throwable getSpecificExceptionType(Class<?> exType) {
    	if (null == exType) {
            return null;
        }
        if (exType.isInstance(this)) {
            return this;
        }
        Throwable cause = getCause();
        if (cause == this) {
            return null;
        }
        if (cause instanceof ApiException) {
            return ((ApiException) cause).getSpecificExceptionType(exType);
        } else {
            while (cause != null) {
                if (exType.isInstance(cause)) {
                    return cause;
                }
                if (cause.getCause() == cause) {
                    break;
                }
                cause = cause.getCause();
            }
            return null;
        }
    	
    }
    
}
