package com.haloai.huduniversalio.inputer.impl;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.halo.gesture.GestureController;
import com.halo.gesture.IGestureConnectionListener;
import com.halo.gesture.IGestureConnectionListener.IGestureNotifier;
import com.halo.gesture.IGestureController;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.HudConstants;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.gesture.GestureClickEngine;
import com.haloai.huduniversalio.gesture.GestureCode;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.inputer.IInputerGestureStatusListener;
import com.haloai.huduniversalio.session.impl.HudIOSession;
import com.haloai.huduniversalio.session.impl.HudIOSessionDispatcher;


/**
 * Created by zhangrui on 16/5/30.
 */
public class GestureInputer implements IGestureNotifier,IGestureConnectionListener {
    static final private float MIN_SCORE = (float) 0.45;
    static final private int MIN_COUNT = 4;
    private IInputerGestureStatusListener mInputerGestureStatusListener;
    private HudIOController mHudIOController;
    private ProgressDialog proDialog;
    private Context mContext;
    private IGestureController mGestureController;

    private final int GESTURE_UPDATE = -2 ;
    private final int GESTURE_PROGRESS = -1 ;
    private final int GESTURE_CODE = 1;
    private final int GESTURE_FITER = 2;
    private final int GESTURE_SLIDE = 3;
    private final int GESTURE_POINT = 4;
    private final int GESTURE_POINTSEND = 5;

    private final int MainPanelCutCount = 3;
    private GesturePoint curGesturePoint;
    private boolean mIsSpeechRecognizeShouldCancel = false;
    private int curAreaIndex = -1;

    private int                 mGestureCode           = 0;
    private boolean             mGestureFlag           = false;
    private float mGestureKeyScore = 0;
 //   private boolean mGestureSlide = false;
    private boolean             mGestureUpdate         = false;
    private int                 mGestureStatus         = HudIOConstants.GESTURE_STATUS_NO_RESPONE;

    private int mRecordGPCallBackCount = 0;//记录手势回调的次数

    private GestureClickEngine mGestureClickEngine = GestureClickEngine.getInstance();

    public void create(Context context,HudIOController hudIOController){
        this.mContext=context;
        this.mHudIOController=hudIOController;
        IntentFilter intentFilter =new IntentFilter();
        if(HudConstants.IS_GESTURE){
            initGestureConnect();
        }else{
            intentFilter.addAction(HudIOConstants.ACTION_REMOTER_INPUTER);
        }
        intentFilter.addAction(HudIOConstants.ACTION_GESTURE_POWER);
        mContext.registerReceiver(mGestureBroadcastReceiver,intentFilter);
    }

