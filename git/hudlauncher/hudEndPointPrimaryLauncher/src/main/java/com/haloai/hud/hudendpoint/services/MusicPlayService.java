package com.haloai.hud.hudendpoint.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;

public class MusicPlayService extends Service {
    private MediaPlayer mMediaPlay;
    private int mCurrentIndex = 0;
    private MusicBroadcastReceiver musicBroadReceiver;
    private ArrayList<String> mSongList;
    private String TAG = MusicPlayService.class.getName();

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlay = new MediaPlayer();
        musicBroadReceiver = new MusicBroadcastReceiver();
        IntentFilter filter = new IntentFilter(HudEndPointConstants.ACTION_MUSIC_SERVICE);
        this.registerReceiver(musicBroadReceiver, filter);
        mMediaPlay.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
        mMediaPlay.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlay.start();
                Log.e("Mediaplay", "music---play-start");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HudStatusManager.setHudMusicStat(HudStatusManager.MUSIC_IDILE);
        if (musicBroadReceiver != null) {
            this.unregisterReceiver(musicBroadReceiver);
        }
        if (mMediaPlay != null) {
            mMediaPlay.stop();
            mMediaPlay.release();
            mMediaPlay = null;
        }
    }

    private void playMusic() {
        if (mMediaPlay != null && !mSongList.isEmpty()) {
            try {
                mMediaPlay.reset();
                mMediaPlay.setDataSource(mSongList.get(mCurrentIndex));
                mMediaPlay.prepareAsync();
                Intent intent = new Intent(HudEndPointConstants.MUSIC_STATUS_UPDATE);
                intent.putExtra(HudEndPointConstants.MUSIC_STATUS_UPDATE, mCurrentIndex);
                this.sendBroadcast(intent);
                HudStatusManager.setHudMusicStat(HudStatusManager.MUSIC_PLAY);
            } catch (IllegalStateException e) {
                nextSong();
                e.printStackTrace();
                Log.e(TAG, e.toString());
                HaloLogger.postE(EndpointsConstants.MUSIC_TAG,"音乐播放加载异常");
                HaloLogger.uploadCatchException(e);
            } catch (IOException e) {
                nextSong();
                e.printStackTrace();
                Log.e(TAG, e.toString());
                HaloLogger.postE(EndpointsConstants.MUSIC_TAG,"音乐播放资源读取异常");
                HaloLogger.uploadCatchException(e);
            }
        }
    }

    private void pauseMusic() {
        if (mMediaPlay != null && HudStatusManager.HudMusicStatus==HudStatusManager.MUSIC_PLAY) {
            mMediaPlay.pause();
            HudStatusManager.setHudMusicStat(HudStatusManager.MUSIC_PAUSE);
        }
    }

    private void resumeMusic() {
        if (HudStatusManager.HudMusicStatus==HudStatusManager.MUSIC_PAUSE && mMediaPlay != null) {
            mMediaPlay.start();
            HudStatusManager.setHudMusicStat(HudStatusManager.MUSIC_PLAY);
        }
    }

    private void stopMusic() {
        if (mMediaPlay != null) {
            mMediaPlay.stop();
            HudStatusManager.setHudMusicStat(HudStatusManager.MUSIC_IDILE);
        }
    }

    private void nextSong() {
        mCurrentIndex++;
        this.mCurrentIndex = mCurrentIndex > mSongList.size() - 1 ? 0 : mCurrentIndex;
        playMusic();
    }

    private void prevSong() {
        mCurrentIndex--;
        this.mCurrentIndex = mCurrentIndex < 0 ? mSongList.size() - 1 : mCurrentIndex;
        playMusic();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String tag = intent.getStringExtra(HudEndPointConstants.MUSIC_SERVICE_MUSIC_TAG);
            if (tag.equalsIgnoreCase("PLAY_MUSIC") && mSongList != null) {
                resumeMusic();
            } else {
                mSongList = intent.getStringArrayListExtra(HudEndPointConstants.MUSIC_SERVICE_SONG_LIST);
                Random random = new Random();
                if(mSongList.size()<=10){
                    mCurrentIndex = 0;
                }else{
                    mCurrentIndex = random.nextInt(mSongList.size()-1);//随机播放列表中的歌曲
                }
                playMusic();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public class MusicBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra(HudEndPointConstants.MUSIC_SERVICE_FLAG, HudEndPointConstants.MUSIC_SERVICE_PLAY);
            switch (flag) {
                case HudEndPointConstants.MUSIC_SERVICE_PLAY:
                    resumeMusic();
                    break;
                case HudEndPointConstants.MUSIC_SERVICE_PAUSE:
                    pauseMusic();
                    break;
                case HudEndPointConstants.MUSIC_SERVICE_NEXT:
                    nextSong();
                    break;
                case HudEndPointConstants.MUSIC_SERVICE_PREV:
                    prevSong();
                    break;
                case HudEndPointConstants.MUSIC_SERVICE_STOP:
                    stopMusic();
                    stopSelf();
                    break;
                default:
                    break;
            }
        }

    }

}
