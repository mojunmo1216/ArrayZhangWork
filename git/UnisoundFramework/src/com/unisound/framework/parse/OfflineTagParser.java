package com.unisound.framework.parse;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unisound.data.source.utils.LogUtils;
import com.unisound.framework.bean.OfflineModel;
import com.unisound.framework.preference.SettingTagsPreference;
import com.unisound.framework.preference.UserPreference;

public class OfflineTagParser {
	private static final String TAG = OfflineTagParser.class.getSimpleName();
	
	private String offlineResult;
	private static final String PHONE_TAG = "Domain_phone_";
	private static final String SETTING_TAG = "Domain_global_setting_";
	private static final String MUSIC_TAG = "Domain_music_";
	private static final String CONFIRM_TAG = "Domain_global_confirm_";
	
	private static final String DEFAULT_DOMAIN = "cn.yunzhisheng.default";
	private static final String SETTING_DEFAULT = "no action";
	
	public OfflineTagParser(String offlineResult){
		this.offlineResult = offlineResult;
	}
	
	private String getDomain(){
		if(offlineResult.contains(PHONE_TAG)){
			return UserPreference.PHONE_DOMAIN;
		} else if(offlineResult.contains(SETTING_TAG)){
			return UserPreference.SETTING_DOMAIN;
		} else if(offlineResult.contains(MUSIC_TAG)){
			return UserPreference.SETTING_DOMAIN;
		} else if(offlineResult.contains(CONFIRM_TAG)){
			return UserPreference.SETTING_DOMAIN;
		}
		LogUtils.e(TAG, "getDomain report : no match tag ! return default domain..");
		return DEFAULT_DOMAIN;
	}
	
	private String getName(){
		String contactName = matchFind(
				"<Domain_phone_contact_slot_>([^<\\/Domain_phone_contact_slot_>]*)",
				offlineResult);
		return contactName;
	}
	
	private String getText() {
		return offlineResult.replaceAll("<[a-zA-Z0-9_/]*>| ", "").trim();
	}
	
	public OfflineModel getOfflineMode(){
		OfflineModel offlineModel = new OfflineModel();
		offlineModel.setDomain(getDomain());
		offlineModel.setText(getText());
		if(UserPreference.PHONE_DOMAIN.equals(getDomain())){
			offlineModel.setName(getName());
		} else if(UserPreference.SETTING_DOMAIN.equals(getDomain())){
			offlineModel.setIntent(getSettingMap());
		}
		return offlineModel;
	}
	
	private String getSettingMap() {
		Map<String, String> settingMap = SettingTagsPreference.getInstance().getSettingMap();
		if(settingMap != null && settingMap.size() > 0){
			for(Entry<String, String> entry : settingMap.entrySet()){
				if(offlineResult.contains(entry.getKey())){
					return entry.getValue();
				}
			}
		}
		return SETTING_DEFAULT;
	}

	private String matchFind(String offlineResult, String result) {
		Pattern p = Pattern.compile(offlineResult);
		Matcher m = p.matcher(result);
		if (m.find()) {
			result = m.group(1);
			result = result.replace(" ", "");
		}
		return result;
	}
}
