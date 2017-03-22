package com.haloai.huduniversalio.speech;

import java.util.List;

import android.content.Context;


public interface ISpeechEngine {

	public abstract void init(Context context, ISpeechNotifier speechNotifier, String engineResPath, String normalWakeupWord);
	public abstract void destroy();
	public abstract void setWakupWords(List<String> wakupWords);//不超过十个,只包括命令唤醒词，基础唤醒词引擎内部自动添加
	/*
	 * keepRecognize表示TTS播放后，是否马上开识别（常用于聊天时)
	 * !!!TODO 目前keepRecognize=true有问题，为true时播放beep音会出错
	 */
	public abstract void playTTS(String ttsContent,Boolean keepRecognize);
	public abstract void stopTTS();
	public abstract void pauseSpeech(); //暂停语音唤醒
	public abstract void resumeSpeech();//恢复语音唤醒
	public abstract void startRecognizeManually();
	public abstract void cancelRecognizeManually();
	public abstract boolean isEngineReady();
	public abstract void setWakeupMode(String wakeupMode);//可识别参数: "oneshot" 或 "normal"
	public abstract boolean isOneShotMode();
	public abstract void startCompileManual();//手动编译引擎(主要是编译电话本)


	public interface ISpeechNotifier {
		public abstract void onInitEngineDone();
		public abstract void onWakeup(); //收到基础唤醒词，可以开始做收音动画
		public abstract void onWakeupPrompt(); //收到基础唤醒词，未进入one-shot，切换为普通唤醒模式
		public abstract void onWakeupCommand(String wakeupCmd); //收到命令唤醒词
		public abstract void onWakeupWordsReady();//设置命令唤醒词成功回调
		public abstract void onRecognizeStart(); //收音结束，开始识别，可做识别动画
		public abstract void onVolumeChanged(int volume); //收音动画波动
		public abstract void onProtocol(String protocol);//识别结束，结构化的语义json返回，此时可结束loading动画
		public abstract void onError(String errorReason);//语音引擎错误，参数为错误原因
		public abstract void onTTSBegin();//TTS播报开始
		public abstract void onTTSEnd(); //TTS播报介绍
		public abstract void onEngineCompileStart();//引擎编译开始
		public abstract void onEngineCompileFinished();//引擎编译结束
	}
}
