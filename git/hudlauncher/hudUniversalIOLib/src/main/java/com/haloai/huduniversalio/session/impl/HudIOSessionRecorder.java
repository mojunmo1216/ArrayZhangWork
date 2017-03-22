package com.haloai.huduniversalio.session.impl;

import android.util.Log;

import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback.HudRecorderCommandType;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback.HudRecorderVideoType;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWords;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsRecorderBrowse;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsRecorderPreview;

public class HudIOSessionRecorder extends HudIOSession {//implements SpeechPlayerStateCallBack{

    //	public static final String CAMERA_ACTION_PREFIX = "camera_";
//	public static final String CAMERA_ACTION_SHOW = "camera_show";
//	public static final String CAMERA_ACTION_HIDE = "camera_hide";
//	public static final String CAMERA_ACTION_TAKE_PICTURE = "camera_take_picture";
    public static final String TAG = "SpeechSessionTakephoto";
    public static final String EXTRA_ACTION = "action";
    public static final String CAMERA_SPEECH_ENGINE_RECOG_VOICE_DETECTED = "speech_engine_voice_detected_camera";

    //行车记录仪预览
    private static final int SESSION_STATE_RECORDER_PREVIEW = SESSION_STATE_BASE + 1;
    //行车记录仪浏览视频
    private static final int SESSION_STATE_RECORDER_BROWSE = SESSION_STATE_BASE + 2;
    //行车记录仪删除视频
    //private static final int SESSION_STATE_RECORDER_DELETE = SESSION_STATE_BASE + 3;

    private String mVideoPath;

    //	private SpeechPlayer speechPlayer = new SpeechPlayer(this.context, this);
    private IHudIOSessionRecorderCallback mSessionCallback;

    public HudIOSessionRecorder(HudIOController hudIOController) {
        super(hudIOController);
        mIOSessionType = HudIOSessionType.RECORDER;
        mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserRecord();
    }

