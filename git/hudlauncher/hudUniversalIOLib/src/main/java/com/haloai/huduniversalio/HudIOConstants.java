package com.haloai.huduniversalio;

import android.view.KeyEvent;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.speech.ISpeechParserMusic.*;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HudIOConstants {

    public static final String GESTURE_TAG = "GESTURE_TAG";
    public final static String PHONE_REMOTER_PLAY_VIDEO="com.haloai.hud.hudendpoint.remoter.playvideo";
    public final static String PHONE_REMOTER_ONCLICK="com.haloai.hud.hudendpoint.remoter.onclick";
    public final static String GESTURE_CONNECT_STATUS="com.haloai.hud.hudendpoint.gesture.connect";
    public final static String GESTURE_CONNECT_DEBUG="com.haloai.hud.hudendpoint.gesture.debug"; //调试是否开关手势

    public final static String ACTION_TYPE = "actionType";
    public final static String ACTION_BTCALL_OUTCALL = "bluetooth_call_from_third_action";//光晕去电,主动更新电话本
    public final static String ACTION_BTCALL_INCOMING = "action.COME.PHONE";//达讯来电
    public final static String ACTION_BTCALL_OVER = "action.PHONE_STATE_RECOVERY";//通话结束
    public final static String ACTION_CONTACTS_DOWNLOAD_OVER = "action.CONTACTS.DOWNLOAD.FINISH";//电话本加载完成
    public final static String ACTION_CONTACTS_DOWNLOAD_START = "action.START.UPLOADPBOOK";//电话本加载完成
    public final static String ACTION_CONTACTS_UPLOAD = "bluetooth_auto_upload_action";
    public final static String ACTION_BTCALL_CONNECT_CHANGE = "com.txznet.bluetooth.connect_change";//蓝牙电话连接状态
    public final static String ACTION_BTCALL_CONFIG = "com.txznet.bluetooth.config";//蓝牙配置
    public final static String ACTION_BTCALL_ON_CALLING = "action.ON.CALLING";//蓝牙电话通话中
    public final static String ACTION_BTCALL_SAME_CONNECT = "action.CONNECT.SAMEDEVICE";//相同设备连接
    public final static String ACTION_ON_OUT_CALLING = "action.OUT.CALL";//通知拨号出

    public static final String ACTION_GESTURE_POWER = "action.hud.gesture.power";//upgrade升级成功

    public final static String CALL_DIAL = "dialCall";// 主叫(主叫操作)
    public final static String CALL_ANSWER = "answerCall";// 接听操作
    public final static String CALL_HANGUP = "hangupCall";//接听后挂掉,拨出后挂断
    public final static String CALL_REFUSE = "refuseCall";//未接听，就挂掉
    public final static String BT_OPEN = "openBT";//开启蓝牙
    public final static String BT_CLOSE = "closeBT";//关闭蓝牙
    public static final String BT_CONNECTED = "is_connected";//蓝牙是否连接
    public static final String CONTACTS_UPLOAD = "uploadContacts";//更新电话本
    public static final String BT_NAME = "bluetoothName";//更新蓝牙名



    public final static int GESTURE_STATUS_NO_RESPONE = -1;//手势连接无反应
    public final static int GESTURE_STATUS_OFFLINE = 0;//手势连接断线
    public final static int GESTURE_STATUS_CONNECTED = 1;//手势连接正常
    public final static String ACTION_REMOTER_INPUTER = "com.halo.hud.inputer.remoter";
    public final static int INPUTER_REMOTER_ENTER = KeyEvent.KEYCODE_ENTER;
    public final static int INPUTER_REMOTER_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    public final static int INPUTER_REMOTER_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    public final static int INPUTER_GESTURE_CLOSE_PANEL = -1;
    public final static int INPUTER_GESTURE_POINT = 0;
    public final static int INPUTER_GESTURE_V = 1;
    public final static int INPUTER_GESTURE_SLIDE = 2;
    public final static int INPUTER_GESTURE_CLOCK = 3;
    public final static int INPUTER_GESTURE_EASTE = 4;
    public final static int INPUTER_GESTURE_PALM = 5;

    /**
     * Hud设置协议中，sp存取用的key
     */
    public static String FILENAME_OF_SETTING = "filename_of_setting";
    public static String KEY_OF_HOME_PATHNAME = "key_of_home_pathname";
    public static String KEY_OF_HOME_LONGITUDE = "key_of_home_longitude";
    public static String KEY_OF_HOME_LATITUDE = "key_of_home_latitude";
    public static String KEY_OF_COMPANY_PATHNAME = "key_of_company_pathname";
    public static String KEY_OF_COMPANY_LONGITUDE = "key_of_company_longitude";
    public static String KEY_OF_COMPANY_LATITUDE = "key_of_company_latitude";
    public static String KEY_OF_FM_VALUE = "key_of_FM_value";
    public static final String Is_Blight_Auto_close = "lightAuto";//光感自动是否已关闭?

    public final static String [] DISPATCHER_ARRAYS ={"返回","取消","关机","公司","回家","日志","打开录像","关闭录像"};
    public final static String [] HUD_ARRAYS ={"导航","电话","打电话","音乐","退出","关闭"};
    public final static String [] HEAD_ARRAYS ={"我想","我要","帮我","帮"};
    public final static String [] NAVI_ARRAYS ={"导航到","导航","到","去","回","走","我想","我要","帮我","帮"};
    public final static String [] MUSIC_ARRAYS ={"听","歌","播放","我想","音乐","一首","我要","帮我","帮"};

    public static final int MUSIC_IDILE = 0;
    public static final int MUSIC_PLAY = 1;
    public static final int MUSIC_PAUSE = 2;
    public static int HudMusicStat = MUSIC_IDILE;

    public static final int OPERAT_MUSIC_IDILE = 1;
    public static final int OPERAT_MUSIC_USER = 2;
    public static final int OPERAT_MUSIC_CALL = 3;

    public static int MusicPlayStat = 0;
    public static int MusicPauseStat = 0;

    public enum HudIOSessionType {
        EXIT,
        SIMULATION_NAVI,
        NAVI,
        INCOMING_CALL,
        OUTGOING_CALL,
        RECORDER,
        WECHAT_ARRI,
        HANGUP,
        DISPATCHER,
        MUSIC,
        COMMAND
    }

    public enum HudInputerType {
        SPEECH_INPUT,
        REMOTER_INPUT,
        GESTURE_INPUT,
        PHONE_INPUT
    }

    public enum HudOutputerType {
        SPEECH_OUTPUT,
        PHONE_OUTPUT
    }

    public enum HudPromatType {
        MUSIC_PROMAT,
        CALL_PROMAT,
        NAVI_PROMAT,
        EXIT_PROMAT,
        SHUTDOWN_PROMAT,
        OPEN_AMAP,
        OPEN_AWAY,
        OPEN_RECORD,
        CLOSE_RECORD
    }

    /**
     * 描述一个联系人的号码信息
     * contactName 联系人姓名
     * mPhoneNumbers 联系人的N个号码
     *
     * @author 龙
     */
    public class ContactInfo {
        //姓名
        public String name;
        //号码集合
        public List<String> phone;

        public ContactInfo(String contactName, List<String> phoneNumbers) {
            name = contactName;
            phone = phoneNumbers;
        }
    }

    public static String MUSIC_LIST = "[\n" +
            "    {\n" +
            "        \"id\": \"2067242\",\n" +
            "        \"title\": \"说谎\",\n" +
            "        \"artist\": \"林宥嘉\",\n" +
            "        \"album\": \"感官世界\",\n" +
            "        \"album\": \"感官世界\",\n" +
            "        \"duration\": 252,\n" +
            "        \"url\": \"ttp:\\/\\/m5.file.xiami.com\\/517\\/23517\\/351048\\/1769167647_3420732_l.mp3?auth_key=6edd51ddeb244e05fd265c22dae05d41-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511_1.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511.jpg\",\n" +
            "        \"lyric\": \"http://img.xiami.net/lyric/upload/42/2067242_1376116262.lrc\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"1774917404\",\n" +
            "        \"title\": \"有点甜\",\n" +
            "        \"artist\": \"汪苏泷;By2\",\n" +
            "        \"album\": \"陪你度过漫长岁月\",\n" +
            "        \"duration\": 242,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/411\\/75411\\/523816\\/1771135899_3698653_l.mp3?auth_key=692ab2ff5ee14d2642fe9f1411f516f1-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img35/135/21002167761444703497_1.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img35/135/21002167761444703497.jpg\",\n" +
            "        \"lyric\": \"\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"2067257\",\n" +
            "        \"title\": \"红玫瑰\",\n" +
            "        \"artist\": \"陈奕迅\",\n" +
            "        \"album\": \"1997-2007 跨世纪国语精选\",\n" +
            "        \"duration\": 242,\n" +
            "        \"url\": \"http://m5.file.xiami.com/135/135/167589/2067257_2104508_l.mp3?auth_key=5adfcd53501d3e714799c83b8053a348-1473811200-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511_1.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511.jpg\",\n" +
            "        \"lyric\": \"http://img.xiami.net/lyric/2067257_13776054145573.lrc\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"2067248\",\n" +
            "        \"title\": \"逆流成河\",\n" +
            "        \"artist\": \"金南玲\",\n" +
            "        \"album\": \"1997-2007 跨世纪国语精选\",\n" +
            "        \"duration\": 262,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/289\\/108289\\/1287174226\\/1772409519_11198531_l.mp3?auth_key=c12e67987be18a1253a4646368903c82-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511_1.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img35/135/1675891330309511.jpg\",\n" +
            "        \"lyric\": \"http://img.xiami.net/lyric/48/2067248_13807330791275.lrc\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"1775751240\",\n" +
            "        \"title\": \"思念是一种病\",\n" +
            "        \"artist\": \"许巍\",\n" +
            "        \"album\": \"张震岳;蔡健雅\",\n" +
            "        \"duration\": 308,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/210\\/1210\\/6213\\/76280_36572_l.mp3?auth_key=17c67d262fbcb6d9aa6217c0f4cd674e-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img46/1046/21002904081458280614_3.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img46/1046/21002904081458280614_3.jpg\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"380252\",\n" +
            "        \"title\": \"你最珍贵\",\n" +
            "        \"artist\": \"张学友;高慧君\",\n" +
            "        \"album\": \"勇气\",\n" +
            "        \"duration\": 239,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/198\\/1198\\/6098\\/74680_11077410_l.mp3?auth_key=a1608aaef58bebead8967d696be966e4-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img36/1836/100751465709891_3.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img36/1836/100751465709891_3.jpg\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"1770409075\",\n" +
            "        \"title\": \"还是要幸福\",\n" +
            "        \"artist\": \"田馥甄\",\n" +
            "        \"album\": \"My Love\",\n" +
            "        \"duration\": 283,\n" +
            "        \"url\": \"http://m5.file.xiami.com/523/78523/459960/1770409075_2622355_l.mp3?auth_key=d4f882beddf5505a33bf47226a0d0f2c-1473811200-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img23/78523/4599601470214993_3.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img23/78523/4599601470214993_3.jpg\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"373971\",\n" +
            "        \"title\": \"五环之歌\",\n" +
            "        \"artist\": \"煎饼侠 电影原声\",\n" +
            "        \"album\": \"我们是五月天\",\n" +
            "        \"duration\": 253,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/535\\/1217578535\\/1335129649\\/1774438099_16832223_l.mp3?auth_key=5aa5f99e6f9f01e1c84c7fa2954e4f77-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img10/3110/15644_1.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img10/3110/15644.jpg\",\n" +
            "        \"lyric\": \"http://img.xiami.net/lyric/upload/71/373971_1310695578.lrc\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"174652\",\n" +
            "        \"title\": \"漂浮的家\",\n" +
            "        \"artist\": \"老狼\",\n" +
            "        \"album\": \"海阔天空\",\n" +
            "        \"duration\": 326,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/489\\/489\\/2102408905\\/1792654425_1476769508986.mp3?auth_key=542640ae63ed93b8f254a221fd94d1d0-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img29/2629/141351470297389_3.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img29/2629/141351470297389_3.jpg\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"3500258\",\n" +
            "        \"title\": \"痒\",\n" +
            "        \"artist\": \"黄龄\",\n" +
            "        \"album\": \"静茹&情歌 别再为他流泪\",\n" +
            "        \"duration\": 260,\n" +
            "        \"url\": \"http:\\/\\/m5.file.xiami.com\\/236\\/7236\\/32868\\/392388_4041793_l.mp3?auth_key=4d79c1e30ea432ded03dcb5145077716-1477958400-0-null\",\n" +
            "        \"imgUrl\": \"http://img.xiami.net/images/album/img36/1836/3155111465810499_3.jpg\",\n" +
            "        \"hdImgUrl\": \"http://img.xiami.net/images/album/img36/1836/3155111465810499_3.jpg\",\n" +
            "        \"errorCode\": 0\n" +
            "    },\n" +
            "]";

    public static List<MusicInfo> getMusicList(){
        JSONArray musicinfo = JsonTool.parseToJSONOArray(MUSIC_LIST);
        List<MusicInfo> mMusicInfoList=new ArrayList<>();
        for(int i=0;i<musicinfo.length();i++){
            MusicInfo musicInfo=new MusicInfo();
            JSONObject object=JsonTool.getJSONObject(musicinfo, i);
            musicInfo.song_url=JsonTool.getJsonValue(object, "url");
            musicInfo.title=JsonTool.getJsonValue(object, "title");
            musicInfo.artist=JsonTool.getJsonValue(object, "artist");
            musicInfo.imag_url=JsonTool.getJsonValue(object, "hdImgUrl");
            musicInfo.duration=JsonTool.getJsonValue(object,"duration",0);
            mMusicInfoList.add(musicInfo);
        }
        return mMusicInfoList;
    }

}
