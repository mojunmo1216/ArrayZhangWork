package com.haloai.hud.hudendpoint;


import android.graphics.Point;

import com.haloai.hud.hudendpoint.services.MusicPlayService;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.IHaloLogger;


public class HudEndPointConstants {
	
	//Debug Constants.
	public final static IHaloLogger gLogger = HaloLogger.logger;
	
	//Screen Size in Pixels.
	public final static int HUD_SCREEN_WIDTH = 840;
	public final static int HUD_SCREEN_HEIGHT = 280;

	public static boolean IS_CARCORD = false;
	public static boolean IS_OUTCALL = false;
	public static boolean IS_CALLING = false;

	//全局HUD设备信息，系统启动期间完成初始化
	public static String gHudVersionNumber;	//HUD版本号，直接用该App Version Name
	public static String gHudSerialNumber = "HaloHud0001";	//HUD序列号，由设备硬件码等组成
	public static Point gHudScreenSize;		//HUD屏幕Size
	public static String gSimICCID = "999999999";
	
	//Gesture Command Constants
	/**
	 * Client端发送到Service端的消息，用来设置Client端的Messenger，Service可以通过该Messenger向Client发送消息；
	 * Client端需要在msg.replyto中设置该Messenger
	*/
	public final static int C2S_MSG_GESTURE_SERVICE_SET_LISTENER	=	100;	
	/**
	 * Client端发送到Service的消息，用来注册感兴趣的Gesture命令。
	 * 其参数通过msg.data的Bundle传递:
	 * Bundle Key为C2S_MSG_GESTURE_SERVICE_REGISTER_CMD_DATA_KEY	
	 * Bundle Value为ArrayList<String>,包含了Gesture命令列表。
	 */
	public final static int C2S_MSG_GESTURE_SERVICE_REGISTER_CMD	=	101;
	public final static String C2S_MSG_GESTURE_SERVICE_REGISTER_CMD_DATA_KEY	=	"c2s_msg_gesture_service_register_cmd_data_key";
	/**
	 * Client端发送到Service的消息，用来清除之前登记的所有Gesture命令监听。
	 */
	public final static int C2S_MSG_GESTURE_SERVICE_UNREGISTER_CMD	=	102;
	/**
	 * Service端发送到Client端的消息，通知检测到的一个Gesture命令，
	 * 其参数通过msg.object传递，为一个String类型的Gesture命令标识符。
	 */
	public final static int S2C_MSG_GESTURE_SERVICE_GESTURE_CMD_RECEIVED	=	200;
	/**
	 * 
	 */
	public final static String S2C_MSG_GESTURE_TEST_COMMAND_OK	=	"s2c_msg_gesture_test_command_ok";
	
	public final static boolean IS_DEBUG_MODE=false;


	public final static String ACTION_MUSIC_SERVICE="com.haloai.hud.hudendpoint.music";	//Music广播
//	public final static String ACTION_RECORD_STATUS="com.digissin.RECORDER_STATUS"; //行车记录仪状态更新广播
	public final static String MUSIC_STATUS_UPDATE="com.haloai.hud.hudendpoint.music.status.update";//歌曲信息更新Action
	public final static String ACTION_NET_REQ="com.digissin.requestnet";//请求查询网络
	public final static String ACTION_NET_DATA="com.digissin.data";//歌曲信息更新Action
	public final static String ACTION_RECOED_ERROR="intent.action.ACTION_CARCORDER_ERROR";//行车记录仪录制异常状态

	public final static String MUSIC_SERVICE_FLAG=MusicPlayService.class.getName()+"_FLAG";
	public final static String MUSIC_SERVICE_SONG_LIST=MusicPlayService.class.getName()+"_SONG_LIST";
	public final static String MUSIC_SERVICE_MUSIC_TAG=MusicPlayService.class.getName()+"_MUSIC_TAG";


	public final static int MUSIC_SERVICE_BASE =80;

	public static boolean showReceiver = false;
	/**
	 * 音乐播放控制的命令
	 */
	public final static int MUSIC_SERVICE_START =MUSIC_SERVICE_BASE+0;
	public final static int MUSIC_SERVICE_ERROR =MUSIC_SERVICE_BASE-1;
	public final static int MUSIC_SERVICE_PLAY =MUSIC_SERVICE_BASE+1;
	public final static int MUSIC_SERVICE_PAUSE =MUSIC_SERVICE_BASE+2;
	public final static int MUSIC_SERVICE_NEXT =MUSIC_SERVICE_BASE+3;
	public final static int MUSIC_SERVICE_PREV =MUSIC_SERVICE_BASE+4;
	public final static int MUSIC_SERVICE_STOP =MUSIC_SERVICE_BASE+5;

