package com.haloai.hud.hudendpoint.fragments.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.primarylauncher.HUDApplication;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名称：primarylauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/5/17 21:23
 * 修改人：zhang
 * 修改时间：2016/5/17 21:23
 * 修改备注：
 */
public class SpeechReceiverView extends RelativeLayout {

    private Context mContext;
    private static final int SPEECHVIEW_RECEIVE_VOLUME = 0;
    private static final int SPEECHVIEW_GESTURE_TIMER = 1;
    private static final int SPEECHVIEW_REVEST = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SPEECHVIEW_RECEIVE_VOLUME:
                    setVolumeRing((int) msg.obj);
                    break;
                case SPEECHVIEW_GESTURE_TIMER:
                    if (HudEndPointConstants.showReceiver) {
                        mIvMicrophone.setVisibility(View.VISIBLE);
                    } else {
                        mIvMicrophone.setVisibility(View.GONE);
                    }
                    mIvGestureIcon.setAlpha(0f);
                    // 需要把外面blackboard隐藏掉。
                    if (mOnHideBlackboardListener != null) {
                        mOnHideBlackboardListener.onHideBlackboard();
                    }

                    displayFakeReceiver(false);
                    mIvFakeRecoginizeRing.setAlpha(1f);
                    mShowCenterGestureTimer.cancel();
                    break;
                case SPEECHVIEW_REVEST:
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvGreen.getLayoutParams();
                    params.leftMargin = DisplayUtil.dip2px(mContext, -28f);
                    mIvGreen.setLayoutParams(params);

                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mIvBlack.getLayoutParams();
                    param.leftMargin = DisplayUtil.dip2px(mContext, -25.5f);
                    mIvBlack.setLayoutParams(param);
                    break;
            }
        }
    };
    private ImageView mIvFakeRecoginizeRing, mIvGestureIcon, mIvGreen, mIvBlack, mIvOuterRing, mIvInnerRing, mIvUpCircle, mIvDownCircle, mIvRecognize, mIvReceive, mIvMicrophone, mIvLeftLine, mIvRightLine;
    private RelativeLayout mRlFakeReceiver;
    private boolean isAnimat = false;
    private boolean isHomeAnimation = false;
    private boolean isInNavi = false;
    private boolean isLoading = false;

    private int volume = 0;
    private int previousvolume = 0;
    private boolean volumeFlag = false;
    public static boolean TTSvolumeFlag = true;
    private AnimatorSet recoginizeSet;

    public SpeechReceiverView(Context context) {
        super(context);
        mContext = context;
    }

    public SpeechReceiverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_speechreceiver, this);
        mIvGreen = (ImageView) findViewById(R.id.view_greencircle);
        mIvBlack = (ImageView) findViewById(R.id.view_blackcircle);
        mIvInnerRing = (ImageView) findViewById(R.id.view_innerring);
        mIvOuterRing = (ImageView) findViewById(R.id.view_outerring);
        mIvUpCircle = (ImageView) findViewById(R.id.view_up);
        mIvDownCircle = (ImageView) findViewById(R.id.view_down);
        mIvRecognize = (ImageView) findViewById(R.id.view_recognize);
        mIvReceive = (ImageView) findViewById(R.id.view_receiver);
        mIvMicrophone = (ImageView) findViewById(R.id.view_microphone);
        mIvLeftLine = (ImageView) findViewById(R.id.view_leftline);
        mIvRightLine = (ImageView) findViewById(R.id.view_rightline);
        mIvGestureIcon = (ImageView) findViewById(R.id.view_gesture_icon);
        mIvFakeRecoginizeRing = (ImageView) findViewById(R.id.view_fake_recognize_ring);
        mRlFakeReceiver = (RelativeLayout) findViewById(R.id.view_fake_receiver);
        new Thread() {
            @Override
            public void run() {
                // 每隔50毫秒就去重新设置当前的volume，和previousvolume。
                while (true) {
                    int currentvolume = volume;
                    if (previousvolume == currentvolume) {
                        SystemClock.sleep(50);
                    } else {
                        for (int i = 0; i < 5; i++) {
                            SystemClock.sleep(10);
                            Message message = handler.obtainMessage();
                            message.what = SPEECHVIEW_RECEIVE_VOLUME;
                            message.obj = (int) (previousvolume + (currentvolume - previousvolume) / 5.0f * (i + 1));
                            message.sendToTarget();
                        }
                        previousvolume = currentvolume;
                    }
                }
            }
        }.start();
    }

    private AnimatorSet mHomeAnimatorSet1 = null;


    /**
     * 第一阶段动画
     */
    public void homeAnimatorFirsPeriod() {
        resetView();
        mIvDownCircle.setVisibility(View.GONE);
        mIvUpCircle.setVisibility(View.GONE);
        mIvOuterRing.setAlpha(0f);
        mIvMicrophone.setAlpha(0f);
        mIvInnerRing.setAlpha(0f);
        mIvUpCircle.setAlpha(0f);
        mIvDownCircle.setAlpha(0f);
        mIvReceive.setVisibility(View.INVISIBLE);
        mIvReceive.setAlpha(0f);
        mIvRecognize.setAlpha(0f);
        mIvLeftLine.setAlpha(0f);
        mIvRightLine.setAlpha(0f);
        mIvBlack.setAlpha(1f);
        mIvGreen.setAlpha(1f);
        mHomeAnimatorSet1 = new AnimatorSet();
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvGreen, "translationX",  0f ,DisplayUtil.dip2px(HUDApplication.getInstance(), 80));
        animator3.setDuration(200);

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvBlack, "translationX",   0f ,DisplayUtil.dip2px(HUDApplication.getInstance(), 80));
        animator4.setDuration(200);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvBlack, "scaleX", 1f, 0.8f);
        animator5.setDuration(200);

        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvBlack, "scaleY", 1f, 0.8f);
        animator6.setDuration(200);

        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mIvGreen, "scaleX", 1f, 1.2f);
        animator7.setDuration(200);

        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mIvGreen, "scaleY", 1f, 1.2f);
        animator8.setDuration(200);

        mHomeAnimatorSet1.setInterpolator(new AccelerateDecelerateInterpolator());
        mHomeAnimatorSet1.playTogether(animator3, animator4, animator5, animator6, animator7, animator8);
        mHomeAnimatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimat = true;
                isHomeAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimat = false;
                homeAnimatorSecondPeriod();
                handler.sendEmptyMessage(SPEECHVIEW_REVEST);
            }
        });
        mHomeAnimatorSet1.start();
    }

    private AnimatorSet mHomeAnimatorSet2 = null;

    /**
     * 第二阶段动画
     */
    private void homeAnimatorSecondPeriod() {
        // 1、圆圈变淡，  2、显示innerring,并且扩大
//        mIvInnerRing.setAlpha(1f);
        mHomeAnimatorSet2 = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvGreen, "alpha", 1f, 0.2f, 0.1f, 0.05f, 0f);
        animator1.setDuration(400);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvInnerRing, "scaleX", 1f, 5.5f, 5.5f);
        animator2.setDuration(400);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvInnerRing, "scaleY", 1f, 5.5f, 5.5f);
        animator3.setDuration(400);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvInnerRing, "alpha", 1f, 0.8f, 0f);
        animator2.setDuration(400);

        mHomeAnimatorSet2.playTogether(animator1, animator2, animator3, animator4);
        mHomeAnimatorSet2.setInterpolator(new AccelerateDecelerateInterpolator());
        mHomeAnimatorSet2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimat = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimat = false;
                homeAnimatorThirdPeriod();
            }
        });
        mHomeAnimatorSet2.start();
    }

    private AnimatorSet mHomeAnimatorSet3 = null;

    /**
     * 第三阶段动画
     */
    private void homeAnimatorThirdPeriod() {
        // 1、显示外圈，并且变小，
        // 2、显示上下半弧，上下两个半弧落下来。
        // 3、中间的话筒开始变大，同时透明度变亮。
        // 4、blackring设置不可见
        mIvBlack.setAlpha(0f);
        mIvOuterRing.setAlpha(1f);
        mIvUpCircle.setVisibility(View.VISIBLE);
        mIvUpCircle.setAlpha(1f);
        mIvDownCircle.setVisibility(View.VISIBLE);
        mIvDownCircle.setAlpha(1f);

        mHomeAnimatorSet3 = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleX", 1.5f, 1f);
        animator1.setDuration(400);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleY", 1.5f, 1f);
        animator2.setDuration(400);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvUpCircle, "scaleX", 0.1f, 1f);
        animator3.setDuration(400);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvUpCircle, "scaleY", 0.1f, 1f);
        animator4.setDuration(400);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleX", 0.1f, 1f);
        animator5.setDuration(400);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleY", 0.1f, 1f);
        animator6.setDuration(400);


        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mIvUpCircle, "translationY", 0f, DisplayUtil.dip2px(mContext, 18f));
        animator7.setDuration(400);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mIvDownCircle, "translationY", 0f, DisplayUtil.dip2px(mContext, -18f));
        animator8.setDuration(400);

        ObjectAnimator animator9 = ObjectAnimator.ofFloat(mIvMicrophone, "alpha", 0f, 1f);
        animator9.setDuration(400);
        ObjectAnimator animator10 = ObjectAnimator.ofFloat(mIvLeftLine, "alpha", 0f, 1f);
        animator10.setDuration(400);
        ObjectAnimator animator11 = ObjectAnimator.ofFloat(mIvRightLine, "alpha", 0f, 1f);
        animator11.setDuration(400);


        mHomeAnimatorSet3.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7, animator8, animator9, animator10, animator11);
        mHomeAnimatorSet3.setInterpolator(new LinearInterpolator());
        mHomeAnimatorSet3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimat = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimat = false;
                isHomeAnimation = false;
                mIvUpCircle.setAlpha(0f);
                mIvDownCircle.setAlpha(0f);
                mIvDownCircle.setVisibility(View.GONE);
                mIvUpCircle.setVisibility(View.GONE);
                mIvRecognize.setAlpha(1f);
            }
        });
        mHomeAnimatorSet3.start();
    }

    // 设置音量环的大小， 每隔10毫秒被内部调用一次。
    private void setVolumeRing(int volume) {
        float scaleValue;
        if (volume <= 25) {
            mIvReceive.setAlpha(0f);
            mIvMicrophone.setAlpha(0.3f);
            return;
        } else if (volume <= 75 && volume > 25) {
            scaleValue = 0.3f + 0.7f * (volume - 25) / 50f;
        } else {
            scaleValue = 1f;
        }
        mIvReceive.setScaleX(scaleValue);
        mIvReceive.setScaleY(scaleValue);
        mIvReceive.setAlpha(scaleValue);
        mIvMicrophone.setAlpha(scaleValue);
    }

    /**
     * 识别开始，
     */
    public void startRecoginizeAnimation() {
        // 内圈消失， 收音圈消失，识别圈开始旋转
        mIvReceive.setVisibility(View.GONE);
        mIvReceive.setAlpha(0f);
        mIvMicrophone.setAlpha(1f);
        mIvMicrophone.setVisibility(View.VISIBLE);
        mIvOuterRing.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mIvMicrophone, "alpha", 0.99f, 1f).setDuration(1200).start();
        ObjectAnimator.ofFloat(mIvOuterRing, "alpha", 0.99f, 1f).setDuration(1200).start();
        mIvRecognize.setVisibility(View.VISIBLE);
        mIvDownCircle.setVisibility(View.GONE);
        mIvUpCircle.setVisibility(View.GONE);
        mIvLeftLine.setAlpha(1f);
        mIvRightLine.setAlpha(1f);
        recoginizeSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvRecognize, "rotation", 0f, -360f);
        animator.setDuration(700);
        animator.setInterpolator(new LinearInterpolator());

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvRecognize, "rotation", 0f, 360f);
        animator2.setDuration(700);
        animator2.setRepeatCount(-1);
        animator2.setInterpolator(new LinearInterpolator());

        recoginizeSet.playSequentially(animator, animator2);
        recoginizeSet.setInterpolator(new LinearInterpolator());
        recoginizeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimat = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimat = false;
            }
        });
        recoginizeSet.start();
    }

    /**
     * receiver消失动画
     */
    public void receiverDisappearAnimation() {
        mIvMicrophone.setAlpha(0f);
        mIvReceive.setAlpha(0f);
        mIvRecognize.setAlpha(1f);
        mIvUpCircle.setAlpha(0f);
        mIvDownCircle.setAlpha(0f);
        mIvDownCircle.setVisibility(View.GONE);
        mIvUpCircle.setVisibility(View.GONE);
        AnimatorSet set6 = new AnimatorSet();
        // 识别圈变得越来越慢，等一段时间之后变淡消失， 收音桶在短时间内变淡消失，外圆圈逐渐变小。
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvRecognize, "rotation", 0f, 180f, 270f, 315f, 337f, 349f, 355f, 358f, 360f);
        animator1.setDuration(800);
        animator1.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvRecognize, "alpha", 1f, 1f, 1f, 1f, 1f, 1f, 0.6f, 0.4f, 0f);
        animator2.setDuration(800);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvMicrophone, "alpha", 1f, 1f, 1f, 1f, 1f, 1f, 0.6f, 0.4f, 0f);
        animator3.setDuration(800);

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleX", 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.75f, 0.5f, 0.3f, 0.3f, 0.3f, 0.15f, 0f);
        animator4.setDuration(1200);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleY", 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.75f, 0.5f, 0.3f, 0.3f, 0.3f, 0.15f, 0f);
        animator5.setDuration(1200);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvOuterRing, "alpha", 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.75f, 0.5f, 0.3f, 0.3f, 0.3f, 0.15f, 0f);
        animator6.setDuration(1200);

        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mIvLeftLine, "alpha", 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.5f, 0.2f, 0.1f, 0f);
        animator7.setDuration(800);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mIvRightLine, "alpha", 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.5f, 0.2f, 0.1f, 0f);
        animator8.setDuration(800);

        set6.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7, animator8);
        set6.setInterpolator(new LinearInterpolator());
        set6.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimat = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimat = false;
                mIvMicrophone.setVisibility(View.INVISIBLE);
            }
        });
        set6.start();
    }

    /**
     * 恢复到首页状态，准备好做首页动画
     */
    public void recoverHomeState() {
        if (isHomeAnimation) {
            // 立马让动画停止
            if (mHomeAnimatorSet1 != null) {
                mHomeAnimatorSet1.pause();
            }
            if (mHomeAnimatorSet2 != null) {
                mHomeAnimatorSet2.pause();
            }
            if (mHomeAnimatorSet3 != null) {
                mHomeAnimatorSet3.pause();
            }
            isAnimat = false;
            isHomeAnimation = false;
        }
        mIvDownCircle.setVisibility(View.GONE);
        mIvUpCircle.setVisibility(View.GONE);
        volumeFlag = false;
        mIvOuterRing.setAlpha(0f);
        mIvMicrophone.setAlpha(0f);
        mIvInnerRing.setAlpha(0f);
        mIvMicrophone.setVisibility(View.INVISIBLE);
        mIvBlack.setAlpha(1f);
        mIvUpCircle.setAlpha(0f);
        mIvDownCircle.setAlpha(0f);
        mIvReceive.setVisibility(View.INVISIBLE);
        mIvReceive.setAlpha(0f);
        mIvRecognize.setAlpha(0f);
        mIvLeftLine.setAlpha(0f);
        mIvRightLine.setAlpha(0f);
        AnimatorSet Set00 = new AnimatorSet();
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvGreen, "translationX", 0f, DisplayUtil.dip2px(HUDApplication.getInstance(), -80));
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvBlack, "translationX", 0f, DisplayUtil.dip2px(HUDApplication.getInstance(), -80));
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvBlack, "scaleX", 1f, 1.25f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvBlack, "scaleY", 1f, 1.25f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mIvGreen, "scaleX", 1f, 0.83333f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mIvGreen, "scaleY", 1f, 0.83333f);

        ObjectAnimator animator9 = ObjectAnimator.ofFloat(mIvGreen, "alpha", 0f, 1f);

        ObjectAnimator animator16 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleX", 1f, 0.2f);
        ObjectAnimator animator17 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleY", 1f, 0.2f);

        Set00.setInterpolator(new AccelerateDecelerateInterpolator());
        Set00.playTogether(animator3, animator4, animator5, animator6, animator7, animator8, animator9, animator16, animator17/*, animator18, animator19*/);
        Set00.start();
        navi2Home();
    }

    public void startHomeAnimator(boolean isGestureCause) {
        if (!isGestureCause) {
            mIvMicrophone.setVisibility(View.VISIBLE);
            mIvReceive.setVisibility(View.VISIBLE);
        }
        mIvMicrophone.setAlpha(0f);
        mIvReceive.setAlpha(0f);
        if (isAnimat) {
            return;
        }
        if (fakeReceiverDisplayStatus) {
            // 假的reciver存在，直接显示
            displayReciver();
            return;
        }
        volumeFlag = true;
        homeAnimatorFirsPeriod();
        isInNavi = false;
    }

    /**
     * 设置外界音量值
     *
     * @param volume
     */
    public void setvolume(int volume) {
     /*   HaloLogger.logE("zhangyong", "volumeFlag:" + volumeFlag + "---------TTSvolumeFlag:" + TTSvolumeFlag + "---------isLoading:" + isLoading + "---------isAnimat:" + isAnimat);
        boolean b = volumeFlag && TTSvolumeFlag && !isLoading && !isAnimat;*/
        if (volumeFlag && TTSvolumeFlag && !isLoading && !isAnimat) {
            // 记录，在本应该传送volume的时段，volume却没有被传送。再来判断是那个值出了问题
            // 如何判断本应该传送volume的时段。感觉可能是loading或者isAnimat出了问题。也就是loading中完了没解除loading，很有可能。
            this.volume = volume;
        } else {
            this.volume = 0;
        }
        /*HaloLogger.logE("zhangyong", "外界传入的音量值:" + this.volume);*/
    }

    public void startLoading() {
        if (mIvRecognize.getAlpha() <= 0) {
            return;
        }
        displayReciver();
        if (isAnimat || isLoading) {
            return;
        }
        startRecoginizeAnimation();
        isLoading = true;
    }

    public void stopLoading() {
        if (recoginizeSet == null || !isLoading) {
            return;
        }
        recoginizeSet.end();
        isLoading = false;
        mIvMicrophone.setAlpha(1f);
        mIvReceive.setVisibility(View.VISIBLE);
    }

    public void enterNavi() {
        if (isAnimat || isInNavi) {
            return;
        }
        volumeFlag = false;
        receiverDisappearAnimation();
        isInNavi = true;
        mIvMicrophone.setAlpha(0f);
        mIvReceive.setAlpha(0f);
    }


    public void navi2SecondMenu() {
        if (isAnimat) {
            return;
        }
        recoverHomeState();
        navi2Home();
        homeAnimatorFirsPeriod();
        volumeFlag = true;
        mIvMicrophone.setVisibility(View.VISIBLE);
        mIvMicrophone.setAlpha(0f);
        mIvReceive.setVisibility(View.VISIBLE);
        mIvReceive.setAlpha(0f);
    }


    public void navi2Home() {
        isInNavi = false;
        AnimatorSet set22 = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleX", 0f, 1f);
        animator1.setDuration(0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleY", 0f, 1f);
        animator2.setDuration(0);

        set22.playTogether(animator1, animator2);
        set22.start();
    }

    public void resetView(){
        AnimatorSet Set00 = new AnimatorSet();
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvGreen, "translationX", 0f, DisplayUtil.dip2px(HUDApplication.getInstance(), -80));
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvBlack, "translationX", 0f, DisplayUtil.dip2px(HUDApplication.getInstance(), -80));
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvBlack, "scaleX", 1f, 1.25f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvBlack, "scaleY", 1f, 1.25f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mIvGreen, "scaleX", 1f, 0.83333f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mIvGreen, "scaleY", 1f, 0.83333f);

        ObjectAnimator animator16 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleX", 1f, 0.2f);
        ObjectAnimator animator17 = ObjectAnimator.ofFloat(mIvDownCircle, "scaleY", 1f, 0.2f);

        Set00.setInterpolator(new AccelerateDecelerateInterpolator());
        Set00.playTogether(animator3, animator4, animator5, animator6, animator7, animator8, animator16, animator17/*, animator18, animator19*/);
        Set00.start();
        AnimatorSet set22 = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleX", 0f, 1f);
        animator1.setDuration(0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvOuterRing, "scaleY", 0f, 1f);
        animator2.setDuration(0);
        set22.playTogether(animator1, animator2);
        set22.start();

    }

    public void displayReciver() {
        mIvOuterRing.setVisibility(View.VISIBLE);
        mIvOuterRing.setAlpha(1f);
        mIvMicrophone.setVisibility(View.VISIBLE);
        mIvMicrophone.setAlpha(1f);
        mIvRightLine.setAlpha(1f);
        mIvLeftLine.setAlpha(1f);
        mIvRightLine.setAlpha(1f);
        mIvReceive.setVisibility(View.VISIBLE);
        mIvRecognize.setAlpha(1f);
    }

    public void hideReciver() {
        // 由于在执行hide之前会有最后一个音量的输入，从而导致后续的
        mIvMicrophone.setVisibility(View.GONE);
        mIvReceive.setVisibility(View.GONE);
        mIvOuterRing.setAlpha(0f);
        mIvMicrophone.setAlpha(0f);
        mIvLeftLine.setAlpha(0f);
        mIvRightLine.setAlpha(0f);
        mIvRecognize.setAlpha(0f);
        mIvInnerRing.setAlpha(0f);
    }


    private Timer mShowCenterGestureTimer;
    private ShowCenterGestureIconTimerTask mShowCenterIconTimerTask;
    private int centerGestureIconId;

    /**
     * 显示手势图标，yes, no, 音量调节
     *
     * @param id
     */
    public void displayGestureIcon(int id) {
        centerGestureIconId = id;
        mShowCenterGestureTimer = new Timer(true);
        mIvMicrophone.setVisibility(View.GONE);
        mIvGestureIcon.setAlpha(1f);
        mIvGestureIcon.setImageResource(id);
        showCenterGestureIconTimer();
    }

    private boolean fakeReceiverDisplayStatus = false;    // 设立标记，如果假的还在，真的要出现的时候，那么就应该直接出现

    public void displayFakeReceiver(Boolean show) {
        fakeReceiverDisplayStatus = show;
        mRlFakeReceiver.setAlpha(show ? 1f : 0f);
    }

    public void setReceiverDisplayStatus(boolean show){
        fakeReceiverDisplayStatus = show;
        mRlFakeReceiver.setAlpha(0f);
    }

    public void displayGesturePoint(int id){
        centerGestureIconId = id;
        mIvMicrophone.setVisibility(View.GONE);
        mIvGestureIcon.setAlpha(1f);
        mIvReceive.setVisibility(View.GONE);
        mIvGestureIcon.setImageResource(id);
    }

    public void showMicView(){
        mIvReceive.setVisibility(View.VISIBLE);;
        mIvMicrophone.setVisibility(View.VISIBLE);
    }

    /**
     * 识别失败
     */
   /* public void gestureFailure() {
        ObjectAnimator.ofFloat(mIvGestureRecoginizeFailure, "alpha", 0f, 1f).setDuration(500).start();

        mShowCenterGestureTimer = new Timer(true);
        showCenterGestureIconTimer();
    }*/

    // 计时器。
    public void showCenterGestureIconTimer() {
        if (mShowCenterGestureTimer != null) {
            if (mShowCenterIconTimerTask != null) {
                mShowCenterIconTimerTask.cancel();  //将原任务从队列中移除
            }

            mShowCenterIconTimerTask = new ShowCenterGestureIconTimerTask();  // 新建一个任务
            mShowCenterGestureTimer.schedule(mShowCenterIconTimerTask, 1500);
        }
    }

    class ShowCenterGestureIconTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!isVolumeRingDisplaying || centerGestureIconId != R.drawable.soundview_gesture_volume_icon) {
                handler.sendEmptyMessage(SPEECHVIEW_GESTURE_TIMER);
            }
        }
    }

    /*新添加的标记，为了防止音量图标比音量环先消失而设立的标记。*/
    public static boolean isVolumeRingDisplaying = false;

    public void disappearCenterVolumeIcon() {
        // 如果当前存留的图片是音量图片，那么就让音量图片消失
        if (centerGestureIconId == R.drawable.soundview_gesture_volume_icon) {
            handler.sendEmptyMessage(SPEECHVIEW_GESTURE_TIMER);
        }
    }

    // 为了配合识别圈变成带箭头的圈，提供方法让识别圈消失
    public void hideRecoginizeCircle(boolean hideState) {
        mIvRecognize.setAlpha(hideState ? 0f : 1f);
        //同时也要把假的识别环消失
        mIvFakeRecoginizeRing.setAlpha(0f);
    }

    //
    public interface OnHideBlackboardListener {
        void onHideBlackboard();
    }

    private OnHideBlackboardListener mOnHideBlackboardListener;

    public void setOnHideBlackboardListener(OnHideBlackboardListener listener) {
        mOnHideBlackboardListener = listener;
    }

}

