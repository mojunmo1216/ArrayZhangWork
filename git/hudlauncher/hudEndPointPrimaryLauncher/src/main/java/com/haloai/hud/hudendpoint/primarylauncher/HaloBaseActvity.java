package com.haloai.hud.hudendpoint.primarylauncher;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.haloai.hud.hudendpoint.protocol.HudPreferenceSetController;
import com.haloai.hud.hudendpoint.protocol.IHudPreferenceSetController;
import com.haloai.hud.hudendpoint.upgrade.HudUpgradeManager;
import com.haloai.hud.hudendpoint.utils.BrightnessTools;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.HudIOControllerFactory;
import com.haloai.huduniversalio.IHudIOController;
import com.haloai.huduniversalio.IHudIOControllerListener;
import com.haloai.huduniversalio.inputer.IInputerGestureStatusListener;
import com.haloai.huduniversalio.inputer.IInputerPhoneStatusListener;
import com.haloai.huduniversalio.inputer.IInputerRemoterStatusListener;
import com.haloai.huduniversalio.inputer.IInputerSpeechStatusListener;
import com.haloai.huduniversalio.inputer.IInputerStatusListener;
import com.haloai.huduniversalio.outputer.IHudOutputer;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionCommandCallback;
import com.haloai.huduniversalio.session.IHudIOSessionDispatcherCallback;
import com.haloai.huduniversalio.session.IHudIOSessionExitCallback;
import com.haloai.huduniversalio.session.IHudIOSessionHangUpCallback;
import com.haloai.huduniversalio.session.IHudIOSessionIncomingCallCallback;
import com.haloai.huduniversalio.session.IHudIOSessionMusicCallback;
import com.haloai.huduniversalio.session.IHudIOSessionNaviCallback;
import com.haloai.huduniversalio.session.IHudIOSessionOutgoingCallCallback;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback;
import com.haloai.huduniversalio.session.IHudIOSessionWechatCallback;

import java.util.List;

