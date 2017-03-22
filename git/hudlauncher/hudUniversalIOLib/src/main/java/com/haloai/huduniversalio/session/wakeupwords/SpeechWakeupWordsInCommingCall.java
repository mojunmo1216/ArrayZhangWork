package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;

public class SpeechWakeupWordsInCommingCall extends SpeechWakeupWords{
	public static final int WAKEUP_RESULT_CONFIRM = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_CANCLE = WAKEUP_RESULT_BASE + 1;
	
	private static List<String> SESSION_WAKEUP_WORDS_IN_COMMING = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_IN_COMMING.add("接听");
		SESSION_WAKEUP_WORDS_IN_COMMING.add("挂断");
	}

	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_IN_COMMING;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int wordIdx = SESSION_WAKEUP_WORDS_IN_COMMING.indexOf(wakeupWord);
		if (wordIdx == -1) {
			return WAKEUP_RESULT_NOT_FOUND;
		}
		return wordIdx;
	}

	@Override
	public String getPromptText() {
		return "请说接听或挂断";
	}

}
