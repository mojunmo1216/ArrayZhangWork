package com.halo.gesture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.halo.gesture.IGestureConnectionListener.IGestureNotifier;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by ylq on 16/9/3.
 */
public class GestureController implements IGestureController{

    private final static int GESTURE_STATUS_SUCCESS = 0;
    private Context mContext;
    static{
        System.loadLibrary("GEngine");
		if(GEngine.isHudApk){
			Log.i("GEngine", "chgperjni load!");
			System.loadLibrary("chgperjni");
		}
    }
   
    @Override
    public  void initWithNotifers(Context context,IGestureNotifier keyNotifier,IGestureConnectionListener statusNotifier){
    	mContext = context;
        if(keyNotifier != null && statusNotifier != null ){
            GEngine.init(keyNotifier);
            GEngine.setConnectionListener(statusNotifier);
        }
    }
    
    @Override
    public  void connectToChip(){
    	if(GEngine.openGestureUsbPort() == GESTURE_STATUS_SUCCESS){
    		GEngine.connectGestureDevice();
    	}
    }

    @Override
    public  void disconnectToChip(){
        GEngine.disconnectGestureDevice();
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        GEngine.closeGestureUsbPort();
    }

	@Override
	public void setGesturePower(int code) {
		try
        {
            PowerManager powerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
            Class<?> pmClass = Class.forName(powerManager.getClass().getName());
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            Object iPM = field.get(powerManager);
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            Method method = iPMClass.getDeclaredMethod("setGesturePower", int.class);
            method.setAccessible(true);
            method.invoke(iPM, code);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
		
	}

}
