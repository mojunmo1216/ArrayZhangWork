package com.haloai.huduniversalio.speech;

import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;


public interface ISpeechSessionDispatcher {
	
	public abstract HudIOSessionType parseSpeechText(String speechText,boolean isFilter);
	public abstract String getErrorMessageText();
}
