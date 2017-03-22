package com.haloai.huduniversalio.session.impl;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionHangUpCallback;

public class HudIOSessionHangUp extends HudIOSession{

	private IHudIOSessionHangUpCallback mSessionCallback;
	
	public HudIOSessionHangUp(HudIOController hudIOController) {
		super(hudIOController);
		mIOSessionType = HudIOSessionType.HANGUP;
	}

	@Override
	public void continueSession() {
		
	}

	@Override
	public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
		if (!(sessionCallback instanceof IHudIOSessionHangUpCallback)) {
			throw new IllegalArgumentException("Requirst the IHudIOSessionHangUpCallback");
		}
		this.mSessionCallback=(IHudIOSessionHangUpCallback)sessionCallback;

	}

	@Override
	public void onWakeupWord(String wakeupWord) {
		
	}

	@Override
	public void onProtocol(String protocol) {
		HaloLogger.logE("speech_info","HangUp:"+protocol);
		if(protocol.equalsIgnoreCase(HudIOConstants.ACTION_BTCALL_OVER)){
			mSessionCallback.onSessionCallHangUp();
			endIOSession(false);
		}
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

}
