package com.haloai.huduniversalio.speech.unisound;

import org.json.JSONException;
import org.json.JSONObject;

import com.haloai.huduniversalio.speech.ISpeechParserIncomingCall;
import com.haloai.huduniversalio.utils.JsonTool;

public class UnisoundSpeechParserIncomingCall implements ISpeechParserIncomingCall {

	@Override
	public int parseSpeechText(String speechText) {
		JSONObject obj = JsonTool.parseToJSONObject(speechText);
		String service  = JsonTool.getJsonValue(obj, "service", "");
		if(!service.equalsIgnoreCase(UnisoundSpeech.SERVICE_SETTING)){
			return PARSE_RES_CODE_IMPROPER_SPEECH_INPUT;
		}
		
		JSONObject sematicObject = JsonTool.getJSONObject(obj, "semantic");
		JSONObject intentObject = JsonTool.getJSONObject(sematicObject, "intent");
		if (intentObject == null){
			return PARSE_RES_CODE_NOT_CLEAR_SPEECH_INPUT;
		}

		if(!intentObject.isNull("operator")){
//			try {
//				action = intentObject.getString("operator");
//			} catch (JSONException e) {
//				e.printStackTrace();
//				return PARSE_RES_CODE_INTERNAL_ERROR;
//			}
//			if(action.equalsIgnoreCase("ACT_NUM_GOTO")){
				return PARSE_RES_CODE_IMPROPER_SPEECH_INPUT;
//			}
		} else if(!intentObject.isNull("confirm")){
			String action;
			try {
				action = intentObject.getString("confirm");
			} catch (JSONException e) {
				return PARSE_RES_CODE_IMPROPER_SPEECH_INPUT;
			}

			if(action.equalsIgnoreCase("CANCEL")){
				return PARSE_RES_CODE_HANGUP;
			}else if (action.equalsIgnoreCase("OK")){
				return PARSE_RES_CODE_ANSWERED;
			}
		}

		return PARSE_RES_CODE_INTERNAL_ERROR;
	}

//	@Override
//	public String getResultDescription(int resultCode) {
//
//		if (resultCode == PARSE_RES_CODE_IMPROPER_SPEECH_INPUT)
//			return MSG_UNSUPPORT_SELECTION;
//		else if (resultCode == PARSE_RES_CODE_NOT_CLEAR_SPEECH_INPUT)
//			return MSG_NOT_CLEAR_INPUT;
//		return null;
//	}

}
