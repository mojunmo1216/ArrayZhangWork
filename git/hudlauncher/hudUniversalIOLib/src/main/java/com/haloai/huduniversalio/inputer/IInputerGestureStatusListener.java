package com.haloai.huduniversalio.inputer;


import com.haloai.huduniversalio.gesture.GestureCode;

public interface IInputerGestureStatusListener extends IInputerStatusListener {

    public abstract boolean onGestureCommand(int gestureType);
    public abstract void onGestureShow(GestureCode gestureCode, boolean keyEvent);
    public abstract boolean onGesturePoint(int areaIndex);//返回的布尔值为true则代表此手势已经被消费
}
