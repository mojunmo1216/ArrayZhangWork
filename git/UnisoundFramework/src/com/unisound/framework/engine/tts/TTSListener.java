package com.unisound.framework.engine.tts;

public interface TTSListener {
	public void onTTSBegin();
	public void onTTSEnd();
	public void onTTSModelMissing();
}
