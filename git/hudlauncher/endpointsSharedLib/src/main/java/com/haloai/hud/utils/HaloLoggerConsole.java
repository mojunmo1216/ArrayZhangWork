package com.haloai.hud.utils;

import android.util.Log;

/**
 * 输出日志到控制台
 * @author Created by Harry Moo
 */
public class HaloLoggerConsole implements IHaloLogger {

    @Override
    public void logI(String tag, String msg) {
//       if (Log.isLoggable("tag", Log.INFO))
            Log.i("HALO+"+tag, msg);
    }

    @Override
    public void logW(String tag, String msg) {
//        if (Log.isLoggable("tag", Log.WARN))
            Log.w("HALO+"+tag, msg);
    }

    public void logE(String tag, String msg) {
//        if (Log.isLoggable("tag", Log.ERROR))
            Log.e("HALO+"+tag, msg);
    }

	@Override
	public void logD(String tag, String msg) {
//		if (isNaviFeatureLog(tag))
//        if (Log.isLoggable("tag", Log.DEBUG))
			Log.d("DEBUGNavi-" + tag, msg);
	}

    @Override
    public void flush() {

    }

    @Override
    public void split() {
        logD("tag","*******split*****************split**************split*************");
    }
	/*
	 * filter示例
	 * 该函数中对比自己想要的TAG类名，在各个接口函数中调用filter，只打印需要的日志TAG；注意：在提交代码时谨记一定要恢复原实现；
	private boolean isNaviFeatureLog(String tag) {
		if (tag.contains("Navi") || tag.equalsIgnoreCase("HudIOSession"))
			return true;
		
		return false;
	}
	 */
}
