package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;


import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudInfoDeclaim;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.model.v2.PhoneInfoDeclaim;
import com.haloai.hud.utils.HudConstants;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/4/19 17:34
 * 修改人：zhang
 * 修改时间：2016/4/19 17:34
 * 修改备注：
 */
public class ExchangeMessageDispatcherImp implements IDataDispatcher {
    private Context mContext;

    public ExchangeMessageDispatcherImp(Context context) {
        this.mContext = context;
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        PhoneInfoDeclaim phoneInfoDeclaim = phone2HudMessages.getPhoneInfoDeclaim();
        if (phoneInfoDeclaim != null) {

            SharedPreferences sp = mContext.getSharedPreferences(HudEndPointConstants.SP_PHONE_INFO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("PhoneAppVersionCode", phoneInfoDeclaim.getPhoneAppVersionCode());
            edit.putString("PhoneAppVersionStr", phoneInfoDeclaim.getPhoneAppVersionStr());
            edit.putString("PhoneNumber", phoneInfoDeclaim.getPhoneNumber());
            edit.putString("PhonePlatformVersion", phoneInfoDeclaim.getPhonePlatformVersion());
            edit.putString("PhoneUniqueId", phoneInfoDeclaim.getPhoneUniqueId());
            edit.putInt("ProtocolVersionCode", phoneInfoDeclaim.getProtocolVersionCode());
            edit.commit();
            /*//TODO 测试程序，之后删除
            Handler testHandler=new Handler(Looper.getMainLooper());
            final String number=phoneInfoDeclaim.getPhoneNumber();
            testHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"已连接设备:"+number,Toast.LENGTH_LONG).show();
                }
            });
            HaloLogger.logE("ExchangeDispatcherImp", phoneInfoDeclaim.getPhoneUniqueId());*/

            HudInfoDeclaim hudInfoDeclaim = new HudInfoDeclaim();
            hudInfoDeclaim.setHudUniqueId(HudEndPointConstants.gHudSerialNumber);//getHudUuid(mContext));
            hudInfoDeclaim.setHudSimId(HudEndPointConstants.gSimICCID);
            hudInfoDeclaim.setLauncherAppVersionCode(getVersionCode(mContext));
            hudInfoDeclaim.setLauncherAppVersionStar(getVersionName(mContext));
            hudInfoDeclaim.setFirmwareVersion(getInner_Ver());
            hudInfoDeclaim.setProtocolVersionCode(1);

            return hudInfoDeclaim;
        }
        return null;
    }


    public String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getInner_Ver() {
        String ver = "";
        if (android.os.Build.DISPLAY.contains(android.os.Build.VERSION.INCREMENTAL)) {
            ver = android.os.Build.DISPLAY;
        } else {
            ver = android.os.Build.VERSION.INCREMENTAL;
        }
        return ver;
    }


   /* public String getHudUUID(){
        String uuidKey = "HUDUUID";
        SharedPreferences sp = mContext.getSharedPreferences(HudEndPointConstants.SP_PHONE_INFO_KEY,Context.MODE_PRIVATE);
        String uuid = sp.getString(uuidKey,null);
        if ( uuid == null) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(uuidKey, uuid);
            edit.commit();
        }
        return uuid;
    }*/

    public String getHudUuid(Context context) {
        String uuidSpFiles = "uuid_filse";
        String hudUuidKey = "uuidkey";
        UUID uuid;
        SharedPreferences prefs = context.getSharedPreferences(uuidSpFiles, Context.MODE_PRIVATE);
        String id = prefs.getString(hudUuidKey, null);
        if (id != null) {
            // Use the ids previously computed and stored in the prefs file
            uuid = UUID.fromString(id);
        } else {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // Use the Android ID unless it's broken, in which case fallback on deviceId,
            // unless it's not available, then fallback on a random number which we store
            // to a prefs file
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            // Write the value out to the prefs file
            prefs.edit().putString(hudUuidKey, uuid.toString()).commit();
        }
        return uuid.toString();
    }
}
