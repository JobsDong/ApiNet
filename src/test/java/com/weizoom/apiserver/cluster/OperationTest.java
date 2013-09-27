/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-8
 */
package com.weizoom.apiserver.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.weizoom.apiserver.cluster.executor.ApiTaskExecutor;
import com.weizoom.apiserver.cluster.local.LocalNode;
import com.weizoom.apiserver.cluster.local.NodeSettings;

/**
 * ≤‚ ‘‘ÀŒ¨Ω”ø⁄µƒµ•‘™≤‚ ‘
 * @author wuyadong
 *
 */
public class OperationTest {
	private Cluster cluster;
	
	@Before
	public void startCluster() {
		ClusterSettings.setClusterAddress("127.0.0.1", 6396);
		ClusterSettings.setClusterRegisterAddress("127.0.0.1", 3344);
		ClusterSettings.setClusterDataExchangeAddress("127.0.0.1", 3115);
		cluster = new Cluster();
		cluster.start();
	}
	
	@After
	public void stopCluster() {
		cluster.stop();
	}
	
	@Test
	public void testOperatorWithNoNode() {
		//≤‚ ‘simple_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=simple_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			assertEquals(0, json.getInt("executing_tasks_count"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//≤‚ ‘nodes_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=nodes_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			assertTrue(json.containsKey("nodes_status"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//≤‚ ‘weight_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=weight_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			assertTrue(json.containsKey("status"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testOperatorWithNode() {
		NodeSettings.setLocalNodeAddress("127.0.0.1", 5656);
		LocalNode localNode = new LocalNode(new ApiTaskExecutor());
		localNode.start();
		
		//≤‚ ‘simple_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=simple_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			assertTrue(json.containsKey("isactive_nodes"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//≤‚ ‘nodes_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=nodes_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			JSONArray jsonArray = json.getJSONArray("nodes_status");
			assertEquals(1, jsonArray.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//≤‚ ‘weight_status
		try {
			HttpResponse response = LightHttpClientUseNetty.send("127.0.0.1", 6396, "http://127.0.0.1:6396/?is.operation=yes&operation.name=weight_status");
			assertTrue(true);
			JSONObject json = parseToJson(response);
			assertNotNull(json);
			JSONArray jsonArray = json.getJSONArray("status");
			assertEquals(1, jsonArray.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	
	}
	
	private JSONObject parseToJson(HttpResponse httpResponse) {
		assert(httpResponse != null);
		String str = httpResponse.getContent().toString(ClusterSettings.getClusterChannelCharset());
		JSONObject json = JSONObject.fromObject(str);
		return json;
	}
}
