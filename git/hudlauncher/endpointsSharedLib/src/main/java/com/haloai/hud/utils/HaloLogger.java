package com.haloai.hud.utils;

import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

public class HaloLogger {

	public static final IHaloLogger logger ;//HaloLoggerConsole(), HaloLoggerFile();

	static {
		if(HudConstants.IS_LOGFILE){
			logger = new HaloLoggerFile();
		}else {
			logger = new HaloLoggerConsole();
		}
	}

	/**
	 * 切分前后log
	 */
	public static void split(){
		if (logger == null) {
			logger.flush();
		}
	}
	public static void flush(){
		if (logger == null) {
			logger.flush();
		}
	}
	public static void logI(String tag, String message) {
		if (logger != null)
			logger.logI(tag, message);
	}

	public static void logD(String tag, String message) {
		if (logger != null)
			logger.logD(tag, message);
	}

	public static void logW(String tag, String message) {
		if (logger != null)
			logger.logW(tag, message);
	}

	public static void logE(String tag, String message) {
		if (logger != null)
			logger.logE(tag, message);
	}

	public static void postI(String tag,String message){
		logI(tag,message);
		BuglyLog.i(tag,message);
	}

	public static void postD(String tag,String message){
		logD(tag,message);
		BuglyLog.d(tag,message);
	}

	public static void postW(String tag,String message){
		logW(tag,message);
		BuglyLog.w(tag,message);
	}

	public static void postE(String tag,String message){
		logE(tag,message);
		BuglyLog.e(tag, message);
	}

	public static void uploadCatchException(Throwable throwable){
		if(throwable != null){
			CrashReport.postCatchedException(throwable);
		}
	}

	public static void uploadHaloLog(String message){
		if(message != null ){
			CrashReport.postCatchedException(new HaloLogCollectException(message));
		}
	}

	public static void uploadHaloLog(){
		CrashReport.postCatchedException(new HaloLogCollectException());
	}

}
