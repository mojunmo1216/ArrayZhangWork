package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;


public class SpeechWakeupWordsRecorderPreview extends SpeechWakeupWords {

	public static final int WAKEUP_RESULT_WONDERFUL = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_LOOPING = WAKEUP_RESULT_BASE + 1;
	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 2;
	
	private static final String PROMPT_TEXT = "";
	
	private static List<String> SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("精彩视频");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("循环视频");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add(WAKEUP_EXIT);
	}
	
	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int wordIdx = SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.indexOf(wakeupWord);
		if (wordIdx < 0 || wordIdx >= SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.size()) {
			return WAKEUP_RESULT_NOT_FOUND;
		}
		return wordIdx;
	}

	@Override
	public String getPromptText() {
		return PROMPT_TEXT;
	}

}
