package com.haloai.huduniversalio.session.impl;


import android.content.Context;
import android.content.SharedPreferences;

import com.haloai.hud.model.v2.NaviRouteInfo;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudPromatType;
import com.haloai.huduniversalio.R;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.outputer.IHudOutputer;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionDispatcherCallback;
import com.haloai.huduniversalio.speech.ISpeechParserDispatcher;
import com.haloai.huduniversalio.speech.ISpeechSessionDispatcher;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.speech.unisound.UnisoundSpeechSessionDispatcher;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONObject;

/**
 * Created by zhangrui on 16/5/19.
 */
public class HudIOSessionDispatcher extends HudIOSession {
    private String TAG = "HudIOSessionDispatcher";
    private IHudIOSessionDispatcherCallback mSessionCallback;
    private ISpeechSessionDispatcher mSpeechSessionDispatcher;
    private HudIOSessionType mDispatcherSessionType;
    private String errorSpeechText;
    private static final int SESSION_STATE_DISPATCHER_SESSION = SESSION_STATE_BASE + 1;
    private int curIndex = -1;

    public HudIOSessionDispatcher(HudIOController hudIOController) {
        super(hudIOController);
        mIOSessionType = HudIOSessionType.DISPATCHER;
        mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserDispatcher();
        mSpeechSessionDispatcher = new UnisoundSpeechSessionDispatcher();
    }

    @Override
    public void continueSession() {

    }

