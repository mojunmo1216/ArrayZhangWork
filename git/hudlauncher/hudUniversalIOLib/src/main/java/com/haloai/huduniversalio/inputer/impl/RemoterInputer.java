package com.haloai.huduniversalio.inputer.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.inputer.IInputerRemoterStatusListener;
import com.haloai.huduniversalio.session.impl.HudIOSession;

/**
 * Created by zhangrui on 16/5/26.
 */
public class RemoterInputer {
    private HudIOController mHudIOController;
    private IInputerRemoterStatusListener mInputerRemoterStatusListener;
    private Context mContext;

    public void create(Context context,HudIOController hudIOController){
        this.mHudIOController=hudIOController;
        this.mContext=context;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(HudIOConstants.ACTION_REMOTER_INPUTER);
        mContext.registerReceiver(new RemoterBroastReceiver(),intentFilter);
    }

    public void setIInputerRemoterStatusListener(IInputerRemoterStatusListener inputerRemoterStatusListener){
        this.mInputerRemoterStatusListener=inputerRemoterStatusListener;
    }

    public void onRemoterWakeUp(){
        HaloLogger.logE("speech_info","onRemoterWakeUp");
        HudIOSession currentSession=mHudIOController.getCurrentSession();
        boolean keyEvent=false;
        if(currentSession!=null){
            keyEvent=currentSession.onRemoteKey(HudIOConstants.INPUTER_REMOTER_ENTER);
        }
        if(!keyEvent&&mInputerRemoterStatusListener!=null){
            mInputerRemoterStatusListener.onWakeupPrompt(HudIOConstants.HudInputerType.REMOTER_INPUT);
        }
    }

    public void onRemoterKey(int keyCode){
        HaloLogger.logE("speech_info","keyCode:"+keyCode);
        HudIOSession currentSession=mHudIOController.getCurrentSession();
        boolean keyEvent=false;
        if(currentSession!=null){
            keyEvent=currentSession.onRemoteKey(keyCode);
        }
        if(mInputerRemoterStatusListener!=null&&!keyEvent){
            mInputerRemoterStatusListener.onRemoterKey(keyCode);
        }

    }

    class RemoterBroastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equalsIgnoreCase(HudIOConstants.ACTION_REMOTER_INPUTER)){
                int keyCode=intent.getIntExtra(HudIOConstants.ACTION_REMOTER_INPUTER,0);
                HaloLogger.logE("speech_info","RemoterBroastReceiver:"+keyCode);
                switch (keyCode){
                    case HudIOConstants.INPUTER_REMOTER_ENTER:
                        onRemoterWakeUp();
                        break;
                    case HudIOConstants.INPUTER_REMOTER_LEFT:
                        onRemoterKey(HudIOConstants.INPUTER_REMOTER_LEFT);
                        break;
                    case HudIOConstants.INPUTER_REMOTER_RIGHT:
                        onRemoterKey(HudIOConstants.INPUTER_REMOTER_RIGHT);
                        break;
                }
            }
        }
    }
}
