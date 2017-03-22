package com.haloai.huduniversalio.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.haloai.hud.hudendpoint.phoneconnection.PhoneConnectionManager;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.IHudIOController;
import com.haloai.huduniversalio.IHudIOControllerListener;
import com.haloai.huduniversalio.inputer.IInputerGestureStatusListener;
import com.haloai.huduniversalio.inputer.IInputerPhoneStatusListener;
import com.haloai.huduniversalio.inputer.IInputerRemoterStatusListener;
import com.haloai.huduniversalio.inputer.IInputerSpeechStatusListener;
import com.haloai.huduniversalio.inputer.IInputerStatusListener;
import com.haloai.huduniversalio.inputer.dispatchImpl.NaviRouteDispatcherImp;
import com.haloai.huduniversalio.inputer.dispatchImpl.PhoneCommandDispatcherImp;
import com.haloai.huduniversalio.inputer.impl.GestureInputer;
import com.haloai.huduniversalio.inputer.impl.PhoneInputer;
import com.haloai.huduniversalio.inputer.impl.RemoterInputer;
import com.haloai.huduniversalio.outputer.IHudOutputer;
import com.haloai.huduniversalio.outputer.impl.HudOutputerPhone;
import com.haloai.huduniversalio.outputer.impl.HudOutputerSpeech;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.impl.HudIOSession;
import com.haloai.huduniversalio.session.impl.HudIOSessionCommand;
import com.haloai.huduniversalio.session.impl.HudIOSessionDispatcher;
import com.haloai.huduniversalio.session.impl.HudIOSessionExit;
import com.haloai.huduniversalio.session.impl.HudIOSessionHangUp;
import com.haloai.huduniversalio.session.impl.HudIOSessionIncomingCall;
import com.haloai.huduniversalio.session.impl.HudIOSessionMusicPlay;
import com.haloai.huduniversalio.session.impl.HudIOSessionNavi;
import com.haloai.huduniversalio.session.impl.HudIOSessionOutgoingCall;
import com.haloai.huduniversalio.session.impl.HudIOSessionRecorder;
import com.haloai.huduniversalio.speech.SpeechManager;
import com.haloai.huduniversalio.SpeechResourceManager;

import java.util.Stack;

public class HudIOController implements IHudIOController {
	
	private static final String TAG = HudIOController.class.getName();

	private Context mContext;
	private boolean bInited = false;
	private IHudIOControllerListener mHudIOControllerListener;
	private Stack<HudIOSession> mHudIOSessionStack = new Stack<HudIOSession>();
	private SpeechManager mHudSpeechManager = new SpeechManager();
	private RemoterInputer mRemoterInputer = new RemoterInputer();
	private GestureInputer mGestureInputer = new GestureInputer();
	private PhoneInputer mPhoneInputer = new PhoneInputer();
	private HudOutputerSpeech mHudOutputerSpeech;
	private HudOutputerPhone mHudOutputerPhone;



	public HudIOController(Context appContext) {
		this.mContext = appContext;
	}


	public Context getmContext(){
		return mContext;
	}


	@Override
	public void init() {
		if (!bInited) {
			IntentFilter intentFilter=new IntentFilter();
			intentFilter.addAction(HudIOConstants.ACTION_BTCALL_OVER);
			intentFilter.addAction(HudIOConstants.ACTION_BTCALL_INCOMING);
			intentFilter.addAction(HudIOConstants.ACTION_CONTACTS_DOWNLOAD_OVER);
			intentFilter.addAction(HudIOConstants.ACTION_ON_OUT_CALLING);
			intentFilter.addAction(HudIOConstants.GESTURE_CONNECT_DEBUG);
			mContext.registerReceiver(mPhoneCallBroadcastReceiver, intentFilter);
			mHudSpeechManager.create(mContext, this);
			mRemoterInputer.create(mContext, this);
			mGestureInputer.create(mContext, this);
			mPhoneInputer.create(mContext, this);
			mHudOutputerSpeech = new HudOutputerSpeech(mHudSpeechManager.getSpeechEngine());
			mHudOutputerPhone = new HudOutputerPhone();
			SpeechResourceManager.init(mContext);
			PhoneConnectionManager.getInstance().addDataDispatcher(new NaviRouteDispatcherImp(mPhoneInputer));
			PhoneConnectionManager.getInstance().addDataDispatcher(new PhoneCommandDispatcherImp(mContext,mPhoneInputer));
			bInited = true;
		}
	}

