/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-12
 */

package com.weizoom.apiserver.cluster;

import net.sf.json.JSONObject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.weizoom.apiserver.cluster.common.IJsonSerializable;
import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.constant.ResultField;
import com.weizoom.apiserver.cluster.exception.TaskResultJsonFormatException;

/**
 * TaskExecuteResult用于描述{@link com.weizoom.apiserver.cluster.Cluster}的计算结果<br />
 * 包括如下:
 * <li>taskId，是这个result对应的{@link com.weizoom.apiserver.cluster.Task}描述</li>
 * <li>code,是这个result对于的返回码,详细的返回码见{@link com.weizoom.apiserver.cluster.constant.ResultCode}</li>
 * <li>result,是这个result对于的计算结果</li>
 * 
 * @author wuyadong
 */
public class TaskResult implements IJsonSerializable {
	final protected String taskId;
	protected ResultCode code;
	protected JSONObject result;
	protected String message;
	
	public TaskResult(String taskId) {
		this(taskId, ResultCode.SUCCESS);
	}
	
	public TaskResult(String taskId, ResultCode code) {
		this(taskId, code, new JSONObject());
	}
	
	public TaskResult(String taskId, ResultCode code, JSONObject result) {
		this(taskId, code, result, code.toString());
	}
	
	public TaskResult(String taskId, ResultCode code, String message) {
		this(taskId, code, new JSONObject(), message);
	}
	
	public TaskResult(String taskId, ResultCode code, JSONObject result, String message) {
		if (taskId == null) {
			throw new NullPointerException("taskId");
		}
		if (code == null) {
			throw new NullPointerException("code");
		}
		if (result == null) {
			throw new NullPointerException("result");
		}
		if (message == null) {
			throw new NullPointerException("message");
		}
		this.taskId = taskId;
		this.code = code;
		this.result = result;
		this.message = message;
	}
	
	public ResultCode getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setCode(ResultCode code) {
		if (code == null) {
			throw new NullPointerException("code");
		}
		this.code = code;
	}
	
	public void setMessage(String message) {
		if (message == null) {
			throw new NullPointerException("message");
		}
		this.message = message;
	}
	
	public String getTaskId() {
		return this.taskId;
	}
	
	public JSONObject getResult() {
		return this.result;
	}
	
	public void setResult(JSONObject result) {
		if (result == null) {
			throw new NullPointerException("result");
		}
		this.result = result;
	}

	/***
	 * 序列化函数，将taskResult序列化json格式
	 */
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put(ResultField.JSON_FIELD.getContent(), result);
		json.put(ResultField.TASK_FIELD.getContent(), taskId);
		json.put(ResultField.CODE_FIELD.getContent(), code.getCode());
		json.put(ResultField.MESSAGE_FIELD.getContent(), message);
		return json;
	}
	
	/**
	 * 该方法是将计算的结果包装成一个HttpResponse。通常用于inClusterEnv=false的情况.
	 * 包含的内容如下:
	 * <li>协议版本：http1.1</li>
	 * <li>code:200</li>
	 * <li>内容：是({@link #result})json格式</li>
	 * @return
	 */
	public HttpResponse toHttpResponse() {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeBytes(result.toString().getBytes(ClusterSettings.getClusterChannelCharset()));
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.setContent(buf);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, String.format("application/json; charset=%s", ClusterSettings.getClusterChannelCharset()));
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buf.readableBytes()));
		return response;
	}
	
	/**
	 * 将json反序列化成一个TaskExecuteResult.
	 * 该json必须包括如下元素，否则会抛出{@link com.weizoom.apiserver.exception.TaskJsonFormatException}
	 * @param json
	 * @return
	 */
	final static public TaskResult fromJson(JSONObject json) {
		if (null == json) {
			throw new NullPointerException("json");
		}
		
		checkTaskJsonFormat(json);
		String taskId = json.getString(ResultField.TASK_FIELD.getContent());
		ResultCode code = ResultCode.toRESULT_CODE(json.getInt(ResultField.CODE_FIELD.getContent()));
		JSONObject result = JSONObject.fromObject(json.getJSONObject(ResultField.JSON_FIELD.getContent()));
		String message = json.getString(ResultField.MESSAGE_FIELD.getContent());
		return new TaskResult(taskId, code, result, message);
	}
	
	final static private void checkTaskJsonFormat(JSONObject json) {
		assert (json != null);
		
		if (! json.containsKey(ResultField.TASK_FIELD.getContent())) {
			throw new TaskResultJsonFormatException(json, "not contains taskId");
		} 
		if (! json.containsKey(ResultField.CODE_FIELD.getContent())) {
			throw new TaskResultJsonFormatException(json, "not contains code");
		}
		if (! json.containsKey(ResultField.JSON_FIELD.getContent())) {
			throw new TaskResultJsonFormatException(json, "not contains json");
		}
		if (! json.containsKey(ResultField.JSON_FIELD.getContent())) {
			throw new TaskResultJsonFormatException(json, "not contains message");
		}
	}
}
