/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-2
 */
package com.weizoom.apiserver.cluster.local;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.weizoom.apiserver.cluster.ClusterSettings;
import com.weizoom.apiserver.cluster.NodeGroup;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.command.ClusterCommandTask;
import com.weizoom.apiserver.cluster.command.NodeRegisterCommander;
import com.weizoom.apiserver.cluster.common.NodeAddress;
import com.weizoom.apiserver.cluster.constant.ResultCode;

/**
 * 
 * <code>RegisterChannel</code>ר������localNodeע���Cluster����<br />
 * @author wuyadong
 */

class RegisterChannel {
	final static private Logger LOG = Logger.getLogger(RegisterChannel.class);
	
	private RegisterChannel() {	}
	
	/**
	 * ����nodeAddress������һ������ע���Task
	 * @param nodeAddress
	 * @return
	 */
	final static private ClusterCommandTask buildRegisterCommandTask(NodeAddress nodeAddress, NodeGroup group) {
		assert (nodeAddress != null);
		assert (group != null);
		
		JSONObject param = new JSONObject();
		param.put(ClusterCommandTask.COMMAND_TASK_FLAG_ATTR, "true");
		param.put(ClusterCommandTask.COMMAND_NAME_ATTR, NodeRegisterCommander.COMMAND_NAME);
		param.put(NodeRegisterCommander.REGISTER_PORT, nodeAddress.getPort());
		param.put(NodeRegisterCommander.REGISTER_HOST, nodeAddress.getHost());
		param.put(NodeRegisterCommander.REGISTER_PATH, nodeAddress.getPath());
		param.put(NodeRegisterCommander.REGISTER_GROUP, group.name());
		
		ClusterCommandTask commandTask = new ClusterCommandTask(NodeRegisterCommander.COMMAND_NAME, param, System.currentTimeMillis());
		return commandTask;
	}
	
	final static private InetSocketAddress extractResult(TaskResult result) {
		assert (result != null);
		//not success
		if (result.getCode() != ResultCode.SUCCESS) {
			return null;
		} else {
			JSONObject param = result.getResult();
			String host = param.getString(NodeRegisterCommander.PROXY_HOST);
			int port = param.getInt(NodeRegisterCommander.PROXY_PORT);
			return new InetSocketAddress(host, port);
		}
	}
	
	final static private TaskResult buildRegisterResult(String content) {
		if (content == null) {
			throw new NullPointerException("content");
		}

		JSONObject json = JSONObject.fromObject(content);
		TaskResult result = TaskResult.fromJson(json);
		return result;
	}
	
	/**
	 * ��<code>nodeAddress</code>����ע��<br />
	 * ����ɹ��ͷ���ProxyNode�ĵ�ַ<br />
	 * ���򷵻�Null
	 * @param nodeAddress
	 * @return
	 */
	static InetSocketAddress registerToCluster(NodeAddress nodeAddress, NodeGroup group) {
		if (nodeAddress == null) {
			throw new NullPointerException("node address");
		}
		InetSocketAddress clusterRegisterListenerAddress = ClusterSettings.getClusterRegisterAddress();
		InetSocketAddress proxyNodeAddress = null;
		
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			socket = new Socket();
			//1. ����command�˿�
			socket.connect(clusterRegisterListenerAddress);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			//2. ����ע����Ϣ
			String jsonStr = buildRegisterCommandTask(nodeAddress, group).toJson().toString();
			byte[] bs = jsonStr.getBytes();
			out.writeInt(bs.length);
			out.write(bs);
			//3. �������������ջش�����
			int length = in.readInt();
			byte[] bs2 = new byte[length];
			in.read(bs2, 0, length);
			//4. ת����result
			TaskResult result = buildRegisterResult(new String(bs2,0,length));
			//5. ����result
			proxyNodeAddress = extractResult(result);
		} catch (Exception e) {
			LOG.error(String.format("Failed to connect to cluster(%s)!", ClusterSettings.getClusterRegisterAddress()), e);
			return proxyNodeAddress;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					LOG.warn("datainputstram close exception.", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					LOG.warn("dataoutputstream close exception.", e);
				}
			}
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ignore) {
				ignore.printStackTrace();
				LOG.warn("socket close exception.", ignore);
			}
		}
		return proxyNodeAddress;
	}
}