	@Override
	public void setHudIOControllerListener(IHudIOControllerListener ioControllerListener) {
		mHudIOControllerListener = ioControllerListener;
	}

	@Override
	public boolean isInputerReady(HudInputerType inputerType) {
		if (inputerType == HudInputerType.SPEECH_INPUT)
			return mHudSpeechManager.getSpeechEngine().isEngineReady();
		return false;
	}

	@Override
	public boolean isOutputerReady(HudOutputerType outputerType) {
		if (outputerType == HudOutputerType.SPEECH_OUTPUT)
			return mHudSpeechManager.getSpeechEngine().isEngineReady();
		return false;
	}

	@Override
	public void setInputerStatusListener(HudInputerType inputerType, IInputerStatusListener inputerStatusListener) {
		if (inputerType == HudInputerType.SPEECH_INPUT) {
			if (!(inputerStatusListener instanceof IInputerSpeechStatusListener)) {
				throw new IllegalArgumentException("Illegal inputer status listener type.");
			}
			mHudSpeechManager.setSpeechInputerStatusListener((IInputerSpeechStatusListener)inputerStatusListener);
		}else if(inputerType == HudInputerType.REMOTER_INPUT){
			if(!(inputerStatusListener instanceof IInputerRemoterStatusListener)){
				throw new IllegalArgumentException("Illegal inputer status listener type.");
			}
			mRemoterInputer.setIInputerRemoterStatusListener((IInputerRemoterStatusListener) inputerStatusListener);
		}else if(inputerType == HudInputerType.GESTURE_INPUT){
			if(!(inputerStatusListener instanceof IInputerGestureStatusListener)){
				throw new IllegalArgumentException("Illegal inputer status listener type.");
			}
			mGestureInputer.setInputerGestureStatusListener((IInputerGestureStatusListener) inputerStatusListener);
		}else if(inputerType == HudInputerType.PHONE_INPUT){
			if(!(inputerStatusListener instanceof IInputerPhoneStatusListener)){
				throw new IllegalArgumentException("Illegal inputer status listener type.");
			}
			mPhoneInputer.setIInputerPhoneStatusListener((IInputerPhoneStatusListener) inputerStatusListener);
		}
		else {
			throw new IllegalArgumentException("Unknow inputerType.");
		}
	}

	@Override
	public void resumeHudInputer(HudInputerType inputerType) {
		if(inputerType == HudInputerType.GESTURE_INPUT){
			mGestureInputer.resumeGestureInputer();
		}else if(inputerType == HudInputerType.SPEECH_INPUT){
			mHudSpeechManager.resumeSpeechInputer();
		}
	}

	@Override
	public void pauseHudInputer(HudInputerType inputerType) {
		if(inputerType == HudInputerType.GESTURE_INPUT){
			mGestureInputer.pauseGestureInputer();
		}else if(inputerType == HudInputerType.SPEECH_INPUT){
			mHudSpeechManager.pauseSpecchInputer();
		}
	}

	@Override
	public IHudOutputer getHudOutputer(HudOutputerType outputerType) {
		if (outputerType == HudOutputerType.SPEECH_OUTPUT)
			return mHudOutputerSpeech;
		else if (outputerType == HudOutputerType.PHONE_OUTPUT)
			return mHudOutputerPhone;

		return null;
	}

	public SpeechManager getHudSpeechManager() {
		return mHudSpeechManager;
	}

	public void onInputerReady(HudInputerType inputerType) {
		mHudIOControllerListener.onInputerReady(inputerType);
	}

