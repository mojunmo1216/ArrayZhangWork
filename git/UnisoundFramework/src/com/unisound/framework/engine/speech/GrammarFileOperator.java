package com.unisound.framework.engine.speech;

import java.io.File;

import android.os.AsyncTask;

import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.utils.LogUtils;

public class GrammarFileOperator {
	private static final String TAG = GrammarFileOperator.class.getSimpleName();
	
	private GrammarFileListener grammarFileListener;
	
	private static final int GRAMMAR_FILE_EXIST = 2000;
	private static final int GRAMMAR_FILE_NOT_EXIST = 2100;
	
	public GrammarFileOperator(){}
	
	public void setListener(GrammarFileListener grammarFileListener){
		this.grammarFileListener = grammarFileListener;
	}
	
	public void copyFile(){
		new GrammarFileCopyTask().execute();
	}
	
	public boolean isGrammarFileExists() {
		File grammarFile = new File(UserPreference.grammarPath);
		if(grammarFile.exists()){
			return true;
		}else {
			LogUtils.e(TAG, "grammar file not exist !!");
			return false;
		}
	}
	
	private class GrammarFileCopyTask extends AsyncTask<Void, Void, Integer>{
		@Override
		protected Integer doInBackground(Void... params) {
			if(isGrammarFileExists()){
				return GRAMMAR_FILE_EXIST;
			}else {
				return GRAMMAR_FILE_NOT_EXIST;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			LogUtils.d(TAG, "call onPostExecute, code - " + result);
			switch(result){
				case GRAMMAR_FILE_EXIST:
					grammarFileListener.onGrammarFileExist();
					break;
				case GRAMMAR_FILE_NOT_EXIST:
					grammarFileListener.onGrammarFileNotExist();
					break;
				default:
					break;
			}
		}
	}
}
