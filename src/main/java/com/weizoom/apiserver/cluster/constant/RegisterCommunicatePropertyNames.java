/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-24
 */
package com.weizoom.apiserver.cluster.constant;

/**
 * 
 * ����{@link com.weizoom.apiserver.cluster.local.RegisterChannel}��.
 * ����ע��ʱ�����ݸ�{@link com.weizoom.apiserver.cluster.Cluster}<br />
 * ��������Ԫ��<br />
 * <li>NODE_HOST, host����node��������ַ</li>
 * <li>NODE_PORT, port, ��node�������˿�</li>
 * <li>NODE_PATH, path, ��node��·��</li>
 * <li>CLUSTER_ACK, ack, ��cluster����ע���ǵĻظ�</li>
 * @author wuyadong
 *
 */
public enum RegisterCommunicatePropertyNames {
	NODE_HOST("host"),
	NODE_PORT("port"),
	NODE_PATH("path"),
	CLUSTER_ACK("ack");
	
	private String content;
	private RegisterCommunicatePropertyNames(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
}
