package com.haloai.hud.lib.transportlayer.fm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import com.digissin.fm.aidl.IFMStatusService;

import java.util.List;

/**
 * 项目名称：FmTest
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/10/10 14:01
 * 修改人：zhang
 * 修改时间：2016/10/10 14:01
 * 修改备注：
 */
public class FmManager {
    private IFMStatusService mService;
    private Context mContext;
    private static FmManager mFmManager;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = IFMStatusService.Stub.asInterface(binder);
            Log.e("speech_info", "onServiceconnected, fm连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("speech_info", "onServiceDisconnected, fm连接失败");
        }
    };

    private FmManager(Context contenxt) {
        mContext = contenxt;
    }

    public static FmManager getSingleFmManagerInstance(Context context) {
        if (mFmManager == null) {
            mFmManager = new FmManager(context);
        }
        return mFmManager;
    }

    public boolean isOpen() {
        boolean result = false;
        if (mService != null) {
            try {
                result = mService.isFMOpen();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("speech_info", "openFm fail!"+e.getMessage());
            }
        }
        return result;
    }

    public boolean openFm() {
        boolean result = false;
        if (mService != null) {
            try {
                result = mService.TakeOnFM();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("speech_info", "openFm fail!"+e.getMessage());
            }
        }
        return result;
    }

    public boolean closeFm() {
        boolean result = false;
        if (mService != null) {
            try {
                result = mService.TakeOffFM();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("speech_info", "close fail!"+e.getMessage());
            }
        }
        return result;
    }

    public boolean setRate(float rate) {
        boolean result = false;
        if (rate < 70 || rate > 108) {
            Log.e("speech_info", "请输入有效频率");
        } else {
            if (mService != null) {
                try {
                    Log.e("speech_info", "SetFMRate1："+rate);
                    result = mService.SetFMRate(rate);
                    Log.e("speech_info", "SetFMRate2："+rate);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("speech_info", "Rate fail!"+e.getMessage());
                }
            }
        }
        return result;
    }

    public void bindFMService() {
        final Intent intent = new Intent();
        intent.setAction("com.digissin.fm.services.FMService");
        Intent explicitFromImplicitIntent = createExplicitFromImplicitIntent(mContext, intent);
        if (explicitFromImplicitIntent != null) {
            final Intent eintent = new Intent(explicitFromImplicitIntent);
            mContext.bindService(eintent, connection, Service.BIND_AUTO_CREATE);
        }
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(implicitIntent);

        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    private void startActivity(String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName cn = new ComponentName(packageName, className);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e("speech_info", "start " + packageName + "fail!");
        }
    }
}
