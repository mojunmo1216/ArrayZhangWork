package com.unisound.framework.pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstanderListener;
import com.unisound.data.source.bean.Contact;
import com.unisound.framework.data.contact.ContactsListener;
import com.unisound.framework.data.contact.ContactsOperator;
import com.unisound.framework.engine.speech.SpeechEngine;
import com.unisound.framework.engine.tts.TTSEngine;
import com.unisound.framework.engine.tts.TTSListener;
import com.unisound.framework.parse.Offline2OnlineParser;
import com.unisound.framework.parse.OfflineProtocalParse;
import com.unisound.framework.parse.OfflineTagParser;
import com.unisound.framework.parse.OnlineProtocalParser;
import com.unisound.framework.parse.WakeupWordParser;
import com.unisound.framework.preference.ErrorMessage;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.session.SessionFilter;
import com.unisound.framework.utils.BeepPlayer;
import com.unisound.framework.utils.LogUtils;
import com.unisound.framework.utils.TimeoutTask;
import com.unisound.framework.utils.VoiceOperator;

public class SpeechOperator{
	private static final String TAG = SpeechOperator.class.getSimpleName();
	
	private Context mContext;
	private SpeechEngine speechEngine;
	private TTSEngine ttsEngine;
	private static SpeechOperator speechOperator;
	private SpeechListener speechListener;
	
	private boolean isFirstCompile = true;
	private boolean isCommandWakeupWords = false;
	private boolean isWakeupSuccess = false;

	private BeepPlayer beepPlayer;
	private TimeoutTask vadTimeoutTask;
	private String defaultWakeupWord;
	
	private String main_tag = "unidrive_main";
	private String contact_tag = "Domain_phone_contact_slot";
	
	private SpeechOperator(){}
	
	public static SpeechOperator getInstance(){
		if(speechOperator == null){
			synchronized (SpeechOperator.class) {
				if(speechOperator == null){
					speechOperator = new SpeechOperator();
				}
			}
		}
		return speechOperator;
	}
	
	public void init(Context mContext) {
		this.mContext = mContext;
		initBeepPlayer();
		initSpeechSystem();
	}

	private void initBeepPlayer() {
		beepPlayer = new BeepPlayer(mContext, UserPreference.beepPath);
		beepPlayer.setVolume(1.0f);
	}

	private void initSpeechSystem() {
		initSessionFilter();
		initSpeechEngine();
		initVadTimeoutTask();
		initOnProtocalTimeoutTask();
		initTTSEngine();
		initVoiceOperator();
	}

	private void initSessionFilter() {
		sessionFilter = new SessionFilter();
	}

	private void initVoiceOperator() {
		voiceOperator = new VoiceOperator(mContext);
	}

	private void initOnProtocalTimeoutTask() {
		onProtocalTask = new TimeoutTask("onProtocalTimeout", new Runnable() {
			@Override
			public void run() {
				LogUtils.d(TAG, "asr task timeout , auto invoke on Error timeout");
				callbackError(ErrorMessage.ASR_TIMEOUT_ERROR);
			}
		}, UserPreference.asrTimeout);
	}

	private void initDataSource() {
		ContactsOperator.getInstance(mContext, new ContactsListener() {
			@Override
			public void onSearchEnd(List<Contact> contacts) {
				contactsList = contacts;
				sessionFilter.setContactList(contactsList);
				updateUserData();
			}
			
			@Override
			public void onSearchBegin() {
				
			}
		}).startSearchContacts();
	}

	private void initVadTimeoutTask() {
		vadTimeoutTask = new TimeoutTask("VadTimeoutTask", new Runnable() {
			@Override
			public void run() {
				LogUtils.d(TAG, "vad task timeout , auto invoke TIMEOUT EVENT");
				myUnderstanderListener.onEvent(SpeechConstants.ASR_EVENT_VAD_TIMEOUT, UserPreference.VAD_BACK_TIMEOUT);
			}
		}, UserPreference.vadTimeOutTime);
	}

	private void initTTSEngine() {
		ttsEngine = new TTSEngine(mContext, speechEngine, beepPlayer, this);
		ttsEngine.setVadTimeoutTask(vadTimeoutTask);
		ttsEngine.setListener(new TTSListener() {
			@Override
			public void onTTSBegin() {
				speechListener.onTTSBegin();
			}
			@Override
			public void onTTSEnd() {
				speechListener.onTTSEnd();
			}
			@Override
			public void onTTSModelMissing() {
				speechListener.onTTSModelMissing();
			}
		});
		ttsEngine.initTTSModel();
	}

