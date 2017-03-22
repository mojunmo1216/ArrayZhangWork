package com.haloai.hud.hudendpoint.primarylauncher;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.github.ybq.android.spinkit.sprite.SpriteContainer;
import com.github.ybq.android.spinkit.style.Circle;
import com.haloai.hud.carrecorderlibrary.utils.CarRecorderFileUtils;
import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.HudStatusManager.HudStatus;
import com.haloai.hud.hudendpoint.IHudStatusCallBack;
import com.haloai.hud.hudendpoint.arwaylib.ARWayController;
import com.haloai.hud.hudendpoint.arwaylib.arway.impl_gl.ARwayOpenGLFragment;
import com.haloai.hud.hudendpoint.fragments.amap.HudAMapFragmentMain;
import com.haloai.hud.hudendpoint.fragments.amap.HudAMapFragmentNavi;
import com.haloai.hud.hudendpoint.fragments.common.HudAMapFragmentOutgoing;
import com.haloai.hud.hudendpoint.fragments.common.HudCarRecorderFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudDialogFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudSpeechPanelFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudSpeechPanelFragment.PanelType;
import com.haloai.hud.hudendpoint.fragments.common.HudSpeechReceiverFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudStatusBarFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudStatusBarFragment.HudStatusItemType;
import com.haloai.hud.hudendpoint.services.MusicPlayService;
import com.haloai.hud.hudendpoint.utils.CameraUtil;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.hudendpoint.utils.FileUtils;
import com.haloai.hud.music.MusicSDKAdapter;
import com.haloai.hud.music.XiMaLaYaSDKAdapter;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter.RouteSearchData;
import com.haloai.hud.navigation.NavigationSDKAdapter;
import com.haloai.hud.navigation.NavigationSDKAdapter.NavigationNotifier;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.HudConstants;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.HudIOConstants.HudPromatType;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.gesture.GestureCode;
import com.haloai.huduniversalio.outputer.IHudOutputer.HudOutputContentType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionNaviCallback;
import com.haloai.huduniversalio.speech.ISpeechParserMusic;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class HaloAMapNaviStartActivity extends HaloBaseActvity implements

        NavigationNotifier, IHudStatusCallBack, IHudIOSessionNaviCallback {

    private static final int DELYKEY = 121;
    private static String TAG = "HaloAMapNaviStartActivity";

    private ProgressBar    mLoadingProgressBar;
    private ViewGroup      mLoadingProgressBarParent;
    private RelativeLayout mMainView;

    private HudStatusManager mHudStatusManager;
    private HudAMapFragmentMain mMainFragment;
    private ARwayOpenGLFragment mNavingFragment;
    private HudAMapFragmentOutgoing mOutgoingFragment;
    private HudDialogFragment mDialogFragment;
    private HudCarRecorderFragment mCarRecorderFragment;
    private HudSpeechPanelFragment mSpeechPanelFragment;
    private HudSpeechReceiverFragment mSpeechReceiverFragment;
    private HudStatusBarFragment mStatusBarFragment;
    private HudAMapFragmentNavi mNaviFragment;
//    private HudAMapFragmentNative mNativeFragment;

    private static final int AMAP_BASE = 100;
    private static final int AMAP_UPDATE_MSG_POI_LIST = AMAP_BASE + 0;
    private static final int AMAP_DELAY_SHUT_DOWN = AMAP_BASE + 1;
    private static final int AMAP_CHOOSE_STRATEGY_COUNT_DOWN = AMAP_BASE + 2;
    private static final int AMAP_SHOW_MAIN_FRAGMENT = AMAP_BASE + 3;
    private static final int AMAP_CUSTOM_HIDE_CANCELNAVI = AMAP_BASE + 4;
    private static final int AMAP_TEST_LOCATION_CHANGE_OR_NAVIINFO_UPDATE = AMAP_BASE + 5;
    private static final int AMAP_INCOMING_RINGING = AMAP_BASE + 8;
    private static final int AMAP_DIALOG_HIDE = AMAP_BASE + 9;
    private static final int AMAP_DELAYED_TEST = AMAP_BASE + 10;
    private static final int AMAP_OUTGOING_CANCEL = AMAP_BASE + 11;
    private static final int AMAP_NAVI_START_TIME_OVER = AMAP_BASE + 12;
    private static final int AMAP_DELAYED_MESSAGE = AMAP_BASE + 13;
    private static final int AMAP_DELAYED_EXIT_NAVI = AMAP_BASE + 14;
    private static final int AMAP_DELAYED_START_NAVI = AMAP_BASE + 15;
    private static final int AMAP_SET_MUSIC_VOLUME = AMAP_BASE + 16;
    private static final int AMAP_ARWAY_LOADED = AMAP_BASE + 17;

    private List<String> mCurrentPOIList;
    private int currentPOIPageIndex;
    private int currentPOIPageFlag;
    private boolean mCanOutputGPSInfo = true;
    private boolean mGPSDelayNavi = false;
    private boolean IS_RECONGIZE = false;
    private boolean IS_HUD_PAUSE = false; //光机息屏
    private static final int MAX_SPEECH_PANEL_TIMEOUT_COUNT = 3;
    private static int MAX_SPEECH_PANEL_UNRECOGNISE_COUNT = 3;
    private static int MAX_3RD_SPEECH_PANEL_TIMEOUT_COUNT = 3;
    private static final int MAX_SPEECH_UNRECOGNISE_BACK_COUNT = 2;
    private static final int TIME_SKIP_SPEECH_TIMEOUT = 1000 * 6;

    private int mSpeechPanelTimeoutCount = 0;
    private int mSpeechPanelUnrecogniseCount = 0;
    private int mNavingCalculateCount = 0;
    private long mSpeechProtocolErrorTime = System.currentTimeMillis();
    private boolean mLastProtocolError = false;
    private boolean IS_START_NAVI = false;

    private RouteSearchData mShortestDistance, mFastestTime, mDefaultStrategy;
    private RouteSearchData[] ROUTE_ARRAYS;
    private int mShowCountDownTime;
    private boolean mAdjustMusicVolume = false;
    private AMapNavigationSDKAdapter mAMapNaviAdapter;
    private XiMaLaYaSDKAdapter mXiMaLaYaAdapter;
    private int currentPOIListPageCount;
    private CountDownTimeThread mCountDownTimeThread;
    private AudioManager mAudioManager;
    private int mStrategyIndex;

    private boolean mIsAfterGesturePoint = false;

    public BroadcastReceiver mSDCardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
              //  playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_recorder_secard_no_found), EndpointsConstants.IS_PANEL);
                if (mCarRecorderFragment.getRecorderStatus() > 0) {
                    mHudIOController.endCurrentSession(HudIOSessionType.RECORDER);
                    hideCarRecorder();
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_amap_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        mAMapNaviAdapter = (AMapNavigationSDKAdapter) NavigationSDKAdapter
                .getNavigationAdapter(NavigationSDKAdapter.NAVIGATION_SDK_AMAP, this);

        mXiMaLaYaAdapter = (XiMaLaYaSDKAdapter) MusicSDKAdapter
                .getMusicSDKAdapter(this,MusicSDKAdapter.MUSIC_SDK_XIMALAYA);

        mAMapNaviAdapter.setNaviNotifier(this);

        mMainView = (RelativeLayout)this.findViewById(R.id.amap_main);
        mMainFragment = (HudAMapFragmentMain) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_main);
        mNavingFragment = (ARwayOpenGLFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_naving);
        mOutgoingFragment = (HudAMapFragmentOutgoing) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_outgoing);
        mDialogFragment = (HudDialogFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_dialog);
        mCarRecorderFragment = (HudCarRecorderFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_carrecorder);
        mNaviFragment = (HudAMapFragmentNavi) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_navi);
        mSpeechPanelFragment = (HudSpeechPanelFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_speechpanel);
        mSpeechReceiverFragment = (HudSpeechReceiverFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_receiver);
        mStatusBarFragment = (HudStatusBarFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_status);
     /*   mNativeFragment = (HudAMapFragmentNative) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_native_amap);*/

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mStatusBarFragment);
        ft.hide(mNavingFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechPanelFragment);
        ft.show(mSpeechReceiverFragment);
        ft.hide(mDialogFragment);
        ft.hide(mNaviFragment);
 //       ft.hide(mNativeFragment);
        ft.commitAllowingStateLoss();


        init();//初始化配置

        initMainLayout();//初始化界面布局调整

    }


    private void init(){
        mHudStatusManager = HudStatusManager.getInstance();
        mHudStatusManager.create(getApplicationContext(), this, mHudSpeechOutputer);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        IntentFilter sDCardFilter = new IntentFilter();
        sDCardFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        sDCardFilter.addDataScheme("file");
        registerReceiver(mSDCardReceiver, sDCardFilter);
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_MAIN);//主界面Bugly_TAG
        HaloLogger.postI(EndpointsConstants.NAVI_TAG,"HUD is start! ,thread is "+Thread.currentThread());

    }

    private void initProgressBar(Activity a) {
        mLoadingProgressBar = (ProgressBar) createLayoutProgressBar(a);
        SpriteContainer drawable = new Circle();
        drawable.setBounds(0, 0, 150, 150);
        drawable.setColor(Color.WHITE);

        mLoadingProgressBar.setIndeterminateDrawable(drawable);
        hideLoadingProcessBar();
    }

    private void initMainLayout(){
        //动态设置应用margintop

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMainView.getLayoutParams();
        params.topMargin = DisplayUtil.dip2px(this, EndpointsConstants.MARGIN_TOP);
        mMainView.setLayoutParams(params);

        //状态栏的margin_right

        RelativeLayout.LayoutParams paramRight = (RelativeLayout.LayoutParams) mStatusBarFragment.getView().getLayoutParams();
        paramRight.rightMargin = DisplayUtil.dip2px(this, EndpointsConstants.MARGIN_RIGHT-203);
        mStatusBarFragment.getView().setLayoutParams(paramRight);

        initProgressBar(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        HaloLogger.logE("speech_info", "onSaveInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HaloLogger.logE("speech_info", "onDestroy");
        unregisterReceiver(mSDCardReceiver);
        mHudStatusManager.unBind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHudStatusManager.onPause();
        HaloLogger.logE("speech_info", "onPause");
    }


    @Override
    protected void onStart() {
        super.onStart();
        HaloLogger.logE("speech_info", "onStart");
    }


    @Override
    protected void onStop() {
        super.onStop();
        HaloLogger.logE("speech_info", "onStop");
        /*if (mHudIOController != null && !IS_HUD_PAUSE) {
            IS_HUD_PAUSE = true;
            mHudIOController.pauseHudInputer(HudInputerType.GESTURE_INPUT);
            mHudIOController.pauseHudInputer(HudInputerType.SPEECH_INPUT);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHudStatusManager.onResume();
        HaloLogger.logE("speech_info", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        HaloLogger.logE("speech_info", "onRestart");
        /*if (mHudIOController != null && IS_HUD_PAUSE) {
            IS_HUD_PAUSE = false;
            mHudIOController.resumeHudInputer(HudInputerType.GESTURE_INPUT);
            mHudIOController.resumeHudInputer(HudInputerType.SPEECH_INPUT);
        }*/
    }

    //调试样机代码，别注释
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (HudConstants.IS_GESTURE) {
            if (keyCode == EndpointsConstants.KEYCODE_HALO_GESTURE_ON) {
                mHudIOController.resumeHudInputer(HudInputerType.GESTURE_INPUT);
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_open_gesture), EndpointsConstants.IS_PANEL);
                return true;
            } else if (keyCode == EndpointsConstants.KEYCODE_HALO_GESTURE_OFF) {
                mHudIOController.pauseHudInputer(HudInputerType.GESTURE_INPUT);
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_close_gesture), EndpointsConstants.IS_PANEL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mXiMaLaYaAdapter.getCategories();
        return super.onTouchEvent(event);
    }

    Handler mDelyTimeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    Runnable delyRunnable = new Runnable() {
        @Override
        public void run() {
            onGestureCommand(HudIOConstants.INPUTER_GESTURE_CLOSE_PANEL);
        }
    };

    Handler mAmapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case AMAP_DELAYED_START_NAVI:
                    mAMapNaviAdapter.startNavigateSimulately();
                    break;
                case AMAP_SET_MUSIC_VOLUME:
                    if(mAudioManager != null) mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, msg.arg1,0);
                    break;
                case DELYKEY:
                    displaySpeechPanel(PanelType.MusicControlPanelType);
                    break;
            }
        }
    };

    Runnable mAmapRunnable = new Runnable() {
        @Override
        public void run() {
            HudStatusManager.HudCurrentStatus = HudStatus.MAIN;
            mSpeechReceiverFragment.stopLoading();
            playSpeechTextForCustom("网络超时,请稍候重试",false);
            displayMain();
        }
    };

    private void startTimeTask(int type , long delayMillis){
        switch (type){
            case HudEndPointConstants.GESTURE_TIME:
                mDelyTimeHandler.postDelayed(delyRunnable , delayMillis);
                break;
            case HudEndPointConstants.NETWORK_TIME:
                mAmapHandler.postDelayed(mAmapRunnable, delayMillis);
                break;
        }
    }

    private void cancleTimeTask(int type){
        switch (type){
            case HudEndPointConstants.GESTURE_TIME:
                mDelyTimeHandler.removeCallbacks(delyRunnable);
                break;
            case HudEndPointConstants.NETWORK_TIME:
                mAmapHandler.removeCallbacks(mAmapRunnable);
                break;
            default:
                break;
        }
    }



    @Override
    public void naviSessionSetDestination(String poiNameToSearch,int index,boolean isLoading) {
        // state is poi_show and mainFragment is not in loading state
        if (isLoading) mSpeechReceiverFragment.startLoading();
        mHudStatusManager.setHudPOIName(poiNameToSearch);
        startTimeTask(HudEndPointConstants.NETWORK_TIME,10000);
        if(index>=0){
            mNaviFragment.choosePOI(index);
        }
    }


    @Override
    public void naviSessionChooseStrategy(int index) {
        if (HudStatusManager.HudCurrentStatus == HudStatus.CHOOSE_STRAGETY && index>=0 && index<3) {
            mShowCountDownTime = 10;
            if(mCountDownTimeThread != null)mCountDownTimeThread.setDelayTime(5000);
            if(mStrategyIndex != index){
                playNormalStrategyTTS(ROUTE_ARRAYS[index],IHudIOSessionNaviCallback.STRATEGY_ARRAY[index]);
                mNaviFragment.startStrategyViewAnimation(index);
                mNaviFragment.displayChooseStrategy(index);
                mStrategyIndex = index;
            }
            HaloLogger.postI(EndpointsConstants.NAVI_TAG, "ChooseStrategy:" + IHudIOSessionNaviCallback.STRATEGY_ARRAY[index]);
        }
    }

    @Override
    public void naviSessionChoosePOI(int index) {
        if(HudStatusManager.HudCurrentStatus == HudStatus.POI_SHOW && mCurrentPOIList != null){
            mNaviFragment.choosePOI(index);
            playSpeechTextForNavigation(mCurrentPOIList.get(index));
        }

    }


    @Override
    public void naviSessionStart() {
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        cancleTimeTask(HudEndPointConstants.NETWORK_TIME);
        if(HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING){
            mAMapNaviAdapter.stopNavigate();
        }
        mNavingFragment.stopDrawHudway();
        HudStatusManager.HudCurrentStatus = HudStatus.NAVIGATING;
        mShowCountDownTime = -1;
        mSpeechReceiverFragment.enterNavi();
        Message message = uiAMapNaviInfoUpdateHandler.obtainMessage();
        message.what = AMAP_DELAYED_MESSAGE;
        message.arg1 = HudEndPointConstants.DELAYED_STATR_NAVI;
        uiAMapNaviInfoUpdateHandler.sendMessageDelayed(message,1000);
        IS_START_NAVI = true;
        mGPSDelayNavi = false;
     /*   if(HudStatusManager.HudGPSNum > 0){
            mGPSDelayNavi = false;
            mAmapHandler.postDelayed(mAmapRunnable,10000);
            mAMapNaviAdapter.startNavigate();
        }
*/
    }


    Handler uiAMapNaviInfoUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            FragmentTransaction ft = HaloAMapNaviStartActivity.this.getFragmentManager().beginTransaction();
            switch (msg.what) {
                case AMAP_UPDATE_MSG_POI_LIST:
                    HudStatusManager.HudCurrentStatus = HudStatus.POI_SHOW;
                    mNaviFragment.displayPoiList(mHudStatusManager.getHudPOIName(), mCurrentPOIList,
                            currentPOIPageIndex, currentPOIListPageCount, currentPOIPageFlag);
                    break;
                case AMAP_CHOOSE_STRATEGY_COUNT_DOWN:
                    mNaviFragment.setCountDown(mShowCountDownTime);
                    break;
                case AMAP_SHOW_MAIN_FRAGMENT:
                    ft.show(mMainFragment);
                    ft.commitAllowingStateLoss();
                    break;
                case AMAP_DELAY_SHUT_DOWN:
                    Intent power_off_intent = new Intent("ACTION_SYSTEM_SHUTDOWN");
                    getApplication().sendBroadcast(power_off_intent);
                    break;
                case AMAP_TEST_LOCATION_CHANGE_OR_NAVIINFO_UPDATE:
                    if (msg.obj instanceof AMapNaviLocation) {
                        AMapNaviLocation naviLocation = (AMapNaviLocation) msg.obj;
                        mAMapNaviAdapter.testLocationChange(naviLocation);
                    } else {
                        NaviInfo naviInfo = (NaviInfo) msg.obj;
                        mAMapNaviAdapter.testNaviInfoUpdate(naviInfo);
                    }
                    break;
                case AMAP_CUSTOM_HIDE_CANCELNAVI:
                    break;
                case AMAP_OUTGOING_CANCEL:
                    ft.hide(mOutgoingFragment);
                    ft.commitAllowingStateLoss();
                    break;
                case AMAP_INCOMING_RINGING:
                    displayDialogFragment();
                    mDialogFragment.ringing((String) (msg.obj));
                    break;
                case AMAP_DIALOG_HIDE:
                    HudEndPointConstants.IS_CALLING = false;
                    ft.hide(mDialogFragment);
                    ft.hide(mSpeechPanelFragment);
                    ft.commitAllowingStateLoss();
                    hideSpeechReceiverView();
                    if (HudEndPointConstants.IS_CARCORD) {
                        mCarRecorderFragment.on_offScanWindow(true);
                    }
                    break;
                case AMAP_NAVI_START_TIME_OVER:
                    mHudIOController.endCurrentSession(HudIOSessionType.NAVI);
                    naviSessionStart();
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG, "TimeOver 开始导航 !!!");
                    break;
                case AMAP_DELAYED_MESSAGE:
                    switch (msg.arg1) {
                        case HudEndPointConstants.DELAYED_STATR_NAVI:
                            // 显示导航fragment,
                            mNavingFragment.onARWayStart();
                            mAMapNaviAdapter.startNavigate();
                            showLoadingProcessBar();
                            break;
                        case HudEndPointConstants.DELAYED_NAGATION_CALUTRE:
                            if(msg.arg2 == -1){
                                mAMapNaviAdapter.reCalculate();
                            }else {
                                mAMapNaviAdapter.startNavigate();
                            }
                            mNavingCalculateCount++;
                            break;
                        case HudEndPointConstants.DELAYED_NETWORK_DISCONNECTED:
                            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.speech_network_fail_word), EndpointsConstants.IS_PANEL);
                            mNavingFragment.onNetworkStatusChanged(false);
                            break;
                        case HudEndPointConstants.DELAYED_PLAY_VIDEO:
                            String path = msg.obj.toString();
                            int index = msg.arg2;
                            mCarRecorderFragment.playingByPhone(path, index);
                            break;
                        case HudEndPointConstants.DELAYED_MATCH_POI:
                            int i = msg.arg2;
                            mAMapNaviAdapter.startNavigate(i);
                            break;
                    }
                    break;
                case AMAP_DELAYED_EXIT_NAVI:
                    if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
                        HudStatusManager.HudCurrentStatus = HudStatus.MAIN;
                        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_arrive_estination_exit), EndpointsConstants.IS_PANEL);
                        mMainFragment.IS_SHOW = true;
                        displayMain();
                    }
                    break;
                case AMAP_DELAYED_TEST:

                    break;
                case AMAP_ARWAY_LOADED:
                    hideLoadingProcessBar();
                    break;
                default:
                    break;
            }

        }
    };

    //flag 表示状态是显示还是隐藏
    @Override
    public void onHudStatusUpdate(HudStatusItemType hudStatusItemType, boolean flag) {
     /*   if (hudStatusItemType == HudStatusItemType.FM) {
            if (!flag) {
                mNavingFragment.onNetworkStatusChanged(true);
                MAX_SPEECH_PANEL_UNRECOGNISE_COUNT = 5;
                MAX_3RD_SPEECH_PANEL_TIMEOUT_COUNT = 3;
            } else {
                MAX_SPEECH_PANEL_UNRECOGNISE_COUNT = 2;
                MAX_3RD_SPEECH_PANEL_TIMEOUT_COUNT = 2;
                mNavingFragment.onNetworkStatusChanged(false);
            }
        } else */
        if (hudStatusItemType == HudStatusItemType.BLUETOOTH) {
            if (flag) {
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_connect_tts), EndpointsConstants.IS_PANEL);
            } else {
                switch (HudStatusManager.HudBTCallStatus){
                    case HudStatusManager.BLUETOOTH_CALL_TALKING:
                        mStatusBarFragment.stopTalking();
                        break;
                    case HudStatusManager.BLUETOOTH_CALL_COMING:
                    case HudStatusManager.BLUETOOTH_CALL_DIALING:
                        uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
                        break;
                }
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_disconnect_tts), EndpointsConstants.IS_PANEL);
            }
        }else if(hudStatusItemType == HudStatusItemType.RECORD){
            mStatusBarFragment.updateRecord(flag);
            return;
        }
        mStatusBarFragment.updateStatusBar(hudStatusItemType, flag);
        HaloLogger.logE("status_info",hudStatusItemType+":"+flag);
    }

    @Override
    public void onMusicStatusUpdate(int index) {
        mStatusBarFragment.changeMusicStatus(index);
    }

    @Override
    public void onBTCalling() {
        if (HudStatusManager.HudBTCallStatus == HudStatusManager.BLUETOOTH_CALL_DIALING) {
            HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_TALKING;
            mHudIOController.endCurrentSession(HudIOSessionType.OUTGOING_CALL);
            mStatusBarFragment.startTalking();
            uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
        } else if (HudStatusManager.HudBTCallStatus == HudStatusManager.BLUETOOTH_CALL_COMING) {
            HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_TALKING;
            mStatusBarFragment.startTalking();
            mHudIOController.endCurrentSession(HudIOSessionType.INCOMING_CALL);
            uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
        }
    }

    @Override
    public void onGSMStateUpdate(int level) {
        if(mStatusBarFragment != null){
            mStatusBarFragment.GSMStateUpdate(level);
        }
    }

    private class CountDownTimeThread extends Thread {
        private int mDelayTime = 0;
        private boolean mIsRestart;

        public CountDownTimeThread(int delayTime) {
            this.mDelayTime = delayTime;
            this.mIsRestart = true;
            mShowCountDownTime = 10;
        }

        public void setDelayTime(int delayTime) {
            this.mDelayTime = delayTime;
            this.mIsRestart = true;
        }

        public void run() {
            while (mShowCountDownTime > 0) {
                if (mIsRestart) {
                    SystemClock.sleep(mDelayTime);
                    mIsRestart = false;
                }
                if (mHudIOController != null) {
                    if (mHudIOController.getCurrentSessionType() == HudIOSessionType.INCOMING_CALL ||
                            mHudIOController.getCurrentSessionType() == HudIOSessionType.OUTGOING_CALL || !mSpeechPanelFragment.isHidden()) {
                        mShowCountDownTime++;
                    }
                }
                mShowCountDownTime--;
                uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_CHOOSE_STRATEGY_COUNT_DOWN);
                SystemClock.sleep(1000);
            }
            if (mShowCountDownTime == 0) {
                if (HudStatusManager.HudCurrentStatus == HudStatus.CHOOSE_STRAGETY) {
                    uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_NAVI_START_TIME_OVER);
                }

            }
        }

    }

    private void playStrategySpeechText(RouteSearchData routeData) {
        String string;
        int min = (int) (routeData.duration / 60);
        if (min < 60) {
            string = "耗时" + min + "分钟";
        } else {
            String buf = min % 60 == 0 ? "" : min % 60 + "分钟";
            string = "耗时" + min / 60 + "小时" + buf;
        }
        StringBuffer buffer = new StringBuffer("目的地，");
        buffer.append(mHudStatusManager.getHudPOIName());
   //     buffer.append("已为您推荐路线,");
        buffer.append(string);
        buffer.append("，你可以说第几个或者路线名");
        playSpeechTextForCustom(buffer.toString(), false);
    }

    private void playNormalStrategyTTS(RouteSearchData routeData, String strategy) {
        String string;
        int min = (int) (routeData.duration / 60);
        if (min < 60) {
            string = "耗时" + min + "分钟";
        } else {
            String buf = min % 60 == 0 ? "" : min % 60 + "分钟";
            string = "耗时" + min / 60 + "小时" + buf;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(strategy);
        buffer.append("路线，");
        buffer.append(string);
        playSpeechTextForCustom(buffer.toString(), false);
    }

    private void playSpeechTextForNavigation(String text) {
        mHudSpeechOutputer.output(text, HudOutputContentType.CONTENT_TYPE_NAVIGATION, false);
        HaloLogger.logE("speech_info", "TTS:" + text);
    }

    private void playSpeechTextForCustom(String text, boolean keepRecongize) {
        mHudSpeechOutputer.output(text, HudOutputContentType.CONTENT_TYPE_CUSTOM, keepRecongize);
        HaloLogger.logE("speech_info", "TTS:" + text);
    }

    // TODO --------------------------------------------change
    // fragmentstart------------------------------------------//

    private void displayMain() {
        hideLoadingProcessBar();
        EndpointsConstants.IS_NAVI = false;
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
      //  mSpeechReceiverFragment.recoverHomeState();
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mNavingFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mDialogFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mNaviFragment);
 //       ft.hide(mNativeFragment);
        ft.hide(mSpeechReceiverFragment);
        ft.show(mMainFragment);
        ft.commitAllowingStateLoss();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
            HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_IDILE;
            commandSessionPlay(HudCommandType.PLAY);
        }
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_MAIN);//主界面Bugly_TAG
    }


    private void displayNavingStrategyChoose() {
        mSpeechReceiverFragment.displayReciver(true);
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mNaviFragment);
        ft.show(mSpeechReceiverFragment);
        ft.hide(mNavingFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mDialogFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechPanelFragment);
        ft.hide(mMainFragment);
 //       ft.hide(mNativeFragment);
        ft.commitAllowingStateLoss();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
            HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_IDILE;
            commandSessionPlay(HudCommandType.PAUSE);
        }
    }

    private void displayNaving() {
        EndpointsConstants.IS_NAVI = true;
        EndpointsConstants.IS_PANEL = false;
        IS_START_NAVI = false;
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mMainFragment);
        ft.hide(mNaviFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mDialogFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechReceiverFragment);
        ft.hide(mSpeechPanelFragment);
        ft.show(mNavingFragment);
  //      ft.hide(mNativeFragment);
        ft.commitAllowingStateLoss();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
            HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_IDILE;
            commandSessionPlay(HudCommandType.PLAY);
        }
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_ARWay);//ARWay Bugly_TAG
    }

    // TODO: 2016/10/18  
    private void displayNativeNaving() {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mMainFragment);
        ft.hide(mNaviFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mDialogFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechReceiverFragment);
        ft.hide(mSpeechPanelFragment);
        ft.hide(mNavingFragment);
  //      ft.show(mNativeFragment);
        ft.commitAllowingStateLoss();
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_ARWay);
    }


    private void displayNaviPOIList() {
        mSpeechReceiverFragment.displayReciver(true);
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mNavingFragment);
 //       ft.hide(mNativeFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mDialogFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechPanelFragment);
        ft.hide(mMainFragment);
        ft.show(mSpeechReceiverFragment);
        ft.show(mNaviFragment);
        ft.commitAllowingStateLoss();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
            HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_IDILE;
            commandSessionPlay(HudCommandType.PAUSE);
        }
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_NAVI);//规划导航Bugly_TAG
    }


    private void displayOutgoing() {
        mSpeechReceiverFragment.displayReciver(true);
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        if (HudEndPointConstants.IS_CARCORD) {
            mCarRecorderFragment.on_offScanWindow(false);
        }
        HudEndPointConstants.IS_OUTCALL = true;
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mOutgoingFragment);
        ft.show(mSpeechReceiverFragment);
        ft.hide(mSpeechPanelFragment);
        ft.commitAllowingStateLoss();
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_CALL);//蓝牙电话 Bugly_TAG
    }

    private void displayDialogFragment() {
        HudEndPointConstants.IS_CALLING = true;
        mSpeechReceiverFragment.displayReciver(true);
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        if (HudEndPointConstants.IS_CARCORD) {
            mCarRecorderFragment.on_offScanWindow(false);
        }
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mDialogFragment);
        ft.show(mSpeechReceiverFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mSpeechPanelFragment);
        ft.commitAllowingStateLoss();
    }

    private void displayCarRecorder() {
        mSpeechReceiverFragment.displayReciver(true);
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        mIsAfterGesturePoint = false;
        HudEndPointConstants.IS_CARCORD = true;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HudStatusManager.MUSIC_VLUME, 0);
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mCarRecorderFragment);
        ft.show(mSpeechReceiverFragment);
 //       ft.hide(mNativeFragment);
        ft.hide(mSpeechPanelFragment);
        ft.commitAllowingStateLoss();
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_RECORD);//行车记录仪 Bugly_TAG
    }

    private void hideDialogFragment() {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mDialogFragment);
        ft.commitAllowingStateLoss();
    }

    private void displaySpeechPanel(HudSpeechPanelFragment.PanelType panelType) {
        hideLoadingProcessBar();
        EndpointsConstants.IS_PANEL = true;
        mSpeechReceiverFragment.displayReciver(true);
        if (mSpeechPanelFragment.isHidden()) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.show(mSpeechPanelFragment);
            ft.show(mSpeechReceiverFragment);
            ft.commitAllowingStateLoss();
        }
        if (HudEndPointConstants.IS_CARCORD) {
            mCarRecorderFragment.on_offScanWindow(false);
        }
        mSpeechReceiverFragment.stopLoading();
        mSpeechPanelTimeoutCount = 0;
        mSpeechPanelUnrecogniseCount = 0;
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY&&panelType!=PanelType.MusicControlPanelType) {
            HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_IDILE;
            int flag = HudEndPointConstants.MUSIC_SERVICE_PAUSE;
            mStatusBarFragment.stopMusicIconRolling();
            Intent intent = new Intent(HudEndPointConstants.ACTION_MUSIC_SERVICE);
            intent.putExtra(HudEndPointConstants.MUSIC_SERVICE_FLAG, flag);
            this.sendBroadcast(intent);
        }
        mSpeechPanelFragment.upgradePanel(panelType);
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_PANEL);//语音面板 BUGLY_TAG
    }

    private void playSpeechMenuTTS(HudSpeechPanelFragment.PanelType type) {
        switch (type) {
            case MainPanelType:
                if (HudStatusManager.IS_NETWORK_CONNECTED) {
                    playTTS(R.string.tts_normal_recognized_default, true);
                } else {
                    playTTS(R.string.speech_network_fail_tts, true);
                }
                break;
            case NaviPanelType:
                if (HudStatusManager.IS_NETWORK_CONNECTED) {
                    playTTS(R.string.tts_recognized_navigation, true);
                } else {
                    playTTS(R.string.speech_network_fail_navi_tts, true);
                }
                break;
            case MusicPanelType:
                if (HudStatusManager.IS_NETWORK_CONNECTED) {
                    playTTS(R.string.tts_recognized_music, true);
                } else {
                    playTTS(R.string.speech_network_fail_music_tts, true);
                }
                break;
            case CallPanelType:
                if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLED) {
                    playTTS(R.string.tts_recognized_call, true);
                } else if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLING) {
                    playTTS(R.string.speech_bluetooth_call_compling_tts, true);
                } else {
                    playTTS(R.string.speech_bluetooth_fail_call_tts, true);
                }
                break;
            default:
                if (HudStatusManager.IS_NETWORK_CONNECTED) {
                    playTTS(R.string.tts_normal_recognized_default, true);
                } else {
                    playTTS(R.string.speech_network_fail_tts, true);
                }
                break;
        }
    }

    private void onSpeechPanelUnrecogized(String errorMessage) {
        //TODO 如何防止反复调用
        if (mSpeechPanelFragment.isHidden()) {//主动弹出语音菜单
            resetSpeechPanel();
            displaySpeechPanel(HudSpeechPanelFragment.PanelType.MainPanelType);
        }
        //每次未识别均播报，包括已经在语音菜单的情况下
        if ((++mSpeechPanelUnrecogniseCount) >= MAX_SPEECH_PANEL_UNRECOGNISE_COUNT) {
            hideSpeechPanel();
            mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
            playTTS(R.string.tts_error_timeout_back_default_view, false);
        } else {
            playSpeechTextForCustom(errorMessage, true);
        }
        HaloLogger.logI(TAG, "超时次数： " + mSpeechPanelTimeoutCount + " 未识别" + mSpeechPanelUnrecogniseCount);
    }

    private void judgeHideSpeechPanel(SpeechEngineErrorCode errorCode) {
        //TODO 如果超时处理和识别语音错误发生时间相关不大，会造成播音打断
        HaloLogger.logE("speech_info", "judgeHideSpeechPanel:" + errorCode);
        boolean playtts = false;
        if ((!mSpeechPanelFragment.isHidden())) {
            if (errorCode == SpeechEngineErrorCode.SPEECH_ASR_TIMEOUT) {//超时处理
                long currentTime = System.currentTimeMillis();
                HaloLogger.logI(TAG, "识别超时: timeout error" + "Time is :" + currentTime);
                boolean needReturn = false;
                //如果protocol error最后一次出现，且与protocol error 调用间隔小于6S,不处理本次逻辑
                if (((currentTime - mSpeechProtocolErrorTime) < TIME_SKIP_SPEECH_TIMEOUT) && mLastProtocolError) {
//					needReturn = true;
                }
                mSpeechProtocolErrorTime = currentTime;
                mLastProtocolError = false;
                if (needReturn) {
                    return;
                }
                mLastProtocolError = false;
                if (mSpeechPanelFragment.getCurrentPanelType() == HudSpeechPanelFragment.PanelType.MainPanelType) {//二级菜单处理
                    if ((++mSpeechPanelTimeoutCount) >= MAX_SPEECH_PANEL_TIMEOUT_COUNT) {
                        hideSpeechPanel();
                        playTTS(R.string.tts_error_timeout_back_default_view, false);
                        mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                    } else {
                        playtts = true;
                    }
                } else {//三级菜单处理
                    if ((mSpeechPanelTimeoutCount++) >= MAX_3RD_SPEECH_PANEL_TIMEOUT_COUNT) {
                        hideSpeechPanel();
                        playTTS(R.string.tts_error_timeout_back_default_view, false);
                        mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                    } else {
                        playtts = true;
                    }
                }
            } else if (errorCode == SpeechEngineErrorCode.SPEECH_PROTOCAL_ERROR || errorCode == SpeechEngineErrorCode.SPEECH_NULL_PROTOCAL_ERROR) {//识别语音错误
                mLastProtocolError = true;
                mSpeechProtocolErrorTime = System.currentTimeMillis();
                HaloLogger.logI(TAG, "识别语音错误: protocal error" + "Time is :" + mSpeechProtocolErrorTime);
                if ((++mSpeechPanelUnrecogniseCount) >= MAX_SPEECH_PANEL_UNRECOGNISE_COUNT) {
                    hideSpeechPanel();
                    mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                    playTTS(R.string.tts_error_timeout_back_default_view, false);
                } else {
                    HaloLogger.logI(TAG, "识别语音错误: protocal error");
                    if (HudStatusManager.IS_NETWORK_CONNECTED) {
                        playTTS(R.string.tts_error_null_protocal, true);
                    } else {
                        playtts = true;
                        //  playTTS(R.string.speech_network_fail_tts, true);
                    }
                }
            }
            if (playtts) {
                playSpeechMenuTTS(mSpeechPanelFragment.getCurrentPanelType());
            }

        } else {
            resetSpeechPanel();
                displaySpeechPanel(HudSpeechPanelFragment.PanelType.MainPanelType);
            playTTS(R.string.wakeup_prompt, true);
        }
        HaloLogger.logI("speech_info", "超时次数： " + mSpeechPanelTimeoutCount + " 未识别" + mSpeechPanelUnrecogniseCount);

    }

    private void hideSpeechPanel() {
        mIsAfterGesturePoint = false;
        EndpointsConstants.IS_PANEL = false;
        cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
        if (!mSpeechPanelFragment.isHidden()) {
            resetSpeechPanel();
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.hide(mSpeechPanelFragment);
            ft.commitAllowingStateLoss();
        }
        hideSpeechReceiverView();
        if (HudEndPointConstants.IS_CARCORD) {
            mCarRecorderFragment.on_offScanWindow(true);
        }
        if ((HudStatusManager.HudCurrentStatus == HudStatus.MAIN ||HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING)) {
            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE && HudIOConstants.OPERAT_MUSIC_IDILE >= HudIOConstants.MusicPauseStat) {
                int flag = HudEndPointConstants.MUSIC_SERVICE_PLAY;
                mStatusBarFragment.startMusicIconRolling();
                Intent intent = new Intent(HudEndPointConstants.ACTION_MUSIC_SERVICE);
                intent.putExtra(HudEndPointConstants.MUSIC_SERVICE_FLAG, flag);
                this.sendBroadcast(intent);
            }
        }
    }

    private void resetSpeechPanel() {
        mSpeechPanelTimeoutCount = 0;
        mSpeechPanelUnrecogniseCount = 0;
    }

    private void hideCarRecorder() {
        HudEndPointConstants.IS_CARCORD = false;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HudStatusManager.HudMusicVlume, 0);
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.hide(mCarRecorderFragment);
        ft.commitAllowingStateLoss();
    }

    private void hideSpeechReceiverView() {
        if( !HudEndPointConstants.IS_CARCORD && !HudEndPointConstants.IS_OUTCALL && !HudEndPointConstants.IS_CALLING){
            if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING ) {
                navingShowHideDistancePanel(true);
             //  mSpeechReceiverFragment.enterNavi();
                 hideSpeechReceiver();
            } else if (HudStatusManager.HudCurrentStatus == HudStatus.MAIN) {
             //   mSpeechReceiverFragment.recoverHomeState();
                  hideSpeechReceiver();
            }
        }
    }

    private void displaySpeechReceiver() {
        if (mSpeechReceiverFragment.isHidden()) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.show(mSpeechReceiverFragment);
            ft.commitAllowingStateLoss();
        }

    }

    public void hideSpeechReceiver() {
        if (!mSpeechReceiverFragment.isHidden()) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.hide(mSpeechReceiverFragment);
            ft.commitAllowingStateLoss();
        }
    }

    private void showSpeechReceiverView() {
        if (HudStatusManager.HudCurrentStatus == HudStatus.MAIN && !mMainFragment.isHidden()) {
            mSpeechReceiverFragment.homeAnimator(false);
        } else if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING && (!mNavingFragment.isHidden())) {
            navingShowHideDistancePanel(false);
            mSpeechReceiverFragment.naviToSecondMenu();
        }
    }

    // -------------------------change fragment end--------------------------------//

    //  -------------------------navi info return start----------------------------------//
    @Override
    public void onNaviInfo(NaviInfo info) {
        HaloLogger.logE("sen_debug_navi","onNaviInfo called");
        if (HudStatusManager.HudCurrentStatus.ordinal() > HudStatus.CHOOSE_STRAGETY.ordinal()) {
            try {
                mNavingFragment.updateRouteInfo(info.getIconType(),
                    info.getCurStepRetainDistance());
                mNavingFragment.updateTimeAndDistance(info.getPathRetainTime(),
                    info.getPathRetainDistance());
                mNavingFragment.updateCurPointIndex(info.getCurPoint(),
                    info.getCurStep());
                mNavingFragment.updateNaviInfo(info);
                mSpeechPanelFragment.updateNaviInfo(info);
                mDialogFragment.updateNaviInfo(info);
            }catch (Exception e){
                 HaloLogger.postE(EndpointsConstants.ARWAY_TAG, "NaviInfo异常");
                e.printStackTrace();
                HaloLogger.uploadCatchException(e);
            }
        }
    }


    @Override
    public void onSpeedUpgraded(float speed) {
        // m/s to km/h
        int kmSpeed = (int) (speed * 3.6);
        ARWayController.SpeedBeanUpdater.setSpeed(kmSpeed);
        /*
        * 车速高于 30km/h 时，达到1S时,隐藏“您好，魔方”字样
		* 车速降到 0km/h 时，开始计时，达到5S时，重新显示“您好，魔方”字样
		* */
        HaloLogger.logE("speed_upgrad", "speed:" + speed);
        mNavingFragment.onSpeedUpgraded(speed);
        mMainFragment.updateSpeed((int) speed);
    }

    @Override
    public void onLocationChanged(Object location) {
        if (!(location instanceof AMapNaviLocation)) {
            throw new IllegalArgumentException(
                    "Request the AMapNaviLocation instance");
        }
        HaloLogger.logE(TAG,"ordinal1:"+HudStatusManager.HudCurrentStatus.ordinal()+"ordinal2:"+HudStatus.CHOOSE_STRAGETY.ordinal());
        if (HudStatusManager.HudCurrentStatus.ordinal() > HudStatus.CHOOSE_STRAGETY.ordinal()) {
            mNavingFragment.updateLocation((AMapNaviLocation) location);
            HaloLogger.logE(TAG,"onLocationChanged:"+location.toString());
        }
    }

    @Override
    public void onPathCalculated(Object aMapNavi) {
        cancleTimeTask(HudEndPointConstants.NETWORK_TIME);
     //   playSpeechTextForNavigation("路径计算完成");
        if(IS_START_NAVI) displayNaving();
        mNavingCalculateCount = 0;
        if (!(aMapNavi instanceof AMapNavi)) {
            throw new IllegalArgumentException("Request AMapNavi instance");
        }
        try {
            HaloLogger.postI(EndpointsConstants.ARWAY_TAG, "onPathCalculated called :thread is "+Thread.currentThread());
            HaloLogger.postI(EndpointsConstants.ARWAY_TAG, "导航路径计算成功:"+ ((AMapNavi) aMapNavi).getNaviPath().getSteps().size());
            mNavingFragment.updatePath((AMapNavi) aMapNavi);

        }catch (Exception e){
            HaloLogger.postE(EndpointsConstants.ARWAY_TAG, "计算导航路径异常");
            e.printStackTrace();
            HaloLogger.uploadCatchException(e);
        }
        uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_ARWAY_LOADED);

    }

    @Override
    public void onCrossShow(Bitmap cross) {
        if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
//            mNavingFragment.showCrossImage(cross);
            //mSpeedFragment.hide();
        }
    }

    @Override
    public void setFinishOnTouchOutside(boolean finish) {
        super.setFinishOnTouchOutside(finish);
    }

    @Override
    public void onCrossHide() {
        if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
            mNavingFragment.hideCrossImage();
            //mSpeedFragment.show();
        }
    }

    @Override
    public void onSatelliteFound(int satelliteNum) {
        if (satelliteNum == 0 && mCanOutputGPSInfo) {
            playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_gps_signal_week));
            mCanOutputGPSInfo = false;
        }

     /*  if (satelliteNum > 0 && mGPSDelayNavi) {
            mGPSDelayNavi = false;
            mAmapHandler.postDelayed(mAmapRunnable,15000);
            mAMapNaviAdapter.startNavigate();
            HaloLogger.logE("speech_info", "GPSDelayNavi");
        }
        */
        if (satelliteNum > 5 && !mCanOutputGPSInfo) {
            mCanOutputGPSInfo = true;
        }
        if (satelliteNum < 4) {
            mNavingFragment.onGpsStatusChanged(false);
        } else {
            mNavingFragment.onGpsStatusChanged(true);
        }
        mStatusBarFragment.updateGPSNum(satelliteNum);
        mHudStatusManager.HudGPSNum = satelliteNum;
    }

    @Override
    public void onYawStart() {
        /**偏航时，保持在ARWAY最开始开始阶段,reset即可，准备重新开始**/
        HaloLogger.logE("sen_debug_yaw", "onYawStart called");
        mNavingFragment.updateYawStart();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_nav_calculate_onyaw), EndpointsConstants.IS_PANEL);
    }

    @Override
    public void onYawEnd() {
        HaloLogger.logE("sen_debug_yaw", "onYawEnd called");
        mNavingFragment.updateYawEnd();
    }

    @Override
    public void onNaviText(NaviTextType textType, String text) {
        HaloLogger.logE("onNaviText", text);
        mNavingFragment.updateNaviText(textType, text);
        if (textType == NaviTextType.FrontTrafficText) {

        } else if (textType == NaviTextType.NaivInfoText) {

            if (!EndpointsConstants.IS_PANEL ) {
                if (text.contains("准备出发")) {
                    playSpeechTextForCustom(text, false);
                } else {
                    String string = text.replace("多发地", "多发第");
                    playSpeechTextForNavigation(string);
                }
            }
        } else if (textType == NaviTextType.WholeTrafficText) {

        }
    }

    @Override
    public void onNaviCalculateRouteFailure(int errorInfo) {
       // mNavingFragment.onNaviCalculateRouteFailure(errorInfo);
        // TODO: 16/6/24 计算路径重新计算三次，四次后加提示，还未做
        cancleTimeTask(HudEndPointConstants.NETWORK_TIME);
        if(mNavingCalculateCount < 3){
            HaloLogger.logE("speech_info", "onNaviCalculateRouteFailure:" + mNavingCalculateCount);
            Message msg = uiAMapNaviInfoUpdateHandler.obtainMessage();
            msg.what = AMAP_DELAYED_MESSAGE;
            msg.arg1 = HudEndPointConstants.DELAYED_NAGATION_CALUTRE;
            msg.arg2 = errorInfo;
            uiAMapNaviInfoUpdateHandler.sendMessageDelayed(msg, 3000);
        }else{
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_naving_calculate_route_failure), false);
            displayMain();
            String str = "1";
            int i = Integer.valueOf(str);
        }
        HaloLogger.postE(EndpointsConstants.NAVI_TAG,"导航路径计算失败:"+errorInfo);
    }


    @Override
    public void onReCalculateRouteForTrafficJam() {
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_re_planing_the_route), false);
    }

    @Override
    public void onArriveDestination() {
        mNavingFragment.onArriveDestination();
        uiAMapNaviInfoUpdateHandler.sendEmptyMessageDelayed(AMAP_DELAYED_EXIT_NAVI, 3000);
        HaloLogger.postI(EndpointsConstants.ARWAY_TAG, "到达目的地");
    }

    @Override
    public void naviSessionPoiItemNextPage(List<String> currentPOIList, int currentPageIndex) {
        if (HudStatusManager.HudCurrentStatus == HudStatus.POI_SHOW) {
            this.currentPOIPageIndex = currentPageIndex;
            this.currentPOIPageFlag = 1;
            mCurrentPOIList = currentPOIList;
            uiAMapNaviInfoUpdateHandler
                    .sendEmptyMessage(AMAP_UPDATE_MSG_POI_LIST);
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.amap_poilist_choose_next), false);

        }
    }


    @Override
    public void naviSessionPoiItemPrevPage(List<String> currentPOIList, int currentPageIndex) {
        if (HudStatusManager.HudCurrentStatus == HudStatus.POI_SHOW) {
            this.currentPOIPageIndex = currentPageIndex;
            this.currentPOIPageFlag = -1;
            mCurrentPOIList = currentPOIList;
            uiAMapNaviInfoUpdateHandler
                    .sendEmptyMessage(AMAP_UPDATE_MSG_POI_LIST);
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.amap_poilist_choose_prev), false);
        }
    }

    @Override
    public boolean onSessionInvalidInput(HudInputerType inputType, String input, String suggestion) {
        return false;
    }


    @Override
    public void onSessionIncomingCallRinging(String contactName) {
        HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_COMING;
        displaySpeechReceiver();
        showSpeechReceiverView();
        Message msg = Message.obtain();
        msg.what = AMAP_INCOMING_RINGING;
        msg.obj = contactName;
        uiAMapNaviInfoUpdateHandler.sendMessage(msg);
        playSpeechTextForCustom(String.format(SpeechResourceManager.getInstanse().getString(R.string.call_speech_incomming), contactName), false);
    }

    @Override
    public void onSessionIncomingCallAnswered() {
        Intent intent = new Intent(HudIOConstants.ACTION_BTCALL_OUTCALL);
        intent.putExtra(HudIOConstants.ACTION_TYPE, HudIOConstants.CALL_ANSWER);
        this.sendBroadcast(intent);
    }

    @Override
    public void onSessionIncomingCallRejected() {
        HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_IDILE;
        Intent intent = new Intent(HudIOConstants.ACTION_BTCALL_OUTCALL);
        intent.putExtra(HudIOConstants.ACTION_TYPE, HudIOConstants.CALL_REFUSE);
        this.sendBroadcast(intent);
        //mDialogFragment.disappearDialogGradually();
        uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
    }

    @Override
    public void onSessionOutgoingCallContactList(List<String> contactList, int pagCount) {
        resetSpeechPanel();
        mHudIOController.continueCurrentSession(HudIOSessionType.OUTGOING_CALL);
        mOutgoingFragment.showContactNames(contactList, pagCount);
        HaloLogger.logE("speech_info", contactList.toString());
        displayOutgoing();
    }

    @Override
    public void onSessionOutgoingCalling(String name, String phone, boolean isHud) {
        if (isHud) {
            Intent intent = new Intent(HudIOConstants.ACTION_BTCALL_OUTCALL);
            intent.putExtra(HudIOConstants.ACTION_TYPE, HudIOConstants.CALL_DIAL);
            intent.putExtra("number", phone);
            intent.putExtra("name", name);
            this.sendBroadcast(intent);
            HaloLogger.postI(EndpointsConstants.BTCALL_TAG, "语音拨号:" + name);
        }
        HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_DIALING;
        mHudIOController.continueCurrentSession(HudIOSessionType.OUTGOING_CALL);
        resetSpeechPanel();
        displayDialogFragment();
        mDialogFragment.showSingleStatus(name, phone);
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
            HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_CALL;
            commandSessionPlay(HudCommandType.PAUSE);
        }
    }

    @Override
    public void onSessionOutgoingCallPhoneNumList(String name, List<String> phoneList) {
        resetSpeechPanel();
        mHudIOController.continueCurrentSession(HudIOSessionType.OUTGOING_CALL);
        mOutgoingFragment.showContactNumbers(name, phoneList);
        displayOutgoing();
    }

    @Override
    public void onSessionOutgoingCallEnd(boolean isCancelled) {
        Message msg = Message.obtain();
        if (isCancelled) {
            msg.what = AMAP_OUTGOING_CANCEL;
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_speech_exit), false);
        } else {
            msg.what = AMAP_DIALOG_HIDE;
            HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_IDILE;
            Intent intent = new Intent(HudIOConstants.ACTION_BTCALL_OUTCALL);
            intent.putExtra(HudIOConstants.ACTION_TYPE, HudIOConstants.CALL_HANGUP);
            this.sendBroadcast(intent);
        }
        uiAMapNaviInfoUpdateHandler.sendMessage(msg);
    }

    @Override
    public void onSessionOutgoingCallNextPage() {
        mOutgoingFragment.nextPage();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.amap_poilist_choose_next), false);
    }

    @Override
    public void onSessionOutgoingCallPrevPage() {
        mOutgoingFragment.prePage();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.amap_poilist_choose_prev), false);
    }
    //-----------------------outgoing end ---------------------

    @Override
    public void onSessionCallHangUp() {
        HaloLogger.logE("speech_info", "HangUp HudBTCallStatus:" + HudStatusManager.HudBTCallStatus);
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.call_speech_hangup), EndpointsConstants.IS_PANEL);
        if (HudStatusManager.HudBTCallStatus == HudStatusManager.BLUETOOTH_CALL_COMING) {
            uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
        } else if (HudStatusManager.HudBTCallStatus == HudStatusManager.BLUETOOTH_CALL_DIALING) {
            onSessionOutgoingCallEnd(false);
        } else if (HudStatusManager.HudBTCallStatus == HudStatusManager.BLUETOOTH_CALL_TALKING) {
            mStatusBarFragment.stopTalking();
        }
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
            HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_CALL;
            commandSessionPlay(HudCommandType.PLAY);
        }
        //mDialogFragment.disappearDialogGradually();
        HudStatusManager.HudBTCallStatus = HudStatusManager.BLUETOOTH_CALL_IDILE;
    }

    @Override
    public void onSpeechEngineReady() {
        if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLING) {
            mHudStatusManager.setHudBTContactsStatus(HudStatusManager.BLUETOOTH_CONTACTS_COMPLED);
            mStatusBarFragment.updateStatusBar(HudStatusItemType.BLUETOOTH,true);
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_bluetooth_download_finish), !mSpeechPanelFragment.isHidden());
        }
    }

