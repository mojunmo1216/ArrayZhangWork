package com.haloai.huduniversalio.session.impl;

import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWords;
import com.haloai.huduniversalio.speech.ISpeechEngine;
import com.haloai.huduniversalio.speech.ISpeechParser;

import java.util.ArrayList;


public abstract class HudIOSession {
	
	protected static final int SESSION_STATE_BASE = 0;
	public static final int SESSION_STATE_IDLE = SESSION_STATE_BASE + 0;


	protected HudIOSessionType mIOSessionType;
	protected int mSessionState = SESSION_STATE_IDLE;
	protected double confidentOfRec;
	protected HudIOController mHudIOController;
	protected ISpeechParser mSpeechParser;
	protected ISpeechEngine mSpeechEngine;
	protected SpeechWakeupWords mCurrentSpeechWakeupWords;

	public HudIOSession(HudIOController hudIOController) {
		this.mHudIOController = hudIOController;
		this.mSpeechEngine = hudIOController.getHudSpeechManager().getSpeechEngine();
	}

	public HudIOSessionType getIOSessionType() {
		return mIOSessionType;
	}
	
	public void resumeCurrentWakeupWords() {
		if (mCurrentSpeechWakeupWords != null) {
			this.mSpeechEngine.setWakupWords(mCurrentSpeechWakeupWords.getAllWakeupWords());
		}
	}
	
	//每个Session完成最后一步后调用
	protected void endIOSession(boolean isCancelled) {
		if(mIOSessionType==HudIOSessionType.NAVI||mIOSessionType==HudIOSessionType.EXIT)
			mSpeechEngine.setWakupWords(new ArrayList<String>());
		mSessionState = SESSION_STATE_IDLE;
		mHudIOController.endHudIOSession(mIOSessionType, isCancelled);
	}
	
//	protected int get

	public abstract void continueSession();
	public abstract void setSessionCallback(IHudIOSessionCallback sessionCallback);
	public abstract void onWakeupWord(String wakeupWord);
	public abstract void onProtocol(String protocol);
	public abstract boolean onRemoteKey(int keyCode);
	public abstract boolean onGestureKey(int gestureCode);
	public abstract boolean onGesturePoint(GesturePoint point,PointGestureResult result);
	public int queryGesturePointArea(){
		return -1;
	}//查询点击手势选中区个数,-1为不需要此功能
}
