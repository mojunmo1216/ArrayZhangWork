package com.unisound.framework;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.pub.SpeechListener;
import com.unisound.framework.pub.SpeechOperator;
import com.unisound.framework.utils.LogUtils;

public class MainActivity extends Activity {
	private static final String TAG = "FrameWork_MainActivity";
	
	private Context mContext;
	private SpeechOperator speechOperator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		this.mContext = this.getApplicationContext();
		
		initSpeech();
		initView();
	}

	private void initSpeech() {
		speechOperator = SpeechOperator.getInstance();
		speechOperator.setNormalWakeupWord(UserPreference.DEFAULT_WAKEUP_WORD);
		speechOperator.setListener(new SpeechListener() {
			@Override
			public void onWakeupPrompt() {
				LogUtils.d(TAG, "onWakeupPrompt");
				speechOperator.playTTS("请说", true);
			}
			
			@Override
			public void onWakeupCommand(String wakeupCmd) {
				LogUtils.d(TAG, "onWakeupCommand : " + wakeupCmd);
			}
			
			@Override
			public void onVolumeChanged(int volume) {
			}
			
			@Override
			public void onTTSEnd() {
				LogUtils.d(TAG, "onTTSEnd");
			}
			
			@Override
			public void onTTSBegin() {
				LogUtils.d(TAG, "onTTSBegin");
			}

			@Override
			public void onRecognizeStart() {
				LogUtils.d(TAG, "onRecognizeStart");
			}
			
			@Override
			public void onProtocol(String protocol) {
				LogUtils.d(TAG, "onProtocol" + protocol);
			}
			
			@Override
			public void onError(String errorReason) {
				LogUtils.d(TAG, "onError : " + errorReason);
			}

			@Override
			public void onTTSModelMissing() {
				LogUtils.e(TAG, "onTTSModelMissing");
			}

			@Override
			public void onInitEngineDone() {
				LogUtils.d(TAG, "onInitEngineDone");
			}

			@Override
			public void onWakeupWordsReady() {
				LogUtils.d(TAG, "onWakeupWordsReady");
			}

			@Override
			public void onWakeup() {
				LogUtils.d(TAG, "onWakeup");
			}

			@Override
			public void onEngineCompileStart() {
				LogUtils.d(TAG, "onEngineCompileStart");
			}

			@Override
			public void onEngineCompileFinished() {
				LogUtils.d(TAG, "onEngineCompileFinished");
			}
		});
		speechOperator.init(mContext);
	}

	private void initView() {
		Button bt_tts_recognize = (Button) findViewById(R.id.bt_tts_recognize);
		Button bt_tts_wakeup = (Button) findViewById(R.id.bt_tts_wakeup);
		Button bt_set_wakeup_word = (Button) findViewById(R.id.bt_set_wakeup_word);
		Button bt_set_normal_wakeup = (Button) findViewById(R.id.bt_set_normal_wakeup);
		Button bt_set_oneshot_wakeup = (Button) findViewById(R.id.bt_set_oneshot_wakeup);
		Button bt_start_compile_manual = (Button) findViewById(R.id.bt_start_compile_manual);
		Button bt_cancel_speech = (Button) findViewById(R.id.bt_cancel_speech);
		Button bt_set_current_city = (Button) findViewById(R.id.bt_set_current_city);
		
		bt_tts_recognize.setOnClickListener(mClickListener);
		bt_tts_wakeup.setOnClickListener(mClickListener);
		bt_set_wakeup_word.setOnClickListener(mClickListener);
		bt_set_normal_wakeup.setOnClickListener(mClickListener);
		bt_set_oneshot_wakeup.setOnClickListener(mClickListener);
		bt_start_compile_manual.setOnClickListener(mClickListener);
		bt_cancel_speech.setOnClickListener(mClickListener);
		bt_set_current_city.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.bt_tts_recognize:
					speechOperator.playTTS("您想听谁的歌", true);
					break;
				case R.id.bt_tts_wakeup:
					speechOperator.playTTS("北京云知声信息技术有限公司，简称云知声是一家专注于智能语音识别及语言处理技术的移动互联网公司，其拥有完全自主知识产权的世界顶尖的智能语音识别技术。其主要产品包括语音云平台、微信语音输入插件、云知声输入法等。", false);
					break;
				case R.id.bt_set_wakeup_word:
					LogUtils.d(TAG, "bt_set_wakeup_word onclick");
					List<String> wakeupWords = new ArrayList<String>();
					wakeupWords.add("第一个");
					wakeupWords.add("第二个");
					wakeupWords.add("第三个");
					wakeupWords.add("第四个");
					wakeupWords.add("第五个");
					speechOperator.setWakupWords(wakeupWords);
					break;
				case R.id.bt_set_normal_wakeup:
					if(speechOperator.isOneShotMode()){
						speechOperator.setWakeupMode(UserPreference.NORMAL_WAKEUP_MODE);
					} else{
						Toast.makeText(mContext, "already normal mode !", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.bt_set_oneshot_wakeup:
					if(!speechOperator.isOneShotMode()){
						speechOperator.setWakeupMode(UserPreference.ONESHOT_WAKEUP_MODE);
					} else {
						Toast.makeText(mContext, "already oneshot mode !", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.bt_start_compile_manual:
					LogUtils.d(TAG, "bt_start_compile_manual onclick");
					speechOperator.startCompileManually();
					break;
				case R.id.bt_cancel_speech:
					LogUtils.d(TAG, "bt_cancel_speech onclick");
					speechOperator.cancelRecognizeManually();
					break;
				case R.id.bt_set_current_city:
					LogUtils.d(TAG, "bt_set_current_city onclick");
					speechOperator.setCurrentCity("北京");
					break;
				default:
					break;
			}
		}
	};
}
