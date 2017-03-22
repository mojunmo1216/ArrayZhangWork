package com.haloai.huduniversalio.session.impl;

import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionCommandCallback;
import com.haloai.huduniversalio.session.IHudIOSessionCommandCallback.HudCommandType;
import com.haloai.huduniversalio.speech.ISpeechParserCommand;

public class HudIOSessionCommand extends HudIOSession {
	
	public static final String TAG = "InteractionSessionSetting";

	private IHudIOSessionCommandCallback mSessionCallback;

	public HudIOSessionCommand(HudIOController hudIOController) {
		super(hudIOController);
		mIOSessionType = HudIOSessionType.COMMAND;
		mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserSetting();
	}

	@Override
	public void onWakeupWord(String wakeupWord) {
		
	}

	@Override
	public void onProtocol(String protocol) {
		ISpeechParserCommand speechParserCommand=(ISpeechParserCommand)mSpeechParser;
		int res = speechParserCommand.parseSpeechText(protocol);
		HudCommandType commandType=HudCommandType.ERROR;
		if(res == ISpeechParserCommand.PRASE_COMMAND_PLAY){
			if(HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_PAUSE) HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_USER;
			commandType=HudCommandType.PLAY;
		}else if(res == ISpeechParserCommand.PRASE_COMMAND_PAUSE){
			if(HudIOConstants.MusicPauseStat <= HudIOConstants.OPERAT_MUSIC_USER) HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_USER;
			commandType=HudCommandType.PAUSE;
		}else if(res == ISpeechParserCommand.PRASE_COMMAND_NEXT){
			commandType=HudCommandType.NEXT;
		}else if(res == ISpeechParserCommand.PRASE_COMMAND_PREVI){
			commandType=HudCommandType.PREV;
		}else if(res == ISpeechParserCommand.PRASE_COMMAND_STOP){
			commandType=HudCommandType.STOP;
		}else if(res == ISpeechParserCommand.PRASE_COMMAND_EXIT){
			commandType=HudCommandType.EXIT;
		} else {
			commandType=HudCommandType.ERROR;
		}
		if(commandType!=null)
			mSessionCallback.commandSessionPlay(commandType);
		endIOSession(false);
	}

	public void musicPlayControl(int action){ //0代表播放／暂停   1代表下一首
		switch (action){
			case 0:
				if(HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_PAUSE) {
					HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_USER;
				}else if(HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_PLAY){
					HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_USER;
				}
				mSessionCallback.commandSessionPlay(HudCommandType.ACTION);
				break;
			case 1:
				mSessionCallback.commandSessionPlay(HudCommandType.NEXT);
				break;
			case 2:
				mSessionCallback.commandSessionPlay(HudCommandType.STOP);
				break;
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
		if (!(sessionCallback instanceof IHudIOSessionCommandCallback)) {
			throw new IllegalArgumentException("Requirst the IHudIOSessionCommandCallback");
		}
		this.mSessionCallback = (IHudIOSessionCommandCallback) sessionCallback;
	}

	@Override
	public void continueSession() {
		
	}

}
