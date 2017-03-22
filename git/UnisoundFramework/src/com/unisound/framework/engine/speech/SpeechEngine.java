package com.unisound.framework.engine.speech;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.pub.SpeechListener;
import com.unisound.framework.utils.FileHelper;
import com.unisound.framework.utils.LogUtils;

public class SpeechEngine {
	private final String TAG = SpeechEngine.class.getSimpleName();

	private Context mContext;
	private SpeechUnderstander speechUnderstander;
	private SpeechUnderstanderListener mSpeechListener;
	private SpeechListener speechListener;

	private String recognizeTag = "unidrive_main";
	private String oneShotTag = "oneshot:unidrive_main";
	private String normalWakeupTag = "wakeup";

	private boolean isOneShotMode = true;

	public SpeechEngine(Context mContext) {
		this.mContext = mContext;
	}

	public void initGrammarFile() {
		GrammarFileOperator grammarFileOperator = new GrammarFileOperator();
		grammarFileOperator.setListener(new GrammarFileListener() {
			@Override
			public void onGrammarFileExist() {
				initSpeech();
			}

			@Override
			public void onGrammarFileNotExist() {
				// TODO this place should return error message (this step can be
				// done when write off line order part)
				initSpeech();
			}
		});
		grammarFileOperator.copyFile();
	}

	private void initSpeech() {
		LogUtils.d(TAG, "speech init");
		speechUnderstander = new SpeechUnderstander(mContext,
				UserPreference.appKey, UserPreference.appSecret);
		speechUnderstander.setOption(SpeechConstants.ASR_SERVICE_MODE,
				SpeechConstants.ASR_SERVICE_MODE_MIX);
		speechUnderstander.setOption(SpeechConstants.ASR_DOMAIN, "poi");
		speechUnderstander.setOption(SpeechConstants.NLU_SCENARIO, "incar");
		speechUnderstander.setOption(SpeechConstants.ASR_OPT_RESULT_FILTER,
				false);
		speechUnderstander.setOption(SpeechConstants.ASR_NET_TIMEOUT, 15);
		speechUnderstander.setOption(
				SpeechConstants.WAKEUP_OPT_THRESHOLD_VALUE,
				UserPreference.wakeupScore);
		speechUnderstander.setOption(
				SpeechConstants.ASR_OPT_ONESHOT_VADBACKSIL_TIME, 1000);
		speechUnderstander.setOption(SpeechConstants.ASR_VAD_TIMEOUT_BACKSIL,
				1000);

		if (UserPreference.IS_VAD_DETE_MUSIC) {
			speechUnderstander.setOption(
					SpeechConstants.ASR_VAD_DETEMUSIC_ENABLE, true);
			speechUnderstander.setOption(SpeechConstants.ASR_VAD_MUSICTH, 0.5f);
		}

		if (UserPreference.SUPER_DEBUG) {
			speechUnderstander.setOption(SpeechConstants.ASR_OPT_PRINT_LOG,
					true);
			speechUnderstander.setOption(
					SpeechConstants.ASR_OPT_PRINT_ENGINE_LOG, true);
			speechUnderstander.setOption(
					SpeechConstants.ASR_OPT_PRINT_TIME_LOG, false);
		}

		// aec mode
		if (UserPreference.IS_AEC_MODE) {
			AudioSourceImplPro audioSourceImplPro = new AudioSourceImplPro();
			audioSourceImplPro.setDebug(UserPreference.IS_SAVE_AEC_RECODE);
			speechUnderstander.setAudioSource(audioSourceImplPro);
		} else {
			speechUnderstander.setAudioSource(new AudioSourceImpl(
					speechListener));
		}

		speechUnderstander.setListener(getSpeechListner());
		speechUnderstander.init("");
		LogUtils.d(
				TAG,
				"speechUnderstander version : "
						+ speechUnderstander.getVersion());
	}

	public void setSpeechListener(SpeechUnderstanderListener mSpeechListener,
			SpeechListener speechListener) {
		this.mSpeechListener = mSpeechListener;
		this.speechListener = speechListener;
	}

	private SpeechUnderstanderListener getSpeechListner() {
		if (mSpeechListener != null) {
			return mSpeechListener;
		} else {
			throw new RuntimeException("mSpeechListener is null");
		}
	}

