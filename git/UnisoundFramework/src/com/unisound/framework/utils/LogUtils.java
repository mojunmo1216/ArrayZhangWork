package com.unisound.framework.utils;

import android.util.Log;

import com.unisound.framework.preference.UserPreference;


public class LogUtils {
	public static void d(String TAG, String content){
		if(UserPreference.IS_DEBUG){
			Log.d(TAG, content);
		}
	}
	
	public static void e(String TAG, String content){
		if(UserPreference.IS_DEBUG){
			Log.e(TAG, content);
		}
	}
}
