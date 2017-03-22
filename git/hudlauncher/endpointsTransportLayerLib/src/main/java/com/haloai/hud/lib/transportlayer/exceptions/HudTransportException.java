package com.haloai.hud.lib.transportlayer.exceptions;

import com.haloai.hud.utils.HudException;
import com.haloai.hud.utils.IHaloLogger;

public class HudTransportException extends HudException {
	private final static String TAG = HudTransportException.class.getName();

	public HudTransportException(String message) {
		super(message);
	}

    public HudTransportException(String message, IHaloLogger log) {
        super(message);
        if (log != null) log.logE(TAG, toString());
    }

	private static final long serialVersionUID = 5185552705732634487L;

}
