package com.unisound.framework.engine.tts;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.unisound.framework.engine.speech.AudioSourceImpl;
import com.unisound.framework.engine.speech.AudioSourceImplPro;
import com.unisound.framework.engine.speech.SpeechEngine;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.pub.SpeechOperator;
import com.unisound.framework.utils.BeepPlayer;
import com.unisound.framework.utils.LogUtils;
import com.unisound.framework.utils.TimeoutTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.SystemClock;

public class TTSEngine {
	private static final String TAG = TTSEngine.class.getSimpleName();
	
	private static final int FRONT_SILENCE = 0;
	private static final int BACK_SILENCE = 200;
	private static final int DEFAULT_VOLUME = 80;
	private static final int DEFAULT_SPEED = 60;
	
	private SpeechEngine speechEngine;
	private BeepPlayer beepPlayer;
	private TimeoutTask vadTimeoutTask;
	private Context mContext;
	private SpeechSynthesizer ttsEngine;
	private SpeechOperator speechOperator;
	private TTSListener ttsListener;
	private boolean isTTSReady = false;
	private boolean keepRecognize = false;
	
	public TTSEngine(Context mContext, SpeechEngine speechEngine, BeepPlayer beepPlayer, SpeechOperator speechOperator){
		this.mContext = mContext;
		this.speechEngine = speechEngine;
		this.beepPlayer = beepPlayer;
		this.speechOperator = speechOperator;
	}
	
	public void setListener(TTSListener ttsListener){
		this.ttsListener = ttsListener;
	}
	
	public void setVadTimeoutTask(TimeoutTask vadTimeoutTask) {
		this.vadTimeoutTask = vadTimeoutTask;
	}
	
	public void initTTSModel() {
		TTSModelOperator ttsModelOperator = new TTSModelOperator();
		ttsModelOperator.setListener(new TTSModeListener() {
			@Override
			public void onFileExist() {
				initTTSEngine();
			}

			@Override
			public void onFileNotExist() {
				ttsListener.onTTSModelMissing();
			}
		});
		ttsModelOperator.copyMode();
	}

	private void initTTSEngine() {
		ttsEngine = new SpeechSynthesizer(mContext, UserPreference.appKey, UserPreference.appSecret);
		ttsEngine.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, UserPreference.mFrontendModel);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, UserPreference.mBackendModel);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_FRONT_SILENCE, FRONT_SILENCE);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_BACK_SILENCE, BACK_SILENCE);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, DEFAULT_VOLUME);
		ttsEngine.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, DEFAULT_SPEED);
		
		if (UserPreference.IS_SAVE_RECORDING_FILE) {
			ttsEngine.setOption(SpeechConstants.TTS_KEY_IS_DEBUG, true);
			ttsEngine.setOption(2015, UserPreference.SAVE_TTS_FILE_PATH);
		}
		
		ttsEngine.setTTSListener(mTTSListener);
		if (UserPreference.IS_AEC_MODE) {
			ttsEngine.setAudioSource(new AudioSourceImplPro());
		} else {
			ttsEngine.setAudioSource(new AudioSourceImpl(null));
		}
		ttsEngine.init("");
	}
	
	private SpeechSynthesizerListener mTTSListener = new SpeechSynthesizerListener() {
		@Override
		public void onEvent(int type) {
			switch(type){
				case SpeechConstants.TTS_EVENT_INIT:
					LogUtils.d(TAG, "TTS_EVENT_INIT");
					isTTSReady = true;
					break;
				case SpeechConstants.TTS_EVENT_PLAYING_START:
					LogUtils.d(TAG, "TTS_EVENT_PLAYING_START");
					ttsListener.onTTSBegin();
					//if not AEC mode, should stop wake up preventing tts content wakes up
					if(!UserPreference.IS_AEC_MODE){
						stopWakeup();
					}
					break;
				case SpeechConstants.TTS_EVENT_PLAYING_END:
					LogUtils.d(TAG, "TTS_EVENT_PLAYING_END");
					ttsListener.onTTSEnd();
					if(keepRecognize){
						startRecognize();
					} else{
						startWakeup();
					}
					break;
				default:
					break;
			}
		}
		
		@Override
		public void onError(int type, String errorMSG) {
			LogUtils.d(TAG, "tts onError : type-" + type + " errorMsg-" + errorMSG);
		}
	};

	private String ttsContent;
	public void playTTS(String ttsContent, Boolean keepRecognize) {
		this.ttsContent = ttsContent;
		this.keepRecognize = keepRecognize;
		
		if(ttsEngine != null){
			if(isTTSReady == false){
				new Thread(new Runnable() {
					@Override
					public void run() {
						SystemClock.sleep(50);
						ttsEngine.playText(TTSEngine.this.ttsContent);
					}
				}).run();
			}else{
				ttsEngine.playText(TTSEngine.this.ttsContent);
			}
		}
	}

	private void startRecognize(){
		if(beepPlayer != null && speechEngine != null){
			LogUtils.d(TAG, "playBeepSound");
			beepPlayer.playBeepSound(UserPreference.beepPath, false, new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {}
			});
			vadTimeoutTask.execute("tts play end & start recognize");
			speechOperator.startRecording("tts end & start recognize");
			speechEngine.startRecognize();
		}
	}

	private void startWakeup() {
		if(speechEngine != null){
			speechEngine.startWakeup(speechEngine.isOneShotMode());
		}
	}

	private void stopWakeup() {
		if(speechEngine != null){
			speechEngine.stopWakeup();
		}
	}
	
	public int getTTSStatus(){
		if(ttsEngine != null){
			return ttsEngine.getStatus();
		}
		LogUtils.e(TAG, "getTTSStatus report : ttsEngine is null .");
		return SpeechConstants.TTS_STATUS_END;
	}

	public void stopTTS() {
		if(ttsEngine != null){
			ttsEngine.cancel();
		}
	}

	public void destory() {
		if(ttsEngine != null){
			ttsEngine.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
		}
	}
}
