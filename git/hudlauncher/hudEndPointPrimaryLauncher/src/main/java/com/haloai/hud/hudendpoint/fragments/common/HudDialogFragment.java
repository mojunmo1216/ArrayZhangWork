package com.haloai.hud.hudendpoint.fragments.common;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.fragments.view.PathView;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.HaloLogger;

/**
 * Created by zhangrui on 16/7/20.
 */
public class HudDialogFragment extends Fragment {

    private RelativeLayout mRlIncomingView,mNaviInfoView;
//    private ImageView mIvSexangle, mIvLargesex, mIvSmallsex, mIvPhone;
//    private ImageView mIvPointer, mIvPointer2, mIvPointer3, mIvPointer4, mIvPointer5, mIvPointer6, mIvPointer7, mIvLine;
//    private ImageView mIvCover, mIvPointer00, mIvPointer01, mIvPointer02, mIvPointer03, mIvPointer04, mIvPointer05, mIvPointer06, mIvLine0, mIvLeftBlack, mIvRightBlack;

    //导航指示
//    private ImageView mNaviInstructionImageview;
//    private TextView mNaviDistanceTextView,mNaviRoadTextview;

    //导航指示
    private ImageView mNaviInstructionImageview;
    private TextView mNaviDistanceTextView,mNaviRoadTextview,mNaviActionTextView;


    private RelativeLayout mCall_Ring_Panel;
    private TextView mNameTextView;
    private TextView mSubtitleTextView;
    private ImageView mGetCallIcon;
    private ImageView mStopCallIcon;
    private ImageView mStopCallIcon_Center;


    private LinearLayout mReceiveCallLL;
    private LinearLayout mStopCallLL;
    private LinearLayout mStopCallLL_Center;

