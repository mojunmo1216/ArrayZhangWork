package com.haloai.hud.utils;

import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangrui on 16/7/27.
 */
public class HaloLoggerFile implements IHaloLogger {

    private static final int MAX_LINE_NUMBER_LOG_LINES  = 50;

    //TODO: 每小时保存一个新文件
    private static FileOutputStream logFileOS = null;
    private static int logLineNumber = 0;

    @Override
    public void logI(String tag, String msg) {
//        if (Log.isLoggable("tag", Log.DEBUG))
            saveLog("Info", tag, msg);
    }

    @Override
    public void logD(String tag, String msg) {
//        if (Log.isLoggable("tag", Log.DEBUG))
            saveLog("Debug", tag, msg);
    }

    @Override
    public void logW(String tag, String msg) {
//        if (Log.isLoggable("tag", Log.WARN))
            saveLog("Warning", tag, msg);
    }

    @Override
    public void logE(String tag, String msg) {
        if(tag.contains("helong_fix")||tag.contains("matlab")||tag.contains("zhangyong")){
            return;
        }
//        if (Log.isLoggable("tag", Log.ERROR))
            saveLog("Error", tag, msg);
    }

    @Override
    public  void flush() {
        try {
            if (logFileOS != null)
                logFileOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存之前缓存的log,重新开始保存新的log
     */
    @Override
    public void split(){
        flush();
        logFileOS=null;
        logD("tag","*******split*****************split**************split*************");
    }

    private static void saveLog(String type, String tag, String log){
        Log.e(tag,log);
        if (logFileOS == null) {
            //TODO: Format the log file name as log-Year-Month-Day.txt
            String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String fileName = "log-" + time  + ".txt";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                try {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Halo/log/");
                    if (!dir.exists())
                        dir.mkdirs();
                    logFileOS = new FileOutputStream(new File(dir, fileName));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }else{
                return;
            }
        }
        try {
       /*   int processID = Process.myPid();
            long threadID = Thread.currentThread().getId();*/
            String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String msg = String.format("[%s][%s][%s]:%s"+"\n", time,type, tag, log);
             logFileOS.write(msg.getBytes());
            if (logLineNumber++ > MAX_LINE_NUMBER_LOG_LINES) {
                logLineNumber = 0;
                logFileOS.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
