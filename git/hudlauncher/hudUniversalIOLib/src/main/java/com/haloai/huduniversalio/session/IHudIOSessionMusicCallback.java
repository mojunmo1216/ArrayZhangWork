package com.haloai.huduniversalio.session;

import com.haloai.huduniversalio.speech.ISpeechParserMusic;

import java.util.ArrayList;
import java.util.List;


public interface IHudIOSessionMusicCallback extends IHudIOSessionCallback {
	public abstract void musicSessionSearchSong(List<ISpeechParserMusic.MusicInfo> mMusicInfoList , String tag);
	public abstract void musicSessionPlayMusic();
}
