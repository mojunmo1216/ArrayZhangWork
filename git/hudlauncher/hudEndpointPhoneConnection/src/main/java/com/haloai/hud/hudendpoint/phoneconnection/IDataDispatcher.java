package com.haloai.hud.hudendpoint.phoneconnection;

import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;

public interface IDataDispatcher {

	/**
	 * Implements this method to analyze the HudP2HMessages and retrieve interested data to dispatch.
	 *
	 */
	public abstract AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages);

	/**
	 *  Secondary dispatching methods.
	 */
/*	public abstract void addDataListener(String listenerTag, IDataListener listener);
	public abstract void removeListener(String listenerTag);
	public abstract void removeAllListener();
	public interface IDataListener {
	}
	*/
}