    /*private RelativeLayout mContainer;
    private RelativeLayout mRightBiuContainer;
    private AnimatorSet mDiappearSet;
    private boolean biubiuFlag = false;
    private boolean isAnimatingFlag = false;
    private TextView mTvCenterText, mTvRightText, mTvLeftText, mTvOutgoingText;
    private ImageView mIvRightIcon, mIvLeftIcon;
    private PathView mPathView;
    private PathView mPathView2;
    public final int START_BIU = 1000;
    public final int START_TEXT_ANIMATION = 1001;
    public final int START_ANSWER_REFUSE = 1002;
    public final int START_LINE_ALPHA_ANIMATION = 1003;
    public final int START_LINE_ALPHA_ANIMATION2 = 1004;
    public final int START_RIGHT_BIU = 1005;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_BIU:
                    startRightBiuAnimator(mIvPointer, 1300, 1, 1f, 0.4f);
                    startRightBiuAnimator(mIvPointer2, 1270, 30, 0.9f, 0.03f);
                    startRightBiuAnimator(mIvPointer3, 1240, 60, 0.8f, 0.03f);
                    startRightBiuAnimator(mIvPointer4, 1210, 90, 0.7f, 0.03f);
                    startRightBiuAnimator(mIvPointer5, 1180, 120, 0.6f, 0.03f);
                    startRightBiuAnimator(mIvPointer6, 1150, 150, 0.5f, 0.03f);
                    startRightBiuAnimator(mIvPointer7, 1120, 180, 0.4f, 0.03f);
                    startRightLineAnimator(mIvLine, 1150, 150, 0.6f, 0.2f);

                    startLeftBiuAnimator(mIvPointer00, 1300, 1, 1f, 0.4f);
                    startLeftBiuAnimator(mIvPointer01, 1270, 30, 0.9f, 0.03f);
                    startLeftBiuAnimator(mIvPointer02, 1240, 60, 0.8f, 0.03f);
                    startLeftBiuAnimator(mIvPointer03, 1210, 90, 0.7f, 0.03f);
                    startLeftBiuAnimator(mIvPointer04, 1180, 120, 0.6f, 0.03f);
                    startLeftBiuAnimator(mIvPointer05, 1150, 150, 0.5f, 0.03f);
                    startLeftBiuAnimator(mIvPointer06, 1120, 180, 0.4f, 0.03f);
                    startLeftLineAnimator(mIvLine0, 1150, 150, 0.6f, 0.2f);
                    break;
                case START_RIGHT_BIU:
                    startRightBiuAnimator(mIvPointer, 1300, 1, 1f, 0.4f);
                    startRightBiuAnimator(mIvPointer2, 1270, 30, 0.9f, 0.03f);
                    startRightBiuAnimator(mIvPointer3, 1240, 60, 0.8f, 0.03f);
                    startRightBiuAnimator(mIvPointer4, 1210, 90, 0.7f, 0.03f);
                    startRightBiuAnimator(mIvPointer5, 1180, 120, 0.6f, 0.03f);
                    startRightBiuAnimator(mIvPointer6, 1150, 150, 0.5f, 0.03f);
                    startRightBiuAnimator(mIvPointer7, 1120, 180, 0.4f, 0.03f);
                    startRightLineAnimator(mIvLine, 1150, 150, 0.6f, 0.2f);
                    break;
                case START_TEXT_ANIMATION:
                    String name = (String) msg.obj;
                    mTvCenterText.setText(name);
                    blackboardAnimation();
                    break;
                case START_ANSWER_REFUSE:
                    answerRefuseAnimaation();
                    break;
                case START_LINE_ALPHA_ANIMATION:
                    ObjectAnimator.ofFloat(mPathView, "alpha", 0.7f, 0.5f, 0.4f, 0.4f, 0.3f,0.2f, 0f).setDuration(1000).start();
                    break;
                case START_LINE_ALPHA_ANIMATION2:
                    ObjectAnimator.ofFloat(mPathView2, "alpha", 0.7f, 0.5f, 0.4f, 0.4f, 0.3f,0.2f, 0f).setDuration(1000).start();
                    break;
            }
        }
    };*/
    //private float mFactor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRlIncomingView = (RelativeLayout) View.inflate(getActivity(), R.layout.fragment_amap_dialog, null);
        mCall_Ring_Panel = (RelativeLayout)mRlIncomingView.findViewById(R.id.caller_main_ring_panel_viewgroup);
        mNameTextView = (TextView)mCall_Ring_Panel.findViewById(R.id.caller_name_textview);
        mSubtitleTextView = (TextView)mCall_Ring_Panel.findViewById(R.id.sub_title_textview);
        mGetCallIcon = (ImageView)mCall_Ring_Panel.findViewById(R.id.ring_call_icon);
        mStopCallIcon = (ImageView)mCall_Ring_Panel.findViewById(R.id.stop_call_icon);
        mStopCallIcon_Center = (ImageView)mCall_Ring_Panel.findViewById(R.id.stop_call_icon_Center);
        mReceiveCallLL = (LinearLayout)mCall_Ring_Panel.findViewById(R.id.receive_call_linerLayout);
        mStopCallLL = (LinearLayout)mCall_Ring_Panel.findViewById(R.id.stop_call_linerLayout);
        mStopCallLL_Center = (LinearLayout)mCall_Ring_Panel.findViewById(R.id.stop_call_linerLayout_center);

