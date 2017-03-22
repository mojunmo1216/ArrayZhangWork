package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;


public class SpeechWakeupWordsRecorderBrowse extends SpeechWakeupWords {

	public static final int WAKEUP_RESULT_FAST_FORWARD = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_FAST_BACK = WAKEUP_RESULT_BASE + 1;
	public static final int WAKEUP_RESULT_LOCK = WAKEUP_RESULT_BASE + 2;
	public static final int WAKEUP_RESULT_DELETE = WAKEUP_RESULT_BASE + 3;
	public static final int WAKEUP_RESULT_PREV_VIDEO = WAKEUP_RESULT_BASE + 4;
	public static final int WAKEUP_RESULT_NEXT_VIDEO = WAKEUP_RESULT_BASE + 5;
	public static final int WAKEUP_RESULT_BACK = WAKEUP_RESULT_BASE + 6;
	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 7;
	
	private static final String PROMPT_TEXT = "请说上一段下一段来切换视频";
	
	private static List<String> SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("快进");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("快退");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("加锁");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("删除");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("上一段");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("下一段");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("返回");
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