    private void setProgressDialog() {
        proDialog = new ProgressDialog(mContext,ProgressDialog.THEME_HOLO_DARK);
        proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        proDialog.setMessage("正在升级手势，请稍候...");
        proDialog.setMax(100);
        proDialog.setProgress(0);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GESTURE_CODE:
                    int code =msg.arg1;
                    if(mGestureFlag)mGestureCode=code;//对重复的相同手势进行过滤
                    onGestureKey(code,mGestureKeyScore);
                    break;
                case GESTURE_FITER:
                    mGestureFlag = false;
                    break;
                case GESTURE_SLIDE:
             //       mGestureSlide = false;
                    break;
                case GESTURE_PROGRESS:
                    if (!mGestureUpdate) {
                        setProgressDialog();
                        proDialog.show();
                        mGestureUpdate = true;
                        HaloLogger.postI(EndpointsConstants.GESTURE_TAG,"Gesture Update Start...");
                    }
                    proDialog.setProgress(msg.arg1);
                    if (msg.arg1 == proDialog.getMax()) {
                        proDialog.dismiss();
                        mGestureUpdate = false;
                        resumeGestureInputer();
                        HaloLogger.postI(EndpointsConstants.GESTURE_TAG,"Gesture Update Success!");
                    }
                    break;
                case GESTURE_UPDATE:
                  //  Toast.makeText(mContext,"update is :"+(boolean)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case GESTURE_POINT:
                    onGesturePointKey();
                    break;
            }
        }
    };


    Runnable mControlVRun = new Runnable() {
        @Override
        public void run() {
           mGestureFlag = false;
        }
    };



    private void initGestureConnect(){
        mGestureController = new GestureController();
        mGestureController.initWithNotifers(mContext,this,this);
        resumeGestureInputer();
    }

    public void setInputerGestureStatusListener(IInputerGestureStatusListener inputerGestureStatusListener){
        this.mInputerGestureStatusListener=inputerGestureStatusListener;
    }

    public void pauseGestureInputer(){
        if(mGestureController != null && HudConstants.IS_GESTURE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGestureController.disconnectToChip();
                }
            }).start();
        }
    }

    public void resumeGestureInputer(){
        if(mGestureController != null && HudConstants.IS_GESTURE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    mGestureController.connectToChip();
                }
            }).start();
        }
    }

    /**
     * 准确率的使用
     *  V手势和转圈手势，在导航界面和主界面唤醒，准确率为80%才放行，在其它界面直接放行
     *
     * @param gestureCode
     */
    public void  onGestureKey(int gestureCode,float score){
        GestureCode code = new GestureCode(gestureCode,score);
        if(gestureCode==HudIOConstants.INPUTER_GESTURE_V){
            if (mGestureClickEngine.isMoveLocating()){
                mGestureClickEngine.handleSure();
            }
        }
        //停止正在播放的TTS，取消语音引擎的语音识别，关闭语音面板
        if(gestureCode==HudIOConstants.INPUTER_GESTURE_PALM  && EndpointsConstants.IS_PANEL){
            mHudIOController.getHudOutputer(HudIOConstants.HudOutputerType.SPEECH_OUTPUT).cancel();
            mHudIOController.getHudSpeechManager().cancleRecognizeManually();
            mInputerGestureStatusListener.onGestureCommand(HudIOConstants.INPUTER_GESTURE_CLOSE_PANEL);
            return;
        }
        if(EndpointsConstants.IS_PANEL && (gestureCode ==HudIOConstants.INPUTER_GESTURE_CLOCK||gestureCode ==HudIOConstants.INPUTER_GESTURE_EASTE)){
            return;
        }
        boolean keyEvent=false;
        HudIOSession currenSession=mHudIOController.getCurrentSession();
        if(currenSession!=null){
            //手势处理第一顺序:Session(音量调节不走Session)
            keyEvent = currenSession.onGestureKey(gestureCode);
        }
        if(!keyEvent&&mInputerGestureStatusListener!=null){
           boolean result = mInputerGestureStatusListener.onGestureCommand(gestureCode);
            if (gestureCode == HudIOConstants.INPUTER_GESTURE_V){
               if (!result) {
                   //这里开始dispatchSession
                   HudIOSessionDispatcher session = (HudIOSessionDispatcher) mHudIOController.beginHudIOSession(HudIOConstants.HudIOSessionType.DISPATCHER);
                   if (session != null) {
                       session.onGestureProtocol(curAreaIndex);
                   }
               }else {
                   curAreaIndex = -1;
               }
                mIsSpeechRecognizeShouldCancel = true;
            }
        }
        mInputerGestureStatusListener.onGestureShow(code,keyEvent);
    }

    private void onGesturePointKey(){
        //HaloLogger.postE(EndpointsConstants.GESTURE_TAG,"GesturePoint:"+curGesturePoint.x);
        boolean keyEvent = false;
        HudIOSession currenSession = mHudIOController.getCurrentSession();
        if(currenSession != null){
            int areaIndex = mGestureClickEngine.handleGesturePoint(curGesturePoint,currenSession.queryGesturePointArea());
            PointGestureResult pointResult= new PointGestureResult();
            pointResult.setAreaIndex(areaIndex);
            keyEvent = currenSession.onGesturePoint(curGesturePoint,pointResult);
            if (keyEvent){
                mHandler.removeCallbacks(mControlVRun);
                mGestureFlag = false;
                mInputerGestureStatusListener.onGestureShow(new GestureCode(HudIOConstants.INPUTER_GESTURE_POINT,1),true);
            }
        }
        if(!keyEvent && mInputerGestureStatusListener != null){
            int index = mGestureClickEngine.handleGesturePoint(curGesturePoint,MainPanelCutCount);
            if (index >= 0 && index < MainPanelCutCount){
                keyEvent=mInputerGestureStatusListener.onGesturePoint(index);
            }
            if (keyEvent){
                curAreaIndex = index;
                mHandler.removeCallbacks(mControlVRun);
                mGestureFlag = false;
                mInputerGestureStatusListener.onGestureShow(new GestureCode(HudIOConstants.INPUTER_GESTURE_POINT,1),false);
            }

        }
        if (mIsSpeechRecognizeShouldCancel){
            mHudIOController.getHudSpeechManager().cancleRecognizeManually();
            mIsSpeechRecognizeShouldCancel = false;
        }
    }


    //手势回调
    @Override
    public void onGestureCallBack(int gestureCode,float score) {
        //HaloLogger.postE(EndpointsConstants.GESTURE_TAG,"GestureCode:"+type + " Score:"+score);
        /*if(type==HudIOConstants.INPUTER_GESTURE_V && EndpointsConstants.IS_PANEL){
            return;
        }else */
        if((mGestureFlag &&  gestureCode == HudIOConstants.INPUTER_GESTURE_V)){
            return;
        }
        Message message=mHandler.obtainMessage();
        message.what=GESTURE_CODE;
        message.arg1=gestureCode;
        mGestureKeyScore = score;
        mHandler.sendMessage(message);
        mGestureFlag = gestureCode == HudIOConstants.INPUTER_GESTURE_V;
        if (mGestureFlag){
            mRecordGPCallBackCount = 0;
            mHandler.postDelayed(mControlVRun,3000);
        }
     //   mGestureSlide = type == HudIOConstants.INPUTER_GESTURE_SLIDE;
        // if(mGestureFlag)  mHandler.sendEmptyMessageDelayed(GESTURE_FITER,2000);
      //  if(mGestureSlide) mHandler.sendEmptyMessageDelayed(GESTURE_SLIDE,1000);
    }

    @Override
    public void onGesturePoint(int x, int y, int z,float score) {
        //HaloLogger.postE(EndpointsConstants.GESTURE_TAG, "GesturePont_score :" + score);
           if (true /*score >= MIN_SCORE*/) {
               mRecordGPCallBackCount ++;
               if (mRecordGPCallBackCount >= MIN_COUNT){
                   mRecordGPCallBackCount = 0;
               }else {
                   return;
               }
               Message msg = mHandler.obtainMessage();
               msg.what = GESTURE_POINT;
               curGesturePoint = new GesturePoint(x, y, z, score);
               mHandler.sendMessage(msg);
           }
            //   HaloLogger.postE(EndpointsConstants.GESTURE_TAG, "score :" + score);


    }

