package com.haloai.hud.hudendpoint.upgrade;

import android.content.Context;

import com.haloai.hud.hudendpoint.primarylauncher.BuildConfig;
import com.haloai.hud.hudendpoint.utils.PropertyUtil;
import com.haloai.hud.utils.EndpointsConstants;

import java.util.Properties;

/**
 * Created by zhangrui on 16/12/13.
 */
public class UpgradeConstant {

    public final static int UPDATE_IDILE = 0;//当前已是最新的版本

    public final static int UPDATE_WAITING = 1;//升级包下载完成，等待升级

    public final static int UPDATE_START = 2;//开始升级

    public final static String version = "full_dig8665_sd_l-userdebug 5.1 LMY47D 1481702067 test-keys";
    public final static String oem = "jlinkszMT8665";
    public final static String models = "H1";
    public final static String token = "a1dc3278c168cfbe066152acd420d1cc";
    public final static String platform = "MT8665";
    public final static String deviceType = "HUD";

    //广播类型
    public static final String ACTION_DOWNLOAD_PROGRESS = "action.fota.download.progress";
    public static final String ACTION_DOWNLOAD_ERROR = "action.fota.download.error";
    public static final String ACTION_DOWNLOAD_FINISHED = "action.fota.download.finished";

    public static final String ACTION_CHECK_START = "action.fota.check.start";
    public static final String ACTION_CHECK_SUCCESS = "action.fota.check.success";
    public static final String ACTION_CHECK_ERROR = "action.fota.check.error";

    public static final String ACTION_UPGRADE_START = "action.fota.upgrade.start";
    public static final String ACTION_UPGRADE_ERROR = "action.fota.upgrade.error";

    public static final String ACTION_CLOSE_GESTURE = "action.close.gesture.power";
    public static final String ACTION_UPGRADE_SUCCES = "action.iport.recovery.upgrade";

    //传值的key
    public static final String KEY_INTENT_ARG0 = "key_intent_progress";


    public static int setUpgradeCode(int code){
        Properties properties= PropertyUtil.loadConfig();
        if(properties != null){
            properties.setProperty(PropertyUtil.KEY_UPGRADE_CODE,code+"");
            PropertyUtil.saveConfig(properties);
        }
        return code;
    }
}
