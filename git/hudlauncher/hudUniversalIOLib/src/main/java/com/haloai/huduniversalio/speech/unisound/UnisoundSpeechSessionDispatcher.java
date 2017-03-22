package com.haloai.huduniversalio.speech.unisound;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.R;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.speech.ISpeechSessionDispatcher;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONObject;

public class UnisoundSpeechSessionDispatcher implements ISpeechSessionDispatcher {
	private final static String TAG = "Dispatcher";
	private int MESSAGE_ERROR_TEXT_ID =R.string.tts_error_unrecognize_default;
	private String text;

	@Override
	public HudIOSessionType parseSpeechText(String protocol,boolean isFilter) {

		MESSAGE_ERROR_TEXT_ID = R.string.tts_error_unrecognize_default;
		JSONObject obj = JsonTool.parseToJSONObject(protocol);
		String service = JsonTool.getJsonValue(obj, "service", "");
		text=JsonTool.getJsonValue(obj, "text","");
		String code=JsonTool.getJsonValue(obj,"code","");
		HaloLogger.logI(TAG, "code : " + code);
		for(String string : HudIOConstants.HUD_ARRAYS){
			if(text.equals(string)){
				return HudIOSessionType.DISPATCHER;
			}
		}

		for(String string : HudIOConstants.DISPATCHER_ARRAYS){
			if(text.contains(string)){
				return HudIOSessionType.DISPATCHER;
			}
		}

		if (service.equalsIgnoreCase("cn.yunzhisheng.call")){
			//offline存在代表离线搜索到电话信息，不存在代表云端返回信息不做处理
			String offline=JsonTool.getJsonValue(obj,"offline",null);
			if(offline != null){
				return HudIOSessionType.OUTGOING_CALL;
			}else{
				this.MESSAGE_ERROR_TEXT_ID=R.string.tts_session_contact_no_found;
			}
			
		}
		//音乐
		else if(service.equalsIgnoreCase("cn.yunzhisheng.music")){
			//云端返回music
			if(code.contains("SEARCH_TAG")||code.contains("SEARCH_SONG")||code.contains("SEARCH_ARTIST")||code.contains("SEARCH_RANDOM")){
				if(isFilter){
					if(musicFilter(text)){
						return HudIOSessionType.MUSIC;
					}else {
						return null;
					}
				}
				return HudIOSessionType.MUSIC;
			}else {
				this.MESSAGE_ERROR_TEXT_ID=R.string.tts_session_music_no_found;
			}	
		}else if(text.contains("模拟")||text.contains("我要导航")||text.contains("魔力导航")){
			return HudIOSessionType.SIMULATION_NAVI;
		} else if(isNaviRequest(protocol)){
			if(text.contains("退出导航")||text.contains("恢复导航")||text.contains("取消导航")) {
				return HudIOSessionType.EXIT;
			}
			if(isFilter){
				if(naviFilter(text)){
					return HudIOSessionType.NAVI;
				}else {
					return null;
				}
			}
			return HudIOSessionType.NAVI;
		}else if(text.contains("记录仪")){
			return HudIOSessionType.RECORDER;
		}else if(text.contains("关闭音乐")){
			return HudIOSessionType.COMMAND;
		}else if(text.contains("播放音乐")||text.contains("本地音乐")){
			return HudIOSessionType.MUSIC;
		} else if(service.equalsIgnoreCase("cn.yunzhisheng.setting")){
			 if(text.contains("退出导航")||text.contains("结束导航")){
				return HudIOSessionType.EXIT;
			}else if(text.contains("播放音乐")){
				return HudIOSessionType.MUSIC;
			}
			return HudIOSessionType.COMMAND;
		}//退出导航
		else if(service.equalsIgnoreCase("cn.yunzhisheng.appmgr")){
			if(code.equalsIgnoreCase("APP_EXIT")){
				return HudIOSessionType.EXIT;
			}else if(code.equalsIgnoreCase("APP_LAUNCH")){
				if(text.contains("行车记录仪")){
					return HudIOSessionType.RECORDER;
				}
			}
		}
		return null;
	}

	private boolean isNaviRequest(String protocol) {
		if(protocol.contains("cn.yunzhisheng.map")){
			return true;
		} else if(protocol.contains("cn.yunzhisheng.localsearch")){
			return true;
		}
		return false;
	}

	private boolean naviFilter(String text){
		for(String string : HudIOConstants.NAVI_ARRAYS){
			if(text.contains(string)) return true;
		}
		return false;
	}

	private boolean musicFilter(String text){
		for(String string : HudIOConstants.MUSIC_ARRAYS){
			if(text.contains(string)) return true;
		}
		return false;
	}
	@Override
	public String getErrorMessageText() {
		if(text != null && text.contains("光晕")) MESSAGE_ERROR_TEXT_ID = R.string.tts_error_wake_promat;
		return SpeechResourceManager.getInstanse().getString(MESSAGE_ERROR_TEXT_ID);
	}

}
