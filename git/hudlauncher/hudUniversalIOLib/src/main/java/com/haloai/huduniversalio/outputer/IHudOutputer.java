package com.haloai.huduniversalio.outputer;

public interface IHudOutputer {

	public abstract void output(Object object,HudOutputContentType contentType,boolean keepRecongize);
	public abstract void cancel();//取消当前输出
	public abstract void setOutputContentType2IDLE();//将输出内容的状态设置为空闲状态
	
	public enum HudOutputContentType{
		CONTENT_TYPE_IDLE,//空闲状态，此时无语音正在播报
		CONTENT_TYPE_MUSIC,//音乐
		CONTENT_TYPE_WEIXIN,//微信
		CONTENT_TYPE_NAVIGATION,//导航语音
		CONTENT_TYPE_OUTGOING,//去电
		CONTENT_TYPE_INCOMING,//来电
		CONTENT_TYPE_CUSTOM//自定义语音：例如HUD工作流程中播放的语音
	}

}
