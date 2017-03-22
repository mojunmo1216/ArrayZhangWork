package com.unisound.framework.session;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.data.source.bean.Contact;
import com.unisound.framework.parse.OnlineProtocalParser;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.utils.LogUtils;

public class SessionFilter {
	private static final String TAG = SessionFilter.class.getSimpleName();
	
	private static final String CALL_SESSION = UserPreference.PHONE_DOMAIN;

	private List<String> sessionList = new ArrayList<String>();
	private List<Contact> contactsList;
	
	public SessionFilter(){
		init();
	}
	
	private void init() {
		sessionList.add(CALL_SESSION);
	}
	
	public void setContactList(List<Contact> contactsList) {
		this.contactsList = contactsList;
	}
	
	public boolean isSpecific(String domain){
		for(String specificSession : sessionList){
			if(specificSession.equals(domain)){
				LogUtils.d(TAG, "search specific session : " + domain + " return true");
				return true;
			}
		}
		LogUtils.d(TAG, "none specific session : " + domain + " return false");
		return false;
	}
	
	public String handleProtocal(OnlineProtocalParser onlineParser){
		String domain = onlineParser.getDomain();
		String protocal = onlineParser.getOnlineProtocal();
		String text = getText(protocal);
		BaseSession session = null;
		if(CALL_SESSION.equals(domain)){
			session = new CallSession(contactsList, text);
			String realProtocal = session.handleProtocal(protocal);
			return realProtocal;
		}
		LogUtils.e(TAG, "error domain : " + domain);
		throw new RuntimeException("error domain ! why do you handle it ?");
	}

	private String getText(String protocal) {
		if(protocal != null && !protocal.equals("")){
			String text = null;
			try {
				JSONObject onLineProtocalJson = new JSONObject(protocal);
				text = (String) onLineProtocalJson.get("text");
				if(text.contains(UserPreference.DEFAULT_WAKEUP_WORD)){
					text = text.replace(UserPreference.DEFAULT_WAKEUP_WORD, "");
				}
				return text;
			} catch (JSONException e) {
				LogUtils.e(TAG, "getText report, json parser error : " + e.toString());
				throw new RuntimeException("getText report, json parser error");
			}
		}else {
			LogUtils.e(TAG, "getText report : protocal is null");
			throw new RuntimeException("getText report : protocal is null");
		}
	}
}
