package com.haloai.hud.hudendpoint.fragments.mock;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloai.hud.hudendpoint.fragments.view.SpeechReceiverView;
import com.haloai.hud.hudendpoint.primarylauncher.R;

/**
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class MockHudSpeechReceiverFragment extends Fragment {

    public MockHudSpeechReceiverFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mock_hud_speech_receiver, container, false);
    }


    public void homeAnimator(boolean isGestureCause) {

    }


    public void recoverHomeState() {

    }

    public void setVolume(int volume) {
    }

    public void startLoading() {

    }

    public void stopLoading() {

    }

    // 菜单界面进入类似于导航界面的动画
    public void enterNavi() {

    }

    /**
     * 从诸如导航界面进入二三级菜单界面
     */
    public void naviToSecondMenu() {

    }

    public void upgradeSpeechText(String text) {

    }

    public void setVolumeFlag(boolean flag) {
        SpeechReceiverView.TTSvolumeFlag = flag;
    }

    public void displayReciver(boolean display) {

    }

    /**
     * 唤醒
     */
    public void awakeGestureAction() {

    }


    /**
     * yes
     */
    public void showYesGestureIcon() {
    }

    public void showMicView(){
    }

    public void showPointGestureIcon(){
    }

    public void displayGesturePoint(){
    }
    /**
     * 显示左右滑动图标
     */
    public void showSlideGestureIcon() {

    }



    /**
     * 调高音量
     */
    public void upVolume() {

    }

    /**
     * 调低音量
     */
    public void downVolume() {

    }

    public void controllVolumeAnimation(final boolean isUp) {

    }

    /**
     * 显示音量环
     */
    public void displayVolumeRing(final boolean isUp) {
    }

    // 计时器。;
    public void startVolumeRingTimer() {

    }


}
