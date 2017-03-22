package com.haloai.hud.hudendpoint;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.haloai.hud.hudendpoint.fragments.common.HudStatusBarFragment.*;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.hudendpoint.phoneconnection.PhoneConnectionManager;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.protocol.imp.HudPrefSetDispatcherImp;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.outputer.IHudOutputer;


/**
 * Created by zhangrui on 16/6/30.
 */
public class HudStatusManager {

    public enum HudStatus {
        INIT_ERROR,//初始化语音引擎失败
        MAIN, // 语音命令提示
        CRUISE, // 巡航
        POI_SHOW, // 展示兴趣表
        CL_OUTOFSERVICE, // 退出服务
        CHOOSE_STRAGETY, // 选择策略
        NAVIGATING, // 导航中（在用户说出开始导航后进入该状态）
    }
    private static HudStatusManager mHudStatusManager;
    public static HudStatus HudCurrentStatus=HudStatus.MAIN;
    private IHudStatusCallBack mHudStatusCallBack;
    private IHudOutputer mSpeechOutputer;
    private Context mContext;
    private String HudPOIName;
    private int HudBTContactsStatus = BLUETOOTH_CONTACTS_IDILE;

    public static final int BLUETOOTH_CONTACTS_BASE = 0;
    public static final int BLUETOOTH_CONTACTS_IDILE = BLUETOOTH_CONTACTS_BASE +1;
    public static final int BLUETOOTH_CONTACTS_CONNECT = BLUETOOTH_CONTACTS_BASE +2;
    public static final int BLUETOOTH_CONTACTS_COMPLING = BLUETOOTH_CONTACTS_BASE +3;
    public static final int BLUETOOTH_CONTACTS_COMPLED = BLUETOOTH_CONTACTS_BASE +4;

    public static final int MUSIC_IDILE = 0;
    public static final int MUSIC_PLAY = 1;
    public static final int MUSIC_PAUSE = 2;

    public static final int BLUETOOTH_CALL_IDILE = BLUETOOTH_CONTACTS_BASE+1;//空闲
    public static final int BLUETOOTH_CALL_DIALING = BLUETOOTH_CONTACTS_BASE+2;//拨号
    public static final int BLUETOOTH_CALL_COMING = BLUETOOTH_CONTACTS_BASE+3;//来电
    public static final int BLUETOOTH_CALL_TALKING = BLUETOOTH_CONTACTS_BASE+4;//通话

    private static final int STATUS_NETWORK_FITER = 0;

    public static final int MUSIC_VLUME = 5;
    public static int HudMusicStatus = MUSIC_IDILE;
    public static int HudBTCallStatus = BLUETOOTH_CALL_IDILE;
    public static int HudMusicVlume;
    public static int HudGPSNum;
    public static boolean HudGesture;
    public static boolean IS_NETWORK_CONNECTED = true;
    public static boolean HudRecordStatus = true;
    private TelephonyManager mTelephonyManager;
    private HudStateListener mStateListener;
    private final int GSM_LEVEL_NUM = 8; //4G信号强度等级
    private int HudGSMState = -1;

    private static final String TAKE_PHONE_SUCCESS= "com.pg.software.CAPTURE_DONE";



