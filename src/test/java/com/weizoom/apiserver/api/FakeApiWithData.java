/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package com.weizoom.apiserver.api;

import net.sf.json.JSONObject;

/**
 * 
 * @author chuter
 *
 */
public class FakeApiWithData extends FakeApi {
	
	public static JSONObject responseData;
	
	/**
	 * ����Api����������, ���ж��Ƕ��:<br>
	 * <pre>
	 * {
     * 	"FakeData":"Empty",
	 *	"subResult" : {
     * 		"sub_fake" : [
     * 			"fake1", 
     * 			{"fake2" : "empty"}
     * 		]
     * 	},
     * 	"extralinfo" : [
     * 		{
     * 			"data1":"dummy",
     * 			"data2":"dummy"
     * 		},
     * 		"chuter"
     * 	]
     * }
	 * <pre>
	 */
	static {
		responseData = JSONObject.fromObject("{\"FakeData\":\"Empty\",\"subResult\":{\"sub_fake\":[\"fake1\",{\"fake2\":\"empty\"}]},\"extralinfo\":[{\"data1\":\"dummy\",\"data2\":\"dummy\"},\"chuter\"]}");
	}
	
	@Override
	public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
		return responseData;
	}
}

