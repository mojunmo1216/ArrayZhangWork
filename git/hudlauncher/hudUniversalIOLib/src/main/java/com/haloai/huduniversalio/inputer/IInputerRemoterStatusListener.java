package com.haloai.huduniversalio.inputer;



public interface IInputerRemoterStatusListener extends IInputerStatusListener {
    public abstract void onRemoterKey(int keyCode);//遥控器按下左右键
}
