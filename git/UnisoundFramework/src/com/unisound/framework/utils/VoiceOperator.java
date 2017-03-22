package com.unisound.framework.utils;

import android.content.Context;
import android.media.AudioManager;

public class VoiceOperator {
	private static final String TAG = VoiceOperator.class.getSimpleName();
	
	private final int STREAM_MUSIC = AudioManager.STREAM_MUSIC;
	private final int STREAM_ALARM = AudioManager.STREAM_ALARM;
	
	private AudioManager audioManager;
	private boolean isMute = false;
	
	public VoiceOperator(Context mContext){
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void muteVoice(String msg){
		LogUtils.d(TAG, "muteVoice : " + msg + "| ismute : " + isMute);
		
		if(!isMute){
			audioManager.setStreamMute(STREAM_MUSIC, true);
			audioManager.setStreamMute(STREAM_ALARM, true);
			
			isMute = true;
		}
	}
	public void unmuteVoice(String msg){
		LogUtils.d(TAG, "unmuteVoice : " + msg + "| mute : " + isMute);
		
		if(isMute){
			audioManager.setStreamMute(STREAM_MUSIC, false);
			audioManager.setStreamMute(STREAM_ALARM, false);
			
			isMute = false;
		}
	}
}
