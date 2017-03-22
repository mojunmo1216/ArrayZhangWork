package com.haloai.huduniversalio.session;

import com.haloai.huduniversalio.HudIOConstants.HudPromatType;

/**
 * Created by zhangrui on 16/5/19.
 */
public interface IHudIOSessionDispatcherCallback extends IHudIOSessionCallback{

    public abstract void dispatcherSessionPromat(HudPromatType hudPromatType);
    public abstract void dispatcherSessionAdjustLight(boolean isUp);
    public abstract void dispatcherSessionGestureChoose(int index);

}