public abstract class HaloBaseActvity extends FragmentActivity implements IInputerStatusListener,IInputerGestureStatusListener,
		IInputerSpeechStatusListener, IHudIOControllerListener,IInputerRemoterStatusListener,IInputerPhoneStatusListener,
		IHudIOSessionNaviCallback, IHudIOSessionCommandCallback,IHudIOSessionDispatcherCallback,
		IHudIOSessionIncomingCallCallback, IHudIOSessionMusicCallback,
		IHudIOSessionOutgoingCallCallback, IHudIOSessionRecorderCallback,
		IHudIOSessionWechatCallback, IHudIOSessionExitCallback, IHudIOSessionHangUpCallback{

	// private HudCameraStub hudCameraStub;
	private Boolean mScreenBrightnessFlg = true;
	protected IHudIOController mHudIOController;
	protected IHudOutputer mHudSpeechOutputer;
	protected IHudPreferenceSetController mHudPreferenceSetController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHudIOController = HudIOControllerFactory.getHudIOController(this, this);
		mHudSpeechOutputer = mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT);
		mHudIOController.setInputerStatusListener(HudInputerType.SPEECH_INPUT, this);
		mHudIOController.setInputerStatusListener(HudInputerType.REMOTER_INPUT,this);
		mHudIOController.setInputerStatusListener(HudInputerType.GESTURE_INPUT,this);
		mHudIOController.setInputerStatusListener(HudInputerType.PHONE_INPUT,this);
		mHudPreferenceSetController = HudPreferenceSetController.getInstance();
		HudUpgradeManager.getInstance().init(this);//初始化OTA升级服务

		// GlobalVariants.interactionSessionManager.setSessionOutputCallback(this);
		// GlobalVariants.speechRecognitionManger.setSpeechEngineOutputCallback(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// InteractionSession currentSession =
		// GlobalVariants.interactionSessionManager
		// .getCurrentInteractionSession();
		// if (currentSession != null) {
		// if (currentSession.onRemoteKey(keyCode, event)) {
		// Log.v("hanyu", "currentSession handle onKeyUp ");
		// return true;
		// }
		// }
		// Bluetooth remote controller key code. 以挂钩朝上为竖直方向
		switch (keyCode) {
		// iOS丝印
		case KeyEvent.KEYCODE_BUTTON_X: // in came mode.
		case KeyEvent.KEYCODE_VOLUME_UP: // in key mode.

			return onRemoterKeyX();
			// X丝印
		case KeyEvent.KEYCODE_BUTTON_A: // in came mode.
		case KeyEvent.KEYCODE_VOLUME_DOWN: // in key mode.
			return onRemoterKeyA();
			// a丝印
		case KeyEvent.KEYCODE_BUTTON_B: // in came mode.
		case KeyEvent.KEYCODE_ENTER: // in key mode.
			return onRemoterKeyB();
			// 三角丝印
		case KeyEvent.KEYCODE_BUTTON_Y: // in came mode.
		case KeyEvent.KEYCODE_BACK: // in key mode.
			return onRemoterKeyY();
			// Pad向下
		case KeyEvent.KEYCODE_DPAD_RIGHT: // in came mode.
		case KeyEvent.KEYCODE_MEDIA_REWIND: // in key mode.
			return onRemoterKeyDown();
			// Pad向上
		case KeyEvent.KEYCODE_DPAD_LEFT: // in came mode.
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: // in key mode.
			return onRemoterKeyUp();
			// Pad向右
		case KeyEvent.KEYCODE_DPAD_UP: // in came mode.
		case KeyEvent.KEYCODE_MEDIA_NEXT: // in key mode.
			return onRemoterKeyRight();
			// Pad向左
		case KeyEvent.KEYCODE_DPAD_DOWN: // in came mode.
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS: // in key mode.
			return onRemoterKeyLeft();
		}

		return false;
	}

	protected boolean onRemoterKeyUp() {
		return false;
	}

	protected boolean onRemoterKeyDown() {
		return false;
	}

	protected boolean onRemoterKeyLeft() {
		return false;
	}

	protected boolean onRemoterKeyRight() {
		return false;
	}

	protected boolean onRemoterKeyX() {
		BrightnessTools.stopAutoBrightness(this);
		int mScreenBrightness = BrightnessTools.getScreenBrightness(this);
		if (mScreenBrightnessFlg) {
			mScreenBrightness = mScreenBrightness + 50;
			if (mScreenBrightness >= 255) {
				mScreenBrightness = 255;
				mScreenBrightnessFlg = false;
			}
			BrightnessTools.setBrightness(this, mScreenBrightness);

		} else {
			mScreenBrightness = mScreenBrightness - 50;
			if (mScreenBrightness <= 55) {
				mScreenBrightness = 55;
				mScreenBrightnessFlg = true;
			}
			BrightnessTools.setBrightness(this, mScreenBrightness);

		}
		return false;

	}

	protected boolean onRemoterKeyY() {
		return false;
	}

	protected boolean onRemoterKeyA() {
		return false;
	}

	protected boolean onRemoterKeyB() { // a丝印
		// onSpeechEngineRecogBegin("请说话");
		// GlobalVariants.speechRecognitionManger.stopWakeupAndStartRecognitionEngine();

		return false;
	}

	// @Override
	// public void onSessionSettingBrightnessChangeRequested(
	// BrightnessChangeAction action) {
	// Log.e("hanyu", "outputLightSetupResult base : " + action.toString());
	// BrightnessTools.stopAutoBrightness(this);
	// if (action == BrightnessChangeAction.MAX) {
	// BrightnessTools.setBrightness(this, 255);
	//
	// } else if (action == BrightnessChangeAction.MIN) {
	// BrightnessTools.setBrightness(this, 5);
	// } else if (action == BrightnessChangeAction.UP) {
	// int brightness = BrightnessTools.getScreenBrightness(this);
	// brightness += 50;
	//
	// if (brightness > 255) {
	// brightness = 255;
	// // this.hudFunctionsFragment.speechRecogPromptTitle("已经是最大亮度");
	// GlobalVariants.speechRecognitionManger
	// .playTTS("已经是最大亮度", false);
	// }
	// Log.e("hanyu", "brightness: " + brightness);
	// BrightnessTools.setBrightness(this, brightness);
	// } else if (action == BrightnessChangeAction.DOWN) {
	// int brightness = BrightnessTools.getScreenBrightness(this);
	// brightness -= 50;
	// if (brightness < 5) {
	// brightness = 5;
	// // this.hudFunctionsFragment.speechRecogPromptTitle("已经是最小亮度");
	// GlobalVariants.speechRecognitionManger
	// .playTTS("已经是最小亮度", false);
	// }
	// Log.e("hanyu", "brightness: " + brightness);
	// BrightnessTools.setBrightness(this, brightness);
	// }
	//
	// }
//	@Override
//	public void naviSessionSearchDestination(String city, String name) {
//
//	}

	@Override
	public void naviSessionPoiItemNextPage(List<String> currentPOIList , int currentPageIndex) {

	}

	@Override
	public void naviSessionPoiItemPrevPage(List<String> currentPOIList ,int currentPageIndex) {

	}

	@Override
	public boolean onSessionInvalidInput(HudInputerType inputType,
			String input, String suggestion) {
		return false;
	}


	public void onSessionIncomingCallAnswered() {

	}

	@Override
	public void onSessionIncomingCallRejected() {

	}

	@Override
	public void exitSessionConfirm() {

	}


	@Override
	public IHudIOSessionCallback onSessionBegin(HudIOSessionType sessionType) {
		return this;
	}

	@Override
	public void onSessionEnd(HudIOSessionType sessionType, boolean isCancelled) {

	}

	@Override
	public void onInputerReady(HudInputerType inputerType) {

	}

	@Override
	public void onInputerOos(HudInputerType inputerType, int oosReason) {

	}

}