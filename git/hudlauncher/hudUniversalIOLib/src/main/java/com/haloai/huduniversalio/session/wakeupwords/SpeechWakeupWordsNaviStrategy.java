package com.haloai.huduniversalio.session.wakeupwords;

import java.util.ArrayList;
import java.util.List;

import com.haloai.huduniversalio.session.IHudIOSessionNaviCallback;

public class SpeechWakeupWordsNaviStrategy extends SpeechWakeupWords {

	public static final int WAKEUP_RESULT_FASTEST_STRATEGY = WAKEUP_RESULT_BASE + 0;
	public static final int WAKEUP_RESULT_AVOID_CONGESTION_STRATEGY = WAKEUP_RESULT_BASE + 1;
	public static final int WAKEUP_RESULT_SHORTEST_STRATEGY = WAKEUP_RESULT_BASE + 2;
	public static final int WAKEUP_SHORTEST = WAKEUP_RESULT_BASE + 3;
	public static final int WAKEUP_AVOID_CONGESTION= WAKEUP_RESULT_BASE + 4;
	public static final int WAKEUP_FASTEST= WAKEUP_RESULT_BASE + 5;
	public static final int WAKEUP_START_NAVI = WAKEUP_RESULT_BASE + 6;
	public static final int WAKEUP_START = WAKEUP_RESULT_BASE + 7;
	public static final int WAKEUP_RESULT_EXIT = WAKEUP_RESULT_BASE + 8;
	
	protected static List<String> SESSION_WAKEUP_WORDS_STRATEGY = new ArrayList<String>();
	static {
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.FASTEST_STRATEGY);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.AVOID_CONGESTION_STRATEGY);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.SHORTEST_STRATEGY);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.SHORTEST);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.AVOID_CONGESTION);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.FASTEST);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.START_NAVI);
		SESSION_WAKEUP_WORDS_STRATEGY.add(IHudIOSessionNaviCallback.START);
		SESSION_WAKEUP_WORDS_STRATEGY.add(WAKEUP_EXIT);
	}

	@Override
	public List<String> getAllWakeupWords() {
		return SESSION_WAKEUP_WORDS_STRATEGY;
	}

	@Override
	public int getWakeupResult(String wakeupWord) {
		int wordIdx = SESSION_WAKEUP_WORDS_STRATEGY.indexOf(wakeupWord);
		if (wordIdx == -1) {
			return WAKEUP_RESULT_NOT_FOUND;
		}
		return wordIdx;
	}

	@Override
	public String getPromptText() {
		return "请说导航策略名";
	}

}
