package com.haloai.huduniversalio.inputer.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.haloai.hud.model.v2.NaviRouteInfo;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.inputer.IInputerPhoneStatusListener;
import com.haloai.huduniversalio.inputer.IPhoneInputerNotifer;
import com.haloai.huduniversalio.session.impl.HudIOSession;
import com.haloai.huduniversalio.session.impl.HudIOSessionExit;
import com.haloai.huduniversalio.session.impl.HudIOSessionNavi;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsDialog;


/**
 * Created by zhangrui on 16/6/24.
 */
public class PhoneInputer implements IPhoneInputerNotifer{

    private HudIOController mHudIOController;
    private IInputerPhoneStatusListener mInputerPhoneStatusListener;
    private Context mContext;

    public void create(Context context,HudIOController hudIOController){
        this.mHudIOController=hudIOController;
        this.mContext=context;
    }

    public void setIInputerPhoneStatusListener(IInputerPhoneStatusListener inputerPhoneStatusListener){
        this.mInputerPhoneStatusListener=inputerPhoneStatusListener;
    }

    @Override
    public void phonePlayRecorderVideo(String path,int index){
        String protocol=index+"|"+path;
        HudIOSession hudIOSession = mHudIOController.beginHudIOSession(HudIOConstants.HudIOSessionType.RECORDER);
        if(hudIOSession != null){
            hudIOSession.onProtocol(protocol);
        }
    }


    @Override
    public void phoneControllerRecorderVideo(int command){
        String protocol=command+"|"+"PhoneInputer";
        HudIOSession hudIOSession=mHudIOController.getCurrentSession();
        if(hudIOSession != null){
            if(hudIOSession.getIOSessionType() == HudIOConstants.HudIOSessionType.RECORDER){
                hudIOSession.onProtocol(protocol);
            }
        }
    }

    @Override
    public void phoneOnClickCommand(int command){
        if(mInputerPhoneStatusListener!=null) {
            if (command == EndpointsConstants.ONCLIACK_COMMAND_WAKEUP) {
                mInputerPhoneStatusListener.onWakeupPrompt(HudIOConstants.HudInputerType.PHONE_INPUT);
            } else {
                mInputerPhoneStatusListener.onPhoneOnclickCommand(command);
            }
        }
    }

    @Override
    public  void startNavigation(NaviRouteInfo routeInfo){
        HudIOSessionNavi sessionNavi = (HudIOSessionNavi)mHudIOController.beginHudIOSession(HudIOConstants.HudIOSessionType.NAVI);
        if (sessionNavi != null){
            sessionNavi.callNaviSDKSearchRoute(routeInfo);
        }
    }

    @Override
    public void stopNavigation(){
        HudIOSessionExit exitSession = (HudIOSessionExit) mHudIOController.beginHudIOSession(HudIOConstants.HudIOSessionType.EXIT);
        if (exitSession != null) {
            exitSession.exitSessionConfirm();
        }
    }

}
