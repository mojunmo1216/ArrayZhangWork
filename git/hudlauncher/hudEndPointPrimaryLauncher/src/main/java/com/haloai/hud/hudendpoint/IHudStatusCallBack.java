package com.haloai.hud.hudendpoint;

import com.haloai.hud.hudendpoint.fragments.common.HudStatusBarFragment.*;

/**
 * Created by zhangrui on 16/7/11.
 */
public interface IHudStatusCallBack {
    void onHudStatusUpdate(HudStatusItemType hudStatusItemType, boolean flag);
    void onMusicStatusUpdate(int index);
    void onBTCalling();
    void onGSMStateUpdate(int level);
}
