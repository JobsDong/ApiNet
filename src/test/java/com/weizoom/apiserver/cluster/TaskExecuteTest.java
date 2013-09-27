/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-8
 */
package com.weizoom.apiserver.cluster;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import net.sf.json.JSONObject;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.weizoom.apiserver.cluster.executor.ApiTaskExecutor;
import com.weizoom.apiserver.cluster.local.LocalNode;
import com.weizoom.apiserver.cluster.local.NodeSettings;
import com.weizoom.apiserver.cluster.LightHttpClientUseNetty;
/**
 * 测试任务执行的单元测试
 * 
 * @author wuyadong
 */
public class TaskExecuteTest {

	private Cluster cluster;
	private LocalNode localNode;
	
	@Before
	public void start() {
		ClusterSettings.setClusterAddress("127.0.0.1", 6396);
		ClusterSettings.setClusterRegisterAddress("127.0.0.1", 3344);
		ClusterSettings.setClusterDataExchangeAddress("127.0.0.1", 3115);
		cluster = new Cluster();
		cluster.start();
		
		NodeSettings.setLocalNodeAddress("127.0.0.1", 3535);
		localNode = new LocalNode(new ApiTaskExecutor());
		localNode.start();
	}
	
	@Test
	public void testTask() {
		//测试dummy
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "/api/dummy?woaini=32");
			assertTrue(true);
			assertNotNull(response);
			JSONObject jsonObject = parseToJson(response);
			assertEquals(200, jsonObject.getInt("code"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//测试hello
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "/api/hello?woaini=32");
			assertTrue(true);
			assertNotNull(response);
			JSONObject jsonObject = parseToJson(response);
			assertEquals(200, jsonObject.getInt("code"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@After
	public void stop() {
		localNode.stop();
		cluster.stop();
	}
	
	private JSONObject parseToJson(HttpResponse httpResponse) {
		assert(httpResponse != null);
		String str = httpResponse.getContent().toString(ClusterSettings.getClusterChannelCharset());
		System.out.println(str);
		JSONObject json = JSONObject.fromObject(str);
		return json;
	}
}