	public HudIOSession beginHudIOSession(HudIOSessionType sessionType) {
	//	HaloLogger.logI(TAG, "Begin a IOSession. Type is " + sessionType);
		Log.i(TAG, "Begin a IOSession. Type is " + sessionType);
		HaloLogger.postI(EndpointsConstants.NAVI_TAG,"BeginSession:"+sessionType);
		IHudIOSessionCallback ioSessionCallback = mHudIOControllerListener.onSessionBegin(sessionType);
		//如果无法创建session或者当前栈顶为来电session，则不去创建其他session
		if (ioSessionCallback == null || (getCurrentSession()!=null && getCurrentSession().getIOSessionType()==HudIOSessionType.INCOMING_CALL && sessionType != HudIOSessionType.HANGUP )) {
			//上层应用不接受该Session的创建，默默退出不压栈
			return null;
		}
		//TODO pop到相同的为止（1.如果是导航的sessionType清空stack 2.如果不是导航的sessionType，则清空至相同的type或者导航的type为止）
		//TODO 未测试
		HudIOSession newIOSession = createIOSession(sessionType, this, ioSessionCallback);
		//如果不是空栈
		if(getCurrentSession()!=null){
			//如果新session为导航session
			if(sessionType == HudIOSessionType.NAVI){
				while(!mHudIOSessionStack.isEmpty()&&mHudIOSessionStack.peek().getIOSessionType()!=HudIOSessionType.DISPATCHER){
					HudIOSession hudIOSession = mHudIOSessionStack.pop();
					mHudIOControllerListener.onSessionEnd(hudIOSession.getIOSessionType(), false);
				}
			}else{
				//如果新session不是导航session
				HudIOSession hudIOSession = mHudIOSessionStack.peek();
				if(hudIOSession.getIOSessionType()==sessionType){
					mHudIOSessionStack.pop();
					mHudIOControllerListener.onSessionEnd(hudIOSession.getIOSessionType(), false);
				}else if(sessionType == HudIOSessionType.HANGUP && (hudIOSession.getIOSessionType()==HudIOSessionType.OUTGOING_CALL||
						hudIOSession.getIOSessionType()==HudIOSessionType.INCOMING_CALL)){
					endHudIOSession(hudIOSession.getIOSessionType(),false);
				}
			}
		}
		mHudIOSessionStack.push(newIOSession);

		return newIOSession;
	}

	public void endHudIOSession(HudIOSessionType sessionType, boolean isCancelled) {
		if(!mHudIOSessionStack.isEmpty()){
			HudIOSession peekedSession = mHudIOSessionStack.peek();
			if (peekedSession.getIOSessionType() == sessionType) {
				mHudIOSessionStack.pop();
				mHudIOControllerListener.onSessionEnd(sessionType, isCancelled);
				Log.i("Session_Dispatcher", "End a IOSession. Type is " + sessionType);
				if (!mHudIOSessionStack.isEmpty())
					mHudIOSessionStack.peek().resumeCurrentWakeupWords();
			}
		}
	}

	public HudIOSession getCurrentSession() {
		if (mHudIOSessionStack.size() > 0) {
			return mHudIOSessionStack.peek();
		} else {
			return null;
		}
	}


	public static HudIOSession createIOSession(HudIOSessionType sessionType, HudIOController hudIOController, IHudIOSessionCallback sessionCallback) {
		HudIOSession ioSession = null;
		if (sessionType == HudIOSessionType.NAVI) {
			ioSession = new HudIOSessionNavi(hudIOController);
		} else if (sessionType == HudIOSessionType.MUSIC) {
			ioSession = new HudIOSessionMusicPlay(hudIOController);
		} else if (sessionType == HudIOSessionType.OUTGOING_CALL) {
			ioSession = new HudIOSessionOutgoingCall(hudIOController);
		} else if (sessionType == HudIOSessionType.INCOMING_CALL) {
			ioSession = new HudIOSessionIncomingCall(hudIOController);
		} else if (sessionType == HudIOSessionType.RECORDER) {
			ioSession = new HudIOSessionRecorder(hudIOController);
		} else if (sessionType == HudIOSessionType.EXIT) {
			ioSession = new HudIOSessionExit(hudIOController);
		}else if(sessionType == HudIOSessionType.COMMAND){
			ioSession = new HudIOSessionCommand(hudIOController);
		}else if(sessionType == HudIOSessionType.HANGUP){
			ioSession = new HudIOSessionHangUp(hudIOController);
		}else if(sessionType == HudIOSessionType.DISPATCHER){
			ioSession = new HudIOSessionDispatcher(hudIOController);
		}

		if (ioSession != null) {
			ioSession.setSessionCallback(sessionCallback);
		}

		return ioSession;
	}

