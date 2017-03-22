package com.halo.gesture;

/**
 * Created by zhangrui on 16/7/13.
 */
public interface IGestureConnectionListener {
    public void onGestureDevState(int status);
    public void onUpdateFlagChange(boolean upFlag);
    public void onUpdateProgressChange(int upProgVal);

    public interface IGestureNotifier{
        static final int INPUTER_GESTURE_CLICK = 0;
        static final int INPUTER_GESTURE_V_TYPE = 1;
        static final int INPUTER_GESTURE_SLIDE = 2;
        static final int INPUTER_GESTURE_CLOCKWISE = 3;
        static final int INPUTER_GESTURE_ANTICLOCKWISE = 4;
        static final int INPUTER_GESTURE_PALM = 5;
        public abstract void onGestureCallBack(int gestureCode,float score);
        public abstract void onGesturePoint(int x,int y,int z,float score);
    }
}
