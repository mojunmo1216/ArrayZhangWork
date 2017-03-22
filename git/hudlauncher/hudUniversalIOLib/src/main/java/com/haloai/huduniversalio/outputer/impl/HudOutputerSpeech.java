package com.haloai.huduniversalio.outputer.impl;

import android.util.Log;

import com.haloai.huduniversalio.outputer.IHudOutputer;
import com.haloai.huduniversalio.speech.ISpeechEngine;

public class HudOutputerSpeech implements IHudOutputer {
	private ISpeechEngine mSpeechEngine;

	private HudOutputContentType mCurrentSpeechContentType = HudOutputContentType.CONTENT_TYPE_IDLE;
	
	public HudOutputerSpeech(ISpeechEngine speechEngine) {
		mSpeechEngine = speechEngine;
	}

	@Override
	public void output(Object object,HudOutputContentType speechContentType,boolean keepRecognize) {
		if (!(object instanceof String)) {
			throw new IllegalArgumentException("HudOutputSpeech requires the String param");
		}
		if(!canPlay(speechContentType)){
			return;
		}
		String ttsText = (String)object;
		//TODO is or not to keep recognize , if current mode is classic , we should be keep that .
		//TODO if the current mode is one shot , there be false.
		mCurrentSpeechContentType=speechContentType;
		mSpeechEngine.playTTS(ttsText, keepRecognize);
	}
	
	@Override
	public void cancel() {
		mSpeechEngine.stopTTS();
	}
	
	@Override
	public void setOutputContentType2IDLE(){
		mCurrentSpeechContentType = HudOutputContentType.CONTENT_TYPE_IDLE;
	}
	
	private boolean canPlay(HudOutputContentType speechContentType){
		return mCurrentSpeechContentType.compareTo(speechContentType) <= 0;
	}
}
