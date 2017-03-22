package com.haloai.huduniversalio.inputer;



public interface IInputerSpeechStatusListener extends IInputerStatusListener {

	enum SpeechEngineErrorCode{
		SPEECH_INIT_ERROR,
		SPEECH_NET_ERROR,
		SPEECH_ASR_TIMEOUT,
		SPEECH_COMPILE_ERROR,
		SPEECH_MIC_CONFLICT_ERROR, 
		SPEECH_NULL_PROTOCAL_ERROR, 
		SPEECH_PROTOCAL_ERROR
	}
	public abstract void onSpeechEngineReady();//语音引擎完成所有的准备工作，包括编译电话本等
	public abstract void onWakeup();//已唤醒，可以开始做收音动画
//	public abstract void onWakeupPrompt(); //已唤醒，但只收到基础唤醒词，未进入one-shot，切换为普通唤醒模式
	public abstract void onSpeechVolumeChanged(int volume);//收音音量变化,用来做收音动画
	public abstract void onSpeechRecognizeStart(); //收音结束，开始识别，可做识别Loading动画
	public abstract void onSpeechTTSBegin();
	public abstract void onSpeechTTSEnd();
	public abstract void onSpeechUnRecogized(String errorMessage);//语音识别失败
	public abstract void onSpeechEngineError(SpeechEngineErrorCode errorCode, String errorMsg);//语音引擎错误，参数为错误原因
	public abstract void onSpeechText(String text);//解析语音内容

}