	@Override
	public void endCurrentSession(HudIOSessionType sessionType) {
		if(!mHudIOSessionStack.isEmpty()) {
			HudIOSession ioSession = mHudIOSessionStack.peek();
			if (ioSession.getIOSessionType() == sessionType) {
				endHudIOSession(sessionType, false);
			}
		}
	}

	@Override
	public void continueCurrentSession(HudIOSessionType sessionType) {
		if(!mHudIOSessionStack.isEmpty()) {
			HudIOSession ioSession = mHudIOSessionStack.peek();
			if (ioSession.getIOSessionType() == sessionType) {
				ioSession.continueSession();
			}
		}
	}

	@Override
	public HudIOSessionType getCurrentSessionType() {
		if(!mHudIOSessionStack.isEmpty()){
			HudIOSession ioSession = mHudIOSessionStack.peek();
			if (ioSession != null)
				return ioSession.getIOSessionType();
		}
		return null;
	}



	BroadcastReceiver mPhoneCallBroadcastReceiver =new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action =intent.getAction();
			if(action.equalsIgnoreCase(HudIOConstants.ACTION_BTCALL_INCOMING)){
				String actionType=intent.getStringExtra(HudIOConstants.ACTION_TYPE);
				HaloLogger.logE("speech_info","BT_ActionType:"+actionType);
				if(actionType.equalsIgnoreCase("CALL_STATE_RINGING")){
					String name=intent.getStringExtra("name");
					String phoneNumber=intent.getStringExtra("phoneNumber");
					if(name==null||name.equalsIgnoreCase("")){
						name=phoneNumber;
					}
					HudIOSession iosession = beginHudIOSession(HudIOSessionType.INCOMING_CALL);
					HaloLogger.postI(EndpointsConstants.BTCALL_TAG,name+"来电");
					if(iosession != null && iosession.getIOSessionType() == HudIOSessionType.INCOMING_CALL){
						iosession.onProtocol(name);
					}
				}

			}else if(action.equalsIgnoreCase(HudIOConstants.ACTION_BTCALL_OVER)){
				HudIOSession iosession = beginHudIOSession(HudIOSessionType.HANGUP);
				HaloLogger.postI(EndpointsConstants.BTCALL_TAG, "HangUp BTCall!");
				if(iosession != null && iosession.getIOSessionType() == HudIOSessionType.HANGUP){
					iosession.onProtocol(action);
				}
			}else if(action.equals(HudIOConstants.ACTION_CONTACTS_DOWNLOAD_OVER)){//蓝牙电话本加载完成，语音引擎编译
				HaloLogger.logE("speech_info", "CONTACTS_DOWNLOAD_OVER !");
				if(mHudSpeechManager!=null){
					mHudSpeechManager.startCompileManual();
				}
			}else if (action.equals(HudIOConstants.ACTION_ON_OUT_CALLING)){
				String phoneNumber = intent.getStringExtra("phoneNumber");
				String name = intent.getStringExtra("name");
				if(name == null) name = "未知";
				HudIOSessionType hudIOSessionType= getCurrentSessionType();
				if(hudIOSessionType != HudIOSessionType.OUTGOING_CALL){
					HudIOSessionOutgoingCall iosession = (HudIOSessionOutgoingCall)beginHudIOSession(HudIOSessionType.OUTGOING_CALL);
					if(iosession != null && iosession.getIOSessionType() == HudIOSessionType.OUTGOING_CALL){
						iosession.onOutCalling(name,phoneNumber,false);
					}
					HaloLogger.postI(EndpointsConstants.BTCALL_TAG, "手机拨号:"+name);
				}

			}else if(action.equals(HudIOConstants.GESTURE_CONNECT_DEBUG)){
				boolean isOpen = intent.getBooleanExtra(HudIOConstants.GESTURE_CONNECT_DEBUG,false);
				if(isOpen){
					resumeHudInputer(HudInputerType.GESTURE_INPUT);
					mHudOutputerSpeech.output("正在打开手势", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,false);
				}else {
					pauseHudInputer(HudInputerType.GESTURE_INPUT);
					mHudOutputerSpeech.output("正在关闭手势", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,false);
				}
			}
			
		}
	};
		
}