        //mRlIncomingView.addView(mCall_Ring_Panel);
        /*mIvCover = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_cover);

        mFactor = DisplayUtil.dip2px(getActivity(), 100f) / 150f;
        mPathView = (PathView) mRlIncomingView.findViewById(R.id.pathView);
        final Path path = makeConvexArrow1(81f, 81f);
        mPathView.setPath(path);
        mPathView2 = (PathView) mRlIncomingView.findViewById(R.id.pathView2);
        final Path path2 = makeConvexArrow2(81f, 81f);
        mPathView2.setPath(path2);

        mTvCenterText = (TextView) mRlIncomingView.findViewById(R.id.fragment_incoming_center_text);
        mTvOutgoingText = (TextView) mRlIncomingView.findViewById(R.id.fragment_incoming_outgoing_text);
        mIvSexangle = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_sexangle);
        mIvLargesex = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_largesex);
        mIvSmallsex = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_smallsex);
        mIvPhone = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_center_phone_icon);
        mIvPointer = (ImageView) mRlIncomingView.findViewById(R.id.pointer);
        mIvPointer2 = (ImageView) mRlIncomingView.findViewById(R.id.pointer2);
        mIvPointer3 = (ImageView) mRlIncomingView.findViewById(R.id.pointer3);
        mIvPointer4 = (ImageView) mRlIncomingView.findViewById(R.id.pointer4);
        mIvPointer5 = (ImageView) mRlIncomingView.findViewById(R.id.pointer5);
        mIvPointer6 = (ImageView) mRlIncomingView.findViewById(R.id.pointer6);
        mIvPointer7 = (ImageView) mRlIncomingView.findViewById(R.id.pointer7);
        mIvLine = (ImageView) mRlIncomingView.findViewById(R.id.line);
        mIvPointer00 = (ImageView) mRlIncomingView.findViewById(R.id.pointer00);
        mIvPointer01 = (ImageView) mRlIncomingView.findViewById(R.id.pointer01);
        mIvPointer02 = (ImageView) mRlIncomingView.findViewById(R.id.pointer02);
        mIvPointer03 = (ImageView) mRlIncomingView.findViewById(R.id.pointer03);
        mIvPointer04 = (ImageView) mRlIncomingView.findViewById(R.id.pointer04);
        mIvPointer05 = (ImageView) mRlIncomingView.findViewById(R.id.pointer05);
        mIvPointer06 = (ImageView) mRlIncomingView.findViewById(R.id.pointer06);
        mIvLine0 = (ImageView) mRlIncomingView.findViewById(R.id.line0);

        mTvRightText = (TextView) mRlIncomingView.findViewById(R.id.fragment_incoming_hangup_text);
        mTvLeftText = (TextView) mRlIncomingView.findViewById(R.id.fragment_incoming_anwser_text);
        mIvRightIcon = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_hangup_icon);
        mIvLeftIcon = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_anwser_icon);

        mIvLeftBlack = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_text_left_black);
        mIvRightBlack = (ImageView) mRlIncomingView.findViewById(R.id.fragment_incoming_text_right_black);
        mContainer = (RelativeLayout) mRlIncomingView.findViewById(R.id.fragment_incoming_part_container);
        mRightBiuContainer = (RelativeLayout) mRlIncomingView.findViewById(R.id.right_biubiu_container);*/

//        mNaviInfoView = (RelativeLayout) mRlIncomingView.findViewById(R.id.dialog_navi_info_frame);
//        mNaviInstructionImageview = (ImageView) mRlIncomingView.findViewById(R.id.dialog_navi_img);
//        mNaviDistanceTextView = (TextView) mRlIncomingView.findViewById(R.id.dialog_navi_text);
//        mNaviRoadTextview = (TextView) mRlIncomingView.findViewById(R.id.dialog_next_road_text);
        mNaviInfoView = (RelativeLayout) mRlIncomingView.findViewById(R.id.common_navigation_tips);
        mNaviInstructionImageview = (ImageView) mRlIncomingView.findViewById(R.id.next_road_direction_imageview);
        mNaviDistanceTextView = (TextView) mRlIncomingView.findViewById(R.id.next_road_distance_textview);
        mNaviRoadTextview = (TextView) mRlIncomingView.findViewById(R.id.next_road_name_textview);
        mNaviActionTextView =  (TextView) mRlIncomingView.findViewById(R.id.road_name_prefix_textview);

