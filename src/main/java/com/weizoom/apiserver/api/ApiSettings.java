/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2012-5-12
 */
package com.weizoom.apiserver.api;

import com.weizoom.apiserver.settings.Settings;
import com.weizoom.apiserver.settings.Settings.Builder;

/**
 * 所有Api共用的配置对象
 * @author chuter
 *
 */
public class ApiSettings {
	private ApiSettings() {	}
	
	private static Settings ALL_API_COMMON_SETTINGS = null;
	
	static {
		Builder settingsBuilder = Settings.settingsBuilder().loadFromClasspath("apis.settings.yml");
		ALL_API_COMMON_SETTINGS = settingsBuilder.build();
	}
	
	public static final Settings getSettings() {
		return ALL_API_COMMON_SETTINGS;
	}
}
