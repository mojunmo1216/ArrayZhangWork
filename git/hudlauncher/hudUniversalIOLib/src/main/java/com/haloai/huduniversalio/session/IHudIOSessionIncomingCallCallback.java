 package com.haloai.huduniversalio.session;


public interface IHudIOSessionIncomingCallCallback extends IHudIOSessionCallback {
	public abstract void onSessionIncomingCallRinging(String contactName); //来电
	public abstract void onSessionIncomingCallAnswered(); //接听
	public abstract void onSessionIncomingCallRejected(); //拒接
}
