package com.haloai.huduniversalio.speech.unisound;

import org.json.JSONObject;

import android.util.Log;

import com.haloai.huduniversalio.speech.ISpeechParserNavi;
import com.haloai.huduniversalio.utils.JsonTool;

public class UnisoundSpeechParserNavi implements ISpeechParserNavi {
	private String TAG = UnisoundSpeechParserNavi.class.getName();
	private DestinationInfo mDestinationInfo;
	private String category;
	// ResponseText
	// {
	// "history": "cn.yunzhisheng.map",
	// "responseId": "4c19be7163024cfd89bb2db9c1a7970b",
	// "text": "导航到世界之窗",
	// "service": "cn.yunzhisheng.map",
	// "semantic": {
	// "intent": {
	// "toCity": "深圳市",
	// "method": "CAR",
	// "fromCity": "CURRENT_CITY",
	// "pathPoints": [],
	// "toPOI": "世界之窗",
	// "fromPOI": "CURRENT_LOC"
	// },
	// "normalHeader": "正在为您查找去世界之窗的路线"
	// },
	// "nluProcessTime": "60",
	// "code": "ROUTE",
	// "rc": 0
	// }

	@Override
	public int parseSpeechText(String speechText) {

		JSONObject obj = JsonTool.parseToJSONObject(speechText);
		JSONObject sematicObject = JsonTool.getJSONObject(obj, "semantic");
		JSONObject intentObject = JsonTool.getJSONObject(sematicObject, "intent");
		String service = JsonTool.getJsonValue(obj, "service", "");
		if (service.equalsIgnoreCase("cn.yunzhisheng.map")) { 
			if (intentObject != null) {
				String toCity = JsonTool.getJsonValue(intentObject, "toCity");
				String toPOI = JsonTool.getJsonValue(intentObject, "toPOI");
				if(toPOI.contains("CURRENT_LOC")){
					return PARSE_RES_CODE_INTERNAL_ERROR;
				}
				Log.i(TAG, "toCity: " + toCity);
				Log.i(TAG, "toPOI: " + toPOI);
				mDestinationInfo=new DestinationInfo();
				mDestinationInfo.city=toCity;
				mDestinationInfo.name=toPOI;
				return ISpeechParserNavi.PARSE_RES_CODE_NAVI_DESTINATION_NAME;
				
			} 
		}else if(service.equalsIgnoreCase("cn.yunzhisheng.localsearch")){
			if(intentObject != null){
				category = JsonTool.getJsonValue(intentObject,"category","");
				return ISpeechParserNavi.PARSE_RES_CODE_NAVI_DESTINATION_NAME;
			}
		}

		return PARSE_RES_CODE_INTERNAL_ERROR;
	}

	@Override
	public DestinationInfo getParseResultDestinationInfo() {
		return mDestinationInfo;
	}

	@Override
	public String getParseResultRoundCategory() {
		return category;
	}

}
