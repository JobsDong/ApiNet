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
		//��������ɶ���Դ������Ӧ����Ĵ���
		return null;
	}

	@Override
	protected void doStart() throws ApiException {
		//����Api��������
	}

	@Override
	protected void doClose() throws ApiException {
		//����Api�رղ���
	}

	@Override
	public void checkRequstParam(JSONObject paramJson, ApiOperateAction apiOperateAction) throws IllegalApiArugmentException {
		//���в����Ͳ������
	}
}