	private void initSpeechEngine() {
		speechEngine = new SpeechEngine(mContext);
		speechEngine.setSpeechListener(myUnderstanderListener, speechListener);
		speechEngine.initGrammarFile();
	}
	
	private SpeechUnderstanderListener myUnderstanderListener = new SpeechUnderstanderListener() {
		@Override
		public void onResult(int type, String jsonResult) {
			switch (type) {
				case SpeechConstants.ASR_RESULT_RECOGNITION:
					LogUtils.d(TAG, "ASR_RESULT_RECOGNITION : " + jsonResult.toString());
					
					// There is a situation: local result is faster than vad timeout when offline only
					if(vadTimeoutTask.isRunning()){
						vadTimeoutTask.stop("already receive result, vad should be stopped");
						speechListener.onRecognizeStart();
					}
					if(isWakeupSuccess == true){
						stopRecording("already receive result but vad not stop");
					}
					
					onProtocalTask.stop("get asr result so onprotocal task cancel");
					
					OnlineProtocalParser onlineParser = new OnlineProtocalParser(jsonResult.toString());
					OfflineProtocalParse offlineParser = new OfflineProtocalParse(jsonResult.toString());
					
					if(onlineParser.getOnlineProtocal() != null && offlineParser.getResult() != null){
						//if online protocal only contain wakeup word, not call back onProtocal
						if(isOnlyWakeupWord(onlineParser.getOnlineProtocal())){
							speechListener.onWakeupPrompt();
						}
						//normal wakeup
						else {
							// handle offline result
							if(offlineParser.getScore() > UserPreference.offlineScore){
								OfflineTagParser tagParser = new OfflineTagParser(offlineParser.getResult());
								Offline2OnlineParser offline2OnlineParser = new Offline2OnlineParser(mContext, tagParser.getOfflineMode());
								speechListener.onProtocol(offline2OnlineParser.getProtocal());
							}
							// handle online result
							else {
								//if online domain is error, return onError
								if(onlineParser.getDomain().equals(UserPreference.ERROR_DOMAIN)){
									callbackError(ErrorMessage.PROTOCAL_ERROR);
								}else{
									// handle specific protocal
									if(sessionFilter.isSpecific(onlineParser.getDomain())){
										LogUtils.d(TAG, "handle sepecific protocal : " + onlineParser.getOnlineProtocal());
										speechListener.onProtocol(sessionFilter.handleProtocal(onlineParser));
									}else {
										speechListener.onProtocol(onlineParser.getOnlineProtocal());
									}
								}
							}
						}
					}else {
						// handle offline result when online protocal is null
						if(offlineParser.getResult() != null && offlineParser.getScore() > UserPreference.offlineScore){
							OfflineTagParser tagParser = new OfflineTagParser(offlineParser.getResult());
							Offline2OnlineParser offline2OnlineParser = new Offline2OnlineParser(mContext, tagParser.getOfflineMode());
							speechListener.onProtocol(offline2OnlineParser.getProtocal());
						}
						// online protocal is null && offline result is false based on score || offline result is null
						else {
							speechListener.onError(ErrorMessage.NULL_PROTOCAL_ERROR);
						}
					}
					
					break;
				case SpeechConstants.ASR_RESULT_LOCAL:
					break;
				case SpeechConstants.ASR_RESULT_NET:
					break;
				case SpeechConstants.WAKEUP_RESULT:
					LogUtils.d(TAG, "WAKEUP_RESULT");
					stopCurrentTTS();
					judgeWakeupWord(jsonResult);
					break;
				default:
					break;
			}
		}
		
		private boolean isOnlyWakeupWord(String onlineProtocal) {
			JSONObject protocal = null;
			try {
				protocal = new JSONObject(onlineProtocal);
				String keyWord = protocal.getString("code");
				if(keyWord != null && keyWord.equals(UserPreference.WAKEUP_CODE)){
					return true;
				}
			} catch (JSONException e) {}
			return false;
		}

		private void judgeWakeupWord(String jsonResult) {
			WakeupWordParser parser = new WakeupWordParser(jsonResult);
			String commandWakeupWords = parser.getWakeupWord();
			//if wakeup word is default wakeup word. set wakeup status true
			//call back onWakeup() method
			if(commandWakeupWords.equals(defaultWakeupWord)){
				startRecording("wakeup success");
				//if oneshot mode , callback onWakeup method.
				//start vad timeout task
				if(speechEngine.isOneShotMode()){
					speechListener.onWakeup();
					vadTimeoutTask.execute("wakeup success in oneshot mode");
				}
				//if normal mode, callback onWakeupPromote method
				else{
					speechListener.onWakeupPrompt();
				}
			}
			//if wakeup word is command wakeup word. no need to set status.(no need volume change callback)
			//step1.call back onWakeupCommand(command) step2.cancel engine(avoid callback method)
			//step3.start wakeup (in order to start normal wakeup)
			else {
				speechListener.onWakeupCommand(commandWakeupWords);
				vadTimeoutTask.stop("command wakeup success, session done");
				speechEngine.cancel();
				speechEngine.startWakeup(speechEngine.isOneShotMode());
			}
		}

		@Override
		public void onEvent(int type, int time) {
			ArrayList<String> wakeupWords = getDefaultWakeupWrods();
			switch(type){
				case SpeechConstants.ASR_EVENT_ENGINE_INIT_DONE:
					LogUtils.d(TAG, "ASR_EVENT_ENGINE_INIT_DONE");
					//add wakeup word
					speechEngine.setWakeupWords(wakeupWords);
					//load grammar
					String jsgfDatPath = UserPreference.grammarPath;
					speechEngine.loadCompliedJsgf(main_tag, jsgfDatPath);
					break;
				case SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE:
					LogUtils.d(TAG, "ASR_EVENT_LOADGRAMMAR_DONE");
					//set wakeup word
//					setWakupWords(wakeupWords);
					break;
				case SpeechConstants.WAKEUP_EVENT_SET_WAKEUPWORD_DONE:
					LogUtils.d(TAG, "WAKEUP_EVENT_SET_WAKEUPWORD_DONE");
					initWakeupWords();
					initDataSource();
					startWakeup();
					if(isFirstCompile){
						speechListener.onInitEngineDone();
						isFirstCompile = false;
					}
					if(isCommandWakeupWords){
						speechListener.onWakeupWordsReady();
					}
					break;
				case SpeechConstants.WAKEUP_EVENT_RECOGNITION_SUCCESS:
					LogUtils.d(TAG, "WAKEUP_EVENT_RECOGNITION_SUCCESS");
					//if normal wake up mode , cancel engine 
					if(!speechEngine.isOneShotMode()){
						speechEngine.cancel();
					}
					break;
				case SpeechConstants.ASR_EVENT_VAD_TIMEOUT:
					stopRecording("vad timeout");
					
					//if vad is timeout, vad timeout task should be cancel.
					vadTimeoutTask.stop("vad timeout, vad timeout task should be cancel.");
					
					//execute asr timeout engine. This is the beginning of recognize.
					onProtocalTask.execute("vad timeout , this is beginning of recognize");
					
					String statusJsonStr = speechEngine.getOption(SpeechConstants.ASR_OPT_TIMEOUT_STATUS, "default");
					LogUtils.d(TAG, "ASR_EVENT_VAD_TIMEOUT, status json : " + statusJsonStr);
					
					JSONObject statusJson;
					int vadTimeoutType = 0;
					double afterTimeoutVoice = 0;
					try {
						// when sdk call back null json of statusJsonStr
						if (statusJsonStr == null || statusJsonStr.equals("")){
							vadTimeoutType = UserPreference.VAD_BACK_TIMEOUT;
						} else {
							statusJson = new JSONObject(statusJsonStr);
							vadTimeoutType = statusJson.getInt("timeout");
							afterTimeoutVoice = statusJson.getDouble("afterTimeoutVoice");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(vadTimeoutType == UserPreference.VAD_BACK_TIMEOUT || time == UserPreference.VAD_BACK_TIMEOUT){
						stopRecordToRecognize();
					}else if(vadTimeoutType == UserPreference.VAD_ONESHOT_TIMEOUT){
						//if recording time less than 1500ms, treat as the situation of wakeup action in oneshot mode
						if(afterTimeoutVoice < 1500){
							stopRecordToRecord();
						}else{
							stopRecordToRecognize();
						}
					}else {
						LogUtils.e(TAG, "statusJsonStr is " + statusJsonStr);
					}
					break;
				case SpeechConstants.ASR_EVENT_RECORDING_STOP:
					LogUtils.d(TAG, "ASR_EVENT_RECORDING_STOP");
					break;
				case SpeechConstants.ASR_EVENT_RECOGNITION_END:
					LogUtils.d(TAG, "ASR_EVENT_RECOGNITION_END");
					startWakeup();
					break;
				case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
					// callback volume all the time(delete if(isWakeupSuccess){...})
					speechListener.onVolumeChanged(getVolume());
					break;
				default:
					break;
			}
		}

		private void stopRecordToRecord(){
			//cancel speechEngine
			speechEngine.cancel();
			//stop timeout task | there are no protocal(cancel engine), so stop protocal task
			onProtocalTask.stop("stopRecordToRecord : session done, no need protocal");
			
			speechListener.onWakeupPrompt();
		}
		
		private void stopRecordToRecognize() {
			speechEngine.stopRecognize();
			speechListener.onRecognizeStart();
		}
		
		@Override
		public void onError(int type, String msg) {
			if(Arrays.asList(ErrorMessage.TIMEOUT_ERROR).contains(type)){
				speechListener.onError(ErrorMessage.ASR_TIMEOUT_ERROR);
			}
		}
	};

	public void startRecording(String msg) {
		voiceOperator.muteVoice(msg + " ,mute voice");
		setWakeupStatue(true, msg + " ,wakeup is true");
	}
	
	private void stopRecording(String msg){
		voiceOperator.unmuteVoice(msg + " ,unmute voice");
		setWakeupStatue(false, msg + " ,wakeup is false");
	}
	
	private List<Contact> contactsList;
	private TimeoutTask onProtocalTask;
	private VoiceOperator voiceOperator;
	private SessionFilter sessionFilter;

	public void setListener(SpeechListener speechListener){
		this.speechListener = speechListener;
	}

	protected void initWakeupWords() {
		if(speechEngine != null && speechEngine.isOneShotMode()){
			ArrayList<String> wakeupWords = getDefaultWakeupWrods();
			setWakupWords(wakeupWords);
		}
	}

	private ArrayList<String> getDefaultWakeupWrods() {
		ArrayList<String> wakeupWords = new ArrayList<String>();
		wakeupWords.add(defaultWakeupWord);
		return wakeupWords;
	}

	public void setNormalWakeupWord(String wakeupWord){
		if(wakeupWord != null){
			defaultWakeupWord = wakeupWord;
		}else {
			defaultWakeupWord = UserPreference.DEFAULT_WAKEUP_WORD;
		}
	}
	
	private void startWakeup() {
		speechEngine.startWakeup(speechEngine.isOneShotMode());
	}
	
	//TODO to set compile way
	protected void updateUserData() {
		LogUtils.d(TAG, "updateUserData");
		if(contactsList != null){
			StringBuffer contactTag = new StringBuffer();
			contactTag.append(main_tag).append("#").append(contact_tag);
			ArrayList<String> contacts = new ArrayList<String>();
			for(Contact contactItem : contactsList){
				contacts.add(contactItem.getName());
			}
			speechListener.onEngineCompileStart();
			int resultContacts = speechEngine.insertVocab(contacts, contactTag.toString());
			LogUtils.d(TAG, "updateUserData : resultContacts = " + resultContacts);
			if(resultContacts == 0){
				speechListener.onEngineCompileFinished();
			}else {
				speechListener.onError(ErrorMessage.COMPILE_ERROR);
			}
		} else{
			LogUtils.e(TAG, "updateUserData report : contactsList is null");
		}
	}
	
	public void cancelRecognizeManually() {
		if(speechEngine != null){
			LogUtils.d(TAG, "cancelRecognizeManually");
			vadTimeoutTask.stop("vad timeout task stop, because recognize cancel manually");
			onProtocalTask.stop("on protocal task stop, because recognize cancel manually");
			stopRecording("cancel Recognize Manually");
			speechEngine.startWakeup(speechEngine.isOneShotMode());
		}
	}
	
	private void stopCurrentTTS() {
		// if ttsEngine is playing or synthesizing, stop tts
		if(ttsEngine != null && (ttsEngine.getTTSStatus() == SpeechConstants.TTS_STATUS_PLAYING
				|| ttsEngine.getTTSStatus() == SpeechConstants.TTS_STATUS_SYNTHESIZERING)){
			ttsEngine.stopTTS();
		}
	}

	public void pauseSpeech() {
		if(speechEngine != null){
			speechEngine.stopWakeup();
		}
	}

	public void resumeSpeech() {
		if(speechEngine != null){
			speechEngine.startWakeup(speechEngine.isOneShotMode());
		}
	}
	
	public void setWakupWords(List<String> wakupWords) {
		if(speechEngine != null){
			if(wakupWords.contains(defaultWakeupWord)){
				isCommandWakeupWords = false;
			}else {
				isCommandWakeupWords = true;
				// default wakeup word will be set anyway
				wakupWords.add(defaultWakeupWord);
			}
			
			//two conditions: 1.oneshot state 2.set wakeup words(not command wakeup words)
			if(speechEngine.isOneShotMode() && !isCommandWakeupWords){
				//set oneshot wakeup words
				Map<String, List<String>> wakeupWordsMap = new HashMap<String, List<String>>();
				wakeupWordsMap.put("asr_wakeup_words_slot", wakupWords);
				
				//delete normal wakeup to clear wakeup_words_slot tag content
				ArrayList<String> errorList = new ArrayList<String>();
				errorList.add("cccc");
				wakeupWordsMap.put("wakeup_words_slot", errorList);
				
				//insert to vocab
				speechEngine.insertVocab(wakeupWordsMap, main_tag);
			}
			//two situations: 1.nromal state 2.oneshot state && set command wakeup words
			else {
				speechEngine.setWakeupWords(wakupWords);
			}
		}
	}

	public void startRecognizeManually() {
		LogUtils.d(TAG, "startRecognizeManually");
		if(speechEngine != null){
			startRecording("start recording manually");
			speechEngine.startRecognize();
		}
	}
	
	public void startCompileManually(){
		LogUtils.d(TAG, "startCompileManually");
		reInitDataSource();
	}

	private void reInitDataSource() {
		ContactsOperator.newInstance(mContext, new ContactsListener() {
			@Override
			public void onSearchEnd(List<Contact> contacts) {
				contactsList = contacts;
				sessionFilter.setContactList(contactsList);
				updateUserData();
			}
			
			@Override
			public void onSearchBegin() {
				
			}
		}).startSearchContacts();
	}

	public void playTTS(String ttsContext, Boolean keepRecognize) {
		if(ttsEngine != null && speechEngine != null){
			ttsEngine.playTTS(ttsContext, keepRecognize);
		}
	}
	
	public void stopTTS() {
		if(ttsEngine != null){
			ttsEngine.stopTTS();
		}
	}
	
	private int getVolume(){
		if(speechEngine != null){
			return speechEngine.getVolume();
		}
		return -1;
	}

	public boolean isEngineReady(){
		//sdk not offer method
		return true;
	}
	
	public void setWakeupMode(String wakeupMode) {
		if((!isOneShotMode() && wakeupMode.equals(UserPreference.NORMAL_WAKEUP_MODE))
				|| (isOneShotMode() && wakeupMode.equals(UserPreference.ONESHOT_WAKEUP_MODE))){
			LogUtils.e(TAG, "aleady set " + wakeupMode + " mode");
		} else {
			if(speechEngine != null){
				setWakupWords(getDefaultWakeupWrods());
				speechEngine.setWakeupMode(wakeupMode);
			}
		}
	}
	
	private void setWakeupStatue(boolean statue, String msg){
		LogUtils.d(TAG, "set wakeup statue : " + statue + " |Because " + msg + " | " + (statue == !isWakeupSuccess()));
		if(statue == !isWakeupSuccess()){
			isWakeupSuccess = statue;
		}
	}
	
	public void setCurrentCity(String cityName) {
		if(speechEngine != null){
			speechEngine.setCurrentCity(cityName);
		}
	}
	
	private boolean isWakeupSuccess(){
		return isWakeupSuccess;
	}

	public boolean isOneShotMode() {
		if(speechEngine != null){
			return speechEngine.isOneShotMode();
		}else {
			LogUtils.e(TAG, "isOneShotMode report : speechEngine is null !!");
			return false;
		}
	}
	
	private void callbackError(String errorMsg) {
		// cancel to avoid callback method
		if(speechEngine != null){
			speechEngine.cancel();
		}
		speechListener.onError(errorMsg);
		// start wakeup
		if(speechEngine != null){
			speechEngine.startWakeup(speechEngine.isOneShotMode());
		}
	}
	
	public void destroy() {
		if(speechEngine != null){
			speechEngine.destory();
		}
		if(ttsEngine != null){
			ttsEngine.destory();
		}
	}
}
