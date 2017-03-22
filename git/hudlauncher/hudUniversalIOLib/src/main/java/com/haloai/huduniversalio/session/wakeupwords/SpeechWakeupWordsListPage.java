package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;

public class SpeechWakeupWordsListPage extends SpeechWakeupWords {

	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_ITEM_1 = WAKEUP_RESULT_BASE + 1;
	public static final int WAKEUP_RESULT_ITEM_2 = WAKEUP_RESULT_BASE + 2;
	public static final int WAKEUP_RESULT_ITEM_3 = WAKEUP_RESULT_BASE + 3;
	public static final int WAKEUP_RESULT_ITEM_4 = WAKEUP_RESULT_BASE + 4;
	public static final int WAKEUP_RESULT_NEXT_PAGE = WAKEUP_RESULT_BASE + 5;
	public static final int WAKEUP_RESULT_PREV_PAGE = WAKEUP_RESULT_BASE + 6;

	
	private static List<String> SESSION_WAKEUP_WORDS_LIST_PAGE = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_LIST_PAGE.add(WAKEUP_EXIT);
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("第一个");
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("第二个");
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("第三个");
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("第四个");
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("下一页");
		SESSION_WAKEUP_WORDS_LIST_PAGE.add("上一页");

	}

	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_LIST_PAGE;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int wordIdx = SESSION_WAKEUP_WORDS_LIST_PAGE.indexOf(wakeupWord);
		if (wordIdx < 0 || wordIdx >= SESSION_WAKEUP_WORDS_LIST_PAGE.size()) {
			return WAKEUP_RESULT_NOT_FOUND;
		}
		return wordIdx;
	}

	@Override
	public String getPromptText() {
		return "请说第几个";
	}
}
