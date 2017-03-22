package com.haloai.huduniversalio.speech;

public interface ISpeechParserIncomingCall extends ISpeechParser {

	public static final int PARSE_RES_CODE_ANSWERED = PARSE_RES_CODE_BASE + 1;
	public static final int PARSE_RES_CODE_HANGUP = PARSE_RES_CODE_BASE + 2;
	
}
