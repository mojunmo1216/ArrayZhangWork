package com.haloai.hud.hudendpoint.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by zhangrui on 16/7/12.
 */
public class CameraUtil {
    public static String TAKE_PHOTO_ACTION = "intent.action.ACTION_TAKE_PHOTO";

    public static void takePhoto(Context context){
        Intent intent = new Intent(TAKE_PHOTO_ACTION);
        intent.putExtra("msg", "getphoto");
        context.sendBroadcast(intent);
    }

}
