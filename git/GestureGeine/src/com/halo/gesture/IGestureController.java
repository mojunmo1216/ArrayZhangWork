package com.halo.gesture;

import android.content.Context;

import com.halo.gesture.IGestureConnectionListener.IGestureNotifier;

/**
 * Created by ylq on 16/9/2.
 */


public interface IGestureController {

    public abstract void initWithNotifers(Context context,IGestureNotifier keyNotifier,IGestureConnectionListener statusNotifier);

    public abstract void connectToChip();  //if return ture,connect success;else,fail;

    public abstract void disconnectToChip();  //if return ture,disconnect success;else,fail;
    
    public abstract void setGesturePower(int code);//1 打开 0 关闭

}
