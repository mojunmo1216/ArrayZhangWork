package com.unisound.framework.preference;

import android.media.AudioManager;

import com.unisound.framework.utils.FileHelper;

public class UserPreference {
	public static boolean IS_DEBUG = true;
	public static final boolean SUPER_DEBUG = true;
	public static final boolean IS_SAVE_RECORDING_FILE = false;
	
	public static final boolean IS_AEC_MODE = true;
	public static final boolean IS_SAVE_AEC_RECODE = true;
	// 0:left  1:right
    public static int AEC_MIC_CHANNEL = 0;
	public static final boolean IS_VAD_DETE_MUSIC = true;
	
	public static String appKey = "oeiu7uznqgmrzsh6gen64qakp2mnadoergn3zzi5";
	public static String appSecret = "70aa3de3491bc41e1ced9846401fee96";

	public static final String DEFAULT_WAKEUP_WORD = "你好魔方";
	public static final String WAKEUP_CODE = "ON_WAKEUP";
	
	public static double wakeupScore = -3.1f;
	public static double offlineScore = -6.6;
	public static int beepSize = 100;
	public static int vadTimeOutTime = 10000;
	public static int asrTimeout = 10000;
	
	public static final String SAVE_RECOGNIZE_FILE_PATH = FileHelper.getSDCardPath() + "/Halo/record/recognize/";
	public static final String SAVE_WAKEUP_FILE_PATH = FileHelper.getSDCardPath() + "/Halo/record/wakeup/";
	public static final String SAVE_TTS_FILE_PATH = FileHelper.getSDCardPath() + "/Halo/record/tts/";
	public static String beepPath = FileHelper.getSDCardPath() + "/Halo/beep/start_tone.ogg";
	
	public static String grammarPath = FileHelper.getSDCardPath() + "/Halo/grammar/unidrive_main_clg.dat";

	public static String mFrontendModel= FileHelper.getSDCardPath() + "/Halo/tts/frontend_model_offline";
	public static String mBackendModel = FileHelper.getSDCardPath() + "/Halo/tts/backend_female_lpc2wav_22k_pf";
	
	public static final String NORMAL_WAKEUP_MODE = "normal";
	public static final String ONESHOT_WAKEUP_MODE = "oneshot";
	
	public static final String PHONE_DOMAIN = "cn.yunzhisheng.call";
	public static final String SETTING_DOMAIN = "cn.yunzhisheng.setting";
	public static final String ERROR_DOMAIN = "cn.yunzhisheng.error";
	
	public static final int VAD_FRONT_TIMEOUT = 0;
	public static final int VAD_BACK_TIMEOUT = 1;
	public static final int VAD_ONESHOT_TIMEOUT = 2;
	
	public static boolean VAD_LONG_FRONT_TIMEOUT = false;
	
	public static final int STREAM_TYPE = AudioManager.STREAM_ALARM;
}
