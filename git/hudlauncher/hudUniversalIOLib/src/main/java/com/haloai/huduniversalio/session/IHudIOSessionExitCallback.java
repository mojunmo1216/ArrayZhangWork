package com.haloai.huduniversalio.session;

public interface IHudIOSessionExitCallback extends IHudIOSessionCallback {

//	public abstract void exitSessionBegin();
	//"确定退出"的回调
	public abstract void exitSessionConfirm();
	//"取消退出"的回调
//	public abstract void exitSessionCancel();
}
