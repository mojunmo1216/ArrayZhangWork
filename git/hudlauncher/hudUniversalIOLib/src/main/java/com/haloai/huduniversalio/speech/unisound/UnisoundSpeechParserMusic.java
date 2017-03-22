package com.haloai.huduniversalio.speech.unisound;

import com.haloai.huduniversalio.speech.ISpeechParserMusic;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnisoundSpeechParserMusic implements ISpeechParserMusic {

	private List<MusicInfo> mMusicInfoList;
	private String tag;
	@Override
	public int parseSpeechText(String speechText) {
	
		JSONObject obj = JsonTool.parseToJSONObject(speechText);
		String service = JsonTool.getJsonValue(obj, "service", "");
		String text=JsonTool.getJsonValue(obj,"text");
		if(service.equalsIgnoreCase("cn.yunzhisheng.setting")){
			if(text.contains("播放音乐")||text.contains("本地音乐")){
				return PARSE_RES_CODE_MUSIC_PLAY;
			}
		}
		JSONObject data = JsonTool.getJSONObject(obj, "data");
		JSONObject result = JsonTool.getJSONObject(data, "result");
		String code=JsonTool.getJsonValue(obj,"code","SEARCH_SONG");
		int errorCode=JsonTool.getJsonValue(result, "errorCode", -1);
		JSONArray musicinfo = JsonTool.getJsonArray(result, "musicinfo");
		if(errorCode==0&&musicinfo.length()>0){
			String tag;
			mMusicInfoList=new ArrayList<>();
			for(int i=0;i<musicinfo.length();i++){
				MusicInfo musicInfo=new MusicInfo();
	        	JSONObject object=JsonTool.getJSONObject(musicinfo, i);
	        	musicInfo.song_url=JsonTool.getJsonValue(object, "url");
	        	musicInfo.title=JsonTool.getJsonValue(object, "title");
	        	musicInfo.artist=JsonTool.getJsonValue(object, "artist");
	        	musicInfo.imag_url=JsonTool.getJsonValue(object, "hdImgUrl");
				musicInfo.duration=JsonTool.getJsonValue(object,"duration",0);
				mMusicInfoList.add(musicInfo);
	        }
			if(text.contains("播放音乐")&&code.contains("SEARCH_RANDOM")){
				tag="PLAY_MUSIC";
			}else{
				tag="SONG_LIST";
			}
			this.tag=tag;
			return PARSE_RES_CODE_MUSIC_INFO;
		}

		return PARSE_RES_CODE_INTERNAL_ERROR;
	}

	@Override
	public List<MusicInfo> getMusicInfoList() {
		return mMusicInfoList;
	}
	public String getMusicTag(){
		return tag;
	}
}
