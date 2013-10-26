package com.weizoom.apiserver.server;

import java.util.HashSet;
import java.util.Set;

import com.weizoom.apiserver.api.Api;
import com.weizoom.apiserver.api.IApiResponsePropertyNameProcessor;
import com.weizoom.apiserver.util.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * <pre>
 * 	"code":200,
 *  "user":{
 *  	"name":"test"
 *  	"status":{
 *  		"id":1
 *  	}
 *  	...
 *  }
 * }
 * </pre>
 * @author chuter
 *
 */
public class ApiResponseBuilder {
	
	private ApiResponseBuilder() {	}
	
	public static JSONObject build(JSONObject rawResultJson, Api api) {
		JSONObject retJsonObject;
		Set<String> retFieldsSet = buildApiResponseFieldsSet(api);
		if (retFieldsSet.isEmpty()) {
			retJsonObject = rawResultJson;
		} else {
			retJsonObject = new JSONObject();
			filterRetJsonFields(retFieldsSet, retJsonObject, rawResultJson, "");
		}
		
		return processJsonPropertyName(retJsonObject, api.getResponsePropertyNameProcessor());
	}
	
	private static final Set<String> EMPTY_SET = new HashSet<String>();
	private static Set<String> buildApiResponseFieldsSet(Api api) {
		if (api.getResponseFields().length == 0) {
			return EMPTY_SET;
		}
		
		Set<String> fieldsSet = new HashSet<String>();
		for (String field : api.getResponseFields()) {
			fieldsSet.add(Strings.toUnderscoreCase(field));
		}
		return fieldsSet;
	}
	
	private static JSONObject processJsonPropertyName(JSONObject origJsonObject, IApiResponsePropertyNameProcessor processor) {
		if (null == processor) {
			return origJsonObject;
		}
		
		JSONObject processedJsonObject = new JSONObject();
		for (Object key : origJsonObject.keySet()) {
			if (key instanceof String) {
				Object value = origJsonObject.get(key);
				if (value instanceof JSONObject) {
					processedJsonObject.put(processor.process((String)key), processJsonPropertyName((JSONObject)origJsonObject.get(key), processor));
				} else if (value instanceof JSONArray) {
					JSONArray newJsonArray = new JSONArray();
					for (Object item : (JSONArray)value) {
						if (item instanceof JSONObject) {
							item = processJsonPropertyName((JSONObject)item, processor);
						}
						newJsonArray.add(item);
					}
					processedJsonObject.put(processor.process((String)key), newJsonArray);
				} else { //JSONArray
					processedJsonObject.put(processor.process((String)key), origJsonObject.get(key));
				}
			} else {
				processedJsonObject.put(key, origJsonObject.get(key));
			}
		}
		
		return processedJsonObject;
	}
	
	private static void filterRetJsonFields(Set<String> retFieldsSet, JSONObject retJsonObject, JSONObject rawJsonObject, String fieldNamePrefix) {
		for (Object key : rawJsonObject.keySet()) {
			if (key instanceof String) {
				Object value = rawJsonObject.get(key);
				
				String nextfieldNamePrefix;
				if (fieldNamePrefix.length() == 0) {
					nextfieldNamePrefix = Strings.toUnderscoreCase((String)key);
				} else {
					nextfieldNamePrefix = String.format("%s.%s", fieldNamePrefix, Strings.toUnderscoreCase((String)key));
				}
				
				if (retFieldsSet.contains(nextfieldNamePrefix)) {
					retJsonObject.put(key, value);
				} else if (shouldSelectCauseToSubfields(retFieldsSet, nextfieldNamePrefix)) {
					if (value instanceof JSONObject) {
						JSONObject subJsonObject = new JSONObject();
						filterRetJsonFields(retFieldsSet, subJsonObject, (JSONObject) value, nextfieldNamePrefix);
						retJsonObject.put(key, subJsonObject);
					} else if (value instanceof JSONArray) {
						JSONArray subJsonArray = new JSONArray();
						filterRetJsonFields(retFieldsSet, subJsonArray, (JSONArray) value, nextfieldNamePrefix);
						retJsonObject.put(key, subJsonArray);
					}
				}
			} else {
				retJsonObject.put(key, rawJsonObject.get(key));
			}
		}
	}
	
	private static void filterRetJsonFields(Set<String> retFieldsSet, JSONArray retJsonArray, JSONArray rawJsonArray, String fieldNamePrefix) {
		for (Object item : rawJsonArray) {
			if (item instanceof JSONObject) {
				JSONObject subJsonObject = new JSONObject();
				filterRetJsonFields(retFieldsSet, subJsonObject, (JSONObject)item, fieldNamePrefix);
				if (! subJsonObject.isEmpty()) {
					retJsonArray.add(subJsonObject);
				}
			} else {
				retJsonArray.add(item);
			}
		}
	}
	
	/**
	 * <pre>
	 * {
	 * "user":{
	 * 	"name":"chuter"
	 * 	...
	 * }
	 * }
	 * </pre>
	 * 
	 */
	private static boolean shouldSelectCauseToSubfields(Set<String> retFieldsSet, String prefix) {
		String fieldPrefix = prefix + ".";
		for (String retField : retFieldsSet) {
			if (retField.startsWith(fieldPrefix)) {
				return true;
			}
		}
		
		return false;
	}
	
}
