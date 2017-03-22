package com.unisound.framework.pub;

public interface SpeechListener {
	public void onInitEngineDone();
	public void onError(String errorReason);
	
	public void onWakeupWordsReady();
	
	public abstract void onWakeup();
	public void onWakeupPrompt();
	public void onWakeupCommand(String wakeupCmd);

	public void onRecognizeStart();
	
	public void onProtocol(String protocol);
	
	public void onVolumeChanged(int volume);

	public void onTTSBegin();
	public void onTTSEnd();
	public void onTTSModelMissing();

	public void onEngineCompileStart();
	public void onEngineCompileFinished();
}
