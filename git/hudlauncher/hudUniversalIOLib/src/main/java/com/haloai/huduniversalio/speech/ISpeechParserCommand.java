package com.haloai.huduniversalio.speech;

public interface ISpeechParserCommand extends ISpeechParser {
	public static final int PRASE_COMMAND_PLAY=PARSE_RES_CODE_BASE+1;
	public static final int PRASE_COMMAND_PAUSE=PARSE_RES_CODE_BASE+2;
	public static final int PRASE_COMMAND_NEXT=PARSE_RES_CODE_BASE+3;
	public static final int PRASE_COMMAND_PREVI=PARSE_RES_CODE_BASE+4;
	public static final int PRASE_COMMAND_STOP=PARSE_RES_CODE_BASE+5;
	public static final int PRASE_COMMAND_EXIT=PARSE_RES_CODE_BASE+6;

}
