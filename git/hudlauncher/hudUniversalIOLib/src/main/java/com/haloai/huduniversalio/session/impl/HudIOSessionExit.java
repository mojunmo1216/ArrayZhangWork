package com.haloai.huduniversalio.session.impl;

import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionExitCallback;

public class HudIOSessionExit extends HudIOSession {

	private IHudIOSessionExitCallback mSessionCallback;

	public HudIOSessionExit(HudIOController hudIOController) {
		super(hudIOController);
		this.mIOSessionType=HudIOSessionType.EXIT;
	}

	@Override
	public void continueSession() {

	}

	@Override
	public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
		if (!(sessionCallback instanceof IHudIOSessionExitCallback)) {
			throw new IllegalArgumentException("Requirst the IHudIOSessionNaviCallback");
		}
		mSessionCallback = (IHudIOSessionExitCallback) sessionCallback;
	}

	@Override
	public void onWakeupWord(String wakeupWord) {
	/*	if (mCurrentSpeechWakeupWords != null) {
			int wakeupRes = mCurrentSpeechWakeupWords.getWakeupResult(wakeupWord);
			if (wakeupRes == SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
				//播报提示用户语音输入错误
				this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
						mCurrentSpeechWakeupWords.getPromptText(),HudOutputContentType.CONTENT_TYPE_CUSTOM,true);
			} else if (wakeupRes == SpeechWakeupWordsDialog.WAKEUP_RESULT_CONFIRM) {
				exitSessionConfirm();
			} else if (wakeupRes == SpeechWakeupWordsDialog.WAKEUP_RESULT_BACK) {
				exitSessionCancel();
			}
		}*/
	}

	@Override
	public void onProtocol(String protocol) {
		mSessionCallback.exitSessionConfirm();
	/*	mCurrentSpeechWakeupWords = new SpeechWakeupWordsDialog();
		mSpeechEngine.setWakupWords(mCurrentSpeechWakeupWords.getAllWakeupWords());*/
	}

	@Override
	public boolean onRemoteKey(int keyCode) {
	/*	if(keyCode == HudIOConstants.INPUTER_REMOTER_LEFT){
			mSessionCallback.exitSessionConfirm();
			endIOSession(false);
			return true;
		}else if(keyCode == HudIOConstants.INPUTER_REMOTER_RIGHT){
			mSessionCallback.exitSessionCancel();
			endIOSession(false);
			return true;
		}*/
		return false;
	}

	@Override
	public boolean onGestureKey(int gestureCode) {
	/*	if(type == HudIOConstants.INPUTER_GESTURE_V){
			mSessionCallback.exitSessionConfirm();
			endIOSession(false);
			return true;
		}else if(type == HudIOConstants.INPUTER_GESTURE_SLIDE){
			mSessionCallback.exitSessionCancel();
			endIOSession(false);
			return true;
		}*/
		return false;
	}

	@Override
	public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
		return false;
	}

	@Override
	public int queryGesturePointArea() {
		return 0;
	}

	public void exitSessionConfirm(){
		this.mSessionCallback.exitSessionConfirm();
		endIOSession(false);
	}

/*	public void exitSessionCancel(){
		this.mSessionCallback.exitSessionCancel();
		endIOSession(false);
	}*/
}
