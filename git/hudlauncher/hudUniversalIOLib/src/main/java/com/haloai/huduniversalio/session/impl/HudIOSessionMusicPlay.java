package com.haloai.huduniversalio.session.impl;

import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.outputer.IHudOutputer.HudOutputContentType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionMusicCallback;
import com.haloai.huduniversalio.speech.ISpeechParserMusic;
import com.haloai.huduniversalio.speech.ISpeechParserMusic.MusicInfo;

import java.util.List;

public class HudIOSessionMusicPlay extends HudIOSession {
	private static final String MUSIC_SPEECH_PARSE_ERROR="未能找到您说的歌曲，请重试";
	private IHudIOSessionMusicCallback mSessionCallback;

	public HudIOSessionMusicPlay(HudIOController hudIOController) {
		super(hudIOController);
		mIOSessionType = HudIOSessionType.MUSIC;
		mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserMusic();
	}

	@Override
	public void onWakeupWord(String wakeupWord) {

	}

	@Override
	public void onProtocol(String protocol) {
		ISpeechParserMusic speechParserMusic = (ISpeechParserMusic)mSpeechParser;
		int res=speechParserMusic.parseSpeechText(protocol);
		if(res == ISpeechParserMusic.PARSE_RES_CODE_MUSIC_INFO){
			List<MusicInfo> mMusicInfoList=speechParserMusic.getMusicInfoList();
			String tag=speechParserMusic.getMusicTag();
			mSessionCallback.musicSessionSearchSong(mMusicInfoList,tag);
		}else if (res == ISpeechParserMusic.PARSE_RES_CODE_MUSIC_PLAY){
			mSessionCallback.musicSessionPlayMusic();
		}else{
			mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(MUSIC_SPEECH_PARSE_ERROR,HudOutputContentType.CONTENT_TYPE_CUSTOM,true);
		}
		endIOSession(false);
	}

	@Override
	public boolean onRemoteKey(int keyCode) {
		return false;
		
	}

	@Override
	public boolean onGestureKey(int gestureCode) {
		return false;
	}

	@Override
	public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
		return false;
	}


	@Override
	public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
		if (!(sessionCallback instanceof IHudIOSessionMusicCallback)) {
			throw new IllegalArgumentException("Requirst the IHudIOSessionMusicCallback");
		}
		this.mSessionCallback = (IHudIOSessionMusicCallback) sessionCallback;
	}

	@Override
	public void continueSession() {
		
	}

	public void playRandomMusic(){
		mSessionCallback.musicSessionPlayMusic();
		endIOSession(false);
	}

}
