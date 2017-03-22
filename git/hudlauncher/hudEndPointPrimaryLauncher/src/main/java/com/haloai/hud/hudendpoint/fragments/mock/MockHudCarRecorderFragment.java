package com.haloai.hud.hudendpoint.fragments.mock;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloai.hud.carrecorderlibrary.bean.VideoBean;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.huduniversalio.session.IHudIOSessionRecorderCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class MockHudCarRecorderFragment extends Fragment {


    public MockHudCarRecorderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mock_hud_car_recorder, container, false);
    }
    public void enterPlayPage(IHudIOSessionRecorderCallback.HudRecorderVideoType hudRecorderVideoType){}
    public void returnScanPage(){}
    public void enterScanPage(){}
    public void deleteVideo(){}
    public void deleteSure(){}
    public void deleteCancel(){}
    public void fastBackward(){}
    public void fastForward(){}
    public void pauseVideo(){}
    public void resume(){}
    public void lockVideo(){}
    public void nextVideo(){}
    public void previousVideo(){}
    public void exit(){}
    public void playingByPhone(String videoPath, int currentIndex){}
    public int getVideoNumber(IHudIOSessionRecorderCallback.HudRecorderVideoType hudRecorderVideoType){
        return -1 ;
    }
    public void on_offScanWindow(boolean open){}
    public int getRecorderStatus(){
        return -1;
    }
    public void initHudPlayList(HudCarcorderConstants.VideoTypeEnum videoType){}
    public VideoBean getVideoBean(String videoPath){
        return new VideoBean();
    }
    public void openVideoRecording() {}
    public void closeVideoRecording() {
    }
}
