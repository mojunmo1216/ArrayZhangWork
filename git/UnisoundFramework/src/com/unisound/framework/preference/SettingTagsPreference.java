package com.unisound.framework.preference;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingTagsPreference {
	private static SettingTagsPreference settingTagsPreference;
	private static Map<String, String> settingMap;
	
	public static SettingTagsPreference getInstance(){
		if(settingTagsPreference == null){
			synchronized (SettingTagsPreference.class) {
				if(settingTagsPreference == null){
					settingTagsPreference = new SettingTagsPreference();
					initSettingMap();
				}
			}
		}
		return settingTagsPreference;
	}

	private static void initSettingMap() {
		settingMap = new HashMap<String, String>();
		
		// bluetooth
		settingMap.put("Domain_global_setting_bluetooth_open_",
				creatSettingSementicObjc("ACT_OPEN", "OBJ_BLUETOOTH"));
		settingMap.put("Domain_global_setting_bluetooth_close_",
				creatSettingSementicObjc("ACT_CLOSE", "OBJ_BLUETOOTH"));
		settingMap.put("Domain_global_setting_bluetooth_key_match_close_",
				creatSettingSementicObjc("ACT_DISCONNECTED", "OBJ_BLUETOOTH"));
		settingMap.put("Domain_global_setting_bluetooth_key_match_",
				creatSettingSementicObjc("ACT_MATCH", "OBJ_BLUETOOTH"));

		// wifi
		settingMap.put("Domain_global_setting_wifi_open_",
				creatSettingSementicObjc("ACT_OPEN", "OBJ_WIFI"));
		settingMap.put("Domain_global_setting_wifi_close_",
				creatSettingSementicObjc("ACT_CLOSE", "OBJ_WIFI"));

		// volume
		settingMap.put("Domain_global_setting_volume_down_",
				creatSettingSementicObjc("ACT_DECREASE", "OBJ_VOLUMN"));
		settingMap.put("Domain_global_setting_volume_up_",
				creatSettingSementicObjc("ACT_INCREASE", "OBJ_VOLUMN"));
		settingMap.put("Domain_global_setting_volume_max_",
				creatSettingSementicObjc("ACT_MAX", "OBJ_VOLUMN"));
		settingMap.put("Domain_global_setting_volume_mute_open_",
				creatSettingSementicObjc("ACT_OPEN", "OBJ_MODEL_MUTE"));
		settingMap.put("Domain_global_setting_volume_mute_close_",
				creatSettingSementicObjc("ACT_CLOSE", "OBJ_MODEL_MUTE"));
		
		// music
		settingMap.put("Domain_global_setting_play_previous_",
				creatSettingSementicObjc("music_previous", "Music"));
		settingMap.put("Domain_global_setting_play_next_",
				creatSettingSementicObjc("music_next", "Music"));
		settingMap.put("Domain_global_setting_play_continue_",
				creatSettingSementicObjc("music_play", "Music"));
		settingMap.put("Domain_global_setting_play_stop_",
				creatSettingSementicObjc("music_stop", "Music"));
		settingMap.put("Domain_global_setting_play_pause_",
				creatSettingSementicObjc("music_pause", "Music"));
		settingMap.put("Domain_global_setting_lyric_open_",
				creatSettingSementicObjc("ACT_OPEN_LYRIC", "Music"));
		settingMap.put("Domain_global_setting_lyric_close_",
				creatSettingSementicObjc("ACT_CLOSE_LYRIC", "Music"));
		settingMap.put("Domain_global_setting_play_fullcircle_",
				creatSettingSementicObjc("ACT_PLAY", "Music"));
		settingMap.put("Domain_music_play_", 
				creatSettingSementicObjc("ACT_PLAY", "Music"));
		settingMap
				.put("Domain_global_setting_play_order_",
						creatSettingSementicObjc("ACT_PLAY",
								"OBJ_MUSIC_ORDER_PLAYBACK"));
		settingMap.put(
				"Domain_global_setting_play_random_",
				creatSettingSementicObjc("ACT_PLAY",
						"OBJ_MUSIC_SHUFFLE_PLAYBACK"));
		settingMap.put("Domain_global_setting_play_singlecircle_",
				creatSettingSementicObjc("ACT_PLAY", "OBJ_MUSIC_SINGLE_CYCLE"));

		// setting
		settingMap.put("Domain_global_setting_backto_mainscreen_",
				creatSettingSementicObjc("ACT_SET", "OBJ_HOMEPAGE"));
		
		//navigation
		settingMap.put("Domain_global_setting_navigation_start_",
				creatSettingSementicObjc("start_navigation", "Navi"));
		settingMap.put("Domain_global_setting_navigation_stop_",
				creatSettingSementicObjc("cancel_navigation", "Navi"));
		settingMap.put("Domain_global_setting_simulation_navigation_start_",
				creatSettingSementicObjc("start_navigation_simulation", "Navi"));
		settingMap.put("Domain_global_setting_simulation_navigation_stop_",
				creatSettingSementicObjc("cancel_navigation_simulation", "Navi"));
		
		//dvr
		settingMap.put("Domain_global_setting_dvr_start_",
				creatSettingSementicObjc("start", "CAR_DVR"));
		 settingMap.put("Domain_global_setting_dvr_stop_",
					creatSettingSementicObjc("stop", "CAR_DVR"));
		 settingMap.put("Domain_global_setting_dvr_photo_",
					creatSettingSementicObjc("photo", "CAR_DVR"));
		 settingMap.put("Domain_global_setting_dvr_video_",
					creatSettingSementicObjc("video", "CAR_DVR"));
		 settingMap.put("Domain_global_setting_dvr_play_",
					creatSettingSementicObjc("play", "CAR_DVR"));
		settingMap.put("Domain_global_setting_dvr_recall_",
				creatSettingSementicObjc("recall", "CAR_DVR"));
		
		//order
		settingMap.put("Domain_global_setting_navi_",
				creatSettingSementicObjc("navi", "Order"));
		settingMap.put("Domain_global_setting_music_",
				creatSettingSementicObjc("music", "Order"));
		settingMap.put("Domain_global_setting_msg_",
				creatSettingSementicObjc("msg", "Order"));
		settingMap.put("Domain_global_setting_wechat_",
				creatSettingSementicObjc("wechat", "Order"));
		settingMap.put("Domain_global_setting_setting_",
				creatSettingSementicObjc("setting", "Order"));
		
		//confirm_on && confirm_no
		settingMap.put("Domain_global_confirm_ok",
				creatSettingSementicObjcByNameValue("confirm", "OK"));
		settingMap.put("Domain_global_confirm_no",
				creatSettingSementicObjcByNameValue("confirm", "CANCEL"));
		settingMap.put("Domain_global_setting_cancel_",
				creatSettingSementicObjcByNameValue("confirm", "CANCEL"));
	}
	
	private static String creatSettingSementicObjc(String operator,
			String operands) {
		JSONObject sementicObjc = new JSONObject();
		JSONObject intentobj = new JSONObject();
		try {
			intentobj.put("operator", operator);
			if (operands != null) {
				intentobj.put("operands", operands);
			}
			sementicObjc.put("intent", intentobj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sementicObjc.toString();
	}
	
	private static String creatSettingSementicObjcByNameValue(String name, String value){
		JSONObject sementicObjc = new JSONObject();
		JSONObject intentobj = new JSONObject();
		try {
			intentobj.put(name, value);
			sementicObjc.put("intent", intentobj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sementicObjc.toString();
	}
	
	public Map<String, String> getSettingMap(){
		return settingMap;
	}
}
