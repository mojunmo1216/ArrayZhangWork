package com.haloai.huduniversalio.session;


public interface IHudIOSessionCommandCallback extends IHudIOSessionCallback {
	public enum BrightnessChangeAction {
		MAX,
		MIN,
		UP,
		DOWN
	}
	
	public enum HudCommandType{
		ERROR,
		PLAY,
		PAUSE,
		STOP,
		NEXT,
		PREV,
		EXIT,
		ACTION
	}

    public abstract void commandSessionPlay(HudCommandType commandType);
}