/*
    @Override
    public void exitSessionBegin() {
        // 显示确定还是取消
        resetSpeechPanel();
        displayDialogFragment();
        mDialogFragment.showYesOrNoDialog(SpeechResourceManager.getInstanse().getString(R.string.navi_exit_prompt) + "?");
        playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.navi_exit_prompt));
        uiAMapNaviInfoUpdateHandler.sendEmptyMessageDelayed(AMAP_CUSTOM_HIDE_CANCELNAVI, 13000);
    }
*/

    @Override
    public void exitSessionConfirm() {
        hideSpeechPanel();
        // 用户说出确定
        hideSpeechPanel();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_exit_confirm), false);
        HudStatusManager.HudCurrentStatus = HudStatus.MAIN;
        mAMapNaviAdapter.stopNavigate();
        mNavingFragment.stopDrawHudway();
        mMainFragment.IS_SHOW = true;
        displayMain();
    }

 /*   @Override
    public void exitSessionCancel() {
        // 用户说出取消
        naviSessionCancel();
    }
*/
/*    private void naviSessionCancel() {
        if (HudStatusManager.HudCurrentStatus == HudStatus.CANCEL_NAVI) {
            HudStatusManager.HudCurrentStatus = HudStatus.NAVIGATING;
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_exit_cancel), false);
            uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_DIALOG_HIDE);
        }
    }*/

    //三级语音菜单
    @Override
    public void  dispatcherSessionPromat(HudPromatType hudPromatType) {
        HudSpeechPanelFragment.PanelType panelType = HudSpeechPanelFragment.PanelType.MainPanelType;
        String tts = null;
        switch (hudPromatType) {
            case NAVI_PROMAT:
                if (!EndpointsConstants.IS_NAVI) {
                    panelType = HudSpeechPanelFragment.PanelType.NaviPanelType;
                }else {
                    panelType = PanelType.NaviExitType;
                }
                if (HudStatusManager.IS_NETWORK_CONNECTED) {
                    tts = SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_recognized_navigation);
                    displaySpeechPanel(panelType);
                } else {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.speech_network_fail_navi_tts);
                    mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                }
                break;
            case CALL_PROMAT:
                //// TODO: 2016/11/14  
                panelType = HudSpeechPanelFragment.PanelType.CallPanelType;
                if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLED) {
                    tts = SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_recognized_call);
                    displaySpeechPanel(panelType);
                } else if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLING) {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_call_compling_tts);
                    mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                } else if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_CONNECT) {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_call_fail_tts);
                    mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                } else {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_fail_call_tts);
                    mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
                }
                break;
            case MUSIC_PROMAT:
                if(HudStatusManager.HudMusicStatus==HudStatusManager.MUSIC_IDILE){
                    panelType = HudSpeechPanelFragment.PanelType.MusicPanelType;
                    tts = SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_recognized_music);
                }else {
                    panelType = PanelType.MusicControlPanelType;
                    tts = SpeechResourceManager.getInstanse().getString(com.haloai.huduniversalio.R.string.tts_recognized_music_control);
                }
                if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE && HudIOConstants.OPERAT_MUSIC_IDILE >= HudIOConstants.MusicPauseStat) {
                    int flag = HudEndPointConstants.MUSIC_SERVICE_PLAY;
                    mStatusBarFragment.startMusicIconRolling();
                    Intent intent = new Intent(HudEndPointConstants.ACTION_MUSIC_SERVICE);
                    intent.putExtra(HudEndPointConstants.MUSIC_SERVICE_FLAG, flag);
                    this.sendBroadcast(intent);
                }
                if (panelType == PanelType.MusicControlPanelType){
                    mAmapHandler.sendEmptyMessageDelayed(DELYKEY,500);//这里为了防止进入fragment时音乐状态还没更新
                }else {
                    displaySpeechPanel(panelType);
                }

                break;
            case EXIT_PROMAT:
                hideSpeechPanel();
                playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.navi_speech_exit));
                return;
            case SHUTDOWN_PROMAT:
                hideSpeechPanel();
                playSpeechTextForNavigation("正在关机");
                uiAMapNaviInfoUpdateHandler.sendEmptyMessageDelayed(AMAP_DELAY_SHUT_DOWN, 1000);
                return;
            case OPEN_AMAP:
                hideSpeechPanel();
                HaloLogger.uploadHaloLog("上传日志");
                playSpeechTextForNavigation("已上传日志");
                return;
           /* case OPEN_AMAP:
                //  mNavingFragment.pauseARWay();
                if (HudStatusManager.HudCurrentStatus.ordinal() > HudStatus.CHOOSE_STRAGETY.ordinal()) {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.navi_setting_navi_error);
                } else {
                    HudStatusManager.IS_ARWAY = false;
                    tts = SpeechResourceManager.getInstanse().getString(R.string.navi_setting_amap_navi);
                }
                break;
            case OPEN_AWAY:
                //  mNavingFragment.resumeARWay();
                if (HudStatusManager.HudCurrentStatus.ordinal() > HudStatus.CHOOSE_STRAGETY.ordinal()) {
                    tts = SpeechResourceManager.getInstanse().getString(R.string.navi_setting_navi_error);
                } else {
                    HudStatusManager.IS_ARWAY = true;
                    tts = SpeechResourceManager.getInstanse().getString(R.string.navi_setting_arway_navi);
                }
                break;*/
            case OPEN_RECORD:
                mCarRecorderFragment.openVideoRecording();
                playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.record_start_tts));
                return;
            case CLOSE_RECORD:
                mCarRecorderFragment.closeVideoRecording();
                playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.record_stop_tts));
                return;
        }
        playSpeechTextForCustom(tts, true);
        mSpeechReceiverFragment.showMicView();
    }

    @Override
    public void dispatcherSessionAdjustLight(boolean isUp) {
        hideSpeechPanel();
        mHudPreferenceSetController.upDownBrightnessLevel(isUp);
        if (isUp) {
            playSpeechTextForNavigation("已调高亮度");
        } else {
            playSpeechTextForNavigation("已调低亮度");
        }
    }

    @Override
    public void dispatcherSessionGestureChoose(int index) {
        if (EndpointsConstants.IS_PANEL) {
            cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
            startTimeTask(HudEndPointConstants.GESTURE_TIME,5000);
            mSpeechReceiverFragment.showPointGestureIcon();
            boolean res = mSpeechPanelFragment.gesturePointChoose(index);
            if (res) {
                switch (mSpeechPanelFragment.getCurrentPanelType()) {
                    case NaviPanelType:
                        String naviTTS = index == 0?"回家":"公司";
                        playSpeechTextForCustom(naviTTS,IS_RECONGIZE);
                        break;
                    case CallPanelType:
                        String callTTS = "张睿";
                        if(index ==1){
                            callTTS = "刘承杰";
                        }else if (index ==2){
                            callTTS = "何龙";
                        }
                        playSpeechTextForCustom(callTTS,IS_RECONGIZE);
                        break;
                    case MusicPanelType:
                        String musicTTS = index == 0?"本地音乐":"在线阴月";
                        playSpeechTextForCustom(musicTTS,IS_RECONGIZE);
                        break;
                    case MusicControlPanelType:
                        String mcTTS = "关闭音乐";
                        if(index ==0){
                            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY ) {
                                mcTTS = "暂停";
                            }else {
                                mcTTS = "播放";
                            }
                        }else if (index ==1){
                            mcTTS = "下一首";
                        }
                        playSpeechTextForCustom(mcTTS,IS_RECONGIZE);
                        break;
                    case NaviExitType:
                        String exitTTS = "退出导航";
                        if(index ==1){
                            exitTTS = "回家";
                        }else if (index ==2){
                            exitTTS = "公司";
                        }
                        playSpeechTextForCustom(exitTTS,IS_RECONGIZE);
                        break;
                }
            }
        }
    }

    // IOController Callback Methods
    @Override
    public IHudIOSessionCallback onSessionBegin(HudIOSessionType sessionType) {
        Log.e("speech_info", "onSessionBegin:" + sessionType);
        if (sessionType != HudIOSessionType.NAVI) {
            mSpeechReceiverFragment.stopLoading();
        }
        if (sessionType == HudIOSessionType.EXIT) {
            if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
                // 取消当前导航
                return this;
            } else {
                onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_error_exit_navi));
                // 当前状态不接受"退出Session"
                return null;
            }
        } else if (sessionType == HudIOSessionType.NAVI) {
            return this;
        } else if (sessionType == HudIOSessionType.MUSIC) {
            if (mHudIOController.getCurrentSessionType() != HudIOSessionType.RECORDER) {
                return this;
            }

        } else if (sessionType == HudIOSessionType.SIMULATION_NAVI) {
            if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
                hideSpeechPanel();
                if (mGPSDelayNavi) {
                    startTimeTask(HudEndPointConstants.NETWORK_TIME,15000);
                    mAMapNaviAdapter.startNavigate();
                } else {
                    mAMapNaviAdapter.startNavigateSimulately();
                }
            } else {
                onSpeechUnRecogized(IHudIOSessionNaviCallback.MSG_SPEECH_ERROR);
            }
        } else if (sessionType == HudIOSessionType.COMMAND) {
            if (HudStatusManager.HudMusicStatus != HudStatusManager.MUSIC_IDILE) return this;
            onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_error_unactive_command));
        } else if (sessionType == HudIOSessionType.INCOMING_CALL) {
            return this;
        } else if (sessionType == HudIOSessionType.OUTGOING_CALL) {
            if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLED) {
                return this;
            } else if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_COMPLING) {
                onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_call_compling_tts));
            } else if (mHudStatusManager.getHudBTContactsStatus() == HudStatusManager.BLUETOOTH_CONTACTS_CONNECT) {
                onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_call_fail_tts));
            } else {
                onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.speech_bluetooth_fail_call_tts));
            }
        } else if (sessionType == HudIOSessionType.RECORDER) {
            return this;
        } else if (sessionType == HudIOSessionType.DISPATCHER) {
            return this;
        } else if (sessionType == HudIOSessionType.HANGUP) {
            return this;
        }
        return null;
    }

    @Override
    public void onSessionEnd(HudIOSessionType sessionType, boolean isCancelled) {
        Log.e("speech_info", "onSessionEnd:" + sessionType);
        mSpeechReceiverFragment.stopLoading();
        if (isCancelled && sessionType == HudIOSessionType.NAVI) {
            HudStatusManager.HudCurrentStatus = HudStatus.MAIN;
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_speech_exit),false);
            displayMain();
        } else if (sessionType == HudIOSessionType.DISPATCHER ) {
            if(isCancelled){
                mSpeechReceiverFragment.displayReciver(true);
                hideSpeechPanel();
            }
         //   mSpeechReceiverFragment.showMicView();
        }
        HaloLogger.postI(EndpointsConstants.NAVI_TAG, "SessionEnd:" + sessionType);
    }

    @Override
    public void onInputerReady(HudInputerType inputerType) {
        switch (inputerType) {
            case SPEECH_INPUT:
                break;
            default:
                break;
        }

    }

    @Override
    public void onInputerOos(HudInputerType inputerType, int oosReason) {

    }

    public void navingShowHideDistancePanel(boolean isShow) {
        if(HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING&&!EndpointsConstants.IS_PANEL){
            mNavingFragment.showHideBlockView(isShow);
        }
    }


    //手势的输入回调
    @Override
    public boolean onGestureCommand(int gestureType) {
        if (gestureType == HudIOConstants.INPUTER_GESTURE_CLOSE_PANEL&&EndpointsConstants.IS_PANEL) {
            hideSpeechPanel();
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_speech_exit), false);
            mHudIOController.endCurrentSession(HudIOSessionType.DISPATCHER);
            return true;
        } else if (gestureType == HudIOConstants.INPUTER_GESTURE_SLIDE) {
            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY && !EndpointsConstants.IS_PANEL&&
                    (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING||HudStatusManager.HudCurrentStatus == HudStatus.MAIN)) {
                commandSessionPlay(HudCommandType.NEXT);
                return true;
            }
        }else if (gestureType == HudIOConstants.INPUTER_GESTURE_V){
            if (EndpointsConstants.IS_PANEL){
                cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
                if (!mIsAfterGesturePoint){
                    return true;
                }
                return false;
            }else {
                onWakeupPrompt(HudInputerType.GESTURE_INPUT);
                return true;
            }
        }
        return false;
    }


    //手势的输入交互
    @Override
    public void onGestureShow(GestureCode gestureCode, boolean keyEvent) {
        int gestureType =gestureCode.type;
        boolean flag = true;
        switch (gestureType) {
            case HudIOConstants.INPUTER_GESTURE_V:
                if (keyEvent) {
                    flag = false;
                 //   mSpeechReceiverFragment.showYesGestureIcon();
                } else {
                    mSpeechReceiverFragment.awakeGestureAction();
                }
                cancleTimeTask(HudEndPointConstants.GESTURE_TIME);
                break;
            case HudIOConstants.INPUTER_GESTURE_SLIDE:
                if (!EndpointsConstants.IS_PANEL && (keyEvent || HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY)) {
                    mSpeechReceiverFragment.showSlideGestureIcon();
                } else {
                    flag = false;
                }
                break;
            case HudIOConstants.INPUTER_GESTURE_EASTE:
                if ((HudStatusManager.HudCurrentStatus == HudStatus.MAIN || HudStatusManager.HudCurrentStatus ==HudStatus.NAVIGATING)
                        ||HudEndPointConstants.IS_CALLING||HudEndPointConstants.IS_CARCORD) {
                    mSpeechReceiverFragment.downVolume();
                }
                break;
            case HudIOConstants.INPUTER_GESTURE_CLOCK:
                if ((HudStatusManager.HudCurrentStatus == HudStatus.MAIN || HudStatusManager.HudCurrentStatus ==HudStatus.NAVIGATING)
                        ||HudEndPointConstants.IS_CALLING||HudEndPointConstants.IS_CARCORD) {
                    mSpeechReceiverFragment.upVolume();
                }
                break;
            case HudIOConstants.INPUTER_GESTURE_POINT:
                if(!keyEvent){
                    mSpeechReceiverFragment.showPointGestureIcon();
                }else {
                    if(!EndpointsConstants.IS_PANEL){
                        mSpeechReceiverFragment.displayGesturePoint();
                    }
                }
                break;
        }
        if (flag) {
            displaySpeechReceiver();
            if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING && EndpointsConstants.IS_NAVI) {
                navingShowHideDistancePanel(false);
            }
        }
    }

    @Override
    public boolean onGesturePoint(int areaIndex) {
        boolean isConsume = false;
        if (EndpointsConstants.IS_PANEL){
            switch (areaIndex){
                case 0:
                    if (mSpeechPanelFragment.updateUIAccordingGesturePointChange(HudSpeechPanelFragment.GesturePointType.LeftGesturePointType)){
                        playSpeechTextForCustom("导航",IS_RECONGIZE);
                        isConsume = true;
                    }
                    break;
                case 1:
                    if (mSpeechPanelFragment.updateUIAccordingGesturePointChange(HudSpeechPanelFragment.GesturePointType.CenterGesturePointType)){
                        playSpeechTextForCustom("音乐",IS_RECONGIZE);
                        isConsume = true;
                    }
                    break;
                case 2:
                    if (mSpeechPanelFragment.updateUIAccordingGesturePointChange(HudSpeechPanelFragment.GesturePointType.RightGesturePointType)){
                        playSpeechTextForCustom("电话",IS_RECONGIZE);
                        isConsume = true;
                    }
                    break;
            }
            mIsAfterGesturePoint = true;

            startTimeTask(HudEndPointConstants.GESTURE_TIME,5000);
        }
        return isConsume;
    }

    //遥控器的输入回调
    @Override
    public void onRemoterKey(int keyCode) {
        if (keyCode == HudIOConstants.INPUTER_REMOTER_LEFT) {
            commandSessionPlay(HudCommandType.PREV);
        } else if (keyCode == HudIOConstants.INPUTER_REMOTER_RIGHT) {
            commandSessionPlay(HudCommandType.NEXT);
        }

    }

    //手机端OnClick的输入回调
    @Override
    public void onPhoneOnclickCommand(int command) {
        if (command == EndpointsConstants.ONCLIACK_COMMAND_MUSIC) {
            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
                HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_USER;
                commandSessionPlay(HudCommandType.PAUSE);
            } else if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
                HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_USER;
                commandSessionPlay(HudCommandType.PLAY);
            } else if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_IDILE) {
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.onclick_music_error_tts), false);
            }
        } else if (command == EndpointsConstants.ONCLIACK_COMMAND_NEXT) {
            if (HudStatusManager.HudMusicStatus != HudStatusManager.MUSIC_IDILE) {
                commandSessionPlay(HudCommandType.NEXT);
            } else {
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.onclick_next_error_tts), false);
            }
        } else if (command == EndpointsConstants.ONCLIACK_COMMAND_PHOTO) {
            CameraUtil.takePhoto(this);
        }
    }

    // 语音，手机，手势，遥控器输入唤醒的回调
    @Override
    public void onWakeupPrompt(HudInputerType hudInputerType) {
        HaloLogger.logE("speech_info", "Inputer Type:" + hudInputerType);
        if (mSpeechPanelFragment.isHidden()) {
            resetSpeechPanel();
            displaySpeechPanel(HudSpeechPanelFragment.PanelType.MainPanelType);
        }
        if (hudInputerType == HudInputerType.GESTURE_INPUT) {
            displaySpeechReceiver();
        } else if (hudInputerType == HudInputerType.PHONE_INPUT) {
            displaySpeechReceiver();
            showSpeechReceiverView();
        }
        //说您好魔方时调用
        mSpeechReceiverFragment.stopLoading();
        playTTS(R.string.wakeup_prompt, true);
        HaloLogger.postI(EndpointsConstants.NAVI_TAG, "唤醒方式:" + hudInputerType);
    }

    //语音输入音量大小
    @Override
    public void onSpeechVolumeChanged(int volume) {
        mSpeechReceiverFragment.setVolume(volume);
    }

    @Override
    public void onSpeechTTSBegin() {
        mSpeechReceiverFragment.setVolumeFlag(false);
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY && mAudioManager != null) {
           /* int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            currentVolume = currentVolume > 6 ? 6 : currentVolume;*/

            int currentVolume = HudStatusManager.MUSIC_VLUME < HudStatusManager.HudMusicVlume ? HudStatusManager.MUSIC_VLUME : HudStatusManager.HudMusicVlume;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            mAdjustMusicVolume = true;

          /*  HaloLogger.logE("music_info", "TTS_BEGIN:"+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            HaloLogger.logE("music_info", "TTS_BEGIN HudMusicVlume:" + HudStatusManager.HudMusicVlume);*/
        }
    }

    @Override
    public void onSpeechTTSEnd() {
        mSpeechReceiverFragment.setVolumeFlag(true);
        if (mHudSpeechOutputer != null) {
            mHudSpeechOutputer.setOutputContentType2IDLE();
        }
        if (mAdjustMusicVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HudStatusManager.HudMusicVlume, 0);
            mAdjustMusicVolume = false;

            /*HaloLogger.logE("music_info", "TTS_END:"+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            HaloLogger.logE("music_info", "TTS_END HudMusicVlume:" + HudStatusManager.HudMusicVlume);*/
        }
    }

    @Override
    public void onSpeechEngineError(SpeechEngineErrorCode errorCode, String errorMsg) {
        Log.e("speech_info", "errorCode" + errorCode);
        //TODO 此处需要更改
        if (errorCode == SpeechEngineErrorCode.SPEECH_INIT_ERROR) {
            HudStatusManager.HudCurrentStatus = HudStatus.INIT_ERROR;
        } else if (errorCode == SpeechEngineErrorCode.SPEECH_ASR_TIMEOUT) {
            //loading超时
            judgeHideSpeechPanel(errorCode);
            mSpeechReceiverFragment.stopLoading();
        } else if (errorCode == SpeechEngineErrorCode.SPEECH_MIC_CONFLICT_ERROR) {
            //mic冲突
        } else if (errorCode == SpeechEngineErrorCode.SPEECH_PROTOCAL_ERROR) {
            //protocal error
            judgeHideSpeechPanel(errorCode);
            mSpeechReceiverFragment.stopLoading();
        } else if (errorCode == SpeechEngineErrorCode.SPEECH_NULL_PROTOCAL_ERROR) {
            //null protocal error
            judgeHideSpeechPanel(errorCode);
            mSpeechReceiverFragment.stopLoading();
        }

    }

    @Override
    public void onSpeechText(String text) {
        mSpeechReceiverFragment.upgradeSpeechText(text);
    }

    @Override
    public void onWakeup() {
        displaySpeechReceiver();
        showSpeechReceiverView();
        HaloLogger.postI(EndpointsConstants.SPEECH_TAG, "onWakeup");
    }

    @Override
    public void onSpeechRecognizeStart() {
        mSpeechReceiverFragment.startLoading();
    }

    @Override
    public void onSpeechUnRecogized(String errorMessage) {
        mSpeechReceiverFragment.stopLoading();
        onSpeechPanelUnrecogized(errorMessage);
    }

    @Override
    public void onEndEmulatorNavi() {
        mNavingFragment.onArriveDestination();
        uiAMapNaviInfoUpdateHandler.sendEmptyMessageDelayed(AMAP_DELAYED_EXIT_NAVI, 3000);
    }

    @Override
    public void onStartNavi(int type) {
        /*if (type == AMapNavi.GPSNaviMode&&IS_SMUL) {
            IS_SMUL = false;
            mAmapHandler.sendEmptyMessage(AMAP_DELAYED_START_NAVI);
        }*/
        mNavingFragment.onNaviStarted();
        String navi = type == AMapNavi.GPSNaviMode ? "GPS" : "EMULATOR";
        HaloLogger.postI(EndpointsConstants.NAVI_TAG, "onStartNavi:" + navi);
    }


    @Override
    public void musicSessionSearchSong(List<ISpeechParserMusic.MusicInfo> mMusicInfoList, String tag) {
        hideSpeechPanel();
        if (mMusicInfoList == null || mMusicInfoList.size() == 0 || tag == null) {
            return;
        }
        if (tag.contains("PLAY_MUSIC") && HudStatusManager.HudMusicStatus != HudStatusManager.MUSIC_IDILE) {
            commandSessionPlay(HudCommandType.PLAY);
            return;
        }
        ArrayList<String> songList = new ArrayList<>();
        for (ISpeechParserMusic.MusicInfo musicInfo : mMusicInfoList) {
            songList.add(musicInfo.song_url);
        }
        startMusicService(mMusicInfoList, tag, songList);
    }

    @Override
    public void musicSessionPlayMusic() {
        hideSpeechPanel();
        ArrayList<String> list = FileUtils.getLocalMusicList();
        String tag = "SONG_LIST";
        List<ISpeechParserMusic.MusicInfo> musicInfoList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (String musicPath : list) {
                ISpeechParserMusic.MusicInfo info = new ISpeechParserMusic.MusicInfo();
                info.song_url = musicPath;
                info.duration = (int) (Math.random() * 120) + 120;
                info.title = new File(musicPath).getName().replace(".mp3", "");
                musicInfoList.add(info);
            }
        } else {
            playSpeechTextForNavigation("暂无本地音乐");
            return;
        }
        startMusicService(musicInfoList, tag, list);
    }

    private void startMusicService(List<ISpeechParserMusic.MusicInfo> mMusicInfoList, String tag, ArrayList<String> songList) {
        Intent music = new Intent(getApplicationContext(), MusicPlayService.class);
        music.putStringArrayListExtra(HudEndPointConstants.MUSIC_SERVICE_SONG_LIST, songList);
        music.putExtra(HudEndPointConstants.MUSIC_SERVICE_MUSIC_TAG, tag);
        this.startService(music);
        mStatusBarFragment.setMsuicInfoList(mMusicInfoList);
        mStatusBarFragment.updateStatusBar(HudStatusItemType.MUSIC, true);
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_MUSIC);//音乐 Bugly_TAG
    }


    // 音乐播放离线命令
    @Override
    public void commandSessionPlay(HudCommandType commandType) {
        int flag = 0;
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_IDILE) {
            return;
        }
        switch (commandType) {
            case PLAY:
                if(HudIOConstants.MusicPlayStat >= HudIOConstants.MusicPauseStat){
                    flag = HudEndPointConstants.MUSIC_SERVICE_PLAY;
                    if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
                        mStatusBarFragment.startMusicIconRolling();
                    }
                }
                break;
            case PAUSE:
                flag = HudEndPointConstants.MUSIC_SERVICE_PAUSE;
                if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
                    mStatusBarFragment.stopMusicIconRolling();
                }
                break;
            case NEXT:
                flag = HudEndPointConstants.MUSIC_SERVICE_NEXT;
                break;
            case PREV:
                flag = HudEndPointConstants.MUSIC_SERVICE_PREV;
                break;
            case STOP:
                flag = HudEndPointConstants.MUSIC_SERVICE_STOP;
                mStatusBarFragment.updateStatusBar(HudStatusItemType.MUSIC, false);
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.music_play_stop), false);
                break;
            case ACTION:
                if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
                    mStatusBarFragment.startMusicIconRolling();
                    flag = HudEndPointConstants.MUSIC_SERVICE_PLAY;
                }else if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY){
                    mStatusBarFragment.stopMusicIconRolling();
                    flag = HudEndPointConstants.MUSIC_SERVICE_PAUSE;
                }
                break;
            case ERROR:
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.music_play_error), false);
                if(EndpointsConstants.IS_PANEL) hideSpeechPanel();
                break;
            default:
                break;
        }
        if (flag != 0) {
            Intent intent = new Intent(HudEndPointConstants.ACTION_MUSIC_SERVICE);
            intent.putExtra(HudEndPointConstants.MUSIC_SERVICE_FLAG, flag);
            this.sendBroadcast(intent);
            if(EndpointsConstants.IS_PANEL) hideSpeechPanel();
        }
    }

    //-----------------------carrecorder start -------------------

    @Override
    public void onSessionRecorderOpen() {
        /*if (!CarRecorderFileUtils.getSdcard1StorageState(this)) {
            if (!mSpeechPanelFragment.isHidden()) {
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_recorder_secard_no_found), true);
            } else {
                playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_recorder_secard_no_found), false);
            }
            mHudIOController.endCurrentSession(HudIOSessionType.RECORDER);
            return;
        }*/
        mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
        mSpeechReceiverFragment.stopLoading();
        resetSpeechPanel();
        displayCarRecorder();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.record_open_tts), false);
        mCarRecorderFragment.enterScanPage();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
            HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_CALL;
            commandSessionPlay(HudCommandType.PAUSE);
        }

    //    mNativeFragment.removeNativeAMapView();
    }


    @Override
    public void onSessionRecorderBrowseVideo(HudRecorderVideoType hudRecorderVideoType) {
        if (mCarRecorderFragment.getVideoNumber(hudRecorderVideoType) <= 0) {
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_recorder_no_this_type_video), false);
            return;
        }
        mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
        mCarRecorderFragment.enterPlayPage(hudRecorderVideoType);
        if (hudRecorderVideoType == HudRecorderVideoType.WONDERFUL_VIDEO) {
            playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.record_wonderful_video_tts));
        } else {
            playSpeechTextForNavigation(SpeechResourceManager.getInstanse().getString(R.string.record_loop_video_tts));
        }
    }

    @Override
    public void onSessionRecorderClose() {
        // 执行退出操作。
        // mCarRecorderFragment.stopMediaPlayer();
        hideCarRecorder();
        playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.navi_speech_exit), false);
        hideSpeechReceiverView();
        if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
            HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_CALL;
            commandSessionPlay(HudCommandType.PLAY);
        }
   //     mNativeFragment.addNativeAMapView();
    }

    @Override
    public void onSessionRecorderCommand(HudRecorderCommandType hudRecorderCommandType) {
        HaloLogger.logE("speech_info", "HudRecorderCommandType:" + hudRecorderCommandType);
        String command = "";
        switch (hudRecorderCommandType.ordinal()) {
            case 0:
                command = "快进";
                mCarRecorderFragment.fastForward();
                break;
            case 1:
                command = "快退";
                mCarRecorderFragment.fastBackward();
                break;
            case 2:
                command = "加锁";
                mCarRecorderFragment.lockVideo();
                break;
            case 3:
                command = "删除";
//                mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
//                displayDialogFragment();
                //mDialogFragment.showYesOrNoDialog("确定删除该视频吗？");
                mCarRecorderFragment.deleteVideo();
                mCarRecorderFragment.deleteSure();
                playSpeechTextForNavigation("已删除视频");
                break;
            case 4:
                command = "上一段";
                mCarRecorderFragment.previousVideo();
                break;
            case 5:
                command = "下一段";
                mCarRecorderFragment.nextVideo();
                break;
            case 6:
                command = "返回";
                mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
                mCarRecorderFragment.returnScanPage();
                break;
            case 7:
                command = "确定";
                mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
               // hideDialogFragment();
                //mDialogFragment.disappearDialogGradually();
                mCarRecorderFragment.deleteSure();
                break;
            case 8:
                command = "取消";
                mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
               // hideDialogFragment();
                //mDialogFragment.disappearDialogGradually();
                mCarRecorderFragment.deleteCancel();
                break;
            case 9:
                mHudPreferenceSetController.volumeValueChange(true, true);
                break;
            case 10:
                mHudPreferenceSetController.volumeValueChange(true, false);
                break;
            case 11:
                mCarRecorderFragment.resume();
                break;
            case 12:
                mCarRecorderFragment.pauseVideo();
                break;
        }
    }

    @Override
    public void onSessionRecorderPhonePlay(String path, int index) {

        if (index == EndpointsConstants.REMOTER_COMMAND_DELET_VIDEO) {
            // 手机端删除操作
            hideCarRecorder();
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_speech_delete), false);
            hideSpeechReceiverView();
            mCarRecorderFragment.exit();
            CarRecorderFileUtils.deleteFile(path);
            String imagePath = CarRecorderFileUtils.getImageBytes(path);
            CarRecorderFileUtils.deleteFile(imagePath);
            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PAUSE) {
                HudIOConstants.MusicPlayStat = HudIOConstants.OPERAT_MUSIC_CALL;
                commandSessionPlay(HudCommandType.PLAY);
            }
            return;
        } else {
            mHudIOController.continueCurrentSession(HudIOSessionType.RECORDER);
            displayCarRecorder();
            showSpeechReceiverView();
            Message message = uiAMapNaviInfoUpdateHandler.obtainMessage();
            message.what = AMAP_DELAYED_MESSAGE;
            message.arg1 = HudEndPointConstants.DELAYED_PLAY_VIDEO;
            message.arg2 = index;
            message.obj = path;
            uiAMapNaviInfoUpdateHandler.sendMessageDelayed(message, 200);
            if (HudStatusManager.HudMusicStatus == HudStatusManager.MUSIC_PLAY) {
                HudIOConstants.MusicPauseStat = HudIOConstants.OPERAT_MUSIC_CALL;
                commandSessionPlay(HudCommandType.PAUSE);
            }
        }
    }


    private void playTTS(int resource, boolean keepRecongize) {
        String wakeupPrompt = getApplication().getResources().getString(resource);
        playSpeechTextForCustom(wakeupPrompt, keepRecongize);
    }

    public void onNaviSessionPOIResult(List<String> poiList, String destination, int poiPageSize) {
        HaloLogger.logE("speech_info", poiList.toString());
        if (HudStatusManager.HudCurrentStatus == HudStatus.NAVIGATING) {
            mAMapNaviAdapter.stopNavigate();
        }
        HudStatusManager.HudCurrentStatus = HudStatus.POI_SHOW;
        mNavingFragment.stopDrawHudway();
        mSpeechReceiverFragment.stopLoading();
        hideSpeechPanel();
        mHudStatusManager.setHudPOIName(destination);
        mCurrentPOIList = poiList;
        this.currentPOIPageFlag = 0;
        this.currentPOIPageIndex = 0;
        this.currentPOIListPageCount = poiPageSize;
        String poitext = getApplication().getResources().getString(R.string.amap_poilist_voice_next);
        HaloLogger.logE("speech_info", "currentPOIListPageCount" + currentPOIListPageCount);
        /*if (currentPOIListPageCount == 1) {
               poitext = getApplication().getResources().getString(R.string.amap_poilist_voice);
          }*/
        playSpeechTextForCustom(String.format(poitext, destination), false);
        uiAMapNaviInfoUpdateHandler.sendEmptyMessage(AMAP_UPDATE_MSG_POI_LIST);
        displayNaviPOIList();
    }


    public void onPOISearchFailed(String poiNameToSearch, String error) {
        mSpeechReceiverFragment.stopLoading();
        displaySpeechPanel(HudSpeechPanelFragment.PanelType.NaviPanelType);
        playSpeechTextForCustom(error, true);

    }

    public void onCalculateRouteFailure(int errorInfo) {
        // TODO mobing 根据errorInfo类型，合理处理路径计算失败情况,注意errorInof要改为自己的错误描述
        HaloLogger.logE("speech_info", "errorInfo:" + errorInfo);
        //过滤已经计算路径成功又回调失败的错误情况
        if (HudStatusManager.HudCurrentStatus != HudStatus.CHOOSE_STRAGETY) {
            cancleTimeTask(HudEndPointConstants.NETWORK_TIME);
            mSpeechReceiverFragment.stopLoading();
            playSpeechTextForCustom(SpeechResourceManager.getInstanse().getString(R.string.tts_session_nav_calculate_route_failure), false);
        }
    }


    public void onCalculateRoute(RouteSearchData shortestDistance, RouteSearchData fastestTime, RouteSearchData defaultStrategy) {
        if (HudStatusManager.HudCurrentStatus == HudStatus.POI_SHOW) {
            cancleTimeTask(HudEndPointConstants.NETWORK_TIME);
            mShowCountDownTime = 10;
            mCountDownTimeThread = new CountDownTimeThread(7000);
            mCountDownTimeThread.start();
            mSpeechReceiverFragment.stopLoading();
            HudStatusManager.HudCurrentStatus = HudStatus.CHOOSE_STRAGETY;
            displayNavingStrategyChoose();
            this.mShortestDistance = shortestDistance;
            this.mFastestTime = fastestTime;
            this.mDefaultStrategy = defaultStrategy;
            ROUTE_ARRAYS  = new RouteSearchData[]{mDefaultStrategy, mFastestTime,mShortestDistance};
            playStrategySpeechText(mDefaultStrategy);
            mStrategyIndex = 0;
            mNaviFragment.dislayStrategyView(mShortestDistance, mFastestTime, mDefaultStrategy);
        }
    }

    /**
     * 隐藏loading 进度条
     */
    private void hideLoadingProcessBar(){
        if (mLoadingProgressBar != null) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mLoadingProgressBarParent.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示loading 进度条
     */
    private void showLoadingProcessBar(){
        if (mLoadingProgressBar != null) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mLoadingProgressBarParent.setVisibility(View.VISIBLE);
        }
    }

    public ProgressBar createLayoutProgressBar(Activity a){
        FrameLayout rootContainer = (FrameLayout) a.findViewById(android.R.id.content);//固定写法，返回根视图
        ViewGroup layout =  (ViewGroup) LayoutInflater.from(a).inflate(R.layout.launcher_loading_layout,null,false);
        FrameLayout progressBarHolder = (FrameLayout)layout.findViewById(R.id.progress_bar_vg);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        ProgressBar pb = new ProgressBar(a);
        int padding =0;
        pb.setPadding(padding,padding,padding,padding);
        pb.setLayoutParams(lp);
        progressBarHolder.addView(pb);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        layout.setLayoutParams(layoutParams);
        rootContainer.addView(layout);
        mLoadingProgressBarParent = layout;
        mLoadingProgressBarParent.setVisibility(View.INVISIBLE);
        return pb;
    }
    
}


