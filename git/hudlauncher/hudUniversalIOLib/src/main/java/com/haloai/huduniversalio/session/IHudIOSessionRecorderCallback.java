package com.haloai.huduniversalio.session;

public interface IHudIOSessionRecorderCallback extends IHudIOSessionCallback {
	public enum HudRecorderVideoType {
		WONDERFUL_VIDEO, 
		LOOPING_VIDEO,
		EMERGENCY_VIDEO,
		LOCKED_VIDEO, 
		NULL
	}
	
	public enum HudRecorderCommandType{

		FAST_FORWARD,
		FAST_BACK,
		LOCK_VIDEO,
		DELETE_VIDEO,
		PREV_VIDEO,
		NEXT_VIDEO,
		BACK,
		CONFIRM,
		CANCEL,
		UP_VOLUME,
		DOWN_VOLUME,
		PLAY,
		PAUSE,
		NULL
	}
	
	//open the recorder
	public abstract void onSessionRecorderOpen();
	//browse the video list
	public abstract void onSessionRecorderBrowseVideo(HudRecorderVideoType hudRecorderVideoType);
	//close the recorder
	public abstract void onSessionRecorderClose();

	//command about video
	public abstract void onSessionRecorderCommand(HudRecorderCommandType hudRecorderCommandType);

	public abstract void onSessionRecorderPhonePlay(String path,int index);


}
