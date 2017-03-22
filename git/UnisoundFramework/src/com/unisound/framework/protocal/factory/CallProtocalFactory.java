package com.unisound.framework.protocal.factory;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.data.source.bean.Contact;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.utils.LogUtils;

public class CallProtocalFactory extends BaseProtocalFactory{
	private static final String TAG = CallProtocalFactory.class.getSimpleName();
	
	private List<Contact> targetContacts;
	private String text;

	public CallProtocalFactory(List<Contact> targetContacts, String text) {
		this.targetContacts = targetContacts;
		this.text = text;
	}
	
	@Override
	public String getProtocal() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("service", UserPreference.PHONE_DOMAIN);
			obj.put("text", text);
			obj.put("offline", 2);
			
			//jduge state
			if(targetContacts.size() > 1){
				obj.put("state", "multi-contacts");
			}else{
				if(targetContacts.get(0).getPhone().length > 1){
					obj.put("state", "multi-phones");
				} else if(targetContacts.get(0).getPhone().length == 1){
					obj.put("state", "one-phone");
				} else{
					obj.put("state", "error");
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
			obj.put("contacts", contactArray);
			return obj.toString();
		} catch (JSONException e) {
			LogUtils.d(TAG, "getProtocal report : parse call protocal error");
			throw new RuntimeException("getProtocal report : parse call protocal error");
		}
	}
}
