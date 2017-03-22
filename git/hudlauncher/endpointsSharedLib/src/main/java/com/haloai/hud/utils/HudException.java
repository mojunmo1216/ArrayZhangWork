package com.haloai.hud.utils;


/**
 * @author Created by Harry Moo
 */
public class HudException extends RuntimeException {

	private static final long serialVersionUID = 8986703736192629985L;

//	public final String toString() {
//        return "HudException - Ex:[" + super.getClass().getName() + "]; Message: [" + getMessage() + "];" + exceptionSpecificString() != null ? " " + exceptionSpecificString() : "";
//    }
//
    public HudException(String message) {
        super(message);
    }
//
//    public HudException(String message, IHaloLogger log) {
//        super(message);
//        if (log != null) 
//        	log.logE(toString());
//    }
//
//    private String exceptionSpecificString() {
//        return null;
//    }
}
