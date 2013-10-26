package com.weizoom.apiserver.api;

import com.weizoom.apiserver.settings.Settings;
import com.weizoom.apiserver.settings.Settings.Builder;

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