/*
    //手势回调
    @Override
    public void onGestureCallBack(int type) {
        if(type==HudIOConstants.INPUTER_GESTURE_V && EndpointsConstants.IS_PANEL){
            return;
        }else if((mGestureFlag &&  type == HudIOConstants.INPUTER_GESTURE_V)
                ||(mGestureSlide && type == HudIOConstants.INPUTER_GESTURE_SLIDE)){
            return;
        }
        Message message=mHandler.obtainMessage();
        message.what=GESTURE_CODE;
        message.arg1=type;
        mHandler.sendMessage(message);
        mGestureFlag = type == HudIOConstants.INPUTER_GESTURE_V;
        mGestureSlide = type == HudIOConstants.INPUTER_GESTURE_SLIDE;
        if(mGestureFlag)  mHandler.sendEmptyMessageDelayed(GESTURE_FITER,2000);
        if(mGestureSlide) mHandler.sendEmptyMessageDelayed(GESTURE_SLIDE,2000);
        HaloLogger.logE("speech_info","type :"+type+"");
    }
*/


    @Override
    public void onGestureDevState(int gestureStatus) {
        if(this.mGestureStatus != gestureStatus){
            mGestureStatus = gestureStatus;
            Intent intent = new Intent(HudIOConstants.GESTURE_CONNECT_STATUS);
            intent.putExtra(HudIOConstants.GESTURE_CONNECT_STATUS,gestureStatus);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void onUpdateFlagChange(boolean b) {
        Message msg = mHandler.obtainMessage();
        msg.what = GESTURE_UPDATE;
        msg.obj = b;
        mHandler.sendMessage(msg);
        HaloLogger.logE("gesture_info","flag :"+b+"");
    }

    @Override
    public void onUpdateProgressChange(int i) {
        Message msg = mHandler.obtainMessage();
        msg.what = GESTURE_PROGRESS;
        msg.arg1 = i;
        mHandler.sendMessage(msg);
        HaloLogger.logE("gesture_info","index:"+i+"");
    }

    private void setGesturePower(int code){
        mGestureController.setGesturePower(code);
    }


    BroadcastReceiver mGestureBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equalsIgnoreCase(HudIOConstants.ACTION_REMOTER_INPUTER)){
                HudIOSessionDispatcher session = (HudIOSessionDispatcher) mHudIOController.beginHudIOSession(HudIOConstants.HudIOSessionType.DISPATCHER);
                if (session != null) {
                    session.goHomeCompany(true);
                }
            }else if(action.equalsIgnoreCase(HudIOConstants.ACTION_GESTURE_POWER)){
                int code = intent.getIntExtra(HudIOConstants.ACTION_GESTURE_POWER,0);
                setGesturePower(code);
            }
        }
    };
}
