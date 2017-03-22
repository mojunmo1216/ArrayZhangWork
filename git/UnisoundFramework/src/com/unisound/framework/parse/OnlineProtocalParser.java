package com.unisound.framework.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.framework.utils.LogUtils;

public class OnlineProtocalParser {
	private static final String TAG = OnlineProtocalParser.class.getSimpleName();
	
	private String totalProtocal;
	private String onlineProtocal;

	
	public OnlineProtocalParser(String totalProtocal){
		this.totalProtocal = totalProtocal;
		onlineProtocal = parseTotalProtocal();
	}

	private String parseTotalProtocal() {
		if(totalProtocal != null && !totalProtocal.equals("")){
			JSONObject netJson = null;
			try {
				JSONObject totalJson = new JSONObject(totalProtocal);
				JSONArray netArray = totalJson.getJSONArray("net_nlu");
				if(netArray != null){
					netJson = (JSONObject) netArray.get(0);
					LogUtils.d(TAG, "online protocal is : " + netJson);
					return netJson.toString();
				}else {
					LogUtils.e(TAG, "parseTotalProtocal report : net_nlu is null");
					return null;
				}
			} catch (JSONException e) {
				LogUtils.e(TAG, "online protocal paser error : " + e.toString() + " return null");
				return null;
			}
		} else {
			LogUtils.e(TAG, "parseTotalProtocal report : totalProtocal is null ...");
			return null;
		}
	}
	
	public String getOnlineProtocal() {
		if(onlineProtocal != null && !onlineProtocal.equals("")){
			LogUtils.d(TAG, "getOnlineProtocal report : onlineProtocal is -> " + onlineProtocal);
			return onlineProtocal;
		}else {
			LogUtils.e(TAG, "getOnlineProtocal report : onlineProtocal is null");
			return null;
		}
	}
	
	public String getDomain(){
		if(onlineProtocal != null && !onlineProtocal.equals("")){
			String domain = null;
			try {
				JSONObject onLineProtocalJson = new JSONObject(onlineProtocal);
				domain = (String) onLineProtocalJson.get("service");
			} catch (JSONException e) {
				LogUtils.e(TAG, "getOnlineProtocalDomain report, json parser error : " + e.toString());
			}
			LogUtils.d(TAG, "onLineProtocal domain is : " + domain);
			return domain;
		}else {
			LogUtils.e(TAG, "getOnlineProtocalDomain report : onlineprotocal is null");
			return null;
		}
		
	}
	
	public String getText(){
		if(onlineProtocal != null && !onlineProtocal.equals("")){
			String text = null;
			try {
				JSONObject onLineProtocalJson = new JSONObject(onlineProtocal);
				text = (String) onLineProtocalJson.get("text");
				return text;
			} catch (JSONException e) {
				LogUtils.e(TAG, "getText report, json parser error : " + e.toString());
				throw new RuntimeException("getText report, json parser error");
			}
		}else {
			LogUtils.e(TAG, "getText report : onlineprotocal is null");
			throw new RuntimeException("getText report : onlineprotocal is null");
		}
	}
}
