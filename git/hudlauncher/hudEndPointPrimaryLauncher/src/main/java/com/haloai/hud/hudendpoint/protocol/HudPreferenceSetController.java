package com.haloai.hud.hudendpoint.protocol;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.model.v2.NaviRouteInfo;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.HudConstants;
import com.haloai.huduniversalio.HudIOConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * 项目名称：hudlauncher
 * 类描述：手机对hud的设置操作的底层实现。如音量、亮度、wifi信息。。。
 * 创建人：zhang
 * 创建时间：2016/6/7 16:23
 * 修改人：zhang
 * 修改时间：2016/6/7 16:23
 * 修改备注：
 */
public class HudPreferenceSetController implements IHudPreferenceSetController {

    private static HudPreferenceSetController theInstance;
    private Context mContext;
    private AudioManager mAudioManager;
    private SharedPreferences mSp;
    private SensorManager sensorManager;
    private Sensor mligthSensor;
    private final long TempTimeStep =10 * 60 * 1000;//根绝温度调节风扇转速的时间间隔 10 min

    public class SensorListener implements SensorEventListener {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            //获取精度
            float acc = event.accuracy;
            //获取光线强度
            float lux = event.values[0];
            boolean isBrightAutoClose = false;
            mSp = mContext.getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
            mSp.getBoolean(HudIOConstants.Is_Blight_Auto_close,isBrightAutoClose);
            if (isBrightAutoClose == false){
                autoFitBrightness(lux);
            }
        }
    }



    @Override
    public void init(Context context) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setDefaultGuangJiBrightness();
        setFanLevel(HudEndPointConstants.DEFAULT_VALUE_OF_FAN);
        HudStatusManager.HudMusicVlume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mligthSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(new SensorListener(), mligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    SystemClock.sleep(TempTimeStep);//每十分钟更新一次风扇转速
                    int temp=getGuangJiTemperature();
                    setFenlevelByTemperature(temp);
                }
            }
        }).start();
    }

    public static HudPreferenceSetController getInstance() {
        if (theInstance == null) {
            theInstance = new HudPreferenceSetController();
        }
        return theInstance;
    }
    

    private void setDefaultGuangJiBrightness() {
        try {
            int currentBrghtnessValue = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            setLightBrightness(currentBrghtnessValue);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWifiApInfo(String name, String password) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            if (name == null || "".equals(name)) {
                HaloLogger.logE("HudPreferenceSetController", "the name of the wifiap is cannot be null");
                name = getWifiApInfo(true);
            }
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(false);
            }
            Method setupMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = name;
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            if (password == null || password.length() < 8) {
                HaloLogger.logE("HudPreferenceSetController", "the length of wifi password must be 8 or longer");
                password = getWifiApInfo(false);
            }
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.preSharedKey = password;

            // 先关闭wifi热点
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);

            setupMethod.invoke(wifiManager, netConfig, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openWifiAp(boolean flag) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (flag) {
            // 关闭wifi
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            // 获取wifi名字以及密码
            String wifiname = getWifiApInfo(true);
            if (wifiname == null || "".equals(wifiname)) {
                wifiname = HudEndPointConstants.HUD_WIFI_ACQUIESCENT_NAME;
            }
            String wifipassword = getWifiApInfo(false);
            if (wifipassword == null || "".equals(wifipassword)) {
                wifipassword = HudEndPointConstants.HUD_WIFI_ACQUIESCENT_PASSWORD;
            }

            // 设置wifi名字以及密码
            setWifiApInfo(wifiname, wifipassword);
        } else {
            Method method = null;
            try {
                method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getWifiApInfo(boolean isName) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            final WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            return isName ? config.SSID : config.preSharedKey;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setVolumeValue(int value) {
        if (value < 0) {
            value = 0;
        }
        int maxVolume = getMaxVolume();
        if (value > maxVolume) {
            value = maxVolume;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value, 0);
        HudStatusManager.HudMusicVlume = value;
        HaloLogger.logE("music_info", "setVolumeValue:"+HudStatusManager.HudMusicVlume);
    }

    @Override
    public void upDownVolumeValue(boolean isUp) {
        if (isUp) {
            HudStatusManager.HudMusicVlume = HudStatusManager.HudMusicVlume++;
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        } else {
            HudStatusManager.HudMusicVlume = HudStatusManager.HudMusicVlume--;
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        HaloLogger.logE("music_info", "upDownVolumeValue:"+HudStatusManager.HudMusicVlume);
    }

    @Override
    public void volumeValueChange(boolean isMedia, boolean isUp) {
        if (isMedia) {
            if (isUp) {
                HudStatusManager.HudMusicVlume = HudStatusManager.HudMusicVlume++;
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else {
                HudStatusManager.HudMusicVlume = HudStatusManager.HudMusicVlume--;
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        } else {
            if (isUp) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        }
        HudStatusManager.HudMusicVlume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int getVolumeValue() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    @Override
    public int getMaxVolume() {
        if(mAudioManager != null){
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            max = (max*4)/5;
            return max;
        }
        return 0;
    }

    @Override
    public void setBrightnessLevel(int brightnessLevel, boolean isAuto) {
     //   int value = brightnessLevel * HudEndPointConstants.STEP_OF_BRIGHTNESS;

        setAutoBrightness(isAuto);
        if (!isAuto)
            setLightBrightness(brightnessLevel);
    }

    @Override
    public void upDownBrightnessLevel(boolean isUp) {
        int currentBrghtnessValue = 0;
        try {
            currentBrghtnessValue = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        HaloLogger.logE("speech_info", "currentBrghtnessValue1:" + currentBrghtnessValue);
        currentBrghtnessValue = isUp ? currentBrghtnessValue + HudEndPointConstants.STEP_OF_BRIGHTNESS : currentBrghtnessValue - HudEndPointConstants.STEP_OF_BRIGHTNESS;
        setLightBrightness(currentBrghtnessValue);
    }

    @Override
    public int getBrightnessLevel() {
        try {
            int value = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            value = value < 5 ? 5 : value;
            value = value > 255 ? 255 : value;
            return value;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public NaviRouteInfo getRoutInfo(boolean isHome) {
        mSp = mContext.getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
        NaviRouteInfo naviRouteInfo = new NaviRouteInfo();
        if (isHome) {
            naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_HOME_PATHNAME, null));
            naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LONGITUDE, -1f));
            naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_HOME_LATITUDE, -1f));
            naviRouteInfo.setNaviDrivingStrategy(null);

        } else {
            naviRouteInfo.setNaviDestinationPoi(mSp.getString(HudIOConstants.KEY_OF_COMPANY_PATHNAME, null));
            naviRouteInfo.setNaviDestinationLng(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LONGITUDE, -1f));
            naviRouteInfo.setNaviDestinationLat(mSp.getFloat(HudIOConstants.KEY_OF_COMPANY_LATITUDE, -1f));
            naviRouteInfo.setNaviDrivingStrategy(null);
        }
        return naviRouteInfo;
    }

    @Override
    public void setNaviRoutInfo(String destinationName, double longitude, double latitude, HudEndPointConstants.PathEnumType type) {
        // TODO: 2016/6/7 暂时存起来，待处理。
        mSp = mContext.getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSp.edit();

        switch (type) {
            case PATH_TO_HOME:
                edit.putString(HudIOConstants.KEY_OF_HOME_PATHNAME, destinationName);
                edit.putFloat(HudIOConstants.KEY_OF_HOME_LONGITUDE, (float) longitude);
                edit.putFloat(HudIOConstants.KEY_OF_HOME_LATITUDE, (float) latitude);
                HaloLogger.logE("speech_info","home:"+destinationName);
                break;
            case PATH_TO_COMPANY:
                edit.putString(HudIOConstants.KEY_OF_COMPANY_PATHNAME, destinationName);
                edit.putFloat(HudIOConstants.KEY_OF_COMPANY_LONGITUDE, (float) longitude);
                edit.putFloat(HudIOConstants.KEY_OF_COMPANY_LATITUDE, (float) latitude);
                HaloLogger.logE("speech_info","comany:"+destinationName);
                break;
        }
        edit.commit();
    }

    private void autoFitBrightness(float light){
        int res = 5;
        if (light >= 0 && light < 100){
            res = (int)(220 * 0.2 * light/100);
        }else if (light >=100 && light <300){
            res = (int)(44 + 220 * 0.1 * (light-100)/200);
        }else if (light >= 300 && light <1300){
            res = (int)(66 + 220 * 0.1 * (light-300)/1000);
        }else if (light >= 1300){
            res = 88;
        }
        HaloLogger.logE("Blight","ligh:"+light+"  output:"+res);
        setLightBrightness(res);
    }



    private void setAutoBrightness(boolean autoBrightness) {
//        if (autoBrightness)
//            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
//        else
//            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        mSp = mContext.getSharedPreferences(HudIOConstants.FILENAME_OF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSp.edit();
        if (autoBrightness)
            edit.putBoolean(HudIOConstants.Is_Blight_Auto_close,false);
        else
            edit.putBoolean(HudIOConstants.Is_Blight_Auto_close,true);

    }

    private void setLightBrightness(int light) {
        try
        {
            if (light < 5) {
                light = 5;
            } else if (light > HudEndPointConstants.MAX_VALUE_OF_BRIGHTNESS) {
                light = HudEndPointConstants.MAX_VALUE_OF_BRIGHTNESS;
            }
            PowerManager powerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
            Class<?> pmClass = Class.forName(powerManager.getClass().getName());
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            Object iPM = field.get(powerManager);
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());

            Method method = iPMClass.getDeclaredMethod("setTemporaryScreenBrightnessSettingOverride", int.class);
            method.setAccessible(true);
            method.invoke(iPM, light);

            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, light);
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


    public int getGuangJiTemperature() {
        int temp=0;
        File file = new File("/sys/class/thermal/thermal_zone2/temp");
        BufferedReader bfReader = null;
        try {
            bfReader = new BufferedReader(new FileReader(file));
            temp = Integer.parseInt(bfReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
        }
        return temp / 1000;
    }

    private void setFenlevelByTemperature(int temp){

        if(temp >= 50 && temp < 65){
            setFanLevel(700 + (65-temp)*20);
        }else if(temp >= 65){
            setFanLevel(1000);
        }

    }

    private void setFanLevel(int level){
        try
        {
            if (level < 200) {
                level = 200;
            } else if (level > HudEndPointConstants.MAX_VALUE_OF_FAN) {
                level = HudEndPointConstants.MAX_VALUE_OF_FAN;
            }
            PowerManager powerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
            Class<?> pmClass = Class.forName(powerManager.getClass().getName());
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            Object iPM = field.get(powerManager);
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            Method method = iPMClass.getDeclaredMethod("setFanLevel", int.class);
            method.setAccessible(true);
            method.invoke(iPM, level);
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

    @Override
    public String getStorageInfo() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        File sdfile = new File("/mnt/sdcard/");
        long sdtotalSpace = sdfile.getTotalSpace();
        long sdUsedSpace = sdtotalSpace - sdfile.getFreeSpace();

        File file = new File("/mnt/sdcard/");
        long systemSize = file.getTotalSpace() - getFolderSize(file) - sdfile.getFreeSpace();

        File sdCameraFile = new File("/mnt/sdcard/DCIM/camera/");
        long sdCameraSize = getFolderSize(sdCameraFile);

        File sdMusicFile = new File("/mnt/sdcard/Music/");
        long sdMusicSize = getFolderSize(sdMusicFile);

        long otherssize = getFolderSize(file) - sdMusicSize - sdCameraSize;

        File tffile = new File("/mnt/sdcard2/");
        long tftotalSpace = tffile.getTotalSpace();
        long tffileFreeSpace = tffile.getFreeSpace();
        File tfCameraFile = new File("/mnt/sdcard2/DCIM/camera/");
        long tfCameraSize = getFolderSize(tfCameraFile);

        return decimalFormat.format(sdtotalSpace / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(sdUsedSpace / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(systemSize / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(sdMusicSize / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(sdCameraSize / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(otherssize / 1024f / 1024f / 1024f) + "%" +
                decimalFormat.format(tftotalSpace / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(tffileFreeSpace / 1024f / 1024f / 1024f) + "_" + decimalFormat.format(tfCameraSize / 1024f / 1024f / 1024f);
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
