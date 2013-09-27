/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-1
 */
package com.weizoom.apiserver.cluster.common;

/**
 * {@link com.weizoom.apiserver.cluster.Node}�ĵ�ַ��Ϣ
 * ���а�����һ����Ϣ:<br>
 * <dl>
 * <dt>host</dt>
 * <dd>�ڵ��ip������</dd>
 * 
 * <dt>port</dt>
 * <dd>�ڵ�����Ķ˿ں���Ϣ</dd>
 * 
 * <dt>path</dt>
 * <dd>�ڵ�Ĳ���·��</dd>
 * </dl>
 * 
 * @author chuter & wuyadong
 *
 */
public class NodeAddress {
	final static private String DEFAULT_PATH = System.getProperty("user.dir");
	final static private int DEFAULT_PORT = 31152;
	final static private String DEFAULT_HOST = "127.0.0.1";
	//TODO �޸�һ��
	final static public NodeAddress FAKE_ADDRESS = new NodeAddress(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_PATH);
	
	
	private String host;
	private int port;
	private String path;
	
	public NodeAddress(String host, int port) {
		this(host, port, DEFAULT_PATH);
	}
	
	public NodeAddress(String host, int port, String path) {
		if (null == host) {
			throw new NullPointerException("host");
		}

		this.host = host;
		this.port = port;
		this.path = (null == path) ? DEFAULT_PATH : path;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getPath() {
		return path;
	}
	
	public String hashStr() {
		return String.format("%s:%d/%s", host, port, path);
	}
	
	@Override
	public int hashCode() {
		return hashStr().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		
		if (! (o instanceof NodeAddress)) {
			return false;
		}
		
		NodeAddress that = (NodeAddress) o;
		return this.hashCode() == that.hashCode();
	}
}
