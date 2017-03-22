package com.haloai.huduniversalio.speech;


public interface ISpeechParserFactory {
	public abstract ISpeechParserNavi getParserNavi();
	public abstract ISpeechParserIncomingCall getParserIncomingCall();
	public abstract ISpeechParserOutgoingCall getParserOutgoingCall();
	public abstract ISpeechParserMusic getParserMusic();
	public abstract ISpeechParserRecorder getParserRecord();
	public abstract ISpeechParserCommand getParserSetting();
	public abstract ISpeechParserWechat getParserWechat();
	public abstract ISpeechParserDispatcher getParserDispatcher();
}