	public void startWakeup(boolean isOneShot) {
		LogUtils.d(TAG, "start " + (isOneShot ? "oneshot" : "normal")
				+ " mode Wakeup");

		if (UserPreference.IS_SAVE_RECORDING_FILE) {
			speechUnderstander.setOption(SpeechConstants.ASR_OPT_SAVE_RECORDING_DATA,
					UserPreference.SAVE_WAKEUP_FILE_PATH
					+ FileHelper.generateFileName("wakeup", "pcm"));
		}

		if (speechUnderstander != null) {
			if (isOneShot) {
				speechUnderstander.cancel();
				speechUnderstander.stop();
				speechUnderstander.start(oneShotTag);
				isOneShotMode = true;
			} else {
				speechUnderstander.cancel();
				speechUnderstander.stop();
				speechUnderstander.start(normalWakeupTag);
				isOneShotMode = false;
			}
		} else {
			LogUtils.e(TAG, "startWakeup report : speechUnderstander is null");
		}
	}

	public void stopWakeup() {
		LogUtils.d(TAG, "stopWakeup");
		if (speechUnderstander != null) {
			speechUnderstander.cancel();
		}
	}

	public void setWakeupWords(List<String> wakeupWords) {
		LogUtils.d(TAG, "setWakeupWords : " + wakeupWords.toString());
		if (speechUnderstander != null) {
			speechUnderstander.stop();
			speechUnderstander.setWakeupWord(wakeupWords);
		}
	}

	public void startRecognize() {
		LogUtils.d(TAG, "startRecognize");
		if (speechUnderstander != null) {
			speechUnderstander.cancel();
			speechUnderstander.setOption(
					SpeechConstants.ASR_OPT_FRONT_RESET_CACHE_BYTE_TIME,
					UserPreference.beepSize);

			if (UserPreference.IS_SAVE_RECORDING_FILE) {
				speechUnderstander.setOption(SpeechConstants.ASR_OPT_SAVE_RECORDING_DATA,
						UserPreference.SAVE_RECOGNIZE_FILE_PATH
						+ FileHelper.generateFileName("recognize", "pcm"));
			}

			speechUnderstander.start(recognizeTag);
		}
	}

	public void stopRecognize() {
		LogUtils.d(TAG, "stopRecognize");
		if (speechUnderstander != null) {
			speechUnderstander.stop();
		}
	}

	public int loadCompliedJsgf(String tag, String jsgfDatPath) {
		if (speechUnderstander != null) {
			int isLoadSuccess = speechUnderstander.loadCompiledJsgf(tag,
					jsgfDatPath);
			return isLoadSuccess;
		}
		return -1;
	}

	public int insertVocab(List<String> cmdAppNamesList, String string) {
		if (speechUnderstander != null) {
			return speechUnderstander.insertVocab(cmdAppNamesList, string);
		}
		return -1;
	}

	public int insertVocab(Map<String, List<String>> wakeupWordsMap,
			String main_tag) {
		if (speechUnderstander != null) {
			return speechUnderstander.insertVocab(wakeupWordsMap, main_tag);
		}
		return -1;
	}

	public boolean isOneShotMode() {
		return isOneShotMode;
	}

	public void setVadFrontTimeout(int vadFrontTimeout) {
		if (speechUnderstander != null) {
			speechUnderstander.setOption(SpeechConstants.VAD_FRONT_TIMEOUT,
					vadFrontTimeout);
		}
	}

	public void setWakeupMode(String wakeupMode) {
		startWakeup(wakeupMode.equals(UserPreference.ONESHOT_WAKEUP_MODE));
	}

	public void setCurrentCity(String cityName) {
		if (speechUnderstander != null) {
			speechUnderstander
					.setOption(SpeechConstants.GENERAL_CITY, cityName);
		}
	}

	public String getOption(int status, String defaultString) {
		if (speechUnderstander != null) {
			return (String) speechUnderstander
					.getOption(SpeechConstants.ASR_OPT_TIMEOUT_STATUS);
		}
		LogUtils.e(TAG, "getOption error, retrun defaultString");
		return defaultString;
	}

	public int getVolume() {
		if (speechUnderstander != null) {
			return (Integer) speechUnderstander
					.getOption(SpeechConstants.GENERAL_UPDATE_VOLUME);
		}
		return -1;
	}

	public void cancel() {
		LogUtils.d(TAG, "cancel");
		if (speechUnderstander != null) {
			speechUnderstander.cancel();
		}
	}

	public void destory() {
		LogUtils.d(TAG, "destory");
		if (speechUnderstander != null) {
			speechUnderstander.release(SpeechConstants.ASR_RELEASE_ENGINE, "");
		}
	}
}
