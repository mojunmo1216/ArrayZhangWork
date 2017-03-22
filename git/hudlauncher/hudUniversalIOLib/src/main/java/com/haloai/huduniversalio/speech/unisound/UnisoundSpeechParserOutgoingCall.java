package com.haloai.huduniversalio.speech.unisound;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haloai.huduniversalio.HudIOConstants.ContactInfo;
import com.haloai.huduniversalio.speech.ISpeechParserOutgoingCall;
import com.haloai.huduniversalio.utils.JsonTool;

public class UnisoundSpeechParserOutgoingCall implements
		ISpeechParserOutgoingCall {
	private List<ContactInfo> mContactlist=null;
	@Override
	public int parseSpeechText(String speechText) {
		JSONObject obj=JsonTool.parseToJSONObject(speechText);
		if(speechText.contains("contacts")){
			JSONArray contacts=JsonTool.getJsonArray(obj, "contacts");
			Gson gson=new Gson();
			List<ContactInfo> contactList;
			Type type=new TypeToken<ArrayList<ContactInfo>>(){}.getType();
			contactList=gson.fromJson(contacts.toString(), type);
			for(ContactInfo contactInfo:contactList){
				if(contactInfo.phone.size()==0){
					contactList.remove(contactInfo);
				}
			}
			if(contactList.size()>0){
				this.mContactlist=contactList;
				return PARSE_RES_CODE_CALL_CONTACT_LIST;
			}

		}
		return PARSE_RES_CODE_INTERNAL_ERROR;
	}

	@Override
	public List<ContactInfo> getContactListInfo() {	
		return mContactlist;
	}

}

