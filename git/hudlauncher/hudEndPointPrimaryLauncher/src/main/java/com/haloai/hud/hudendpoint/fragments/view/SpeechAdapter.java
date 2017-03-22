package com.haloai.hud.hudendpoint.fragments.view;

/**
 * Created by zhangrui on 17/1/20.
 */
public abstract class SpeechAdapter {

    public abstract void startLoading();

    public abstract void stopLoading();

    public abstract void wakeUpHome();

    public abstract void wakeUpAwary();

    public abstract void setSpeechVlume(int vlume);

    public abstract void showGesture(int type);
}
