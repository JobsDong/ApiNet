/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-28
 */
package com.weizoom.apiserver.cluster.command;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.cluster.ClusterSettings;
import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.NodeGroup;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.common.NodeAddress;
import com.weizoom.apiserver.cluster.constant.ResultCode;

/**
 * <code>NodeRegisterCommander</code>��ר�����ڴ���ע�������Commander
 * ע�������<br />
 * <li>���ע����Ϣ</li>
 * <li>��ѯ�ɷ�ע��</li>
 * <li>����ProxyNode��������һЩ����</li>
 * <li>���ͳɹ��ظ�</li>
 * @author wuyadong
 *
 */
//TODO ʵ��ע��Ĺ���
public class NodeRegisterCommander implements IClusterCommander {
	final static private Logger LOG = Logger.getLogger(NodeRegisterCommander.class);
	
	final static public String COMMAND_NAME = "register_command";
	final static public String REGISTER_HOST = "register_host";
	final static public String REGISTER_PORT = "register_port";
	final static public String REGISTER_PATH = "register_path";
	final static public String REGISTER_GROUP = "register_group";
	
	final static public String PROXY_HOST = "proxy_host";
	final static public String PROXY_PORT = "proxy_port";
	
	public String getCommandName() {
		return COMMAND_NAME;
	}

	public TaskResult operateCommand(ClusterCommandTask task,
			ClusterState clusterState) {
		if (task == null) {
			throw new NullPointerException("task");
		}
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		//1.���ע����Ϣ
		JSONObject param = task.getParamJson();
		String host = param.getString(REGISTER_HOST);
		int port = param.getInt(REGISTER_PORT);
		String path = param.getString(REGISTER_PATH);
		NodeGroup group = NodeGroup.valueOf(param.getString(REGISTER_GROUP));
		
		//2.����ע��
		NodeAddress nodeAddress = new NodeAddress(host, port, path);
		boolean isSuccess = clusterState.addToUnAuthen(nodeAddress, group);
		
		TaskResult result = null;
		//3. ����ע��Ľ��
		if (isSuccess) {
			LOG.info(nodeAddress + ", register successfully!");
			JSONObject json = new JSONObject();
			json.put(PROXY_HOST, ClusterSettings.getClusterDataExchangeAddress().getAddress().getHostAddress());
			json.put(PROXY_PORT, ClusterSettings.getClusterDataExchangeAddress().getPort());
			result = new TaskResult(task.getId(), ResultCode.SUCCESS, json);
		} else {
			LOG.info(nodeAddress + ", register failed!");
			result = new TaskResult(task.getId(), ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY);
		}
		
		return result;
	}
}
