package com.haloai.huduniversalio.speech;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.IHaloLogger;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.R;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.inputer.IInputerSpeechStatusListener;
import com.haloai.huduniversalio.inputer.IInputerSpeechStatusListener.SpeechEngineErrorCode;
import com.haloai.huduniversalio.session.impl.HudIOSession;
import com.haloai.huduniversalio.speech.ISpeechEngine.ISpeechNotifier;
import com.haloai.huduniversalio.speech.unisound.UnisoundSpeech;
import com.haloai.huduniversalio.speech.unisound.UnisoundSpeechParserFactory;
import com.haloai.huduniversalio.speech.unisound.UnisoundSpeechSessionDispatcher;
import com.haloai.huduniversalio.utils.JsonTool;
import com.unisound.framework.preference.ErrorMessage;

import org.json.JSONObject;

/**
 * @author harrymoo
 */
public class SpeechManager implements ISpeechNotifier {
    private static final String TAG = "speech_info";

    private static final int INPUTER_SPEECH_ON_BASE = 110;
    private static final int INPUTER_SPEECH_ON_WAKEUP = INPUTER_SPEECH_ON_BASE + 1;
    private static final int INPUTER_SPEECH_ON_WAKEUP_PROMPT = INPUTER_SPEECH_ON_BASE + 2;
    private static final int INPUTER_SPEECH_ON_VOLUME_CHANGED = INPUTER_SPEECH_ON_BASE + 3;
    private static final int INPUTER_SPEECH_ON_RECONGIZE_STATRT = INPUTER_SPEECH_ON_BASE + 4;
    private static final int INPUTER_SPEECH_ON_TTS_BEGIN = INPUTER_SPEECH_ON_BASE + 5;
    private static final int INPUTER_SPEECH_ON_TTS_END = INPUTER_SPEECH_ON_BASE + 6;
    private static final int INPUTER_SPEECH_ON_UNRECONGIZE_END = INPUTER_SPEECH_ON_BASE + 7;
    private static final int INPUTER_SPEECH_ON_ENGINE_ERROR = INPUTER_SPEECH_ON_BASE + 8;
    private static final int INPUTER_SPEECH_ON_ENGINE_READY = INPUTER_SPEECH_ON_BASE + 9;
    private static final int INPUTER_SPEECH_ON_WAKEUP_COMMAND = INPUTER_SPEECH_ON_BASE + 10;
    private static final int INPUTER_SPEECH_ON_PROTOCOL = INPUTER_SPEECH_ON_BASE + 11;
    private static final String MESSAGE_ERROR_KEY = "ErrorReason";

    private enum EngineMode {
        EngineModeNomal,
        EngineModeOneShot,
        EngineModeMix,
    }

    private static final EngineMode mEngineMode = EngineMode.EngineModeOneShot;

    private Context mContext;
    private ISpeechEngine mSpeechEngine;
    private HudIOController mHudIOController;
    private IInputerSpeechStatusListener mInputerSpeechStatusListener;
    private ISpeechParserFactory mSpeechParserFactory;
    private ISpeechSessionDispatcher mSpeechSessionDispatcher;

    private static IHaloLogger mIHaloLogger = HaloLogger.logger;

    public void create(Context context, HudIOController hudIOController) {
        this.mContext = context;
        this.mHudIOController = hudIOController;
        initSpeechEngine();// TODO 测试时候的注释
    }

    Handler mSpeechManagerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INPUTER_SPEECH_ON_WAKEUP:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onWakeup();
                    break;
                case INPUTER_SPEECH_ON_WAKEUP_PROMPT:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onWakeupPrompt(HudInputerType.SPEECH_INPUT);
                    break;
                case INPUTER_SPEECH_ON_VOLUME_CHANGED:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onSpeechVolumeChanged(msg.arg1);
                    break;
                case INPUTER_SPEECH_ON_RECONGIZE_STATRT:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onSpeechRecognizeStart();
                    break;
                case INPUTER_SPEECH_ON_UNRECONGIZE_END:
                    break;
                case INPUTER_SPEECH_ON_TTS_BEGIN:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onSpeechTTSBegin();
                    break;
                case INPUTER_SPEECH_ON_TTS_END:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onSpeechTTSEnd();
                    break;
                case INPUTER_SPEECH_ON_ENGINE_ERROR:
                    if (mInputerSpeechStatusListener != null) {
                        SpeechEngineErrorCode errorCode = (SpeechEngineErrorCode) msg.obj;
                        String errorMsg = msg.getData().getString(MESSAGE_ERROR_KEY);
                        mInputerSpeechStatusListener.onSpeechEngineError(errorCode, errorMsg);
                    }
                    break;
                case INPUTER_SPEECH_ON_ENGINE_READY:
                    if (mInputerSpeechStatusListener != null)
                        mInputerSpeechStatusListener.onSpeechEngineReady();
                    break;
                case INPUTER_SPEECH_ON_WAKEUP_COMMAND:
                    String wakeupCommand = msg.obj.toString();
                    onHudWakeupCommand(wakeupCommand);
                    break;
                case INPUTER_SPEECH_ON_PROTOCOL:
                    String protocol = msg.obj.toString();
                    onHudProtocol(protocol);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void initSpeechEngine() {
        //TODO 初始化语音引擎的资源文件应该放在内部存储中，而不是sdcard
        mSpeechEngine = new UnisoundSpeech();
        String sdCard = Environment.getExternalStorageDirectory().getPath();
        mSpeechEngine.init(mContext, this, sdCard + "/Halo/", "你好,光晕");
//		mSpeechEngine.setWakeupMode("normal");
        mSpeechParserFactory = new UnisoundSpeechParserFactory();
        mSpeechSessionDispatcher = new UnisoundSpeechSessionDispatcher();
    }

