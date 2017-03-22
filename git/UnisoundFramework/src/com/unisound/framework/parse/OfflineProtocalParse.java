package com.unisound.framework.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.data.source.utils.LogUtils;

public class OfflineProtocalParse {
	private static final String TAG = OfflineProtocalParse.class.getSimpleName();
	
	private String totalProtocal;

	private double score;
	private String result;
	
	public OfflineProtocalParse(String totalProtocal) {
		this.totalProtocal = totalProtocal;
		parseTotalProtocal();
		consistOfflineProtocal(score, result);
	}

	private void consistOfflineProtocal(double score, String result) {
		JSONObject offlineJson = new JSONObject();
		try {
			offlineJson.put("score", score);
			offlineJson.put("result", result);
			LogUtils.d(TAG, "score : " + score + " result : " + result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parseTotalProtocal() {
		try {
			JSONObject totalJson = new JSONObject(totalProtocal);
			JSONArray locals = (JSONArray) totalJson.get("local_asr");
			JSONObject local = (JSONObject) locals.get(0);
			score = local.getDouble("score");
			result = local.getString("recognition_result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public double getScore(){
		return score;
	}
	
	public String getResult(){
		return result;
	}
}
