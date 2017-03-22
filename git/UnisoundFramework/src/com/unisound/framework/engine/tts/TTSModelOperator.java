package com.unisound.framework.engine.tts;

import java.io.File;

import android.os.AsyncTask;
import android.util.Log;

import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.utils.LogUtils;

public class TTSModelOperator {
	private static final String TAG = TTSModelOperator.class.getSimpleName();
	
	private TTSModeListener ttsModeListener;
	
	private static final int FILE_EXIST = 1000;
	private static final int FILE_NOT_EXIST = 1100;
	
	public TTSModelOperator() {}

	public void copyMode(){
		new TTSModelCopyTask().execute();
	}
	
	public boolean isTTSModelExists() {
		File frontEndModel = new File(UserPreference.mFrontendModel);
		File backEndModel = new File(UserPreference.mBackendModel);

		if(frontEndModel.exists() && backEndModel.exists()){
			return true;
		}else {
			return false;
		}
	}
	
	private class TTSModelCopyTask extends AsyncTask<Void, Void, Integer>{
		@Override
		protected Integer doInBackground(Void... params) {
			if (isTTSModelExists()){
				return FILE_EXIST;
			}else {
				Log.e(TAG, "tts model is missing, init fail");
				return FILE_NOT_EXIST;
			}
		}
	
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			LogUtils.d(TAG, "call onPostExecute, code - " + result);
			switch(result){
				case FILE_EXIST:
					ttsModeListener.onFileExist();
					break;
				case FILE_NOT_EXIST:
					ttsModeListener.onFileNotExist();
					break;
				default:
					break;
			}
		}
	}
	
	public void setListener(TTSModeListener ttsModeListener){
		this.ttsModeListener = ttsModeListener;
	}
}
