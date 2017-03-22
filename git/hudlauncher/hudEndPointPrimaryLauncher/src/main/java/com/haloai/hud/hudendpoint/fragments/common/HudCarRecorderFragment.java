package com.haloai.hud.hudendpoint.fragments.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haloai.hud.carrecorderlibrary.bean.HudSDCardStates;
import com.haloai.hud.carrecorderlibrary.bean.VideoBean;
import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;
import com.haloai.hud.carrecorderlibrary.db.CarRecorderDBHelper;
import com.haloai.hud.carrecorderlibrary.factory.HudRecorderControllerFactory;
import com.haloai.hud.carrecorderlibrary.utils.CarRecorderFileUtils;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.carrecorderlibrary.views.CameraView;
import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.HudConstants;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback.HudRecorderVideoType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HudCarRecorderFragment extends Fragment {
    private FrameLayout mFlCarRecorderView;
    private RelativeLayout mRlPlay;
    private FrameLayout mFlVideoContainer;
    private LinearLayout mLlVideoInfo, mLlVideoInfoBack;
    private RelativeLayout mLlSeekbarMicInfo;
    private TextView mOldVideoDate, mOldVideoTime, mOldVideoSize, mNewVideoDate, mNewVideoTime, mNewVideoSize, mTvCurrentTime, mTvTotaltime, mTvPrompt;
    private RelativeLayout mRlScan;
    private IHudRecorderController mController;
    private IHudRecorderController.IHudCarrecordPlayController mRecorderPlayer;
    private View mRecorderSurfaceView;
    private MediaPlayer mMedia;
    private ImageView mIvFirstImage, mIvSecondImage, mIvThirdImage, mIvImageLock;
    private int mIndexOfPlayingVideo = 0;
    private List<VideoBean> mVideoBeanList = new ArrayList<>();
    private List<ImageView> mImageList = new ArrayList<>();
    private HudCarcorderConstants.VideoTypeEnum mVideoType;
    private boolean mImageAnimating = false;
    private boolean mDeleteMark = true;// true代表可以执行删除操作。
    private boolean mPlayingByPhone = false;
    private Timer timer;
    private TimerTask mTimerTask;
    private int mRecorderStatus;
    private CameraView mCameraView;
    private FrameLayout mScanContainer;
    private static final int CARRECORDER_STATE_BASE = 0;                                // 未播进入行车记录模块状态
    private static final int CARRECORDER_STATE_SCANING = CARRECORDER_STATE_BASE + 1;    // 预览状态
    private static final int CARRECORDER_STATE_PLAYING = CARRECORDER_STATE_BASE + 2;    // 播放状态
    private static final int CARRECORDER_STATE_DELETING = CARRECORDER_STATE_BASE + 3;   // 删除状态
    private static final int PLAY_TIME_UPDATE_MARK = 100;


    private BroadcastReceiver videoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(HudEndPointConstants.RECORDER_CREATED_VIDEO)) {
                String videoPath = intent.getStringExtra("path");
                if (TextUtils.isEmpty(videoPath)) {
                    return;
                }
                VideoBean bean = new VideoBean();
                bean.videoPath = videoPath;
                bean.videoName = intent.getStringExtra("name");
                bean.videoSize = intent.getLongExtra("size", 0);
                bean.videoDuration = Long.valueOf(intent.getStringExtra("duration"));
                bean.startTime = CarRecorderFileUtils.getVideoStartTime2(intent.getStringExtra("start_time")); // 达讯格式：2016-09-12 16:43:59
                bean.endTime = bean.startTime + bean.videoDuration;
                bean.thumbBytes = CarRecorderFileUtils.getImageByteArray(bean.videoPath);
                if (bean.videoPath.contains(CarRecorderDBHelper.LOCK_VIDEO)) {
                    bean.videoType = CarRecorderDBHelper.LOCK_VIDEO;
                } else {
                    bean.videoType = CarRecorderDBHelper.LOOP_VIDEO;
                }
                boolean insertsucess = mController.addVideo(bean);
                HaloLogger.logE("HudCarRecorderFragment2", "视频插入数据库成功与否：" + insertsucess + "\n---视频路径:" + bean.videoPath);
                // 此处增加，每当有视频增加的时候，

            } else if (action.equals(HudEndPointConstants.RECORDER_DELETE_VIDEO)) {
                String videoName = intent.getStringExtra("name");
                Cursor cursor = mController.findVideoInfomation(CarRecorderDBHelper.VIDEO_NAME, videoName, BaseColumns._ID);
                if (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    mController.deleteVideo(id);
                } else {
                    HaloLogger.logE("HudCarRecorderFragment2", "需要删除的视频不存在");
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PLAY_TIME_UPDATE_MARK:
                    mTvCurrentTime.setText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter recorderFilter = new IntentFilter();
        recorderFilter.addAction(HudEndPointConstants.RECORDER_CREATED_VIDEO);
        recorderFilter.addAction(HudEndPointConstants.RECORDER_DELETE_VIDEO);
        getActivity().registerReceiver(videoReceiver, recorderFilter);
        initView();
        return mFlCarRecorderView;
    }

    private void initView() {
        mFlCarRecorderView = ((FrameLayout) View.inflate(getActivity(), R.layout.fragment_carrecorder, null));
        mRlPlay = (RelativeLayout) mFlCarRecorderView.findViewById(R.id.carrecorder_play);
        mScanContainer = (FrameLayout) mFlCarRecorderView.findViewById(R.id.carrecorder_scan_sancotainer);
        mRlScan = (RelativeLayout) mFlCarRecorderView.findViewById(R.id.carrecorder_scan);
        mFlVideoContainer = (FrameLayout) mFlCarRecorderView.findViewById(R.id.carrecorder_play_videocontainer);
        mLlVideoInfo = (LinearLayout) mFlCarRecorderView.findViewById(R.id.carcorder_videoinfo);
        mLlVideoInfoBack = (LinearLayout) mFlCarRecorderView.findViewById(R.id.carcorder_videoinfo_back);
        mIvFirstImage = (ImageView) mFlCarRecorderView.findViewById(R.id.carrecorder_play_firstimage);
        mIvSecondImage = (ImageView) mFlCarRecorderView.findViewById(R.id.carrecorder_play_secondimage);
        mIvThirdImage = (ImageView) mFlCarRecorderView.findViewById(R.id.carrecorder_play_thirdimage);
        mIvImageLock = (ImageView) mFlCarRecorderView.findViewById(R.id.carcorder_video_image_lock);
        mLlSeekbarMicInfo = (RelativeLayout) mFlCarRecorderView.findViewById(R.id.carcorder_video_progress_micinfo);
        mTvCurrentTime = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_seekbar_current_time);
        mTvTotaltime = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_seekbar_total_time);
        mTvPrompt = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_prompt);
        mImageList.clear();

        mImageList.add(mIvFirstImage);
        mImageList.add(mIvSecondImage);
        mImageList.add(mIvThirdImage);
        mOldVideoDate = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_date);
        mOldVideoTime = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_time);
        mOldVideoSize = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_size);
        mNewVideoDate = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_date_back);
        mNewVideoTime = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_time_back);
        mNewVideoSize = (TextView) mFlCarRecorderView.findViewById(R.id.carcorder_video_size_back);
        mController = HudRecorderControllerFactory.getHudCarcorderController2(getActivity());
        mController.defaultRecorderSetting();
        HudSDCardStates sdCardStates= mController.getSDCardStates();
        HaloLogger.postE(EndpointsConstants.RECORD_TAG,String.format("carrecorder info ,quality %s , time %s ",sdCardStates.mImageQuality,sdCardStates.mVideoDuration));
        mRecorderPlayer = mController.getRecorderPlayer();
        mRecorderSurfaceView = mController.getRecorderSurfaceView();
        /*初始化预览窗口参数*/
        Intent intent = new Intent("intent.action.ACTION_SET_CAMERA_PREVIEW_POSITION");
        intent.putExtra("xParams", DisplayUtil.dip2px(getActivity(), 227f));
        intent.putExtra("yParams", DisplayUtil.dip2px(getActivity(), getActivity().getResources().getDimension(R.dimen.fragment_top_margin) - 55));
        getActivity().sendBroadcast(intent);
        Intent carcorderSizeIntent = new Intent("intent.action.ACTION_SET_CAMERA_PREVIEW_SIZE");
        carcorderSizeIntent.putExtra("width", DisplayUtil.dip2px(getActivity(), 332f));
        carcorderSizeIntent.putExtra("height", DisplayUtil.dip2px(getActivity(), 160.67f));
        getActivity().sendBroadcast(carcorderSizeIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HaloLogger.logE("HudCarRecorderFragment2", "视频初始化到数据库开始。。。。");
                initHudPlayList(HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO);
                initHudPlayList(HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO);
                HaloLogger.logE("HudCarRecorderFragment2", "视频初始化到数据库。。。。结束");
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mRecorderStatus = CARRECORDER_STATE_BASE;
        getActivity().unregisterReceiver(videoReceiver);
    }

    private void initSurfaceView() {
        ViewGroup localViewGroup = (ViewGroup) mRecorderSurfaceView.getParent();
        if (localViewGroup != null) {
            localViewGroup.removeView(mRecorderSurfaceView);
        }
        mFlVideoContainer.addView(mRecorderSurfaceView);
        mLlSeekbarMicInfo.bringToFront();
    }

    private void setMediaPlayerCallback() {
        mMedia = mRecorderPlayer.getMedia();
        mMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                HaloLogger.logE("HudCarRecorderFragment2", "视频播放完毕");
                if (!mPlayingByPhone) {
                    nextVideo();
                }
            }
        });

        mMedia.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                HaloLogger.logE("HudCarRecorderFragment2", "播放出错");
                if (!mPlayingByPhone) {
                    nextVideo();
                }
                return true;
            }
        });
    }

    // 进入播放视频页面
    public void enterPlayPage(HudRecorderVideoType hudRecorderVideoType) {
        setMediaPlayerCallback();
        switch (hudRecorderVideoType) {
            case LOOPING_VIDEO:
                mVideoType = HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO;
                mTvPrompt.setText(getActivity().getApplicationContext().getResources().getString(R.string.carrecorder_play_loop_order));
                break;
            case LOCKED_VIDEO:
                mVideoType = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
                mTvPrompt.setText(getActivity().getApplicationContext().getResources().getString(R.string.carrecorder_play_lock_order));
                break;
            case WONDERFUL_VIDEO:
                mVideoType = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
                mTvPrompt.setText(getActivity().getApplicationContext().getResources().getString(R.string.carrecorder_play_lock_order));
                break;
            default:
                return;
        }
        if (mController.getVideoNumber(mVideoType) <= 0) {
            return;
        }
        on_offScanWindow(false);
        mRlScan.setVisibility(View.GONE);
        mRlPlay.setVisibility(View.VISIBLE);
        mLlSeekbarMicInfo.bringToFront();
        firstPlay();
        mRecorderStatus = CARRECORDER_STATE_PLAYING;
    }

    // 从播放页面返回预览界面
    public void returnScanPage() {
        mRecorderStatus = CARRECORDER_STATE_SCANING;
        mRecorderPlayer.stopVideo();
        ViewGroup parent = (ViewGroup) mRecorderSurfaceView.getParent();
        if (parent != null) {
            parent.removeView(mRecorderSurfaceView);
        }
        mFlVideoContainer.addView(mRecorderSurfaceView);
        mIndexOfPlayingVideo = 0;
        mVideoType = null;
        mRlPlay.setVisibility(View.INVISIBLE);
        mRlScan.setVisibility(View.VISIBLE);
        on_offScanWindow(true);//是否有摄像头
    }

    private void firstPlay() {
        int videoNumber = mController.getVideoNumber(mVideoType);
        if (videoNumber <= 0) {
            HaloLogger.logE("HudCarRecorderFragment", "视频类型不存在或无该类视频");
            return;
        }

        List<VideoBean> hudVideoList = mController.getHudVideoList(mVideoType, 0, videoNumber);
        if (hudVideoList == null || hudVideoList.size() <= 0) {
            return;
        }
        mVideoBeanList.addAll(hudVideoList);
        VideoBean videoBean = mVideoBeanList.get(0);
        if (videoBean == null) {
            return;
        }
        mNewVideoTime.setAlpha(1f);
        mNewVideoDate.setAlpha(1f);
        setVideoInfomation(videoBean, null);
        mRecorderPlayer.playVideo(videoBean.videoPath);
        setCurrentPosition();
        mPlayingByPhone = false;
        mIndexOfPlayingVideo = 0;
    }

    private void setCurrentPosition() {
        mMedia = mController.getRecorderPlayer().getMedia();
        timer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mMedia != null) {
                    try {
                        int currentPosition = mMedia.getCurrentPosition();
                        String text = CarRecorderFileUtils.milliseconds2NormalTime(currentPosition);
                        Message message = mHandler.obtainMessage();
                        message.what = PLAY_TIME_UPDATE_MARK;
                        message.obj = text;
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(mTimerTask, 0, 100);
    }

    public void enterScanPage() {
        mRecorderStatus = CARRECORDER_STATE_SCANING;
        mRlPlay.setVisibility(View.INVISIBLE);
        mRlScan.setVisibility(View.VISIBLE);
        on_offScanWindow(true);//是否有摄像头
    }

    public void deleteVideo() {
        mRecorderStatus = CARRECORDER_STATE_DELETING;
        if (mIndexOfPlayingVideo < 0 || !mDeleteMark) {
            return;
        }
        mRecorderPlayer.pauseVideo();
    }

    public void deleteSure() {
        mRecorderStatus = CARRECORDER_STATE_PLAYING;
        mDeleteMark = false;
        mRecorderPlayer.stopVideo();
        VideoBean videoBean = mVideoBeanList.get(mIndexOfPlayingVideo);
        if (mController.deleteVideo(videoBean.id)) {
            Intent intent = new Intent("com.digissin.delevideo");
            intent.putExtra("path", videoBean.videoPath);
            getActivity().sendBroadcast(intent);
            if (mIndexOfPlayingVideo < mVideoBeanList.size() && mIndexOfPlayingVideo >= 0) {
                nextVideo();
                mVideoBeanList.remove(mIndexOfPlayingVideo);
            }
        }
    }

    public void deleteCancel() {
        mRecorderStatus = CARRECORDER_STATE_PLAYING;
        mRecorderPlayer.resumeVideo();
    }

    public void fastBackward() {
        if ((mRecorderPlayer != null) && (mController != null)) {
            mRecorderPlayer.fastBackward();
        }
    }

    public void fastForward() {
        if ((mRecorderPlayer != null) && (mController != null)) {
            mRecorderPlayer.fastForward();
        }
    }

    public void pauseVideo() {
        if ((mRecorderPlayer != null) && (mController != null)) {
            mRecorderPlayer.pauseVideo();
        }
    }

    public void resume() {
        if (mRecorderPlayer != null && mController != null) {
            mRecorderPlayer.resumeVideo();
        }
    }

    public void lockVideo() {
        // 加锁不能够仅仅更改标记，还需要把该视频放到最前面去。
        VideoBean videoBean = mVideoBeanList.get(mIndexOfPlayingVideo);
        if (!CarRecorderDBHelper.LOCK_VIDEO.equals(videoBean.videoType)) {
            mController.lockVideo(videoBean.id);
            showLockImage(true);
        }
    }


    public void nextVideo() {
        if (mImageAnimating) {
            return;
        }

        if (mIndexOfPlayingVideo >= mVideoBeanList.size() - 1) {
            // Toast.makeText(getActivity(), "已是最后一个视频", Toast.LENGTH_SHORT).show();
            return;
        }
        mIvImageLock.setAlpha(0f);
        mRecorderPlayer.stopVideo();
        mFlVideoContainer.setVisibility(View.INVISIBLE);
        for (ImageView image : mImageList) {
            image.setVisibility(View.VISIBLE);
            image.bringToFront();
        }
        VideoBean oldBean = mVideoBeanList.get(mIndexOfPlayingVideo);
        final VideoBean newBean = mVideoBeanList.get(mIndexOfPlayingVideo + 1);
        mImageList.get(1).setImageBitmap(getImageFromVideoBean(oldBean, 510, 280));
        mImageList.get(2).setImageBitmap(getImageFromVideoBean(newBean, 510, 280));

        playPageAnimation(oldBean, newBean, true);
        ObjectAnimator UpAnimation = HudAMapAnimation.getNextVideoAnimation(mImageList.get(1), getActivity());
        UpAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImageAnimating = true;
                mRecorderPlayer.playVideo(newBean.videoPath);
                mIndexOfPlayingVideo++;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ImageView removeImage = mImageList.remove(0);
                removeImage.setTranslationY(DisplayUtil.dip2px(getActivity(), 374f) + removeImage.getTranslationY());
                mImageList.add(removeImage);

                mImageAnimating = false;
                mFlVideoContainer.setVisibility(View.VISIBLE);
                mFlVideoContainer.bringToFront();

                for (ImageView image : mImageList) {
                    image.setImageBitmap(null);
                    image.setVisibility(View.GONE);
                }
                mDeleteMark = true;

                if (newBean.videoType.equalsIgnoreCase(CarRecorderDBHelper.LOCK_VIDEO)) {
                    showLockImage(true);
                }
            }
        });
        UpAnimation.start();
        HudAMapAnimation.getNextVideoAnimation(mImageList.get(2), getActivity()).start();
    }

    private Bitmap getImageFromVideoBean(VideoBean bean, float pixelW, float pixelH) {
        Cursor cursor = mController.findVideoInfomation(BaseColumns._ID, bean.id, CarRecorderDBHelper.THUMB_BYTE_ARRAY);
        if (!cursor.moveToNext()) {
            return null;
        }
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(CarRecorderDBHelper.THUMB_BYTE_ARRAY));
        cursor.close();

        if (bytes != null && bytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = (pixelW / w);
            float scaleHeight = (pixelH / h);
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        } else {
            return null;
        }
    }

    /**
     * 信息更换动画
     *
     * @param oldBean 前一个视频
     * @param newBean 即将播放的视频bean
     * @param isNext  上一个或者下一个
     */
    private void playPageAnimation(VideoBean oldBean, VideoBean newBean, boolean isNext) {
        setVideoInfomation(newBean, oldBean);
        float translation = isNext ? -100f : 100f;
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLlVideoInfo, "TranslationY", 0f, translation);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLlVideoInfo, "Alpha", 1f, 0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mLlVideoInfoBack, "TranslationY", -translation, 0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mLlVideoInfoBack, "Alpha", 0f, 1f);
        set.playTogether(animator, animator1, animator2, animator3);
        set.setDuration(1000);
        set.start();
    }

    // 设置播放视频的信息
    private void setVideoInfomation(VideoBean newBean, VideoBean oldBean) {
        String newname = newBean.videoName;
        try {
            String date = newname.substring(9, 13) + "." + newname.substring(13, 15) + "." + newname.substring(15, 17);
            mNewVideoDate.setText(date);
            String time = CarRecorderFileUtils.timeTransform(newBean.startTime) + "—" + CarRecorderFileUtils.timeTransform(newBean.startTime + newBean.videoDuration);
            mNewVideoTime.setText(time);
            String size = CarRecorderFileUtils.byte2M(newBean.videoSize);
            mNewVideoSize.setText(size);
        } catch (Exception e) {
            e.printStackTrace();
            HaloLogger.logE("HudCarRecorderFragment", "从名字获取的信息出错:" + e.getMessage());
        }
        String totalTime = "-" + CarRecorderFileUtils.milliseconds2NormalTime(newBean.videoDuration);
        mTvTotaltime.setText(totalTime);

        if (oldBean == null) {
            mOldVideoTime.setText("");
            mOldVideoDate.setText("");
            mOldVideoSize.setText("");
        } else {
            String name = oldBean.videoName;
            try {
                String date = name.substring(9, 13) + "." + name.substring(13, 15) + "." + name.substring(15, 17);
                mOldVideoDate.setText(date);
                String time = CarRecorderFileUtils.timeTransform(oldBean.startTime) + "—" + CarRecorderFileUtils.timeTransform(oldBean.startTime + oldBean.videoDuration);
                mOldVideoTime.setText(time);
                mOldVideoSize.setText(CarRecorderFileUtils.byte2M(oldBean.videoSize));
            } catch (Exception e) {
                e.printStackTrace();
                HaloLogger.logE("HudCarRecorderFragment", "从名字获取的信息出错:" + e.getMessage());
            }
        }
    }

    public void previousVideo() {
        HaloLogger.logE("HudCarRecorderFragment", "previousVideo");
        if (mImageAnimating) {
            return;
        }

        if (mIndexOfPlayingVideo <= 0) {
            // Toast.makeText(getActivity(), "已是第一个视频", Toast.LENGTH_SHORT).show();
            return;
        }

        mIvImageLock.setAlpha(0f);
        mRecorderPlayer.stopVideo();
        mFlVideoContainer.setVisibility(View.INVISIBLE);

        for (ImageView image : mImageList) {
            image.setVisibility(View.VISIBLE);
            image.bringToFront();
        }
        VideoBean oldBean = mVideoBeanList.get(mIndexOfPlayingVideo);
        final VideoBean newBean = mVideoBeanList.get(mIndexOfPlayingVideo - 1);
        mImageList.get(1).setImageBitmap(getImageFromVideoBean(oldBean, 510, 280));
        mImageList.get(0).setImageBitmap(getImageFromVideoBean(newBean, 510, 280));

        playPageAnimation(oldBean, newBean, false);
        ObjectAnimator DownAnimation = HudAMapAnimation.getCarRecorderPreVideoAnimation(mImageList.get(1), getActivity());
        DownAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImageAnimating = true;
                mRecorderPlayer.playVideo(newBean.videoPath);
                mIndexOfPlayingVideo--;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ImageView removeImage = mImageList.remove(2);
                removeImage.setTranslationY(removeImage.getTranslationY() - DisplayUtil.dip2px(getActivity(), 374f));
                mImageList.add(0, removeImage);

                mImageAnimating = false;
                mFlVideoContainer.setVisibility(View.VISIBLE);
                mFlVideoContainer.bringToFront();

                for (ImageView image : mImageList) {
                    image.setImageBitmap(null);
                    image.setVisibility(View.GONE);
                }

                if (newBean.videoType.equalsIgnoreCase(CarRecorderDBHelper.LOCK_VIDEO)) {
                    showLockImage(true);
                }
            }
        });
        DownAnimation.start();
        HudAMapAnimation.getCarRecorderPreVideoAnimation(mImageList.get(0), getActivity()).start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            exit();
            HudEndPointConstants.IS_CARCORD = false;
            HudIOConstants.MusicPauseStat = 0;
        } else {
            HudEndPointConstants.IS_CARCORD = true;
            initSurfaceView();
        }
    }


    // 退出时调用
    public void exit() {
        HaloLogger.logE("HudCarRecorderFragment", "exit()");
        mRecorderStatus = CARRECORDER_STATE_BASE;
        mRecorderPlayer.stopVideo();
        ViewGroup parent = (ViewGroup) mRecorderSurfaceView.getParent();
        if (parent != null) {
            parent.removeView(mRecorderSurfaceView);
        }
        on_offScanWindow(false);
        mDeleteMark = true;
        mIndexOfPlayingVideo = 0;
        mVideoBeanList.clear();
        mVideoType = null;
        mRlPlay.setVisibility(View.INVISIBLE);
        mRlScan.setVisibility(View.INVISIBLE);
        if (timer != null) {
            timer.cancel();
            mTimerTask.cancel();
            timer = null;
            mTimerTask = null;
        }
    }


    public void playingByPhone(String videoPath, int currentIndex) {
        mVideoBeanList.clear();
        if (videoPath.contains(CarRecorderDBHelper.LOOP_VIDEO)) {
            mVideoType = HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO;
        } else if (videoPath.contains(CarRecorderDBHelper.LOCK_VIDEO)) {
            mVideoType = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
        }
        mRecorderPlayer.stopVideo();
        mRlPlay.setVisibility(View.VISIBLE);
        mRlScan.setVisibility(View.INVISIBLE);
        mRecorderPlayer.playVideo(videoPath);
        mPlayingByPhone = true;
        mNewVideoTime.setAlpha(1f);
        mNewVideoDate.setAlpha(1f);

        List<VideoBean> hudVideoList = mController.getHudVideoList(mVideoType, currentIndex, 1);
        if (hudVideoList != null && hudVideoList.size() > 0) {
            setVideoInfomation(hudVideoList.get(0), null);
        }
        // TODO: 2016/8/23 手机播放完毕之后交给语音控制未完善
    }

    // 暂时不能废弃
    public int getVideoNumber(HudRecorderVideoType hudRecorderVideoType) {
        HudCarcorderConstants.VideoTypeEnum VideoType;
        switch (hudRecorderVideoType) {
            case LOOPING_VIDEO:
                VideoType = HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO;
                break;
            case LOCKED_VIDEO:
                VideoType = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
                break;
            case WONDERFUL_VIDEO:
                VideoType = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
                break;
            default:
                return -1;
        }
        return mController.getVideoNumber(VideoType);
    }

    private void showLockImage(boolean show) {
        if (show) {
            mIvImageLock.bringToFront();
            ObjectAnimator animator = ObjectAnimator.ofFloat(mIvImageLock, "alpha", 0f, 1f);
            animator.setDuration(250);
            animator.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mIvImageLock, "alpha", 1f, 0f);
            animator.setDuration(250);
            animator.start();
        }
    }

    /**
     * 打开或关闭预览窗口
     *
     * @param open 打开或者关闭
     */
    public void on_offScanWindow(boolean open) {
        if (HudConstants.IS_CAMERA) {
            if (open) {
                Intent intentopen = new Intent("intent.action.ACTION_OPEN_CAMERA_PREVIEW_WINDOW");
                getActivity().sendBroadcast(intentopen);
            } else {
                Intent intent = new Intent("intent.action.ACTION_CLOSE_CAMERA_PREVIEW_WINDOW");
                getActivity().sendBroadcast(intent);
            }
        } else {
                mCameraView = CameraView.getSingleInstance(getActivity());
                if (open) {
                    if (mCameraView != null) {
                        if (mCameraView != null && mCameraView.getParent() != null) {
                            ViewGroup parent = (ViewGroup) mCameraView.getParent();
                            parent.removeView(mCameraView);
                        }
                        mScanContainer.addView(mCameraView);
                    }

                } else {
                    if (mCameraView != null && mCameraView.getParent() != null) {
                        ViewGroup parent = (ViewGroup) mCameraView.getParent();
                        parent.removeView(mCameraView);
                    }
                }
        }
    }

    public int getRecorderStatus() {
        return mRecorderStatus;
    }


    /*--------------------------初始化视频到数据库中开始--------------------------*/

    /**
     * 刷新hud端用的视频列表
     *
     * @param videoType 视频类型
     */
    public void initHudPlayList(HudCarcorderConstants.VideoTypeEnum videoType) {
        String path;
        switch (videoType) {
            case LOCKEDVIDEO:
                path = HudCarcorderConstants.LOCKEDVIDEOPATH;
                File file = new File(path);
                if (!file.exists() || file.isFile()) {
                    return;
                }
                initVideoBeanList(path);
                break;
            case LOOPINGVIDEO:
                path = HudCarcorderConstants.LOOPINGVIDEOPATH;
                File loopfile = new File(path);
                if (!loopfile.exists() || loopfile.isFile()) {
                    return;
                }
                initVideoBeanList(path);
                break;
            default:
                Log.e("HudCarcorderController", "没有该类视频");
                break;
        }
    }


    /**
     * 初始化所有的某个类型的视频到list列表中
     *
     * @param path 路径
     */
    private void initVideoBeanList(String path) {
        List<String> pathList = CarRecorderFileUtils.getPathList(path);
        if (pathList == null) {
            Log.e("HudCarcorderController", "刷新视频类型失败");
            return;
        }

        CarRecorderDBHelper helper = CarRecorderDBHelper.getSingleCarRecorderDBHelper(getActivity());
        for (String videopath : pathList) {
            String name = new File(videopath).getName();
            Cursor cursor = helper.findVideoInfo(CarRecorderDBHelper.VIDEO_NAME, name, BaseColumns._ID);
            if (!cursor.moveToNext()) {
                VideoBean videoBean = getVideoBean(videopath);
                helper.insert(videoBean.videoName, videoBean.videoSize, videoBean.videoDuration, videoBean.startTime, videoBean.endTime, videoBean.thumbBytes, videoBean.videoPath, videoBean.videoType);
            }
            cursor.close();
        }
    }


    /**
     * 视频路径转成bean
     *
     * @param videoPath the path of the video
     * @return bean
     */
    public VideoBean getVideoBean(String videoPath) {
        VideoBean bean = new VideoBean();
        File file = new File(videoPath);
        bean.videoName = file.getName();
        bean.videoPath = file.getAbsolutePath();
        bean.videoDuration = getDurationFromVideo(videoPath);
        bean.videoSize = file.length();
        bean.startTime = CarRecorderFileUtils.getVideoStartTime(bean.videoName);
        bean.endTime = CarRecorderFileUtils.getVideoStartTime(bean.videoName) + getDurationFromVideo(videoPath);
        bean.thumbBytes = CarRecorderFileUtils.getImageByteArray(videoPath);
        Log.e("HudCarcorderController", "初始化一个视频信息到数据库中，请耐心等待");
        bean.videoType = file.getAbsolutePath().contains("protect") ? "protect" : "front";
        return bean;
    }


    /**
     * 耗时的获取视频时长的方法：MediaMetadataRetriever
     *
     * @param videoPath the path of the video
     * @return long
     */
    private long getDurationFromVideo(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        long l = -1;
        try {
            mmr.setDataSource(videoPath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            l = Long.valueOf(duration);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            mmr.release();
            return l;
        }
    }
    /*--------------------------初始化视频到数据库中结束--------------------------*/

    /*---------------------开启视频录制----------------------*/
    public void openVideoRecording() {
        Intent intent = new Intent("intent.action.ACTION_START_RECORD");
        getActivity().sendBroadcast(intent);


    }

    public void closeVideoRecording() {
        Intent intent = new Intent("intent.action.ACTION_STOP_RECORD");
        getActivity().sendBroadcast(intent);
    }
    /*---------------------关闭视频录制----------------------*/

}
