package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;

public class SpeechWakeupWordsDialog extends SpeechWakeupWords {
	public static final int WAKEUP_RESULT_CONFIRM = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_BACK = WAKEUP_RESULT_BASE + 1;

	protected static List<String> SESSION_WAKEUP_WORDS_DIALOG = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_DIALOG.add("确定");
		SESSION_WAKEUP_WORDS_DIALOG.add("取消");
	}

	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_DIALOG;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int idxWord = SESSION_WAKEUP_WORDS_DIALOG.indexOf(wakeupWord);
		if (idxWord == -1)
			return WAKEUP_RESULT_NOT_FOUND;
		
		return idxWord;
	}

	@Override
	public String getPromptText() {
		return "请说确定或取消";
	}

}
