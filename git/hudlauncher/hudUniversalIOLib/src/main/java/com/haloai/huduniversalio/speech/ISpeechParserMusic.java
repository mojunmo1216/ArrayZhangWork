package com.haloai.huduniversalio.speech;

import java.util.ArrayList;
import java.util.List;

public interface ISpeechParserMusic extends ISpeechParser {
	public static final int PARSE_RES_CODE_MUSIC_INFO = PARSE_RES_CODE_BASE + 1;
	public static final int PARSE_RES_CODE_MUSIC_PLAY = PARSE_RES_CODE_BASE + 2;
	public class MusicInfo{
		public 	String title;
		public 	String artist;
		public  int duration;
		public  String imag_url;
		public  String song_url;
	}
	
	public abstract List<MusicInfo> getMusicInfoList();
	public abstract String getMusicTag();
}
