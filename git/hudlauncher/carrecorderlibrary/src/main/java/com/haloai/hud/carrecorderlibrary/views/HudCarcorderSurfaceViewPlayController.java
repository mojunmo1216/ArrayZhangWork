package com.haloai.hud.carrecorderlibrary.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class HudCarcorderSurfaceViewPlayController extends SurfaceView implements SurfaceHolder.Callback, IHudRecorderController.IHudCarrecordPlayController {
    private static HudCarcorderSurfaceViewPlayController mSurfaceView;
    protected Context mContext;
    private MediaPlayer mMediaPlayer;
    private boolean pauseFlag = false;
    private SurfaceHolder mSurfaceHolder;
    private Timer timer;
    private TimerTask mTimerTask;

    private HudCarcorderSurfaceViewPlayController(Context context) {
        super(context);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public static HudCarcorderSurfaceViewPlayController getRecorderSurfaceView(Context context) {
        if (mSurfaceView == null) {
            synchronized (HudCarcorderSurfaceViewPlayController.class) {
                if (mSurfaceView == null) {
                    mSurfaceView = new HudCarcorderSurfaceViewPlayController(context);
                }
            }
        }
        return mSurfaceView;
    }

    public MediaPlayer getMedia() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        return mMediaPlayer;
    }

    @Override
    public void playVideo(String videoPath) {
        if ((videoPath == null) || (!new File(videoPath).exists())) {
            return;
        }
        try {
            mMediaPlayer = getMedia();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(videoPath);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setOnPreparedListener(new PrepareListener());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseVideo() {
        if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying()) && (!pauseFlag)) {
            mMediaPlayer.pause();
            pauseFlag = true;
        }
    }

    @Override
    public void resumeVideo() {
        if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying()) && (pauseFlag)) {
            mMediaPlayer.start();
            pauseFlag = false;
        }
    }

    @Override
    public void stopVideo() {
        pauseFlag = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    @Override
    public void fastForward() {
        pauseFlag = false;
        long duration = mMediaPlayer.getDuration();
        if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying())) {
            long currentPosition = mMediaPlayer.getCurrentPosition();
            int destinationPosition = (int) (currentPosition + 10000L);
            if (destinationPosition > duration * 1000L) {
                mMediaPlayer.seekTo((int) (duration * 1000L - 500L));
            } else
                mMediaPlayer.seekTo(destinationPosition);
            mMediaPlayer.start();
        }
    }

    @Override
    public void fastBackward() {
        pauseFlag = false;
        if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying())) {
            long currentPosition = mMediaPlayer.getCurrentPosition();
            int destinationPosition = (int) (currentPosition - 10000L);
            if (destinationPosition < 1)
                mMediaPlayer.seekTo(0);
            else {
                mMediaPlayer.seekTo(destinationPosition);
            }
            mMediaPlayer.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (timer != null) {
            timer.cancel();
            mTimerTask.cancel();
            timer = null;
            mTimerTask = null;
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private final class PrepareListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            pauseFlag = false;
        }
    }
}
