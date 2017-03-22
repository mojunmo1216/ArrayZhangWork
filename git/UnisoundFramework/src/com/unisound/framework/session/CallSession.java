package com.unisound.framework.session;

import java.util.ArrayList;
import java.util.List;

import com.unisound.data.source.bean.Contact;
import com.unisound.framework.parse.CallOnlineProtocalParser;
import com.unisound.framework.protocal.factory.CallProtocalFactory;
import com.unisound.framework.utils.LogUtils;
import com.unisound.framework.utils.PinyinUtils;

public class CallSession extends BaseSession{
	private static final String TAG = CallSession.class.getSimpleName();
	
	private List<Contact> contactsList;
	private String text;
	
	public CallSession(List<Contact> contactsList, String text) {
		this.contactsList = contactsList;
		this.text = text;
	}

	@Override
	protected String handleProtocal(String protocal) {
		LogUtils.d(TAG, "handleProtocal : " + protocal);
		String name = getUserName(protocal);
		List<Contact> targetContacts = new ArrayList<Contact>();
		
		//name is in contact list
		if(name != null && isSpecificName(name, targetContacts)){
			CallProtocalFactory callProtocalFactory = new CallProtocalFactory(targetContacts, text);
			return callProtocalFactory.getProtocal();
		}
		//name is not in contact list
		else{
			LogUtils.d(TAG, "return origin protocal");
			return protocal;
		}
	}

	private boolean isSpecificName(String name, List<Contact> targetContacts) {
		if (targetContacts == null || targetContacts.size() == 0) {
			LogUtils.d(TAG, "There is no contacts");
			return false;
		}
		
		//handle data
		for(Contact contactItem : contactsList){
			if(contactItem.getPyName().equals(PinyinUtils.getPinYin(name))){
				LogUtils.d(TAG, "find match pinyin name : " + PinyinUtils.getPinYin(name) + " " + contactItem.getName());
				targetContacts.add(contactItem);
			}
		}
		
		if(targetContacts.size() > 0){
			return true;
		}else {
			LogUtils.d(TAG, "no match name...");
			return false;
		}
	}

	private String getUserName(String protocal) {
		CallOnlineProtocalParser callOnlineProtocalParser = new CallOnlineProtocalParser(protocal);
		return callOnlineProtocalParser.getName();
	}
}
