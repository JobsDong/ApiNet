/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.server;

import com.weizoom.apiserver.settings.Settings.Builder;

/**
 * {@link ApiServer}����<br>
 * 
 * �������������<br>
 * 
 * <dl>
 * <dt>server.port</dt>
 * <dd>server�����˿ڣ�Ĭ��Ϊ8888��</dd>
 * 
 * <dt>server.monitor.port</dt>
 * <dd>server����̼߳����˿ڣ�Ĭ��Ϊ8889��</dd>
 * 
 * <dt>server.customerized.apis</dt>
 * <dd>ʹ������ӵ�api</dd>
 * </dl>
 * 
 * @author chuter
 *
 */
public class ServerConfig {
	//������Ϣ��ز���
	private static final String SERVER_SETTINGS_FILE_NAME = "server.settings.yml";
	//������
	private static final String serverPortConfName = "server.port";
	private static final String serverMonitorPortConfName = "server.monitor.port";
	private static final String customerizedApis = "server.customerized.apis";
	
	private static com.weizoom.apiserver.settings.Settings SERVER_SETTINGS = com.weizoom.apiserver.settings.Settings.EMPTY_SETTINGS;

	static {
		try {
			Builder settingsBuilder = com.weizoom.apiserver.settings.Settings.settingsBuilder().loadFromClasspath(SERVER_SETTINGS_FILE_NAME);
			SERVER_SETTINGS = settingsBuilder.build();
		} catch (Exception e) {
			System.err.println("Failed to parse the server settings. Please check it.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static final int getServerPort() {
		return SERVER_SETTINGS.getAsInt(serverPortConfName, 8888);
	}
	
	public static final int getServerMonitorPort() {
		return SERVER_SETTINGS.getAsInt(serverMonitorPortConfName, 8889);
	}
	
	private static final String[] EMPTY_STRING_ARRAY = new String[]{};
	public static final String[] getCustomerizedApis() {
		return SERVER_SETTINGS.getAsArray(customerizedApis, EMPTY_STRING_ARRAY);
	}
	
	private ServerConfig() {	}
	
}
