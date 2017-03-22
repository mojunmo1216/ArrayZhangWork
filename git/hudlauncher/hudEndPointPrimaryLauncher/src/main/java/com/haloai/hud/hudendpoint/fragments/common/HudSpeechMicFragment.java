package com.haloai.hud.hudendpoint.fragments.common;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.primarylauncher.R;

/**
 * Created by halo on 2016/11/19.
 */
public class HudSpeechMicFragment extends Fragment{

    private RelativeLayout mMainView,mSpeechView;
    private ImageView mCenterView , mLoadingView,mVlumeView,mVlumeRing,mVlumeLoading;
    private TextView mSpeechText,mVlumeText;
    private boolean isLoading = false;
    private ObjectAnimator mRollAnimator ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = (RelativeLayout)View.inflate(getActivity(),R.layout.fragment_speech_mic,null);
        mSpeechView = (RelativeLayout)mMainView.findViewById(R.id.speech_mic_view);
        mCenterView = (ImageView) mMainView.findViewById(R.id.mic_center_view);
        mLoadingView = (ImageView)mMainView.findViewById(R.id.mic_loding_ring);
        mVlumeRing = (ImageView)mMainView.findViewById(R.id.mic_vlume_ring);
        mSpeechText = (TextView)mMainView.findViewById(R.id.speech_panel_text);
        mVlumeText = (TextView)mMainView.findViewById(R.id.speech_volume_value);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void startLoading(){
        if(!isLoading){
            if (mRollAnimator == null) {
                mRollAnimator = HudAMapAnimation.startLoading(mLoadingView);
            } else {
                mRollAnimator.resume();
            }
        }
    }

    public void stopLoading(){
        if(isLoading){
            if (mRollAnimator == null) {
                mRollAnimator.end();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mSpeechView.setVisibility(View.VISIBLE);
        }else{
            mSpeechView.setVisibility(View.VISIBLE);
        }
    }


}
