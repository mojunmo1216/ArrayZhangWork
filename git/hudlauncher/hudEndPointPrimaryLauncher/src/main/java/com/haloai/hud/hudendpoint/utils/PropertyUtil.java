package com.haloai.hud.hudendpoint.utils;

import android.telephony.PhoneNumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by zhangrui on 17/1/5.
 */
public class PropertyUtil {

    public static String ConfigPath = "/sdcard/Halo/config.properties";

    public static String KEY_MARGIN_TOP = "margin_top";

    public static String KEY_MARGIN_RIG = "margin_right";

    public static String KEY_UPGRADE_CODE = "upgrade_code";



    //保存默认配置文件
    public static boolean saveConfig(Properties properties) {
        try {
            File fil=new File(ConfigPath);
            if(!fil.getParentFile().exists())
                fil.getParentFile().mkdirs();
            if(!fil.exists())
                fil.createNewFile();
            FileOutputStream s = new FileOutputStream(fil);
            properties.store(s, "");
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //读取默认文件
    public static Properties loadConfig() {

        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(ConfigPath);
            properties.load(s);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    //保存配置文件
    public static boolean saveConfig(String file, Properties properties) {
        try {
            File fil=new File(file);
            if(!fil.getParentFile().exists())
                fil.getParentFile().mkdirs();
            if(!fil.exists())
                fil.createNewFile();
            FileOutputStream s = new FileOutputStream(fil);
            properties.store(s, "");
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //读取配置文件
    public static Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

}
