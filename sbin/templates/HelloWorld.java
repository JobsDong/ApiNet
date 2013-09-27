/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-8-2
 */
package ${pacake-name};

import net.sf.json.JSONObject;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.ApiCallMetadata;
import com.weizoom.apiserver.api.ApiException;
import com.weizoom.apiserver.api.ApiOperateAction;
import com.weizoom.apiserver.api.IllegalApiArugmentException;

public class HelloWorld extends Api {
	@Override
	public JSONObject run(JSONObject paramJson, ApiCallMetadata apiCallMetadata) throws ApiException {
		//在这里完成对资源进行相应处理的代码
		return null;
	}

	@Override
	protected void doStart() throws ApiException {
		//进行Api启动操作
	}

	@Override
	protected void doClose() throws ApiException {
		//进行Api关闭操作
	}

	@Override
	public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
		//进行参数和操作检查
	}
}
