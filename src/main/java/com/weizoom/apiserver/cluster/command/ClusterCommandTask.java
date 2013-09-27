/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-28
 */
package com.weizoom.apiserver.cluster.command;

import java.util.UUID;

import com.weizoom.apiserver.cluster.Task;

import net.sf.json.JSONObject;

/**
 * <code>ClusterCommandTask</code>������������Ⱥ�нڵ�ͨ�ŵ�Task����.
 * һ������������ͣ�<br />
 * <li>ע������</li>
 * <li>ע������</li>
 * <li>��������</li>
 * 
 * @author wuyadong
 *
 */
public class ClusterCommandTask extends Task {
	//Task������ָ���Ƿ�Ϊcommmand���͵�����Ϣ
	final static public String COMMAND_TASK_FLAG_ATTR = "is.command";
	final static public String COMMAND_NAME_ATTR = "command.name";
	
	final private String commandName;
	
	public ClusterCommandTask(String commandName, JSONObject param, long startTime) {
		this(generateId(param), commandName, param, startTime);
	}
	
	public ClusterCommandTask(String id, String commandName, JSONObject param, long startTime) {
		super(id, param, startTime);
		if (commandName == null) {
			throw new NullPointerException("command name");
		}
		this.commandName = commandName;
	}
	
	String getCommandName() {
		return this.commandName;
	}
	
	JSONObject getCommandParam() {
		return param;
	}
	
	static private String generateId(JSONObject param) {
		assert (param != null);
		String id = param.toString() + UUID.randomUUID().toString();
		return id;
	}
}