        return mRlIncomingView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mNaviInfoView.setVisibility(View.GONE);
            HudEndPointConstants.IS_CALLING = false;
        }else {
            HudEndPointConstants.IS_CALLING = true;
            if(HudStatusManager.HudCurrentStatus == HudStatusManager.HudStatus.NAVIGATING){
                mNaviInfoView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 更新导航的指示信息
     * */
    public void updateNaviInfo(NaviInfo naviInfo){
        int  instruction = naviInfo.getIconType();
        int distance = naviInfo.getCurStepRetainDistance();
        String roadname = naviInfo.getCurrentRoadName();
        HaloLogger.logE("speed_upgrad", "roadname:" + roadname);
        if (mNaviInstructionImageview != null) {
            int resouce = getInstructionDrawable(instruction);
            if (resouce > 0) {
                mNaviInstructionImageview.setImageResource(resouce);
            }
        }

        if (distance>=0) {
            String distanceText;
            if(distance<1000){
                distanceText=distance+"米";
            }else {
                distanceText=String.format("%.1f%s",distance/1000.0,"公里");
            }
            mNaviDistanceTextView.setText(distanceText);
        }else {
            mNaviDistanceTextView.setText("");
        }

        if (roadname !=null) {
//            String roadText = "沿"+roadname+"行驶";
//            mNaviRoadTextview.setText(roadText);
            mNaviRoadTextview.setText(roadname);
            if (roadname == "目的地"){
                mNaviActionTextView.setText("到达");
            }else {
                mNaviActionTextView.setText("进入");
            }
        }

    }

    /**
     * 获取指示资源
     * */
    public int getInstructionDrawable(int instruction){
        int result = 0;
        switch (instruction) {
            case 15:
                result = R.drawable.greenline_direction__icon_turn_dest;
                break;
            case 13:
                result = R.drawable.greenline_direction__icon_turn_service_area;
                break;
            case 14:
                result = R.drawable.greenline_direction__icon_turn_tollgate;
                break;
            case 16:
                result = R.drawable.greenline_direction__icon_turn_tunnel;
                break;
            case 10:
                result = R.drawable.greenline_direction__icon_turn_passing_point;
                break;
            case 24:
                // TODO 通过索道
                break;
            case 26:
                // TODO 通过通道、建筑物穿越通道
                break;
            case 17:
                // TODO 通过人行横道
                break;
            case 28:
                // TODO 通过游船路线
                break;
            case 1:
                // TODO 自车图标
                break;
            case 11:
                result = R.drawable.greenline_direction__icon_turn_ring;
                break;
            case 31:
                // TODO 通过阶梯
                break;
            case 2:
                result = R.drawable.greenline_direction__icon_turn_left;
                break;
            case 6:
                result = R.drawable.greenline_direction__icon_turn_left_back;
                break;
            case 4:
                result = R.drawable.greenline_direction__icon_turn_left_front;
                break;
            case 8:
                result = R.drawable.greenline_direction__icon_turn_back;
                break;
            case 23:
                // TODO 通过直梯
                break;
            case 0:
                // TODO 无定义
                break;
            case 12:
                result = R.drawable.greenline_direction__icon_turn_ring_out;
                break;
            case 18:
                // TODO 通过过街天桥
                break;
            case 21:
                // TODO 通过公园
                break;
            case 3:
                result = R.drawable.greenline_direction__icon_turn_right;
                break;
            case 7:
                result = R.drawable.greenline_direction__icon_turn_right_back;
                break;
            case 5:
                result = R.drawable.greenline_direction__icon_turn_right_front;
                break;
            case 29:
                // TODO 通过观光车路线
                break;
            case 25:
                // TODO 通过空中通道
                break;
            case 30:
                // TODO 通过滑道
                break;
            case 20:
                // TODO 通过广场
                break;
            case 22:
                // TODO 通过扶梯
                break;
            case 9:
                result = R.drawable.greenline_direction__icon_turn_front;
                break;
            case 19:
                // TODO 通过地下通道
                break;
            case 27:
                // TODO 通过行人道路
                break;
        }
        return result;
    }


//来电
    public void ringing(String contactName) {
        mNameTextView.setText(contactName);
        mSubtitleTextView.setText("来电");
        mGetCallIcon.setVisibility(View.VISIBLE);
        mReceiveCallLL.setVisibility(View.VISIBLE);
        mStopCallIcon.setVisibility(View.VISIBLE);
        mStopCallLL.setVisibility(View.VISIBLE);
        mStopCallIcon_Center.setVisibility(View.GONE);
        mStopCallLL_Center.setVisibility(View.GONE);
    }

//去电
    public void showSingleStatus(String name, String number){
        mNameTextView.setText(name);
        mSubtitleTextView.setText("去电");
        mGetCallIcon.setVisibility(View.GONE);
        mReceiveCallLL.setVisibility(View.GONE);
        mStopCallIcon.setVisibility(View.GONE);
        mStopCallLL.setVisibility(View.GONE);
        mStopCallIcon_Center.setVisibility(View.VISIBLE);
        mStopCallLL_Center.setVisibility(View.VISIBLE);
    }



/*    *//**
     * 来电动画开始接口
     * @param contactName
     *//*
    public void ringing(String contactName) {
        mTvRightText.setText("挂断");
        mTvLeftText.setText("接听");
        mIvRightIcon.setImageResource(R.drawable.call_hangup_icon);
        mIvLeftIcon.setImageResource(R.drawable.call_answer_icon);

        // 处理正在来电或者消失过程中突然来电
        if (mDiappearSet != null) {
            mDiappearSet.end();
        }
        disappearDialogInstant();

        String name = contactName + "  来电";
        displayDialog(name,true);
    }

    *//**
     * 确定取消窗口
     * @param centerText
     *//*
    public void showYesOrNoDialog(String centerText){
        // 设置两边的图片以及文字。
        mTvRightText.setText("取消");
        mTvLeftText.setText("确定");
        mIvRightIcon.setImageBitmap(createBallBitmap(Color.RED));
        mIvLeftIcon.setImageBitmap(createBallBitmap(Color.GREEN));
        if (mDiappearSet != null) {
            mDiappearSet.end();
        }
        disappearDialogInstant();
        displayDialog(centerText,false);
    }

    *//**
     * 显示去电窗口
     *//*
    public void showSingleStatus(String name, String number){
        mTvRightText.setText("挂断");
        mTvLeftText.setText("");
        mIvRightIcon.setImageResource(R.drawable.call_hangup_icon);
        mIvLeftIcon.setImageBitmap(null);
        mTvCenterText.setText("");
        mTvOutgoingText.setText(name + "    " + number);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTvOutgoingText, "alpha", 0f, 1f).setDuration(400);
        animator.start();
        handler.sendEmptyMessageDelayed(START_ANSWER_REFUSE, 0);
        startRightBiu();
        isAnimatingFlag = true;
    }

    *//**
     * 隐藏窗口
     *//*
    public void disappearDialogGradually() {
        if(!isAnimatingFlag){
            return;
        }

        mDiappearSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTvCenterText, "alpha", 1f, 0f);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mContainer, "alpha", 1f, 0f);
        animator2.setDuration(1000);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        mDiappearSet.playTogether(animator,animator2);
        mDiappearSet.start();
        mTvCenterText.setText("");
        biubiuFlag = false;
        isAnimatingFlag = false;
        mTvOutgoingText.setText("");
        ObjectAnimator.ofFloat(mTvOutgoingText, "alpha", 1f, 0f).setDuration(400).start();
    }

    public void disappearDialogInstant(){
        if(!isAnimatingFlag){
            return;
        }
        mTvOutgoingText.setText("");
        mTvOutgoingText.setAlpha(0f);
        mTvCenterText.setAlpha(0f);
        mContainer.setAlpha(0f);
        mTvCenterText.setText("");
        biubiuFlag = false;
        isAnimatingFlag = false;
    }

	*//*---------------------------------来电，确定取消动画start-------------------------------*//*
    private void displayDialog(String centerText,boolean isCall){
        if (isAnimatingFlag) {
            return;
        }
        mTvCenterText.setAlpha(1f);
        sexAngleAnimation(isCall);
        sixAngleGrowAnimation();
        startBiu();
        startTextAnimatioin(centerText);
        handler.sendEmptyMessageDelayed(START_ANSWER_REFUSE, 1200);// 1200毫秒后显示左右接听图标
        isAnimatingFlag = true;
    }

    private Bitmap createBallBitmap(int color){
        Bitmap bitmap = Bitmap.createBitmap(DisplayUtil.dip2px(getActivity(), 15), DisplayUtil.dip2px(getActivity(), 15), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawCircle(DisplayUtil.dip2px(getActivity(), 7),DisplayUtil.dip2px(getActivity(), 7),DisplayUtil.dip2px(getActivity(), 7),paint);
        return bitmap;
    }


    *//**
     * 文字开门动画
     *
     * @param name
     *//*
    private void startTextAnimatioin(final String name) {
        Message message = handler.obtainMessage();
        message.what = START_TEXT_ANIMATION;
        message.obj = name;
        handler.sendMessageDelayed(message, 800);
    }

    *//**
     * 文字左右挡板动画
     *//*
    private void blackboardAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLeftBlack, "TranslationX", 0, DisplayUtil.dip2px(getActivity(), -100));
        animator.setDuration(1300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvRightBlack, "TranslationX", 0, DisplayUtil.dip2px(getActivity(), 100));
        animator2.setDuration(1300);
        animator2.setInterpolator(new DecelerateInterpolator());
        animator2.start();
    }

    *//**
     * 显示两边的红色挂断和绿色接听图片动画
     *//*
    private void answerRefuseAnimaation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mContainer, "Alpha", 0f, 1f);
        animator.setDuration(900);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    *//**
     * 六边形动画(最里面的两个小六边形)
     *//*
    private void sexAngleAnimation(boolean isPhone) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator00 = ObjectAnimator.ofFloat(mIvSexangle, "ScaleX", 0, 0.5f, 0.85f, 1);
        animator00.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animator01 = ObjectAnimator.ofFloat(mIvSexangle, "ScaleY", 0, 0.5f, 0.85f, 1);
        animator01.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animator02 = ObjectAnimator.ofFloat(mIvSexangle, "Alpha", 1f, 0.7f, 0.65f, 0.6f, 0.6f, 0.45f, 0f);
        ObjectAnimator animator10 = ObjectAnimator.ofFloat(mIvLargesex, "ScaleX", 0f, 0.8f, 1);
        ObjectAnimator animator11 = ObjectAnimator.ofFloat(mIvLargesex, "ScaleY", 0f, 0.8f, 1);
        ObjectAnimator animator12 = ObjectAnimator.ofFloat(mIvLargesex, "Alpha", 0f, 0.35f, 0.1f, 0f);
        ObjectAnimator animator20 = ObjectAnimator.ofFloat(mIvSmallsex, "ScaleX", 0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.6f, 1, 1, 1, 1, 1, 1);
        ObjectAnimator animator21 = ObjectAnimator.ofFloat(mIvSmallsex, "ScaleY", 0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.6f, 1, 1, 1, 1, 1, 1);
        ObjectAnimator animator22 = ObjectAnimator.ofFloat(mIvSmallsex, "Alpha", 0f, 0.35f, 0.2f, 0f);
        if(isPhone){
            if(mIvPhone.getVisibility()==View.GONE) mIvPhone.setVisibility(View.VISIBLE);
        }else{
            if(mIvPhone.getVisibility()==View.VISIBLE) mIvPhone.setVisibility(View.GONE);
        }
        ObjectAnimator animator30 = ObjectAnimator.ofFloat(mIvPhone, "ScaleX", 0, 0.4f, 0.7f, 1);
        animator30.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animator31 = ObjectAnimator.ofFloat(mIvPhone, "ScaleY", 0f, 1);
        animator31.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animator32 = ObjectAnimator.ofFloat(mIvPhone, "Alpha", 1f, 1f, 0f);
        set.playTogether(animator00, animator01, animator02, animator10, animator11, animator12, animator20, animator21, animator22, animator30, animator31, animator32);
        set.setDuration(1500);
        set.start();
    }

    *//**
     * Biu动画
     *//*
    private void startBiu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(800);
                biubiuFlag = true;
                while (biubiuFlag) {
                    handler.sendEmptyMessage(START_BIU);
                    SystemClock.sleep(1500);
                }
            }
        }).start();
    }

    *//**
     * Biu动画
     *//*
    private void startRightBiu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(800);
                biubiuFlag = true;
                while (biubiuFlag) {
                    handler.sendEmptyMessage(START_RIGHT_BIU);
                    SystemClock.sleep(1500);
                }
            }
        }).start();
    }

    private void startRightBiuAnimator(ImageView image, long duration, long delay, float firstAlpha, float middleAlpha) {

        ObjectAnimator animator7 = ObjectAnimator.ofFloat(image, "TranslationX", 0f, 4* mFactor, 12* mFactor, 24* mFactor, 40* mFactor, 60* mFactor, 78* mFactor, 93* mFactor, 107* mFactor, 118* mFactor, 128* mFactor, 137* mFactor, 144* mFactor, 150.3f* mFactor, 155.7f* mFactor, 160.4f* mFactor, 164.4f* mFactor, 167.9f* mFactor, 170.8f* mFactor, 173.4f* mFactor, 175.7f* mFactor, 177.7f* mFactor, 179.5f* mFactor, 181f* mFactor, 182.5f* mFactor, 183.7f* mFactor, 184.9f* mFactor, 185.9f* mFactor, 186.8f* mFactor, 187.6f* mFactor, 188.2f* mFactor, 188.8f* mFactor, 189.2f* mFactor, 189.6f* mFactor, 189.8f* mFactor, 189.9f* mFactor, 190f* mFactor, 190f* mFactor);
        animator7.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator7.setStartDelay(delay);
        animator7.start();
        ObjectAnimator animator72 = ObjectAnimator.ofFloat(image, "ScaleX", 0.3f, 0.7f, 0.85f, 0.95f, 1f);
        animator72.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator72.setStartDelay(delay);
        animator72.start();
        ObjectAnimator animator73 = ObjectAnimator.ofFloat(image, "ScaleY", 0.3f, 0.7f, 0.85f, 0.95f, 1f);
        animator73.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator73.setStartDelay(delay);
        animator73.start();
        ObjectAnimator animator74 = ObjectAnimator.ofFloat(image, "Alpha", firstAlpha, middleAlpha, 0f);
        animator74.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator74.setStartDelay(delay);
        animator74.start();
    }

    private void startLeftBiuAnimator(ImageView image, long duration, long delay, float firstAlpha, float middleAlpha) {
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(image, "TranslationX", 0f, -4f*mFactor, -12f*mFactor, -24f*mFactor, -40f*mFactor, -60f*mFactor, -78f*mFactor, -93f*mFactor, -107f*mFactor, -118f*mFactor, -128f*mFactor, -137f*mFactor, -144f*mFactor, -150.3f*mFactor, -155.7f*mFactor, -160.4f*mFactor, -164.4f*mFactor, -167.9f*mFactor, -170.8f*mFactor, -173.4f*mFactor, -175.7f*mFactor, -177.7f*mFactor, -179.5f*mFactor, -181f*mFactor, -182.5f*mFactor, -183.7f*mFactor, -184.9f, -185.9f*mFactor, -186.8f*mFactor, -187.6f*mFactor, -188.2f*mFactor, -188.8f*mFactor, -189.2f, -189.6f*mFactor, -189.8f*mFactor, -189.9f*mFactor, -190f*mFactor, -190f*mFactor);
        animator7.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator7.setStartDelay(delay);
        animator7.start();
        ObjectAnimator animator72 = ObjectAnimator.ofFloat(image, "ScaleX", 0.3f, 0.7f, 0.85f, 0.95f, 1f);
        animator72.setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
        animator72.setStartDelay(delay);
        animator72.start();
        ObjectAnimator animator73 = ObjectAnimator.ofFloat(image, "ScaleY", 0.3f, 0.7f, 0.85f, 0.95f, 1f);
        animator73.setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
        animator73.setStartDelay(delay);
        animator73.start();
        ObjectAnimator animator74 = ObjectAnimator.ofFloat(image, "Alpha", firstAlpha, middleAlpha, 0f);
        animator74.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator74.setStartDelay(delay);
        animator74.start();
    }

    private void startRightLineAnimator(ImageView image, long duration, long delay, float firstAlpha, float middleAlpha) {
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(image, "TranslationX", 0, DisplayUtil.dip2px(getActivity(), 70f), DisplayUtil.dip2px(getActivity(), 90f), DisplayUtil.dip2px(getActivity(), 98.3f));
        animator7.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator7.setStartDelay(delay);
        animator7.start();
        ObjectAnimator animator72 = ObjectAnimator.ofFloat(image, "ScaleX", 0.3f, 0.8f, 1f, 1, 1);
        animator72.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator72.setStartDelay(delay);
        animator72.start();
        ObjectAnimator animator74 = ObjectAnimator.ofFloat(image, "Alpha", firstAlpha, middleAlpha, 0f);
        animator74.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator74.setStartDelay(delay);
        animator74.start();
    }

    private void startLeftLineAnimator(ImageView image, long duration, long delay, float firstAlpha, float middleAlpha) {
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(image, "TranslationX", 0, DisplayUtil.dip2px(getActivity(), -70f), DisplayUtil.dip2px(getActivity(), -90f), DisplayUtil.dip2px(getActivity(), -98.3f));
        animator7.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator7.setStartDelay(delay);
        animator7.start();
        ObjectAnimator animator72 = ObjectAnimator.ofFloat(image, "ScaleX", 0.3f, 0.8f, 1f, 1, 1);
        animator72.setDuration(duration).setInterpolator(new LinearInterpolator());
        animator72.setStartDelay(delay);
        animator72.start();
        ObjectAnimator animator74 = ObjectAnimator.ofFloat(image, "Alpha", firstAlpha, middleAlpha, 0f);
        animator74.setDuration(duration).setInterpolator(new DecelerateInterpolator());
        animator74.setStartDelay(delay);
        animator74.start();
    }


    *//**
     * 外圈六边形生长线性动画
     *//*
    private void sixAngleGrowAnimation(){
        mPathView.getPathAnimator().
                delay(350).
                duration(500).
                interpolator(new AccelerateDecelerateInterpolator()).
                start();
        handler.sendEmptyMessageDelayed(START_LINE_ALPHA_ANIMATION, 350);
        mPathView2.getPathAnimator().
                delay(400).
                duration(500).
                interpolator(new AccelerateDecelerateInterpolator()).
                start();
        handler.sendEmptyMessageDelayed(START_LINE_ALPHA_ANIMATION2, 400);
    }

    *//*制定六边形路径*//*
    private Path makeConvexArrow1(float length, float height) {
        final Path path = new Path();
        path.moveTo(length / 2f, 0.0f);
        path.lineTo(length - length / 10f, height / 4.0f);
        path.lineTo(length - length / 10f, 3 * height / 4.0f);
        path.lineTo(length / 2f, height);
        path.lineTo(length / 10f, 3 * height / 4f);
        path.lineTo(length / 10f, height / 4f);
        path.lineTo(length / 2f, 0.0f);
        return path;
    }
    *//*制定六边形路径*//*
    private Path makeConvexArrow2(float length, float height) {
        final Path path = new Path();
        path.moveTo(length / 2f, height);
        path.lineTo(length / 10f, 3 * height / 4f);
        path.lineTo(length / 10f, height / 4f);
        path.lineTo(length / 2f, 0.0f);
        path.lineTo(length - length / 10f, height / 4.0f);
        path.lineTo(length - length / 10f, 3 * height / 4.0f);
        return path;
    }

	*//*---------------------------------来电，确定取消动画end-------------------------------*/


}
