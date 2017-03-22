package com.haloai.huduniversalio.speech;

import java.util.List;

import com.haloai.huduniversalio.HudIOConstants.ContactInfo;


public interface ISpeechParserOutgoingCall extends ISpeechParser {
	public static final int PARSE_RES_CODE_CALL_CONTACT_LIST = PARSE_RES_CODE_BASE + 1;
	
	public abstract List<ContactInfo> getContactListInfo();
}
