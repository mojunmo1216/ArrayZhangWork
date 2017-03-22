package com.haloai.huduniversalio.speech;

public interface ISpeechParserNavi extends ISpeechParser{

	public static final int PARSE_RES_CODE_NAVI_DESTINATION_NAME = PARSE_RES_CODE_BASE + 1;

	public class DestinationInfo {
		public String city;
		public String name;
	}
	public abstract DestinationInfo getParseResultDestinationInfo();

	public abstract String getParseResultRoundCategory();
}
