package com.haloai.hud.hudendpoint.fragments.mock;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.hudendpoint.fragments.common.HudSpeechPanelFragment;
import com.haloai.hud.hudendpoint.primarylauncher.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MockHudSpeechPanelFragment extends Fragment {


    public MockHudSpeechPanelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mock_hud_speech_panel, container, false);
    }

    private void updateContent(HudSpeechPanelFragment.PanelType type, String[] contents, final int offset, boolean override){

    }
    public void updateNaviInfo(NaviInfo naviInfo){

    }
    public int getInstructionDrawable(int instruction){
        return 0;
    }
    public void upgradePanel(HudSpeechPanelFragment.PanelType panelType, List<String> textList){

    }
    public void upgradePanel(HudSpeechPanelFragment.PanelType panelType, String[] contents, int offset, boolean override) {

    }

    HudSpeechPanelFragment.PanelType mCurrentPanelType = HudSpeechPanelFragment.PanelType.MainPanelType;
    public HudSpeechPanelFragment.PanelType getCurrentPanelType() {
        return mCurrentPanelType;
    }

    public boolean updateUIAccordingGesturePointChange(HudSpeechPanelFragment.GesturePointType type){
        return true;
    }
    public boolean gesturePointChoose(int areaIndex){
        return true;
    }


    public void upgradePanel(HudSpeechPanelFragment.PanelType panelType) {

    }
}
