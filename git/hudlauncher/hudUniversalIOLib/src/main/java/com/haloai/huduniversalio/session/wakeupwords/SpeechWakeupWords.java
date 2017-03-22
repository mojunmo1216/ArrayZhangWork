package com.haloai.huduniversalio.session.wakeupwords;

import java.util.List;

public abstract class SpeechWakeupWords {

	public static final int WAKEUP_RESULT_BASE = 0;
	public static String WAKEUP_EXIT="退出";
	public static final int WAKEUP_RESULT_NOT_FOUND = WAKEUP_RESULT_BASE - 1;

	public abstract List<String> getAllWakeupWords();
	public abstract int getWakeupResult(String wakeupWord);
	public abstract String getPromptText();
}
