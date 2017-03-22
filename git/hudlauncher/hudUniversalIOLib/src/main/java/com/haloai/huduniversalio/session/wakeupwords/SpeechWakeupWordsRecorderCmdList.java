package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;


public class SpeechWakeupWordsRecorderCmdList extends SpeechWakeupWords {

	public static final int WAKEUP_RESULT_PREVIEW = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_VIDEOTAPE = WAKEUP_RESULT_BASE + 1;
	public static final int WAKEUP_RESULT_BROWSE_WONDERFUL = WAKEUP_RESULT_BASE + 2;
	public static final int WAKEUP_RESULT_BROWSE_LOOPING = WAKEUP_RESULT_BASE + 3;
	public static final int WAKEUP_RESULT_BROWSE_EMERGENCY = WAKEUP_RESULT_BASE + 4;
	public static final int WAKEUP_RESULT_BROWSE_LOCKED = WAKEUP_RESULT_BASE + 5;
	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 6;
	
	private static final String PROMPT_TEXT = "请说预览或者某类的视频";
	
	private static List<String> SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("预览");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("录像");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("精彩视频");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("循环视频");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("紧急视频");
		SESSION_WAKEUP_WORDS_RECORDER_CMD_LIST.add("加锁视频");
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
