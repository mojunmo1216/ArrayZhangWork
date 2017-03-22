/*
 * Copyright (C) 2010 ZXing authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unisound.framework.utils;

import java.io.IOException;

import com.unisound.framework.preference.UserPreference;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
public final class BeepPlayer {
	private static final String TAG = "BeepPlayer";

	private final Context mContext;
	private MediaPlayer mMediaPlayer;
	private float mVolume;
	private OnCompletionListener mOnCompleteListener;

	public BeepPlayer(Context context, String path) {
		mContext = context;
		mMediaPlayer = buildMediaPlayer(mContext);
	}

	public void setVolume(float volume) {
		LogUtils.d(TAG, "setVolume:volume " + volume);
		if (volume < 0.0f || volume > 1.0f) {
			LogUtils.e(TAG, "volume out of range[0,1]");
			return;
		}
		mVolume = volume;
	}

	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void playBeepSound(int rawId, boolean looping, OnCompletionListener completeCallback) {
		LogUtils.d(TAG, "playBeepSound:rawId " + rawId + ",lopping " + looping + ",completeCallback " + completeCallback);
		mOnCompleteListener = completeCallback;
		AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);

		if (mMediaPlayer == null) {
			throw new RuntimeException("MediaPlayer has been released.");
		}
		
		mMediaPlayer.setOnCompletionListener(completeCallback);
		
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}

		mMediaPlayer.reset();
		mMediaPlayer.setLooping(looping);

		try {
			mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			mMediaPlayer.setAudioStreamType(UserPreference.STREAM_TYPE);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			LogUtils.d(TAG, "set volume:" + mVolume);
			mMediaPlayer.setVolume(mVolume, mVolume);
		} catch (Exception e) {
			LogUtils.e(TAG, e.toString());
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void playBeepSound(String path, boolean looping,OnCompletionListener completeCallback) {
		LogUtils.d(TAG, "playBeepSound path : " + path + ",lopping " + looping + ",completeCallback " + completeCallback);
		mOnCompleteListener = completeCallback;
		if (mMediaPlayer == null) {
			throw new RuntimeException("MediaPlayer has been released.");
		}
		
		mMediaPlayer.setOnCompletionListener(mOnCompleteListener);
		
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}

		mMediaPlayer.reset();
		mMediaPlayer.setLooping(looping);

		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setAudioStreamType(UserPreference.STREAM_TYPE);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			LogUtils.d(TAG, "set volume:" + mVolume);
			mMediaPlayer.setVolume(mVolume, mVolume);
		} catch (Exception e) {
			LogUtils.e(TAG, e.toString());
		} finally {
		}
	}

	public void stop() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

	private MediaPlayer buildMediaPlayer(Context context) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		return mediaPlayer;
	}
}
