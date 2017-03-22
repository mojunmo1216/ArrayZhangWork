package com.haloai.hud.hudendpoint.fragments.common;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.speech.ISpeechParserMusic;

import java.util.List;

/**
 * Created by zhangrui on 16/5/30.
 */
public class HudStatusBarFragment extends Fragment {
    private RelativeLayout mMainStatusView, mMusicChangeView, mCallStatusView,mCallBgView;
    private LinearLayout mStatusBarView;
    private TextView mSongName, mSongTime,mCallTimeView,mGPSNum;
    private ImageView mWifiView, mMobileView, mBlueToothView, mMusicView, mGPSView , mFMView,mNoGestureView;
    private ImageView mRecordNo,mRecordPoint,mRecordBg;
    private List<ISpeechParserMusic.MusicInfo> mMusicInfoList;
    private final int offset = 16;//移动状态栏的偏移量
    private int statusCount = 0;//状态栏状态显示数量
    private int STATUS_MAVX_COUNT = 8 - 1;//状态栏状态总数量(行车记录仪图标一直存在)
    private ImageView[] mStatusViews;
    private ObjectAnimator mObjectAnimator, mRollAnimator;
    private static final int UPDATE_TALKING_TIME = 101;
    private boolean mIsTalking = false;
    private TalkThread mTalkThread;
    private int[] mGSMList = new int[]{R.drawable.status_4g_1,R.drawable.status_4g_2,R.drawable.status_4g_3,R.drawable.status_icon_4g};

    public enum HudStatusItemType {
        BLUETOOTH,
        WIFI,
        MOBILE,
        FM,
        GPS,
        NO_GESTURE,
        MUSIC,
        RECORD
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainStatusView = (RelativeLayout) inflater.inflate(R.layout.frament_status_bar, null);
        mMusicChangeView = (RelativeLayout) mMainStatusView.findViewById(R.id.status_music_layout);
        mStatusBarView = (LinearLayout) mMainStatusView.findViewById(R.id.status_bar_layout);
        mSongName = (TextView) mMainStatusView.findViewById(R.id.status_song_name);
        mSongTime = (TextView) mMainStatusView.findViewById(R.id.status_song_time);
        mWifiView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_wifi);
        mBlueToothView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_bluetooth);
        mMobileView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_4g);
        mFMView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_fm);
        mMusicView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_music);
        mGPSView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_gps);
        mCallStatusView = (RelativeLayout) mMainStatusView.findViewById(R.id.status_layout_call);
        mCallTimeView = (TextView) mMainStatusView.findViewById(R.id.status_text_incoming);
        mRecordPoint = (ImageView) mMainStatusView.findViewById(R.id.status_record_ok);
        mRecordNo = (ImageView) mMainStatusView.findViewById(R.id.status_record_no);
        mRecordBg = (ImageView) mMainStatusView.findViewById(R.id.status_record_bg);
    //    mRecordView = (ImageView) mMainStatusView.findViewById(R.id.status_bar_record);
        mNoGestureView =(ImageView) mMainStatusView.findViewById(R.id.status_bar_gesture);
        mCallBgView = (RelativeLayout) mMainStatusView.findViewById(R.id.status_bar_call_bg);
        mGPSNum = (TextView)mMainStatusView.findViewById(R.id.status_gps_num);
        mStatusViews = new ImageView[]{mBlueToothView, mWifiView, mMobileView, mFMView, mGPSView,mNoGestureView,mMusicView,mRecordNo};
        updateStatusBar(HudStatusItemType.GPS,true);
        return mMainStatusView;
    }


    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case UPDATE_TALKING_TIME:
                    mCallTimeView.setText(msg.obj.toString());
            }
        }
    };

    public void changeMusicStatus(int songIndex) {
        startMusicIconRolling();
        String songName = mMusicInfoList.get(songIndex).title;
        int duration = mMusicInfoList.get(songIndex).duration;
        String min = duration / 60 < 10 ? "0" + duration / 60 : "" + duration / 60;
        String second = duration % 60 < 10 ? "0" + duration % 60 : "" + duration % 60;
        String songTime = min + ":" + second;
        mSongName.setText(songName);
        mSongTime.setText(songTime);
        if (mObjectAnimator != null) mObjectAnimator.end();
        mObjectAnimator = HudAMapAnimation.startMusicChangeAnimation(mMusicChangeView, DisplayUtil.dip2px(getActivity(), -197));
    }

    //通过状态更新状态栏的图标
    public void updateStatusBar(HudStatusItemType hudStatusItemType, boolean isShow) {
        ImageView statusView = mStatusViews[hudStatusItemType.ordinal()];
        if (isShow && statusView.getVisibility() == View.GONE) {
            statusView.setVisibility(View.VISIBLE);
            statusCount++;
        } else if (!isShow && statusView.getVisibility() == View.VISIBLE) {
            if (hudStatusItemType == HudStatusItemType.MUSIC) {
                stopMusicIconRolling();
                if (mObjectAnimator != null) mObjectAnimator.end();
                mRollAnimator = null;
            }
            statusView.setVisibility(View.GONE);
            statusCount--;
        }
     //   HaloLogger.logE("HudStatusBarFragment", "statusCount" + statusCount+",HudStatusItemType:" + hudStatusItemType);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStatusBarView.getLayoutParams();
        layoutParams.leftMargin = DisplayUtil.dip2px(getActivity(), 51 + (STATUS_MAVX_COUNT - statusCount) * offset);
        mStatusBarView.setLayoutParams(layoutParams);
    }


    //更新GPS的状态,0 无 1-3 弱 3> 良好
    public void updateGPSNum(int num){
        if(num == 0){
            mGPSView.setImageResource(R.drawable.status_icon_gps0);
        } else if(num < 4 && num >0){
            mGPSView.setImageResource(R.drawable.status_icon_gps1);
        }else{
            mGPSView.setImageResource(R.drawable.status_icon_gps2);
        }
       // mGPSNum.setText(num+"");
    }

    public void updateRecord(boolean isRecord){
        if(isRecord){
            mRecordBg.setVisibility(View.VISIBLE);
            mRecordPoint.setVisibility(View.VISIBLE);
            mRecordNo.setVisibility(View.GONE);
            HudAMapAnimation.startRecording(mRecordPoint);
        }else {
            mRecordBg.setVisibility(View.GONE);
            mRecordPoint.setVisibility(View.GONE);
            mRecordNo.setVisibility(View.VISIBLE);

        }
    }



    public void setMsuicInfoList(List<ISpeechParserMusic.MusicInfo> musicInfoList) {
        this.mMusicInfoList = musicInfoList;
    }

    //开始音乐图标的动画
    public void startMusicIconRolling() {
        if (mRollAnimator == null) {
            mRollAnimator = HudAMapAnimation.startMusicIconRolling(mMusicView);
        } else {
            mRollAnimator.resume();
        }

    }

    //暂停音乐图标的动画
    public void stopMusicIconRolling() {
        if (mRollAnimator != null) {
            mRollAnimator.pause();
        }
    }

    public void GSMStateUpdate(int level){
        if(mMobileView.getVisibility() == View.VISIBLE){
            if(level >= 0 && level < mGSMList.length){
                mMobileView.setImageResource(mGSMList[level]);
            }
        }
    }

