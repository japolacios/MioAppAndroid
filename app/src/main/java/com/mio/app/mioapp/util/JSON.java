package com.mio.app.mioapp.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSON {
	public static Map<String, String> parse(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			Iterator keys = jsonObject.keys();
			Map<String, String> map = new HashMap<String, String>();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, jsonObject.getString(key));
			}

			return map;
		} catch (JSONException e) {
			Log.d("json", e.getMessage());
		}
		return null;

	}

	public static Map<String, String> parseTo(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			Iterator keys = jsonObject.keys();
			Map<String, String> map = new HashMap<String, String>();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, jsonObject.getString(key));
			}

			return map;
		} catch (JSONException e) {
			Log.d("json", e.getMessage());
		}
		return null;

	}
}