    @Override
    public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
        if (!(sessionCallback instanceof IHudIOSessionDispatcherCallback)) {
            throw new IllegalArgumentException(
                    "Requirst the IHudIOSessionNaviCallback");
        }
        this.mSessionCallback = (IHudIOSessionDispatcherCallback) sessionCallback;
    }

    @Override
    public void onWakeupWord(String wakeupWord) {

    }

    public boolean goHomeCompany(boolean home){
        SharedPreferences mSp = mHudIOController.getmContext().getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
        NaviRouteInfo naviRouteInfo = new NaviRouteInfo();
        if (home) {
            if(mSp.getString(HudIOConstants.KEY_OF_HOME_PATHNAME, null) == null) return false;
            naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_HOME_PATHNAME, null));
            naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LONGITUDE, -1f));
            naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LATITUDE, -1f));
            naviRouteInfo.setNaviDrivingStrategy(Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.DEFAULT);
        } else {
            if(mSp.getString(HudIOConstants.KEY_OF_COMPANY_PATHNAME, null) == null) return false;
            naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_COMPANY_PATHNAME, null));
            naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LONGITUDE, -1f));
            naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LATITUDE, -1f));
            naviRouteInfo.setNaviDrivingStrategy(Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.DEFAULT);
        }
        endIOSession(false);
        HudIOSessionNavi session = (HudIOSessionNavi) mHudIOController.beginHudIOSession(HudIOSessionType.NAVI);
        if (session != null) {
            session.callNaviSDKSearchRoute(naviRouteInfo);
        }
        return true;
    }


    private void dispatcherOutCall(){
        endIOSession(false);
        HudIOSessionOutgoingCall callSession = (HudIOSessionOutgoingCall)mHudIOController.beginHudIOSession(HudIOSessionType.OUTGOING_CALL);
        if (callSession != null){
            switch (curIndex){
                case 0:
                    callSession.onOutCalling("张睿","18565371565",true);
                    break;
                case 1:
                    callSession.onOutCalling("刘承杰","13510704234",true);
                    break;
                case 2:
                    callSession.onOutCalling("何龙","13632629742",true);
                    break;
            }
        }
    }

    private void dispatcherMusicPlay(){
        endIOSession(false);
        switch (curIndex){
            case 0:
                HudIOSessionMusicPlay musicPlaySession = (HudIOSessionMusicPlay)mHudIOController.beginHudIOSession(HudIOSessionType.MUSIC);
                if (musicPlaySession != null) {
                    musicPlaySession.playRandomMusic();
                }
                break;
            case 1:
                HudIOSessionMusicPlay musicNetPlaySession = (HudIOSessionMusicPlay)mHudIOController.beginHudIOSession(HudIOSessionType.MUSIC);
                if (musicNetPlaySession != null) {
                    mHudIOController.getHudOutputer(HudIOConstants.HudOutputerType.SPEECH_OUTPUT)
                            .output(SpeechResourceManager.getInstanse().getString(R.string.tts_recognized_music), IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,true);
                }
                break;
//            case 1:
//                HudIOSessionCommand commandSession = (HudIOSessionCommand)mHudIOController.beginHudIOSession(HudIOSessionType.COMMAND);
//                if (commandSession != null) {
//                    commandSession.musicPlayControl(0);
//                }
//                break;
//            case 2:
//                HudIOSessionCommand commandNextSession = (HudIOSessionCommand)mHudIOController.beginHudIOSession(HudIOSessionType.COMMAND);
//                if(commandNextSession != null) {
//                    commandNextSession.musicPlayControl(1);
//                }
//                break;
        }
    }

    private void dispatcherMusicControl(){
        endIOSession(false);
        HudIOSessionCommand commandPlaySession = (HudIOSessionCommand)mHudIOController.beginHudIOSession(HudIOSessionType.COMMAND);
        if (commandPlaySession != null) {
            commandPlaySession.musicPlayControl(curIndex);
        }
    }



    private void dispatcherExitNavi(){
        endIOSession(false);
        SharedPreferences mSp = mHudIOController.getmContext().getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
        NaviRouteInfo naviRouteInfo = new NaviRouteInfo();
        switch (curIndex){
            case 0:
                HudIOSessionExit exitSession = (HudIOSessionExit)mHudIOController.beginHudIOSession(HudIOSessionType.EXIT);
                if (exitSession != null){
                    exitSession.onProtocol("whatever");
                }
                break;
            case 1:
                naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_HOME_PATHNAME, null));
                naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LONGITUDE, -1f));
                naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LATITUDE, -1f));
                naviRouteInfo.setNaviDrivingStrategy(Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.DEFAULT);
                HudIOSessionNavi session = (HudIOSessionNavi) mHudIOController.beginHudIOSession(HudIOSessionType.NAVI);
                if (session != null) {
                    session.callNaviSDKSearchRoute(naviRouteInfo);
                }
                break;
            case 2:
                naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_COMPANY_PATHNAME, null));
                naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LONGITUDE, -1f));
                naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LATITUDE, -1f));
                naviRouteInfo.setNaviDrivingStrategy(Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.DEFAULT);
                HudIOSessionNavi session2 = (HudIOSessionNavi) mHudIOController.beginHudIOSession(HudIOSessionType.NAVI);
                if (session2 != null) {
                    session2.callNaviSDKSearchRoute(naviRouteInfo);
                }
                break;
        }
    }



    @Override
    public void onProtocol(String protocol) {
        if (mSessionState == SESSION_STATE_IDLE) {
            curIndex = -1;
            ISpeechParserDispatcher speechParserDispatcher = (ISpeechParserDispatcher) mSpeechParser;
            int res = speechParserDispatcher.parseSpeechText(protocol);
            this.mSessionState = SESSION_STATE_DISPATCHER_SESSION;
            switch (res) {
                case ISpeechParserDispatcher.PARSE_RES_CODE_NAVI_PROMAT:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.NAVI_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.NAVI;
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_CAll_PROMAT:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.CALL_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.OUTGOING_CALL;
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_MUSIC_PROMAT:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.MUSIC_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.MUSIC;
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_EXIT_PROMAT:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.EXIT_PROMAT);
                    endIOSession(false);
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_HOME:
                    if(!goHomeCompany(true)){
                        mHudIOController.getHudSpeechManager().onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_home_nofound));
                    }
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_COMPANY:
                    if(!goHomeCompany(false)){
                        mHudIOController.getHudSpeechManager().onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_company_nofound));
                    }
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_SHUTDOWN:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.SHUTDOWN_PROMAT);
                    endIOSession(false);
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_AMAP:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.OPEN_AMAP);
                    endIOSession(false);
                    break;
                /*case ISpeechParserDispatcher.PARSE_RES_CODE_ARWAY:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.OPEN_AWAY);
                    endIOSession(false);
                    break;*/
                case ISpeechParserDispatcher.PARSE_RES_CODE_OPEN_RECORDER:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.OPEN_RECORD);
                    endIOSession(true);
                    break;
                case ISpeechParserDispatcher.PARSE_RES_CODE_CLOSE_RECORDER:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.CLOSE_RECORD);
                    endIOSession(true);
                    break;
            }

        } else if (mSessionState == SESSION_STATE_DISPATCHER_SESSION) {
            JSONObject obj = JsonTool.parseToJSONObject(protocol);
            String text = JsonTool.getJsonValue(obj, "text", "");
            if (text.equalsIgnoreCase("退出") || text.equalsIgnoreCase("关闭")) {
                mSessionCallback.dispatcherSessionPromat(HudPromatType.EXIT_PROMAT);
                endIOSession(false);
                return;
            }else if(text.contains("回家")){
                if(!goHomeCompany(true)){
                    mHudIOController.getHudSpeechManager().onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_home_nofound));
                }
                return;
            }else if(text.contains("公司")){
                if(!goHomeCompany(false)){
                    mHudIOController.getHudSpeechManager().onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_company_nofound));
                }
                return;
            }
            dispatcherSession(protocol);
        }

    }

    public void createNaviSessionDispatcher(){
        if(mSessionState == SESSION_STATE_IDLE){
            this.mSessionState = SESSION_STATE_DISPATCHER_SESSION;
            mDispatcherSessionType = HudIOSessionType.NAVI;
        }
    }


    public void onGestureProtocol(int index){
        if (mSessionState == SESSION_STATE_IDLE) {
            this.mSessionState = SESSION_STATE_DISPATCHER_SESSION;
            curIndex = -1;
            switch (index) {
                case 0:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.NAVI_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.NAVI;
                    break;
                case 1:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.MUSIC_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.MUSIC;
                    break;
                case 2:
                    mSessionCallback.dispatcherSessionPromat(HudPromatType.CALL_PROMAT);
                    mDispatcherSessionType = HudIOSessionType.OUTGOING_CALL;
                    break;
            }
        }

    }




    @Override
    public boolean onRemoteKey(int keyCode) {
        return false;
    }

    @Override
    public boolean onGestureKey(int gestureCode) {
//        if(type == HudIOConstants.INPUTER_GESTURE_SLIDE){
//            mSessionCallback.dispatcherSessionPromat(HudPromatType.EXIT_PROMAT);
//            endIOSession(false);
//            return true;
//        }
        if (gestureCode == HudIOConstants.INPUTER_GESTURE_V && curIndex != -1) {
            if (this.mSessionState == SESSION_STATE_DISPATCHER_SESSION) {
                switch (mDispatcherSessionType) {
                    case NAVI:
                        if (!EndpointsConstants.IS_NAVI) {
                            if(!goHomeCompany(curIndex == 0)){
                                if(curIndex == 0){
                                    mHudIOController.getHudOutputer(HudIOConstants.HudOutputerType.SPEECH_OUTPUT)
                                            .output(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_home_nofound), IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,true);
                                }else {
                                    mHudIOController.getHudOutputer(HudIOConstants.HudOutputerType.SPEECH_OUTPUT)
                                            .output(SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_company_nofound),IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,true);

                                }
                            }
                        }else {
                            dispatcherExitNavi();
                        }
                        break;
                    case MUSIC:
                        if (HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_IDILE) {
                            dispatcherMusicPlay();
                        }else {
                            dispatcherMusicControl();
                        }
                        break;
                    case OUTGOING_CALL:
                        dispatcherOutCall();
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
        boolean res = true;
        if (mSessionCallback == null){
            return res;
        }
        if (mDispatcherSessionType != null) {
            if (this.mSessionState == SESSION_STATE_DISPATCHER_SESSION) {
                switch (mDispatcherSessionType) {
                    case NAVI:
                        if (!EndpointsConstants.IS_NAVI) {{
                            switch (mDispatcherSessionType) {
                                case NAVI:
                                    if (!EndpointsConstants.IS_NAVI) {
                                        curIndex = result.getAreaIndex();
                                        mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                                    }else {
                                        curIndex = result.getAreaIndex();
                                        mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                                    }
                                    break;
                                case MUSIC:
                                    if (HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_IDILE) {
                                        curIndex = result.getAreaIndex();
                                        mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                                    }else {
                                        curIndex = result.getAreaIndex();
                                        mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                                    }
                                    break;
                                case OUTGOING_CALL:
                                    curIndex = result.getAreaIndex();
                                    mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                                    break;
                            }
                        }
                            curIndex = result.getAreaIndex();
                            mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                        }else {
                            curIndex = result.getAreaIndex();
                            mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                        }
                        break;
                    case MUSIC:
                        if (HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_IDILE) {
                            curIndex = result.getAreaIndex();
                            mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                        }else {
                            curIndex = result.getAreaIndex();
                            mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                        }
                        break;
                    case OUTGOING_CALL:
                        curIndex = result.getAreaIndex();
                        mSessionCallback.dispatcherSessionGestureChoose(curIndex);
                        break;
                }
            }
        }
        return res;

    }

    @Override
    public int queryGesturePointArea() {
        int area = -1;
        if (mDispatcherSessionType != null) {
            if (this.mSessionState == SESSION_STATE_DISPATCHER_SESSION) {
                switch (mDispatcherSessionType) {
                    case NAVI:
                        if (!EndpointsConstants.IS_NAVI) {
                            area=2;
                        }else {
                            area=3;
                        }
                        break;
                    case MUSIC:
                        if (HudIOConstants.HudMusicStat == HudIOConstants.MUSIC_IDILE) {
                            area=2;
                        }else {
                            area=3;
                        }
                        break;
                    case OUTGOING_CALL:
                        area=3;
                        break;
                }
            }
        }
        return area;
    }

    private void dispatcherSession(String protocol) {
        HudIOSessionType sessionType = mSpeechSessionDispatcher.parseSpeechText(protocol,false);
        errorSpeechText = "无效的命令，请重说";
        HaloLogger.logE(TAG, "sessionType:" + sessionType);
        HudIOSession newIOSession;
        if (sessionType != null && filterSessionType(sessionType)) {
            endIOSession(false);
            newIOSession = mHudIOController.beginHudIOSession(sessionType);
            if (newIOSession != null && mHudIOController.getCurrentSessionType() == sessionType) {
                newIOSession.onProtocol(protocol);
            }
        } else {
            mHudIOController.getHudSpeechManager().onSpeechUnRecogized(errorSpeechText);
        }

    }

    private boolean filterSessionType(HudIOSessionType sessionType) {
        boolean flag=false;
        if(mDispatcherSessionType == null || sessionType == null) return flag;
        switch (mDispatcherSessionType) {
            case NAVI:
                flag= sessionType.ordinal()<=HudIOSessionType.NAVI.ordinal();
                errorSpeechText = SpeechResourceManager.getInstanse().getString(R.string.tts_recognized_navigation);
                break;
            case MUSIC:
                flag= sessionType.ordinal()>=HudIOSessionType.MUSIC.ordinal();
                errorSpeechText = SpeechResourceManager.getInstanse().getString(R.string.tts_recognized_music);
                break;
            case OUTGOING_CALL:
                flag= sessionType==HudIOSessionType.OUTGOING_CALL;
                errorSpeechText = SpeechResourceManager.getInstanse().getString(R.string.tts_recognized_call);
                break;
        }
        return flag;
    }

}
