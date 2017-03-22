package com.haloai.hud.hudendpoint.utils;

import android.os.Environment;

import com.haloai.hud.utils.HaloLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhangrui on 16/7/26.
 */
public class LogUtils {
    private static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

    public static void saveLog(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line+"\n");
            }

            long timetamp = System.currentTimeMillis();
            String time = format.format(new Date());
            String fileName = "log-" + time + "-" + timetamp + ".txt";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                try {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Halo/logcat/");
                    HaloLogger.logE("CrashUtils", dir.toString());
                    if (!dir.exists())
                        dir.mkdir();
                    FileOutputStream fos = new FileOutputStream(new File(dir,fileName));
                    fos.write(log.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
