package com.haloai.hud.utils;

import com.amap.api.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class EndpointsConstants {

    public static final String PHONE_NUMBER_UNKNOWN = "PHONE_NUMBER_UNKNOWN";

    public static boolean IS_PANEL = false;

    public static boolean IS_NAVI = false;


    public static String BT_NAME = "HALO H1";

    public static String WIFI_NAME = "Halo_001";

    public static float GESTURE_SCORE = 0;

    public static int NAVI_SPEED = 120;

    public static int MARGIN_TOP = 90;

    public static int MARGIN_RIGHT = 0;

    public static int UPGRADE_STATUS = 0;

    public static final String BasePath = "/system/etc/Halo";//  /system/etc/Halo

    public static LatLng HudStartPoint;

    public static final int KEYCODE_HALO_GESTURE_ON = 267;

    public static final int KEYCODE_HALO_GESTURE_OFF = 266;

    public static final String NAVI_TAG = "NAVI_TAG";
    public static final String MUSIC_TAG = "MUSIC_TAG";
    public static final String ARWAY_TAG = "ARWAY_TAG";
    public static final String RECORD_TAG = "RECORD_TAG";
    public static final String BTCALL_TAG = "BTCALL_TAG";
    public static final String SPEECH_TAG = "SPEECH_TAG";
    public static final String MAIN_TAG = "MAIN_TAG";
    public static final String PHONE_TAG = "PHONE_TAG";
    public static final String GESTURE_TAG = "GESTURE_TAG";



    /**
     * 设置协议中的请求类型
     */
    public final static int HUD_SET_WIFI = 1; //设置HUD热点账户密码
    public final static int HUD_SET_VOLUME = 2; //设置HUD音量
    public final static int HUD_SET_BRIGHTNESS = 3; //设置HUD光机亮度
    public final static int HUD_SET_FM = 4; //设置HUD FM发射
    public final static int HUD_SET_PATH = 5; //设置HUD 导航路径
    public final static int HUD_SET_STORAGE = 6;// 获取内外存储卡存储信息
    public final static int HUD_SET_DEBUG = 7;// 设置HUD调试接口
    public final static int HUD_SET_MUSIC = 8;// 音乐调试接口


    /**
    *  调试协议调试手势
    */

    public final static int HUD_DEBUG_GESTURE = 0; //调试手势开关


    /**
    * 手机做遥控器协议中command类型
    */
    public final static int REMOTER_COMMAND_EXIT_NAVI = 0; //退出导航
    public final static int REMOTER_COMMAND_ONCLICK = 1; //ONCLICK
    public final static int REMOTER_COMMAND_PLAY_VIDEO = 2; //播放协议

    public final static int REMOTER_COMMAND_DELET_VIDEO = -9527; //删除协议


    /**
     * 手机做遥控器播放协议中command参数
     * int_param
     * 播放协议:0加载视频 1播放 2暂停 3快进 4快退 5加锁 6下一段 7上一段 8音量加 9音量减 10退出播放
     * onclick:0语音唤醒 1拍摄功能 2暂停/播放音乐 3下一首
     */
    public final static int COMMAND_VALUVE_START = 0;
    public final static int COMMAND_VALUVE_PLAY = 1;
    public final static int COMMAND_VALUVE_PAUSE = 2;
    public final static int COMMAND_VALUVE_FAST = 3;
    public final static int COMMAND_VALUVE_REWIND = 4;
    public final static int COMMAND_VALUVE_LOCK = 5;
    public final static int COMMAND_VALUVE_NEXT_VIDEO = 6;
    public final static int COMMAND_VALUVE_PREV_VIDEO = 7;
    public final static int COMMAND_VALUVE_UP_VLUME = 8;
    public final static int COMMAND_VALUVE_DOWN_VLUME = 9;
    public final static int COMMAND_VALUVE_EXIT_VIDEO = 10;
    public final static int COMMAND_VALUVE_DELET_VIDEO = 11;

    //onclick:0语音唤醒 1拍摄功能 2暂停/播放音乐 3下一首

    public final static int ONCLIACK_COMMAND_WAKEUP = 0;
    public final static int ONCLIACK_COMMAND_PHOTO = 1;
    public final static int ONCLIACK_COMMAND_MUSIC = 2;
    public final static int ONCLIACK_COMMAND_NEXT = 3;

    public final static String REMOTER_COMMAND_STRING ="command_string_param";
    public final static String REMOTER_VIDEO_TYPE ="command_video_type";
    public final static String REMOTER_VIDEO_INDEX ="command_video_index";

    public final static String FM_SET_PACKAGE_NAME = "com.digissin.fm.services.FMService";



}
