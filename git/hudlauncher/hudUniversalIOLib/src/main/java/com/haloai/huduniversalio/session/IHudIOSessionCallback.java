package com.haloai.huduniversalio.session;

import com.haloai.huduniversalio.HudIOConstants.HudInputerType;

public interface IHudIOSessionCallback {
	/* 当输入为Session的无效输入时回调
	 * @param inputType 无效输入的类型，比如语音、手势和遥控
	 * @param input 具体的输入值
	 * @param suggestione 建议的输入值
	 * @return 返回boolean，为true表示要求终止该Session，为false表示可继续Session
	 */
	public abstract boolean onSessionInvalidInput(HudInputerType inputType, String input, String suggestion);
}