    BroadcastReceiver mStatusBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) { //网络状态
                ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] networkInfos = mConnectivityManager.getAllNetworkInfo();
                for (NetworkInfo networkInfo : networkInfos) { //4G,WIFI
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.WIFI, networkInfo.isConnectedOrConnecting());
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.MOBILE, networkInfo.isConnectedOrConnecting());
                        Log.e("GSM_info","connect:"+networkInfo.isConnectedOrConnecting());
                    }
                }
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) { //有无网络信号
                    //  有网络连接
                    //IS_NETWORK_CONNECTED=true;
                   // mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.FM,false);
                } else {
                    // 无网络连接
                    //IS_NETWORK_CONNECTED=true;
                  //  mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.FM,true);
                  //  mHandler.sendEmptyMessageDelayed(STATUS_NETWORK_FITER,3000);
                }
            }else if (action.equals(HudEndPointConstants.MUSIC_STATUS_UPDATE)) { //更新歌曲信息
                int songIndex = intent.getIntExtra(HudEndPointConstants.MUSIC_STATUS_UPDATE, -1);
                mHudStatusCallBack.onMusicStatusUpdate(songIndex);
            }else if(action.equals(HudIOConstants.ACTION_BTCALL_CONNECT_CHANGE)){ //蓝牙连接状态
               boolean stat = intent.getBooleanExtra(HudIOConstants.BT_CONNECTED,false);
               int status = stat ? BLUETOOTH_CONTACTS_CONNECT : BLUETOOTH_CONTACTS_IDILE;//连接蓝牙,断开蓝牙
                mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.BLUETOOTH,stat);
                HudBTContactsStatus = status;
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"btcall_connect:"+stat);
            }else if(action.equals(HudIOConstants.GESTURE_CONNECT_STATUS)){ //手势连接状态
                int stat = intent.getIntExtra(HudIOConstants.GESTURE_CONNECT_STATUS,-1);
                HudGesture = stat == HudIOConstants.GESTURE_STATUS_CONNECTED;
                mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.NO_GESTURE,!HudGesture);
                HaloLogger.postE(EndpointsConstants.GESTURE_TAG,"gesture_connect:"+HudGesture);
            }else if(action.equals(HudIOConstants.ACTION_BTCALL_ON_CALLING)){ //蓝牙电话通话中
                mHudStatusCallBack.onBTCalling();
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"btcall_calling:");
            }else if(action.equals(HudIOConstants.ACTION_BTCALL_SAME_CONNECT)){ //蓝牙电话相同连接
                if(HudBTContactsStatus == BLUETOOTH_CONTACTS_IDILE){
                    mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.BLUETOOTH,true);
                }
                HudBTContactsStatus=BLUETOOTH_CONTACTS_COMPLED;
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"btcall_same_connct");
            }else if(action.equals(HudIOConstants.ACTION_CONTACTS_DOWNLOAD_START)){ //蓝牙电话本开始加载
                HudBTContactsStatus=BLUETOOTH_CONTACTS_COMPLING;
                mSpeechOutputer.output(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_call_compile_start), IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM, EndpointsConstants.IS_PANEL);
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"btcall_down_connct");
            }else if(action.equals(HudEndPointConstants.ACTION_NET_DATA)){
                long size = intent.getLongExtra("size",0);
                HaloLogger.postI(EndpointsConstants.MAIN_TAG,"size:"+size);
            }else if(action.equals(HudEndPointConstants.ACTION_RECOED_ERROR)){
                HudRecordStatus = false;
                String reason = intent.getStringExtra("reason");
                mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.RECORD,false);
                HaloLogger.postE(EndpointsConstants.RECORD_TAG,"RecordError:"+reason);
            }else if(action.equals(HudEndPointConstants.RECORDER_CREATED_VIDEO)){
                String videoPath = intent.getStringExtra("path");
                if (!TextUtils.isEmpty(videoPath) && !HudRecordStatus) {
                    HudRecordStatus = true;
                    mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.RECORD,true);
                }
            }else if(action.equals(HudEndPointConstants.FM_STATUS_ACTION)){
                boolean isOpen = intent.getBooleanExtra(HudEndPointConstants.FM_STATUS_ACTION,false);
                mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.FM,isOpen);
            }else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)){
                context.unregisterReceiver(mStatusBroadcastReceiver);
                mSpeechOutputer.output("正在关机", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,false);
                Intent power_off_intent = new Intent("ACTION_SYSTEM_SHUTDOWN");
                context.sendBroadcast(power_off_intent);
            }else if(action.equals(TAKE_PHONE_SUCCESS)){
                if(intent.getStringExtra("FrontUrl") != null){
                    String frontUrl = intent.getStringExtra("FrontUrl");
                    HaloLogger.logE("speech_info","FrontUrl"+frontUrl);
                    mSpeechOutputer.output("拍摄成功", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,false);
                }
                if(intent.getStringExtra("BackUrl") != null){
                    String backUrl= intent.getStringExtra("BackUrl");
                    HaloLogger.logE("speech_info","BackUrl"+backUrl);
                }

            }
        }

    };

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STATUS_NETWORK_FITER:
                    if(!IS_NETWORK_CONNECTED){
                        mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.FM,true);
                    }
                    break;
            }
        }
    };

    public static HudStatusManager getInstance(){
        if(mHudStatusManager == null){
            mHudStatusManager = new HudStatusManager();
        }
        return mHudStatusManager;
    }

    public void create(Context context, IHudStatusCallBack hudStatusCallBack, IHudOutputer hudOutputer){
        this.mContext = context;
        this.mHudStatusCallBack = hudStatusCallBack;
        this.mSpeechOutputer = hudOutputer;
        init();
    }

    private void init(){
        IntentFilter hudFilter = new IntentFilter();
        hudFilter.addAction(HudEndPointConstants.MUSIC_STATUS_UPDATE);
        hudFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        hudFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        hudFilter.addAction(HudIOConstants.ACTION_BTCALL_CONNECT_CHANGE);
        hudFilter.addAction(HudIOConstants.GESTURE_CONNECT_STATUS);
        hudFilter.addAction(HudIOConstants.ACTION_BTCALL_ON_CALLING);
        hudFilter.addAction(HudIOConstants.ACTION_BTCALL_SAME_CONNECT);
        hudFilter.addAction(HudIOConstants.ACTION_CONTACTS_DOWNLOAD_START);
        hudFilter.addAction(HudEndPointConstants.ACTION_NET_DATA);
        hudFilter.addAction(HudEndPointConstants.ACTION_RECOED_ERROR);
        hudFilter.addAction(HudEndPointConstants.RECORDER_CREATED_VIDEO);
        hudFilter.addAction(HudEndPointConstants.FM_STATUS_ACTION);
        hudFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        hudFilter.addAction(TAKE_PHONE_SUCCESS);
        mContext.registerReceiver(mStatusBroadcastReceiver, hudFilter);

        mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.NO_GESTURE,true);
        PhoneConnectionManager phoneConnectionManager = PhoneConnectionManager.getInstance();
        IDataDispatcher hudPrefSetDispatcherImp = new HudPrefSetDispatcherImp(mContext,mSpeechOutputer);
        phoneConnectionManager.addDataDispatcher(hudPrefSetDispatcherImp);
        mStateListener = new HudStateListener();
        mTelephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        openGPS();
        openBlueTooth();
    //    updateBTStatus();
        updateRecordStatus();
    }

    public void unBind(){
        mContext.unregisterReceiver(mStatusBroadcastReceiver);
    }

    public void onPause(){
        mTelephonyManager.listen(mStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public void onResume(){
        mTelephonyManager.listen(mStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }


    //强制打开当前设备的蓝牙
    private void openBlueTooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter!=null && !bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
    }

    //强制打开当前设备GPS
    private void openGPS(){
        LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPS){
            Intent GPSIntent = new Intent();
            GPSIntent.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
            GPSIntent.setData(Uri.parse("custom:3"));
            try {
                PendingIntent.getBroadcast(mContext, 0, GPSIntent, 0).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                HaloLogger.postE(EndpointsConstants.NAVI_TAG,"强制打开GPS定位设置异常");
                HaloLogger.uploadCatchException(e);
            }
        }
    }


    private void updateRecordStatus(){
        try {
            int stat = Settings.System.getInt(mContext.getContentResolver(),"RecordStatus");
            mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.RECORD,stat==1);
            HudRecordStatus = stat == 1;
            HaloLogger.postI(EndpointsConstants.RECORD_TAG,"record_status:"+(stat==1));
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            HaloLogger.postE(EndpointsConstants.RECORD_TAG,"未找到行车记录仪录制状态的系统参数");
        //    HaloLogger.uploadCatchException(e);
        }
    }

    private void updateBTStatus(){
        try {
            int stat = Settings.System.getInt(mContext.getContentResolver(),"BTConnectStatus");
            mHudStatusCallBack.onHudStatusUpdate(HudStatusItemType.BLUETOOTH,stat==1);
            HudBTCallStatus = stat == 1 ? BLUETOOTH_CONTACTS_CONNECT : BLUETOOTH_CALL_IDILE;
            HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"bt_status:"+(stat==1));
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            HaloLogger.postE(EndpointsConstants.RECORD_TAG,"未找到蓝牙电话系统参数");
        //    HaloLogger.uploadCatchException(e);
        }
    }


    public String getHudPOIName(){
        return HudPOIName;
    }

    public void setHudPOIName(String POIName){
        this.HudPOIName=POIName;
    }

    public int getHudBTContactsStatus(){
        return HudBTContactsStatus;
    }

    public void setHudBTContactsStatus(int hudBTContactsStatus){
        HudBTContactsStatus = hudBTContactsStatus;
    }

    public static void setHudMusicStat(int stat){
        HudMusicStatus = stat;
        HudIOConstants.HudMusicStat = stat;
        if(stat == HudStatusManager.MUSIC_IDILE){
            HudIOConstants.MusicPauseStat = 0;
            HudIOConstants.MusicPlayStat = 0;
        }else if(stat == HudStatusManager.MUSIC_PLAY){
            HudIOConstants.MusicPauseStat = 0;
            HudIOConstants.MusicPlayStat = 0;
        }
    }

    public class HudStateListener extends PhoneStateListener{

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int strength =signalStrength.getGsmSignalStrength();
            if(strength > 0 && strength < 32){
                int level = strength / GSM_LEVEL_NUM;
                if(HudGSMState != level){
                    HudGSMState = level;
                    mHudStatusCallBack.onGSMStateUpdate(HudGSMState);
                }
            }
        }
    }

}