    @Override
    public void onWakeupWord(String wakeupWord) {
        Log.e("HudIOSessionRecorder", "wakeupWord : " + wakeupWord);
        if (mCurrentSpeechWakeupWords == null) {
            // TODO 错误播报
            return;
        }
        int wakeupWordIndexInList = mCurrentSpeechWakeupWords.getWakeupResult(wakeupWord);
        if (wakeupWordIndexInList == SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
            //TODO 错误index
            return;
        }
        if (mSessionState == SESSION_STATE_RECORDER_PREVIEW) {
            //处于行车记录仪首界面时
            HudRecorderVideoType hudRecorderVideoType = HudRecorderVideoType.NULL;
            switch (wakeupWordIndexInList) {
                case SpeechWakeupWordsRecorderPreview.WAKEUP_RESULT_EXIT:
                    mSessionCallback.onSessionRecorderClose();
                    endIOSession(false);
                    break;
                case SpeechWakeupWordsRecorderPreview.WAKEUP_RESULT_WONDERFUL:
                    hudRecorderVideoType = HudRecorderVideoType.WONDERFUL_VIDEO;
                    break;
                case SpeechWakeupWordsRecorderPreview.WAKEUP_RESULT_LOOPING:
                    hudRecorderVideoType = HudRecorderVideoType.LOOPING_VIDEO;
                    break;
            }
            if (hudRecorderVideoType != HudRecorderVideoType.NULL) {
                mSessionCallback.onSessionRecorderBrowseVideo(hudRecorderVideoType);
            }
        } else if (mSessionState == SESSION_STATE_RECORDER_BROWSE) {
            //处于行车记录仪浏览视频界面时
            if (wakeupWordIndexInList == SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_EXIT) {
                mSessionCallback.onSessionRecorderClose();
                endIOSession(false);
            } else {
                HudRecorderCommandType hudRecorderCommandType = HudRecorderCommandType.NULL;
                switch (wakeupWordIndexInList) {
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_BACK:
                        hudRecorderCommandType = HudRecorderCommandType.BACK;
                        this.mSessionState = SESSION_STATE_IDLE;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_FAST_FORWARD:
                        hudRecorderCommandType = HudRecorderCommandType.FAST_FORWARD;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_FAST_BACK:
                        hudRecorderCommandType = HudRecorderCommandType.FAST_BACK;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_LOCK:
                        hudRecorderCommandType = HudRecorderCommandType.LOCK_VIDEO;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_DELETE:
                        hudRecorderCommandType = HudRecorderCommandType.DELETE_VIDEO;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_PREV_VIDEO:
                        hudRecorderCommandType = HudRecorderCommandType.PREV_VIDEO;
                        break;
                    case SpeechWakeupWordsRecorderBrowse.WAKEUP_RESULT_NEXT_VIDEO:
                        hudRecorderCommandType = HudRecorderCommandType.NEXT_VIDEO;
                        break;
                }
                if (hudRecorderCommandType != HudRecorderCommandType.NULL) {
                    mSessionCallback.onSessionRecorderCommand(hudRecorderCommandType);
                }
            }
        } /*else if (mSessionState == SESSION_STATE_RECORDER_DELETE) {
            HudRecorderCommandType hudRecorderCommandType = HudRecorderCommandType.NULL;
            switch (wakeupWordIndexInList) {
                case SpeechWakeupWordsDialog.WAKEUP_RESULT_CONFIRM:
                    hudRecorderCommandType = HudRecorderCommandType.CONFIRM;
                    break;
                case SpeechWakeupWordsDialog.WAKEUP_RESULT_BACK:
                    hudRecorderCommandType = HudRecorderCommandType.CANCEL;
                    break;
            }
            if (hudRecorderCommandType != HudRecorderCommandType.NULL) {
                this.mSessionState = SESSION_STATE_RECORDER_PREVIEW;
                mSessionCallback.onSessionRecorderCommand(hudRecorderCommandType);
            }
        }*/
    }

    @Override
    public void onProtocol(String protocol) {
        if(protocol.contains("yunzhisheng")){
            mSessionCallback.onSessionRecorderOpen();
        }else if(protocol.contains("PhoneInputer")){
            String[] strings=protocol.split("\\|");
            int command =Integer.valueOf(strings[0]);
            HudRecorderCommandType hudRecorderCommandType = HudRecorderCommandType.NULL;
            switch (command){
                case EndpointsConstants.COMMAND_VALUVE_PLAY:
                    hudRecorderCommandType=HudRecorderCommandType.PLAY;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_PAUSE:
                    hudRecorderCommandType=HudRecorderCommandType.PAUSE;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_NEXT_VIDEO:
                    hudRecorderCommandType=HudRecorderCommandType.NEXT_VIDEO;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_PREV_VIDEO:
                    hudRecorderCommandType=HudRecorderCommandType.PREV_VIDEO;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_FAST:
                    hudRecorderCommandType=HudRecorderCommandType.FAST_FORWARD;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_REWIND:
                    hudRecorderCommandType=HudRecorderCommandType.FAST_BACK;
                    break;
                case EndpointsConstants.COMMAND_VALUVE_UP_VLUME:
                    hudRecorderCommandType=HudRecorderCommandType.UP_VOLUME;
                    break;
                 case EndpointsConstants.COMMAND_VALUVE_DOWN_VLUME:
                    hudRecorderCommandType=HudRecorderCommandType.DOWN_VOLUME;
                    break;
                 case EndpointsConstants.COMMAND_VALUVE_LOCK:
                    hudRecorderCommandType=HudRecorderCommandType.LOCK_VIDEO;
                    break;
                 case EndpointsConstants.COMMAND_VALUVE_DELET_VIDEO:
                     if(mVideoPath!=null){
                         mSessionCallback.onSessionRecorderPhonePlay(mVideoPath,EndpointsConstants.REMOTER_COMMAND_DELET_VIDEO);
                         endIOSession(false);
                         return;
                     }
                     break;
                case EndpointsConstants.COMMAND_VALUVE_EXIT_VIDEO:
                    mSessionCallback.onSessionRecorderClose();
                    endIOSession(false);
                    return;
            }
            if(hudRecorderCommandType!=HudRecorderCommandType.NULL){
                mSessionCallback.onSessionRecorderCommand(hudRecorderCommandType);
            }
        }else {
            String[] strings=protocol.split("\\|");
            int index =Integer.valueOf(strings[0]);
            this.mSessionState = SESSION_STATE_RECORDER_PREVIEW;
            mSessionCallback.onSessionRecorderPhonePlay(strings[1],index);
            mVideoPath = strings[1];
            Log.e("speech_info", "path:" + strings[1]+"index:"+index);
        }

    }

