package com.haloai.huduniversalio;

import android.content.Context;
import android.content.res.Resources;

import com.haloai.huduniversalio.speech.ISpeechResourceHelper;


/**
 * Created by wangshengxing on 16/5/9.
 */
public class SpeechResourceManager implements ISpeechResourceHelper {
    private Resources mResources;
    private static SpeechResourceManager mSpeechResourceManager;

    static {

    }

    public SpeechResourceManager(Context appContext) {
        mResources = appContext.getApplicationContext().getResources();
    }
    public static void init(Context appContext){
        if (mSpeechResourceManager == null) {
            mSpeechResourceManager = new SpeechResourceManager(appContext);
        }
    }

    public static SpeechResourceManager getInstanse(){

        return mSpeechResourceManager;
    }

    public String getString(int id){
        return mResources.getString(id);
    }
}
