package com.haloai.huduniversalio.speech.unisound;

import com.haloai.huduniversalio.speech.ISpeechParserDispatcher;
import com.haloai.huduniversalio.speech.ISpeechParserRecorder;
import com.haloai.huduniversalio.speech.ISpeechParserFactory;
import com.haloai.huduniversalio.speech.ISpeechParserIncomingCall;
import com.haloai.huduniversalio.speech.ISpeechParserMusic;
import com.haloai.huduniversalio.speech.ISpeechParserNavi;
import com.haloai.huduniversalio.speech.ISpeechParserOutgoingCall;
import com.haloai.huduniversalio.speech.ISpeechParserCommand;
import com.haloai.huduniversalio.speech.ISpeechParserWechat;

public class UnisoundSpeechParserFactory implements ISpeechParserFactory {

	public UnisoundSpeechParserFactory() {
		
	}

	@Override
	public ISpeechParserNavi getParserNavi() {
		return new UnisoundSpeechParserNavi();
	}

	@Override
	public ISpeechParserIncomingCall getParserIncomingCall() {
		return new UnisoundSpeechParserIncomingCall();
	}

	@Override
	public ISpeechParserOutgoingCall getParserOutgoingCall() {
		return new UnisoundSpeechParserOutgoingCall();
	}

	@Override
	public ISpeechParserMusic getParserMusic() {
		return new UnisoundSpeechParserMusic();
	}

	@Override
	public ISpeechParserRecorder getParserRecord() {
		return new UnisoundSpeechParserRecorder();
	}

	@Override
	public ISpeechParserCommand getParserSetting() {
		return new UnisoundSpeechParserCommand();
	}

	@Override
	public ISpeechParserWechat getParserWechat() {
		return new UnisoundSpeechParserWechat();
	}

	@Override
	public ISpeechParserDispatcher getParserDispatcher() {
		return new UnisoundSpeechParserDispatcher();
	}

}