    @Override
    public boolean onRemoteKey(int keyCode) {
        return false;
    }

    @Override
    public boolean onGestureKey(int gestureCode) {
        /*if(mSessionState == SESSION_STATE_RECORDER_DELETE){
            if(type==HudIOConstants.INPUTER_GESTURE_V){
                this.mSessionState = SESSION_STATE_RECORDER_PREVIEW;
                mSessionCallback.onSessionRecorderCommand(HudRecorderCommandType.CONFIRM);
                return true;
            }else if(type==HudIOConstants.INPUTER_GESTURE_SLIDE){
                this.mSessionState = SESSION_STATE_RECORDER_PREVIEW;
                mSessionCallback.onSessionRecorderCommand(HudRecorderCommandType.CANCEL);
                return true;
            }
        }else*/ if(mSessionState == SESSION_STATE_RECORDER_PREVIEW){
            if(gestureCode==HudIOConstants.INPUTER_GESTURE_PALM){
                mSessionCallback.onSessionRecorderClose();
                endIOSession(false);
                return true;
            }
        }else if(mSessionState == SESSION_STATE_RECORDER_BROWSE){
            if(gestureCode==HudIOConstants.INPUTER_GESTURE_SLIDE){
                mSessionCallback.onSessionRecorderCommand(HudRecorderCommandType.NEXT_VIDEO);
                return true;
            }else if(gestureCode == HudIOConstants.INPUTER_GESTURE_PALM){
                mSessionCallback.onSessionRecorderClose();
                endIOSession(false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
        return false;
    }

    @Override
    public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
        if (!(sessionCallback instanceof IHudIOSessionRecorderCallback)) {
            throw new IllegalArgumentException("Requirst the IHudIOSessionRecorderCallback");
        }
        this.mSessionCallback = (IHudIOSessionRecorderCallback) sessionCallback;
    }

    @Override
    public void continueSession() {
        if (mSessionState == SESSION_STATE_IDLE) {
            mSessionState = SESSION_STATE_RECORDER_PREVIEW;
        } else if (mSessionState == SESSION_STATE_RECORDER_PREVIEW) {
            mSessionState = SESSION_STATE_RECORDER_BROWSE;
        } /*else if (mSessionState == SESSION_STATE_RECORDER_BROWSE) {
            mSessionState = SESSION_STATE_RECORDER_DELETE;
        }*/
        setWakeupWordsForCurrentState();
    }

    private void setWakeupWordsForCurrentState() {
        if (mSessionState == SESSION_STATE_RECORDER_BROWSE) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsRecorderBrowse();
        } else if (mSessionState == SESSION_STATE_RECORDER_PREVIEW) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsRecorderPreview();
        } /*else if (mSessionState == SESSION_STATE_RECORDER_DELETE) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsDialog();
        }*/

        if (mCurrentSpeechWakeupWords != null) {
            mSpeechEngine.setWakupWords(mCurrentSpeechWakeupWords.getAllWakeupWords());
        }
    }

}