	public  static boolean IS_UNACTIVE_STOP_MUSIC= false;

	public final static String PHONE_REQUEST_NAGATION="com.haloai.hud.hudendpoint.navigation.phone";
	public final static String PHONE_REQUEST_TYPE="com.haloai.hud.hudendpoint.navigation.type";
	public final static String PHONE_START_NAGATION="type.start.navigation";
	public final static String PHONE_STOP_NAGATION="type.stop.navigation";
	public final static String RECORDER_CREATED_VIDEO = "intent.action.ACTION_MEDIA_SAVED";
	public final static String RECORDER_DELETE_VIDEO = "intent.action.ACTION_MEDIA_DELETE";
	public final static String FM_STATUS_ACTION = "com.haloai.hud.hudendpoint.fm";

	/**
	 * Hud和phone交互协议中，所需要的SharedPreferences存储keystring
	 */
	public final static String SP_PHONE_INFO_KEY="com.haloai.hud.hudendpoint.protocol.phoneinfo";
	public final static String SP_BIND_SECRET_KEY="com.haloai.hud.hudendpoint.protocol.bindkey";

	/**
	 * Hud设置协议中，sp存取用的key
	 */
/*
	public static String FILENAME_OF_SETTING = "filename_of_setting";
	public static String KEY_OF_HOME_PATHNAME = "key_of_home_pathname";
	public static String KEY_OF_HOME_LONGITUDE = "key_of_home_longitude";
	public static String KEY_OF_HOME_LATITUDE = "key_of_home_latitude";
	public static String KEY_OF_COMPANY_PATHNAME = "key_of_company_pathname";
	public static String KEY_OF_COMPANY_LONGITUDE = "key_of_company_longitude";
	public static String KEY_OF_COMPANY_LATITUDE = "key_of_company_latitude";
	public static String KEY_OF_FM_VALUE = "key_of_FM_value";
*/

	public final static int STEP_OF_BRIGHTNESS = 25;
	public final static int MAX_VALUE_OF_BRIGHTNESS = 255;
	public final static int MAX_VALUE_OF_FAN = 1023;
	public final static int DEFAULT_VALUE_OF_FAN = 750;
	public final static String HUD_WIFI_ACQUIESCENT_NAME = "halo_hud";
	public final static String HUD_WIFI_ACQUIESCENT_PASSWORD = "12345678";

	public enum PathEnumType{
		PATH_TO_HOME, PATH_TO_COMPANY
	}

	public static final int DELAYED_STATR_NAVI = 1;
	public static final int DELAYED_MUSIC_VLUME = 2;
	public static final int DELAYED_PLAY_VIDEO = 3;
	public static final int DELAYED_NETWORK_DISCONNECTED = 4;
	public static final int DELAYED_NAGATION_CALUTRE = 5;
	public static final int DELAYED_MATCH_POI = 6;

	public static final float GETMAPPATH_ICON_RATIO = 0.6f;
	public static final float MAPPATH_END_ICON_WIDTH = 25f * GETMAPPATH_ICON_RATIO;
	public static final float MAPPATH_END_ICON_HEIGHT = 34f * GETMAPPATH_ICON_RATIO;
	public static final float MAPPATH_START_ICON_WIDTH = 32 * GETMAPPATH_ICON_RATIO;
	public static final float MAPPATH_START_ICON_HEIGHT = 27 * GETMAPPATH_ICON_RATIO;

	public static final int BUGLY_TAG_MAIN = 27599;
	public static final int BUGLY_TAG_NAVI = 27591;
	public static final int BUGLY_TAG_ARWay = 27592;
	public static final int BUGLY_TAG_RECORD = 27595;
	public static final int BUGLY_TAG_MUSIC = 27593;
	public static final int BUGLY_TAG_CALL = 27594;
	public static final int BUGLY_TAG_PANEL = 27623;

	public static final  int OPEN_FM = 0;
	public static final  int CLOSE_FM = 1;

	public static final  int GESTURE_TIME = 0;
	public static final  int NETWORK_TIME = 1;

	public static final String MPATHOFTEMPVIDEOSHOW = "/sdcard/arwaydemo.mp4";
}
