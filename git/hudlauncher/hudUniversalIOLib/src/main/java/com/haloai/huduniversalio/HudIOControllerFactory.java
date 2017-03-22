package com.haloai.huduniversalio;

import android.content.Context;

import com.haloai.huduniversalio.impl.HudIOController;

public class HudIOControllerFactory {
	
	private static IHudIOController sHudIOController;

	public static IHudIOController getHudIOController(Context context, IHudIOControllerListener hudIOControllerListener) {
		if (sHudIOController == null) {
			sHudIOController = new HudIOController(context);
			sHudIOController.init();
			sHudIOController.setHudIOControllerListener(hudIOControllerListener);
		} else {
			sHudIOController.setHudIOControllerListener(hudIOControllerListener);
		}
		
		return sHudIOController;
	}
}