/*    Runnable musicRunable = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            int index= random.nextInt(4);
            mMusicView.setImageResource(mMusicList[index]);
            mHandler.postDelayed(musicRunable,1000);
        }
    };*/


    /**
     * start talking with somebody.
     *
     */
    public void startTalking(){
        if(!mIsTalking) {
            mIsTalking = true;
            mCallBgView.setAlpha(1f);
            HudAMapAnimation.startCallingAnimation(mCallStatusView, DisplayUtil.dip2px(getActivity(), -150));
            mTalkThread = new TalkThread();
            mTalkThread.start();
        }
        HaloLogger.logE("speech_info","startTalking");
    }

    /**
     * stop talking width somebody
     */
    public void stopTalking(){
        if(mIsTalking){
            mIsTalking = false;
            mCallBgView.setAlpha(0f);
            HudAMapAnimation.startCallingAnimation(mCallStatusView, DisplayUtil.dip2px(getActivity(), 150));
            mCallTimeView.setText("00:00:00");
        }
        HaloLogger.logE("speech_info","stopTalking");
    }
    /**
     * talk thread
     *
     */
    class TalkThread extends Thread{
        private int timeCount = 0;
        @Override
        public void run() {
            timeCount = 0;
            String timeStr;
            Message msg;
            int hour;
            int mint;
            int sec;
            while(mIsTalking){
                timeStr = "";
                timeCount++;
                hour = timeCount/60/60;
                mint = (timeCount - hour*60*60)/60;
                sec = timeCount%60;
                timeStr += hour>=10?hour+"":"0"+hour;
                timeStr+=":";
                timeStr += mint>=10?mint+"":"0"+mint;
                timeStr+=":";
                timeStr += sec>=10?sec+"":"0"+sec;
                msg = Message.obtain();
                msg.what = UPDATE_TALKING_TIME;
                msg.obj = timeStr;
                mHandler.sendMessage(msg);
                SystemClock.sleep(1000);
            }
        }
    }

}
