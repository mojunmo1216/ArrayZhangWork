package com.unisound.framework.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.unisound.data.source.bean.Contact;
import com.unisound.data.source.utils.LogUtils;
import com.unisound.data.source.utils.PinyinUtils;
import com.unisound.framework.bean.OfflineModel;
import com.unisound.framework.data.contact.ContactsListener;
import com.unisound.framework.data.contact.ContactsOperator;
import com.unisound.framework.preference.UserPreference;

public class Offline2OnlineParser {
	private static final String TAG = Offline2OnlineParser.class.getSimpleName();
	
	private Context mContext;
	private String protocal;

	public Offline2OnlineParser(Context mContext, OfflineModel offlineMode) {
		LogUtils.d(TAG, "Offline2OnlineParser : " + offlineMode.toString());
		this.mContext = mContext;
		parserOfflineModel(offlineMode);
	}
	
	private void parserOfflineModel(OfflineModel offlineMode) {
		JSONObject jsonProtocal = new JSONObject();
		
		if(UserPreference.PHONE_DOMAIN.equals(offlineMode.getDomain())){
			handlePhoneDomain(offlineMode, jsonProtocal);
		} else if(UserPreference.SETTING_DOMAIN.equals(offlineMode.getDomain())){
			handleSettingDomain(offlineMode, jsonProtocal);
		}
	}

	private void handleSettingDomain(OfflineModel offlineMode, JSONObject jsonProtocal) {
		this.offlineMode = offlineMode;
		this.offlineJsonProtocal = jsonProtocal;
		
		try {
			//add offline tag
			jsonProtocal.put("offline", 1);
			
			//add service
			jsonProtocal.put("service", UserPreference.SETTING_DOMAIN);
			
			//add text
			jsonProtocal.put("text", offlineMode.getText());
			
			//add semantic
			JSONObject semanticObj = new JSONObject(offlineMode.getIntent());
			jsonProtocal.put("semantic", semanticObj);
			
			protocal = jsonProtocal.toString().replace("\\", "");
			LogUtils.d(TAG, "protocal is : " + protocal);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject offlineJsonProtocal;
	private OfflineModel offlineMode;
	private void handlePhoneDomain(OfflineModel offlineMode, JSONObject jsonProtocal) {
		this.offlineMode = offlineMode;
		this.offlineJsonProtocal = jsonProtocal;
		try {
			//add offline value
			jsonProtocal.put("offline", 1);
			//add service
			jsonProtocal.put("service", UserPreference.PHONE_DOMAIN);
			
			//add state
			ContactsOperator.getInstance(mContext, new ContactsListener() {
				@Override
				public void onSearchEnd(List<Contact> contacts) {
					try {
						List<Contact> targetContacts = new ArrayList<Contact>();
						for(Contact contactItem : contacts){
							if(contactItem.getPyName() != null && contactItem.getPyName().equals(
									PinyinUtils.getPinYin(Offline2OnlineParser.this.offlineMode.getName()))){
								targetContacts.add(contactItem);
							}
						}
						if(targetContacts.size() > 1){
							offlineJsonProtocal.put("state", "multi-contacts");
						} else if(targetContacts.size() == 1){
							if(targetContacts.get(0).getPhone().length > 1){
								offlineJsonProtocal.put("state", "multi-phones");
							} else if(targetContacts.get(0).getPhone().length == 1){
								offlineJsonProtocal.put("state", "one-phone");
							} else{
								offlineJsonProtocal.put("state", "error");
							}
						}
						
						//add data
						JSONArray contactArray = new JSONArray();
						for(Contact contactItem : targetContacts){
							JSONObject jsonContact = new JSONObject();
							JSONArray phoneArray = new JSONArray();
							for(String phone : contactItem.getPhone()){
								phoneArray.put(phone);
							}
							jsonContact.put("phone", phoneArray);
							jsonContact.put("name", contactItem.getName());
							contactArray.put(jsonContact);
						}
						offlineJsonProtocal.put("contacts", contactArray);
						
						//add text
						offlineJsonProtocal.put("text", Offline2OnlineParser.this.offlineMode.getText());
						
						protocal = offlineJsonProtocal.toString();
						LogUtils.d(TAG, "phone domain protocal : " + protocal);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onSearchBegin() {}
			}).startSearchContacts();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getProtocal(){
		return protocal;
	}

}
