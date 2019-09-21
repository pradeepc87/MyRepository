package com.utils;

import org.json.JSONObject;

public class JsonUtils {
	
	public JSONObject getJsonFromString(String str) {
		return new JSONObject(str);
	}

}
