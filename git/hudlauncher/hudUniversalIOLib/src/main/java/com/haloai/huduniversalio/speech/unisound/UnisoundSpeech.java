package com.haloai.huduniversalio.speech.unisound;

import android.content.Context;
import android.util.Log;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.speech.ISpeechEngine;
import com.unisound.framework.pub.SpeechListener;
import com.unisound.framework.pub.SpeechOperator;

import java.util.ArrayList;
import java.util.List;

// * 识别结果查询优先顺序： result->post->{sem,nbest_sem}, result->post->nbest, rec
public class UnisoundSpeech implements ISpeechEngine {
	public static final String SERVICE_SETTING = "cn.yunzhisheng.setting";
	public static String TAG="UnisoundSpeech";
	private ISpeechNotifier mSpeechNotifier;
	private SpeechOperator speechOperator;

	@Override
	public void init(Context context, ISpeechNotifier speechNotifier, String engineResPath, String normalWakeupWord) {
		this.mSpeechNotifier = speechNotifier;
		
		speechOperator = SpeechOperator.getInstance();
		speechOperator.setNormalWakeupWord(normalWakeupWord);
		speechOperator.setCurrentCity("深圳市");
		speechOperator.setListener(mListener);
		speechOperator.init(context);
	}

	@Override
	public void destroy() {
		if(speechOperator != null){
			speechOperator.destroy();
		}
	}
	
	@Override
	public boolean isEngineReady() {
		if(speechOperator != null){
			return speechOperator.isEngineReady();
		}
		return false;
	}

	@Override
	public void playTTS(String ttsContent, Boolean keepRecognize) {
		if(speechOperator != null){
			speechOperator.playTTS(ttsContent, keepRecognize);
		}
	}

	@Override
	public void stopTTS() {
		if(speechOperator != null){
			speechOperator.stopTTS();
		}
	}

	@Override
	public void setWakupWords(List<String> wakupWords) {
		Log.e("speech_info","wakupWords : "+wakupWords);
		if(speechOperator != null){
			ArrayList<String> wakeups = new ArrayList<String>();
			wakeups.addAll(wakupWords);
			speechOperator.setWakupWords(wakeups);
		}
	}

	@Override
	public void pauseSpeech() {
		if(speechOperator != null){
			speechOperator.pauseSpeech();
		}
	}

	@Override
	public void resumeSpeech() {
		if(speechOperator != null){
			speechOperator.resumeSpeech();
		}
	}

	@Override
	public void startRecognizeManually() {
		if(speechOperator != null){
			speechOperator.startRecognizeManually();//2*1000
		}
	}

	@Override
	public void cancelRecognizeManually() {
		HaloLogger.logE("speech_info","cancelRecognizeManually");
		if(speechOperator != null){
			speechOperator.cancelRecognizeManually();
		}
	}
	
	@Override
	public void setWakeupMode(String wakeupMode) {
		if(speechOperator != null){
			speechOperator.setWakeupMode(wakeupMode);
		}
	}

	@Override
	public boolean isOneShotMode() {
		if(speechOperator != null){
			return speechOperator.isOneShotMode();
		}
		return false;
	}

	@Override
	public void startCompileManual() {
		if(speechOperator != null){
			speechOperator.startCompileManually();
		}
	}


	private SpeechListener mListener = new SpeechListener() {
		
		@Override
		public void onWakeupPrompt() {
			HaloLogger.logE("speech_info","onWakeupPrompt");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onWakeupPrompt();
			}
		}
		
		@Override
		public void onWakeupCommand(String wakeupCmd) {
			HaloLogger.logE("speech_info","onWakeupCommand : "+wakeupCmd);
			if(mSpeechNotifier != null){
				mSpeechNotifier.onWakeupCommand(wakeupCmd);
			}
		}
		
		@Override
		public void onVolumeChanged(int volume) {
			if(mSpeechNotifier != null){
				mSpeechNotifier.onVolumeChanged(volume);
			}
		}
		
		@Override
		public void onTTSEnd() {
			if(mSpeechNotifier != null){
				mSpeechNotifier.onTTSEnd();
			}
		}
		
		@Override
		public void onTTSBegin() {
			if(mSpeechNotifier != null){
				mSpeechNotifier.onTTSBegin();
			}
		}
		
		public void onRecognizeStart() {
			HaloLogger.logE("speech_info","onRecognizeStart");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onRecognizeStart();
			}
		}
		
		@Override
		public void onProtocol(String protocol) {
			HaloLogger.logE("speech_info","Protocol:"+protocol);
			if(mSpeechNotifier != null){
				mSpeechNotifier.onProtocol(protocol);
			}
		}
				
		@Override
		public void onError(String errorReason) {
			HaloLogger.logE("speech_info","onError : "+errorReason);
			if(mSpeechNotifier != null){
				mSpeechNotifier.onError(errorReason);
			}
		}

		@Override
		public void onTTSModelMissing() {
			if(mSpeechNotifier != null){
			}
		}

		@Override
		public void onInitEngineDone() {
			HaloLogger.logE("speech_info","onInitEngineDone");
//			setWakeupMode("normal");
			Log.e("speech_info","is oneshot : "+isOneShotMode());
			if(mSpeechNotifier != null){
				mSpeechNotifier.onInitEngineDone();
			}
		}

		@Override
		public void onWakeup() {
			HaloLogger.logE("speech_info","onWakeup");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onWakeup();
			}
		}

		@Override
		public void onWakeupWordsReady() {
			HaloLogger.logE("speech_info","onWakeupWordsReady");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onWakeupWordsReady();
			}
		}

		@Override
		public void onEngineCompileFinished() {
			HaloLogger.logE("speech_info","onEngineCompileFinished");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onEngineCompileFinished();
			}
		}

		@Override
		public void onEngineCompileStart() {
			HaloLogger.logE("speech_info","onEngineCompileStart");
			if(mSpeechNotifier != null){
				mSpeechNotifier.onEngineCompileStart();
			}
		}

	};
}
