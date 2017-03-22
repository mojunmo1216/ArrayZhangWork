package com.haloai.hud.hudendpoint.fragments.common;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.fragments.view.SpeechReceiverView;
import com.haloai.hud.hudendpoint.fragments.view.VolumeRingView;
import com.haloai.hud.hudendpoint.primarylauncher.HaloAMapNaviStartActivity;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.protocol.HudPreferenceSetController;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangrui on 16/5/12.
 */
public class HudSpeechReceiverFragment extends Fragment {

    private static final String TAG = "HudSpeechReceiverFragment";

    private RelativeLayout mReceiverRootView;
    private SpeechReceiverView mSpeechReceiverView;
    private TextView mSpeechText;
    private final int UPGRADE_SPEECH_TEXT = 1;
    private int mMaxVolumeLevel;
    private HaloAMapNaviStartActivity mAMapNaviStartActivity;
    HudPreferenceSetController hudPreferenceSetController = HudPreferenceSetController.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  hudPreferenceSetController.init(getActivity().getApplication());
        mAMapNaviStartActivity=(HaloAMapNaviStartActivity)this.getActivity();
        mReceiverRootView = (RelativeLayout) View.inflate(getActivity(), R.layout.fragment_speechreceiver, null);
        mSpeechReceiverView = (SpeechReceiverView) mReceiverRootView.findViewById(R.id.speech_receiver_view);
        mSpeechText = (TextView) mReceiverRootView.findViewById(R.id.speech_panel_text);
        mFl = (FrameLayout) mReceiverRootView.findViewById(R.id.speech_receiver_voice_container);
        mVolumeRingView = new VolumeRingView(getActivity(), DisplayUtil.dip2px(getActivity(), 84), DisplayUtil.dip2px(getActivity(), 88));
        mTvVolumePercent = (TextView) mReceiverRootView.findViewById(R.id.speech_volume_percent);
        mIvVolumeRotateCircle = (ImageView) mReceiverRootView.findViewById(R.id.speech_volume_rotate_circle);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DisplayUtil.dip2px(getActivity(), 84), DisplayUtil.dip2px(getActivity(), 88));
        layoutParams.setMargins(0, 0, 0, 0);
        mVolumeRingView.setLayoutParams(layoutParams);
        mFl.addView(mVolumeRingView);
        mHandler.sendEmptyMessageDelayed(INIT_VULME_PARPMS,5000);
        return mReceiverRootView;
    }

    private void initVolumeParas() {
        HaloLogger.logE(TAG,"private void initVolumeParas");
        mMaxVolumeLevel = hudPreferenceSetController.getMaxVolume();
        // 设置MUSIC 和 ALARM音量一致
        setVolumeSame();

        mSpeechReceiverView.setOnHideBlackboardListener(new SpeechReceiverView.OnHideBlackboardListener() {
            @Override
            public void onHideBlackboard() {
                mAMapNaviStartActivity.navingShowHideDistancePanel(true);
                if(!EndpointsConstants.IS_PANEL&&(HudStatusManager.HudCurrentStatus== HudStatusManager.HudStatus.NAVIGATING||HudStatusManager.HudCurrentStatus== HudStatusManager.HudStatus.MAIN)&&
                        !HudEndPointConstants.IS_CARCORD&&!HudEndPointConstants.IS_OUTCALL&&!HudEndPointConstants.IS_CALLING){
                        mAMapNaviStartActivity.hideSpeechReceiver();
                }
            }
        });
    }

    private void setVolumeSame() {
        HaloLogger.logE(TAG,"private void setVolumeSame");
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (musicVolume == alarmVolume) {
            return;
        }
        int largestVolumeNumber = alarmVolume > musicVolume ? alarmVolume : musicVolume;
        hudPreferenceSetController.setVolumeValue(largestVolumeNumber);
    }


    public void homeAnimator(boolean isGestureCause) {
        HaloLogger.logE(TAG,"public void homeAnimator");
      /*  if (HudEndPointConstants.showReceiver) {
            return;
        }*/
        HudEndPointConstants.showReceiver = true;
        mSpeechReceiverView.startHomeAnimator(isGestureCause);
    }


    public void recoverHomeState() {
        HaloLogger.logE(TAG,"public void recoverHomeState");
        if(!EndpointsConstants.IS_PANEL) {
            mSpeechReceiverView.displayFakeReceiver(false);
            HudEndPointConstants.showReceiver = false;
            mSpeechText.setText("");
            mSpeechReceiverView.recoverHomeState();
        }
    }

    public void setVolume(int volume) {
        mSpeechReceiverView.setvolume(volume);
    }

    public void startLoading() {
        HaloLogger.logE(TAG,"public void startLoading");
        mSpeechReceiverView.startLoading();
    }

    public void stopLoading() {
        HaloLogger.logE(TAG,"public void stopLoading");
        mSpeechReceiverView.stopLoading();
    }

    // 菜单界面进入类似于导航界面的动画
    public void enterNavi() {
        HaloLogger.logE(TAG,"public void enterNavi__outside");
        if(!EndpointsConstants.IS_PANEL){
            HaloLogger.logE(TAG,"public void enterNavi__inside");

            HudEndPointConstants.showReceiver = false;
            mSpeechText.setText("");
            mSpeechReceiverView.enterNavi();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            HudEndPointConstants.showReceiver = false;
            mSpeechReceiverView.setReceiverDisplayStatus(false);
        }else {

        }
    }

    /**
     * 从诸如导航界面进入二三级菜单界面
     */
    public void naviToSecondMenu() {
        HaloLogger.logE(TAG,"public void naviToSecondMenu");
        HudEndPointConstants.showReceiver = true;
        mSpeechReceiverView.navi2SecondMenu();
    }

    public void upgradeSpeechText(String text) {
        HaloLogger.logE(TAG,"public void upgradeSpeechText");
        text = text.replace("你好光晕", "").replaceAll("你好.光晕", "").replace("恢复导航", "退出导航");
        mSpeechText.setText(text);
        mHandler.sendEmptyMessageDelayed(UPGRADE_SPEECH_TEXT, 1500);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPGRADE_SPEECH_TEXT:
                    mSpeechText.setText("");
                    break;
                case DISPLAY_VOLUME_RING_ANIMATION:
                    float volume = (float) msg.obj;
                    mVolumeRingView.setVolume(volume, volume - (20f * mMaxVolumeLevel) * (1f - mCurrentVolumeLevel / mMaxVolumeLevel), true);
                    break;
                case DISPLAY_VOLUME_RING_ANIMATION_REVERSE:
                    mVolumeRingView.setVolume((Float) msg.obj, (Float) msg.obj, false);
                    break;
                case VOLUME_ADD_OR_UP_ANIMATION:
                    mVolumeRingView.setVolume((20f * mMaxVolumeLevel), (float) msg.obj, true);
                    break;
                case DISAPPEAR_VOLUME_RING:
                    // 让音量环消失
                    mTvVolumePercent.setAlpha(0f);
                    mVolumeRingView.setAlpha(0f);
                    mIvVolumeRotateCircle.setAlpha(0f);
                    mVolumeRingView.setVolume(0, 0, true);
                    isVolumeRingDisplaying = false;
                    SpeechReceiverView.isVolumeRingDisplaying = false;
                    mSpeechReceiverView.disappearCenterVolumeIcon();
                    mShowVolumeRingTimer.cancel();
                    break;
                case DELAY_TIAOJIE_VOLUME:
                    // 立马去调节或者延迟500毫秒去调节音量
                    boolean isUp = (boolean) msg.obj;
                    controllVolumeAnimation(isUp);
                    break;
                case VOLUME_PERCENT_UPGRADE:
                    // 音量增加减少动画的末尾 再次显示最新的音量值
                    if (HudStatusManager.HudCurrentStatus != HudStatusManager.HudStatus.CHOOSE_STRAGETY) {
                        mCurrentVolumeLevel = mCurrentVolumeLevel > mMaxVolumeLevel ? mMaxVolumeLevel : mCurrentVolumeLevel;
                        mTvVolumePercent.setText(Math.round(mCurrentVolumeLevel * 100f / mMaxVolumeLevel) + "%");
                    }
                  //  mIvVolumeRotateCircle.setAlpha(0f);
                    if (HudEndPointConstants.showReceiver) {
                        mSpeechReceiverView.hideRecoginizeCircle(false);
                    }
                 //   mIvVolumeRotateCircle.setAlpha(0f);
                    break;
                case VOLUME_CIRCLE_ROTATE:
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mIvVolumeRotateCircle, "Rotation", 0, 360f*msg.arg1);
                    animator.setDuration(800);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                    break;
                case INIT_VULME_PARPMS:
                    initVolumeParas();
                    break;
            }
        }
    };

    public void setVolumeFlag(boolean flag) {
        SpeechReceiverView.TTSvolumeFlag = flag;
    }

    public void displayReciver(boolean display) {
        HaloLogger.logE(TAG,"public void displayReciver = "+display);
        if (display) {
            mSpeechReceiverView.displayReciver();
        } else {
            mSpeechReceiverView.hideReciver();
        }
    }

    /**
     * 唤醒
     */
    public void awakeGestureAction() {
        HaloLogger.logE(TAG,"public void awakeGestureAction");
        mSpeechReceiverView.displayGestureIcon(R.drawable.soundview_gesture_yes_icon);
        if (!HudEndPointConstants.showReceiver) {
            homeAnimator(false);
        }
    }


    /**
     * yes
     */
    public void showYesGestureIcon() {
        HaloLogger.logE(TAG,"public void showYesGestureIcon");
        mSpeechReceiverView.displayGestureIcon(R.drawable.soundview_gesture_yes_icon);
        mSpeechReceiverView.displayFakeReceiver(true);
    }

    public void showMicView(){
        mSpeechReceiverView.showMicView();
    }

    public void showPointGestureIcon(){
        mSpeechReceiverView.displayGesturePoint(R.drawable.soundview_gesture_point);
        mSpeechReceiverView.displayFakeReceiver(true);
    }

    public void displayGesturePoint(){
        mSpeechReceiverView.displayGestureIcon(R.drawable.soundview_gesture_point);
        mSpeechReceiverView.displayFakeReceiver(true);
    }
    /**
     * 显示左右滑动图标
     */
    public void showSlideGestureIcon() {
        HaloLogger.logE(TAG,"public void showSlideGestureIcon");
        mSpeechReceiverView.displayGestureIcon(R.drawable.soundview_gesture_no_icon);
        mSpeechReceiverView.displayFakeReceiver(true);
    }



    /**
     * 调高音量
     */
    public void upVolume() {
        HaloLogger.logE(TAG,"public void upVolume");
        controllVolumeAnimation(true);
        showVolumeGestureIcon();
    }

    /**
     * 调低音量
     */
    public void downVolume() {
        HaloLogger.logE(TAG,"public void downVolume");
        controllVolumeAnimation(false);
        showVolumeGestureIcon();
    }


    /**
     * 手势识别失败
     */



    /*----------------------------------------音量环start--------------------------------------------*/
    private FrameLayout mFl = null;
    private VolumeRingView mVolumeRingView = null;
    private float mCurrentVolumeLevel = 0;
    private boolean isVolumeRingDisplaying = false;
    private boolean isVolumeRingDisplayAnimating = false;
    public static final int DISPLAY_VOLUME_RING_ANIMATION = 200;
    public static final int DISPLAY_VOLUME_RING_ANIMATION_REVERSE = 201;
    public static final int VOLUME_ADD_OR_UP_ANIMATION = 202;
    public static final int DISAPPEAR_VOLUME_RING = 203;
    public static final int DELAY_TIAOJIE_VOLUME = 204;
    public static final int VOLUME_PERCENT_UPGRADE = 205;
    public static final int VOLUME_CIRCLE_ROTATE = 206;
    public static final int INIT_VULME_PARPMS = 207;
    private ShowVolumeRingTimerTask mShowVolumeRingTimerTask = null;
    private TextView mTvVolumePercent;
    private ImageView mIvVolumeRotateCircle;
    private Timer mShowVolumeRingTimer = null;



    public void controllVolumeAnimation(final boolean isUp) {
        HaloLogger.logE(TAG,"public void controllVolumeAnimation = " + isUp);
        mSpeechReceiverView.displayFakeReceiver(true);

        // 先判断音量环是否存在，如果存在，那么重新开始计时，并且音量加减动画，如果不存在，那么先显示，再做音量加减动画
        if (isVolumeRingDisplaying) {
            mShowVolumeRingTimer = new Timer();
            startVolumeRingTimer();
            // 做音量加减
            startVolumeReduceOrIncrease(isUp);
        } else {
            // 先显示音量环，再做音量加减动画
            displayVolumeRing(isUp);
        }
    }

    /**
     * 显示音量环
     */
    public void displayVolumeRing(final boolean isUp) {
        HaloLogger.logE(TAG,"public void displayVolumeRing = " + isUp);
        mCurrentVolumeLevel = hudPreferenceSetController.getVolumeValue();
        // 重新开始计时或者开始计时
        mShowVolumeRingTimer = new Timer();
        startVolumeRingTimer();
        // 如果音量环已经显示（isVolumeRingDisplaying为true），那么再执行显示音量环的操作，就无效
        if (isVolumeRingDisplaying) {
            return;// 但是又不能把timer给return掉，所以timer必须放在前面
        }
        mVolumeRingView.setAlpha(1f);
        isVolumeRingDisplaying = true;
        SpeechReceiverView.isVolumeRingDisplaying = true;
        // 直接显示音量百分值
        if (HudStatusManager.HudCurrentStatus != HudStatusManager.HudStatus.CHOOSE_STRAGETY) {
            mCurrentVolumeLevel = mCurrentVolumeLevel > mMaxVolumeLevel ? mMaxVolumeLevel : mCurrentVolumeLevel;
            mTvVolumePercent.setText(Math.round(mCurrentVolumeLevel * 100f / mMaxVolumeLevel) + "%");
            ObjectAnimator animator = ObjectAnimator.ofFloat(mTvVolumePercent, "alpha", 0f, 1f);
            animator.setDuration(500);
            animator.start();
        }

        if (isUp) {
            new Thread() {
                @Override
                public void run() {
                    isVolumeRingDisplayAnimating = true;

                    for (float i = 0; i <= (20f * mMaxVolumeLevel); i = i + 2) {
                        Message message = mHandler.obtainMessage();
                        message.what = DISPLAY_VOLUME_RING_ANIMATION;
                        message.obj = i;
                        mHandler.sendMessage(message);
                        SystemClock.sleep(3);
                    }
                    isVolumeRingDisplayAnimating = false;
                    Message message = mHandler.obtainMessage();
                    message.obj = isUp;
                    message.what = DELAY_TIAOJIE_VOLUME;
                    mHandler.sendMessage(message);
                }
            }.start();
        } else {
            mVolumeRingView.createCurrentVolumeRingBitmap(mCurrentVolumeLevel * 20);
            new Thread(){
                @Override
                public void run() {
                    for (float i = 20f * mMaxVolumeLevel; i >= 0; i = i - 2) {
                        Message message = mHandler.obtainMessage();
                        message.what = DISPLAY_VOLUME_RING_ANIMATION_REVERSE;
                        message.obj = i;
                        mHandler.sendMessage(message);
                        SystemClock.sleep(3);
                    }
                    isVolumeRingDisplayAnimating = false;
                    Message message = mHandler.obtainMessage();
                    message.obj = isUp;
                    message.what = DELAY_TIAOJIE_VOLUME;
                    mHandler.sendMessage(message);
                }
            }.start();
        }
    }


    /**
     * 同时做音量调节动画和实际调节音量。
     *
     * @param isUp
     */
    private void startVolumeReduceOrIncrease(final boolean isUp) {
        if(mCurrentVolumeLevel >=0 && mCurrentVolumeLevel <= mMaxVolumeLevel){
            setVolumeSame();
            hudPreferenceSetController.upDownVolumeValue(isUp); // 调节hud实际音量。
        }
        if (isVolumeRingDisplayAnimating) {
            //如果正在动画，那么就应该让这个动作延后执行，而不是被return掉。
            Message message = mHandler.obtainMessage();
            message.obj = isUp;
            message.what = DELAY_TIAOJIE_VOLUME;
            mHandler.sendMessageDelayed(message, 600);
        }

        // 执行一个动画，音量环逆时针或者顺时针旋转
        mSpeechReceiverView.hideRecoginizeCircle(true);
        mIvVolumeRotateCircle.setAlpha(1f);

        // 动画只能运行looper线程中
        Message msg = mHandler.obtainMessage();
        msg.what = VOLUME_CIRCLE_ROTATE;
        msg.arg1 = isUp ? 1 : -1 ;
        mHandler.sendMessage(msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (float i = 0; i < 20f; i++) {   // 音量共n个等级，每个等级分20等份。
                    float startVolume = mCurrentVolumeLevel * (mMaxVolumeLevel * 20) / mMaxVolumeLevel;
                    Message message = mHandler.obtainMessage();
                    message.what = VOLUME_ADD_OR_UP_ANIMATION;
                    if (isUp) {
                        message.obj = startVolume + i;
                    } else {
                        message.obj = startVolume - i;
                    }
                    message.sendToTarget();
                    SystemClock.sleep(5);
                }
                mHandler.sendEmptyMessage(VOLUME_PERCENT_UPGRADE);
                mCurrentVolumeLevel = isUp ? mCurrentVolumeLevel + 1 : mCurrentVolumeLevel - 1;
                mCurrentVolumeLevel = mCurrentVolumeLevel > mMaxVolumeLevel ? mMaxVolumeLevel : mCurrentVolumeLevel;
                mCurrentVolumeLevel = mCurrentVolumeLevel < 0 ? 0 : mCurrentVolumeLevel;
            }
        }).start();
    }

    // 计时器。;
    public void startVolumeRingTimer() {
        HaloLogger.logE(TAG,"public void startVolumeRingTimer");
        if (mShowVolumeRingTimer != null) {
            if (mShowVolumeRingTimerTask != null) {
                mShowVolumeRingTimerTask.cancel();  //将原任务从队列中移除
            }

            mShowVolumeRingTimerTask = new ShowVolumeRingTimerTask();  // 新建一个任务
            mShowVolumeRingTimer.schedule(mShowVolumeRingTimerTask, 1500);
        }
    }

    class ShowVolumeRingTimerTask extends TimerTask {
        @Override
        public void run() {
            // 子线程运行，所以更改ui的动作需要发送到主线程去运行
            // 发消息去隐藏音量环
            HaloLogger.logE(TAG,"mHandler.sendEmptyMessage(DISAPPEAR_VOLUME_RING);");
            mHandler.sendEmptyMessage(DISAPPEAR_VOLUME_RING);
        }
    }

    /**
     * 显示中间的音量图标
     */
    private void showVolumeGestureIcon() {
        HaloLogger.logE(TAG,"private void showVolumeGestureIcon");
        mSpeechReceiverView.displayGestureIcon(R.drawable.soundview_gesture_volume_icon);
    }

    /*----------------------------------------音量环end--------------------------------------------*/

}
