package com.haloai.huduniversalio.speech.unisound;

import com.haloai.huduniversalio.speech.ISpeechParserCommand;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONObject;

public class UnisoundSpeechParserCommand implements ISpeechParserCommand {

	@Override
	public int parseSpeechText(String speechText) {
		JSONObject obj = JsonTool.parseToJSONObject(speechText);
		JSONObject sematicObject = JsonTool.getJSONObject(obj, "semantic");
		JSONObject intentObject = JsonTool.getJSONObject(sematicObject, "intent");		
		String operator=JsonTool.getJsonValue(intentObject, "operator"," ");
		if(operator.equalsIgnoreCase("music_play")||operator.equalsIgnoreCase("ACT_PLAY")){
			return PRASE_COMMAND_PLAY;
		}else if(operator.equalsIgnoreCase("music_pause")||operator.equalsIgnoreCase("ACT_PAUSE")){
			if(speechText.contains("关闭音乐")){
				return PRASE_COMMAND_STOP;
			}
			return PRASE_COMMAND_PAUSE;
		}else if(operator.equalsIgnoreCase("music_next")||operator.equalsIgnoreCase("ACT_NEXT")){
			return PRASE_COMMAND_NEXT;
		}else if(operator.equalsIgnoreCase("music_previous")||operator.equalsIgnoreCase("ACT_PREV")){
			return PRASE_COMMAND_PREVI;
		}else if(operator.equalsIgnoreCase("music_stop")||operator.equalsIgnoreCase("ACT_STOP")){
			return PRASE_COMMAND_STOP;
		}else if(operator.equalsIgnoreCase("ACT_CLOSE")){
			return PRASE_COMMAND_EXIT;
		}
		return PARSE_RES_CODE_INTERNAL_ERROR;	
	}

}