    public void setSpeechInputerStatusListener(IInputerSpeechStatusListener inputerSpeechStatusListener) {
        mInputerSpeechStatusListener = inputerSpeechStatusListener;
    }

    public void destroy() {
        mSpeechEngine.destroy();
    }

    public ISpeechParserFactory getSpeechParserFactory() {
        return mSpeechParserFactory;
    }

    public ISpeechEngine getSpeechEngine() {
        return mSpeechEngine;
    }

    public void onReadyForSpeech(String tips) {

    }

    public void onHudProtocol(String protocol) {
        JSONObject obj = JsonTool.parseToJSONObject(protocol);
        String text = JsonTool.getJsonValue(obj, "text", "");
        mInputerSpeechStatusListener.onSpeechText(text);
        HaloLogger.postI(EndpointsConstants.SPEECH_TAG,"protocol:"+protocol);
        HudIOSession currentSession = mHudIOController.getCurrentSession();
        if (currentSession != null) {
            if (currentSession.getIOSessionType() == HudIOSessionType.DISPATCHER) {
                currentSession.onProtocol(protocol);
                HaloLogger.logE("speech_info", "currentSession : " + currentSession.getIOSessionType());
                return;
            }
        }
        HudIOSessionType sessionType = mSpeechSessionDispatcher.parseSpeechText(protocol,false);
        String MESSAGE_ERROR_TEXT = mSpeechSessionDispatcher.getErrorMessageText();
        HudIOSession newIOSession;
        if (sessionType != null) {
            if (sessionType == HudIOSessionType.OUTGOING_CALL) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.OUTGOING_CALL);
            } else if (sessionType == HudIOSessionType.COMMAND) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.COMMAND);
            } else if (sessionType == HudIOSessionType.MUSIC) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.MUSIC);
            } else if (sessionType == HudIOSessionType.NAVI) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.NAVI);
            } else if (sessionType == HudIOSessionType.EXIT) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.EXIT);
            } else if (sessionType == HudIOSessionType.SIMULATION_NAVI) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.SIMULATION_NAVI);
            } else if (sessionType == HudIOSessionType.RECORDER) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.RECORDER);
            } else if (sessionType == HudIOSessionType.DISPATCHER) {
                newIOSession = mHudIOController.beginHudIOSession(HudIOSessionType.DISPATCHER);
            } else {
                onSpeechUnRecogized(MESSAGE_ERROR_TEXT);
                return;
            }
        } else {
            onSpeechUnRecogized(MESSAGE_ERROR_TEXT);
            return;
        }
        HaloLogger.logE("HudIOSessionRecorder", newIOSession == null ? "null" : "" + newIOSession.getIOSessionType());
        HudIOSession currentIOSession = mHudIOController.getCurrentSession();
        if (currentIOSession != null && newIOSession != null && newIOSession.getIOSessionType() == currentIOSession.getIOSessionType()) {
            newIOSession.onProtocol(protocol);
        }

    }

    public void onHudWakeupCommand(String wakeupCmd) {
        HaloLogger.postI(EndpointsConstants.SPEECH_TAG,"WakeupCommand:"+wakeupCmd);
        HudIOSession ioSession = mHudIOController.getCurrentSession();
        if (ioSession != null)
            ioSession.onWakeupWord(wakeupCmd);
    }

    @Override
    public void onProtocol(String protocol) {
        if (protocol != null) {
            Message message = new Message();
            message.what = INPUTER_SPEECH_ON_PROTOCOL;
            message.obj = protocol;
            mSpeechManagerHandler.sendMessage(message);
        } else {
            onSpeechUnRecogized(SpeechResourceManager.getInstanse().getString(R.string.tts_error_unrecognize_default));
        }
    }

    public void startRecognizeManually() {
        mSpeechEngine.startRecognizeManually();
    }

    public void cancleRecognizeManually() {
        mSpeechEngine.cancelRecognizeManually();
        HaloLogger.postI(EndpointsConstants.SPEECH_TAG,"CancelRecognizeManually");
    }

    public void pauseSpecchInputer(){
        mSpeechEngine.cancelRecognizeManually();
        mSpeechEngine.pauseSpeech();
    }

    public void resumeSpeechInputer(){
        mSpeechEngine.resumeSpeech();
    }

    //手动编译引擎(主要是编译电话本)
    public void startCompileManual(){
        HaloLogger.logE("speech_info","startCompileManual");
        mSpeechEngine.startCompileManual();
    }

    public void playTTS(String ttsContent, Boolean keepRecognize) {
        mSpeechEngine.playTTS(ttsContent, keepRecognize);
    }

    public void onSpeechEngineRecogNetworkFail() {
        if (mInputerSpeechStatusListener != null) {
            Message message = new Message();
            Bundle bun = new Bundle();
            bun.putString(MESSAGE_ERROR_KEY, "NetworkFail");
            message.obj = SpeechEngineErrorCode.SPEECH_NET_ERROR;
            message.what = INPUTER_SPEECH_ON_ENGINE_ERROR;
            message.setData(bun);
            mSpeechManagerHandler.sendMessage(message);
        }
    }

    @Override
    public void onInitEngineDone() {
        mHudIOController.onInputerReady(HudInputerType.SPEECH_INPUT);
        if (mEngineMode == EngineMode.EngineModeNomal) {
            mSpeechEngine.setWakeupMode("normal");
        }

    }

    @Override
    public void onWakeup() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_WAKEUP);
    }

    @Override
    public void onWakeupPrompt() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_WAKEUP_PROMPT);
    }

    @Override
    public void onWakeupCommand(String wakeupCmd) {
        if (wakeupCmd != null) {
            Message message = new Message();
            message.what = INPUTER_SPEECH_ON_WAKEUP_COMMAND;
            message.obj = wakeupCmd;
            mSpeechManagerHandler.sendMessage(message);
        }
    }

    @Override
    public void onWakeupWordsReady() {
        LogI("onWakeupWordsReady called ");
    }

    public void onSpeechUnRecogized(String errorMessage) {
        if (mInputerSpeechStatusListener != null) {
            mInputerSpeechStatusListener.onSpeechUnRecogized(errorMessage);
        }
    }

    @Override
    public void onRecognizeStart() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_RECONGIZE_STATRT);
    }

    @Override
    public void onVolumeChanged(int volume) {
        if (mInputerSpeechStatusListener != null) {
            Message message = mSpeechManagerHandler.obtainMessage(INPUTER_SPEECH_ON_VOLUME_CHANGED);
            message.arg1 = volume;
            message.what = INPUTER_SPEECH_ON_VOLUME_CHANGED;
            mSpeechManagerHandler.sendMessage(message);
        }
    }

    @Override
    public void onError(String errorReason) {
        HaloLogger.postE(EndpointsConstants.SPEECH_TAG,"SpeechError:"+errorReason);
        if (this.mInputerSpeechStatusListener != null) {
            Message message = new Message();
            Bundle bun = new Bundle();
            bun.putString(MESSAGE_ERROR_KEY, errorReason);
            if (ErrorMessage.ASR_TIMEOUT_ERROR.equals(errorReason)) {
                message.obj = SpeechEngineErrorCode.SPEECH_ASR_TIMEOUT;
            } else if (ErrorMessage.MIC_CONFLICT_ERROR.equals(errorReason)) {
                message.obj = SpeechEngineErrorCode.SPEECH_MIC_CONFLICT_ERROR;
            } else if (ErrorMessage.COMPILE_ERROR.equals(errorReason)) {
                message.obj = SpeechEngineErrorCode.SPEECH_COMPILE_ERROR;
            } else if (ErrorMessage.NULL_PROTOCAL_ERROR.equals(errorReason)) {
                message.obj = SpeechEngineErrorCode.SPEECH_NULL_PROTOCAL_ERROR;
            } else if (ErrorMessage.PROTOCAL_ERROR.equals(errorReason)) {
                message.obj = SpeechEngineErrorCode.SPEECH_PROTOCAL_ERROR;
            } else {
                message.obj = SpeechEngineErrorCode.SPEECH_INIT_ERROR;
            }
            message.what = INPUTER_SPEECH_ON_ENGINE_ERROR;
            message.setData(bun);
            mSpeechManagerHandler.sendMessage(message);
        }
    }

    @Override
    public void onTTSBegin() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_TTS_BEGIN);
    }

    @Override
    public void onTTSEnd() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_TTS_END);
    }

    @Override
    public void onEngineCompileStart() {

    }

    @Override
    public void onEngineCompileFinished() {
        mSpeechManagerHandler.sendEmptyMessage(INPUTER_SPEECH_ON_ENGINE_READY);
    }

    private void LogI(String msg) {
        mIHaloLogger.logI(TAG, msg);
    }
}
