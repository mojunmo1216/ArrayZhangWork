package com.haloai.huduniversalio.session.impl;

import android.util.Log;

import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.outputer.IHudOutputer.HudOutputContentType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionIncomingCallCallback;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWords;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsInCommingCall;

public class HudIOSessionIncomingCall extends HudIOSession {//PhoneNotificationDispatcher.IPhoneCallStatusListener  {

    private static final String TAG = HudIOSessionIncomingCall.class.getName();

    public static final int CALL_STATE_START_SESSION = 201;
    public static final int CALL_STATE_WAITING_FOR_CONFIRM = 202;
    public static final int CALL_STATE_END_SESSION = 203;


    private IHudIOSessionIncomingCallCallback mSessionCallback;
    private SpeechWakeupWords mSpeechWakeupWords;

    public HudIOSessionIncomingCall(HudIOController hudIOController) {
        super(hudIOController);
        mIOSessionType = HudIOSessionType.INCOMING_CALL;
        this.mSessionState = CALL_STATE_WAITING_FOR_CONFIRM;
        mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserIncomingCall();
        this.mSpeechWakeupWords = new SpeechWakeupWordsInCommingCall();
        mSpeechEngine.setWakupWords(mSpeechWakeupWords.getAllWakeupWords());
        mCurrentSpeechWakeupWords = mSpeechWakeupWords;
    }

    @Override
    public void onWakeupWord(String wakeupWord) {
        if (mCurrentSpeechWakeupWords == null)
            return;

        int res = mCurrentSpeechWakeupWords.getWakeupResult(wakeupWord);
        if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
            if (res == SpeechWakeupWordsInCommingCall.WAKEUP_RESULT_CONFIRM) {
                mSessionCallback.onSessionIncomingCallAnswered();
            } else if (res == SpeechWakeupWordsInCommingCall.WAKEUP_RESULT_CANCLE) {
                mSessionCallback.onSessionIncomingCallRejected();
            }
            endIOSession(false);
        } else {
            this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                    mCurrentSpeechWakeupWords.getPromptText(), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
        }
    }

    @Override
    public void onProtocol(String protocol) {
        if (protocol.contains("yunzhisheng")) {
            Log.e(TAG, protocol);
        } else {
            mSessionCallback.onSessionIncomingCallRinging(protocol);
        }
    }




    @Override
    public boolean onRemoteKey(int keyCode) {

        return false;
    }

    @Override
    public boolean onGestureKey(int gestureCode) {
        if(gestureCode == HudIOConstants.INPUTER_GESTURE_V){
            mSessionCallback.onSessionIncomingCallAnswered();
            endIOSession(false);
            HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"手势V接听来电");
            return true;
        }else if(gestureCode == HudIOConstants.INPUTER_GESTURE_PALM){
            mSessionCallback.onSessionIncomingCallRejected();
            endIOSession(false);
            HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"手势滑动挂断来电");
            return true;
        }
        return false;
    }

    @Override
    public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
        return false;
    }

    @Override
    public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
        if (!(sessionCallback instanceof IHudIOSessionIncomingCallCallback)) {
            throw new IllegalArgumentException("Requirst the IHudIOSessionIncomingCallCallback");
        }
        this.mSessionCallback = (IHudIOSessionIncomingCallCallback) sessionCallback;
    }

    @Override
    public void continueSession() {

    }


}
