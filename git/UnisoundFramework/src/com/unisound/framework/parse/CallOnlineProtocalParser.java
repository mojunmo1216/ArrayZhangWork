package com.unisound.framework.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.framework.utils.LogUtils;

public class CallOnlineProtocalParser extends OnlineProtocalParser {
	private static final String TAG = CallOnlineProtocalParser.class.getSimpleName();
	
	private String totalProtocal;

	public CallOnlineProtocalParser(String totalProtocal) {
		super(totalProtocal);
		this.totalProtocal = totalProtocal;
		LogUtils.d(TAG, "CallOnlineProtocalParser : " + totalProtocal);
	}
	
	public String getName(){
		try {
			JSONObject totalObj = new JSONObject(totalProtocal);
			JSONObject semanticObj = (JSONObject) totalObj.get("semantic");
			JSONObject intentObj = (JSONObject) semanticObj.get("intent");
			
			if (intentObj != null && intentObj.has("name")){
				String name = (String) intentObj.get("name");
				return name;
			}else {
				return null;
			}
		} catch (JSONException e) {
			LogUtils.e(TAG, "parse protocal error : " + e.toString());
			throw new RuntimeException("parse protocal error");
		}
	}
}
