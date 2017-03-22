package com.haloai.hud.hudendpoint.utils;

/**
 * 项目名称：
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/7/7 18:50
 * 修改人：zhang
 * 修改时间：2016/7/7 18:50
 * 修改备注：
 */

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * WIFI热点业务类
 *
 * @author zhang
 */
public class WifiHostBiz {
    private final String TAG = "WifiHostBiz";
    private WifiManager wifiManager;
 //   private String WIFI_HOST_SSID = "Halo_0001";
    private String WIFI_HOST_SSID = "Halo_";
    private String WIFI_HOST_PRESHARED_KEY = "12345678";// 密码必须大于8位数
    private Context mContext;

    public WifiHostBiz(Context context) {
        super();
        //获取wifi管理服务
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mContext = context;
    }

    /**
     * 判断热点开启状态
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState() {
        int tmp;
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(wifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }
    /**
     * wifi热点开关
     *
     * @param enabled true：打开	false：关闭
     * @return true：成功	false：失败
     */
    public boolean setWifiApEnabled(boolean enabled,String androidID) {
        String number = "0001";
        if(androidID!=null&&androidID.length()>4){
            number = androidID.substring(0,4);
        }
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
            System.out.println(TAG + ":关闭wifi");
        } else {
            wifiManager.setWifiEnabled(true);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = WIFI_HOST_SSID+number;
            //配置热点的密码
            apConfig.preSharedKey = WIFI_HOST_PRESHARED_KEY;
            //安全：WPA2_PSK
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

   private String getAndroidID(){
       String number= "0001";
       String id = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.ANDROID_ID);
       if(id!=null&&id.length()>4){
           number = id.substring(0,4);
       }
       return number;
   }
}
