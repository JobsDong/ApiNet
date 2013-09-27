/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */
package com.weizoom.apiserver.cluster.common;

import net.sf.json.JSONObject;

/**
 * 
 * @author chuter
 *
 */
public interface IJsonSerializable {
	public JSONObject toJson() throws InterruptedException;
}
