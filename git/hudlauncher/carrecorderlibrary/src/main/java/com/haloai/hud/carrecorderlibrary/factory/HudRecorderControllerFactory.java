package com.haloai.hud.carrecorderlibrary.factory;

import android.content.Context;

import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;
import com.haloai.hud.carrecorderlibrary.impl.HudRecorderContronller;


public class HudRecorderControllerFactory {
    private static IHudRecorderController mHudCarcorderController2;

    public static IHudRecorderController getHudCarcorderController2(Context context) {
        if (mHudCarcorderController2 == null) {
            synchronized (IHudRecorderController.class) {
                if (mHudCarcorderController2 == null) {
                    mHudCarcorderController2 = new HudRecorderContronller(context);
                }
            }
        }
        return mHudCarcorderController2;
    }
}
