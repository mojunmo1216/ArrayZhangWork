package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;

public class SpeechWakeupWordsExit extends SpeechWakeupWords {
	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 0;

	protected static List<String> SESSION_WAKEUP_WORDS_EXIT = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_EXIT.add(WAKEUP_EXIT);
	}

	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_EXIT;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int wordIdx = SESSION_WAKEUP_WORDS_EXIT.indexOf(wakeupWord);
		if (wordIdx == -1)
			return WAKEUP_RESULT_NOT_FOUND;

		return wordIdx;
	}

	@Override
	public String getPromptText() {
		return "您可以说退出";
	}

}
