package com.haloai.hud.utils;


import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author Created by Harry Moo
 */
public interface IHaloLogger {
    public abstract void logI(String tag, String msg);
    
    public abstract void logD(String tag, String msg);

    public abstract void logW(String tag, String msg);

    public abstract void logE(String tag, String msg);

    public abstract void flush();

    public abstract void split();//切分前后log

}
