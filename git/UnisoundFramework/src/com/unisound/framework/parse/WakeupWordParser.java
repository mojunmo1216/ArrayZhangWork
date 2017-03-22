package com.unisound.framework.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.framework.utils.LogUtils;

public class WakeupWordParser{
	private static final String TAG = WakeupWordParser.class.getSimpleName();
	
	private String wakeupWord;
	
	public WakeupWordParser(String wakeupJson){
		LogUtils.d(TAG, "WakeupWordParser init : " + wakeupJson);
		wakeupWord = parseJson(wakeupJson);
	}

	private String parseJson(String wakeupJson) {
		JSONObject json;
		try {
			json = new JSONObject(wakeupJson);
			JSONArray localAsr = json.getJSONArray("local_asr");
			JSONObject mainJson = (JSONObject) localAsr.get(0);
			String wakeupString = mainJson.getString("recognition_result");
			
			String wakeupWord = wakeupString.replaceAll("<[a-zA-Z0-9_/]*>| ", "").trim();
			LogUtils.d(TAG, "wakeupWord : " + wakeupWord);
			return wakeupWord;
		} catch (JSONException e) {
			LogUtils.e(TAG, "WakeupWordParser error, error msg : " + e.toString());
		}
		return null;
	}

	public String getWakeupWord() {
		return wakeupWord;
	}

}
