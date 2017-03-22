package com.haloai.huduniversalio;

import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;


public interface IHudIOControllerListener {

	/*
	 * 一个IOSession开始的回调函数
	 * @return IHudIOSessionCallback Library将用IHudIOSessionCallback的回调实例来更新Session的后续信息
	 */
	public abstract IHudIOSessionCallback onSessionBegin(HudIOSessionType sessionType);
	
	/*
	 * 一个IOSession结束
	 */
	public abstract void onSessionEnd(HudIOSessionType sessionType, boolean isCancelled);

	/*
	 * Inputer可用
	 */
	public abstract void onInputerReady(HudInputerType inputerType);

	/*
	 * Inputer不可用(Out of Service)
	 * @param oosRease 不可用原因，由Inputer自行定义
	 */
	public abstract void onInputerOos(HudInputerType inputerType, int oosReason);


//	public abstract void onWaitingUserInput();
//	public abstract void onOnlyLocalSpeechRecog();
//	public abstract void onStopOnlyLocalSpeechRecog();
}
