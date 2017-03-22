package com.unisound.framework.engine.speech;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import com.unisound.client.IAudioSource;
import com.unisound.framework.preference.ErrorMessage;
import com.unisound.framework.preference.UserPreference;
import com.unisound.framework.pub.SpeechListener;
import com.unisound.framework.utils.LogUtils;

/**
 * 音频源设置外部实现类
 * @author unisound Copyright (c) 2015, unisound.com All Rights Reserved
 *	该类用来定义相应的audiosource 操作
 */
public class AudioSourceImpl implements IAudioSource{
	private static final String TAG = AudioSourceImpl.class.getSimpleName();
	
	public static final int FREQUENCY_16K = 16000;
	private static int FREQUENCY = FREQUENCY_16K;
	private static int bufferSizeInBytes;
	private AudioRecord audioRecord = null;

	protected static int CHANNEL = AudioFormat.CHANNEL_IN_MONO; // .CHANNEL_CONFIGURATION_MONO;
	protected static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	protected static int STREAM_TYPE = UserPreference.STREAM_TYPE;
	protected static int SAMPLE_RATE = (int) (22.05 * 1000);
	protected static int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO;
	protected static int MODE = AudioTrack.MODE_STREAM;
	
	private AudioTrack mAudioTrack = null;
	private SpeechListener speechListener;
	
	static {
		bufferSizeInBytes = 6400; 
		int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL, ENCODING);
		if (bufferSizeInBytes < bufferSize) {
			bufferSizeInBytes = bufferSize;
		}
	}
	
	public AudioSourceImpl(SpeechListener speechListener){
		this.speechListener = speechListener;
	}

	@Override
	public int openAudioIn() {
		return open();
	}

	@Override
	public int openAudioOut() {
		return openOut();
	}

	@Override
	public int readData(byte[] buffer, int size) {
		return read(buffer, size);
	}

	@Override
	public int writeData(byte[] buffer, int size) {
		return write(buffer, size);
	}

	@Override
	public void closeAudioIn() {
		close();		
	}

	@Override
	public void closeAudioOut() {
		closeOut();
	}
	
	private int open() {
		try{
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
				16000, CHANNEL, ENCODING, bufferSizeInBytes);
			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
					audioRecord.startRecording();
					return 0;
			}
		}catch(Exception e){
			if(speechListener != null){
				speechListener.onError(ErrorMessage.MIC_CONFLICT_ERROR);
			}
			LogUtils.e(TAG, "open report : mic conflict error !");
			return -1;
		}
		return -1;
	}
	
	private int openOut() {
		mAudioTrack = new AudioTrack(STREAM_TYPE, SAMPLE_RATE, CHANNEL_OUT, ENCODING, bufferSizeInBytes, MODE);
		if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			mAudioTrack.play();
			return 0;
		}
		return -1;
	}
	
	private void close() {
		if(audioRecord != null) {
			try{
	 			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
	 				audioRecord.stop();
	 			}
	 			audioRecord.release();
	 			audioRecord = null;
			}catch(Exception e){
				if(speechListener != null){
					speechListener.onError(ErrorMessage.MIC_CONFLICT_ERROR);
				}
				LogUtils.e(TAG, "close report : mic conflict error");
			}
 		}
	}
	
	private void closeOut() {
		if (mAudioTrack != null) {
			if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
				mAudioTrack.stop();
			}
			mAudioTrack.flush();
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}

	private int read(byte[] buffer , int size) {
		int read = 0;
		if(audioRecord != null) {
			read = audioRecord.read(buffer, 0, size);
		}
		return read;
	}
	
	private int write(byte[] buffer, int size){
		if(mAudioTrack != null) {
			return mAudioTrack.write(buffer, 0, size);
		}
		return 0;
	}
}
