package com.haloai.huduniversalio.session;

import com.haloai.huduniversalio.R;

import java.util.List;

public interface IHudIOSessionOutgoingCallCallback extends IHudIOSessionCallback {
	public static final int MESSAGE_ERROR_EMPTY_PHONE_ID= R.string.tts_session_contact_no_found;
	public abstract void onSessionOutgoingCallContactList(List<String> contactList,int pageCount);
	public abstract void onSessionOutgoingCallNextPage();
	public abstract void onSessionOutgoingCallPrevPage();	
	public abstract void onSessionOutgoingCalling(String name ,String phone,boolean isHud);
	public abstract void onSessionOutgoingCallPhoneNumList(String name,List<String> phoneList);
	public abstract void onSessionOutgoingCallEnd(boolean isCancelled);



}
