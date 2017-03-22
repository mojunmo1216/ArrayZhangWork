package com.haloai.hud.hudendpoint.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.fota.iport.Const;
import com.haloai.hud.hudendpoint.utils.FileUtils;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;

import java.io.File;


/**
 * Created by zhangrui on 16/12/13.
 */
public class HudUpgradeManager {

    private static HudUpgradeManager mUpgradeManager;
    private  Context mContext;
    private  final int UPGRADE_START = 2 ;
    private  final int CLOSE_GESTURE_POWER = 0;
    private  final int OPEN_GESTURE_POWER = 1 ;

    public static HudUpgradeManager getInstance(){
        if(mUpgradeManager == null){
            mUpgradeManager = new HudUpgradeManager();
        }
        return mUpgradeManager;
    }

    public void init(Context context){
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpgradeConstant.ACTION_CHECK_ERROR);
        filter.addAction(UpgradeConstant.ACTION_CHECK_START);
        filter.addAction(UpgradeConstant.ACTION_CHECK_SUCCESS);
        filter.addAction(UpgradeConstant.ACTION_DOWNLOAD_ERROR);
        filter.addAction(UpgradeConstant.ACTION_DOWNLOAD_FINISHED);
        filter.addAction(UpgradeConstant.ACTION_DOWNLOAD_ERROR);
        filter.addAction(UpgradeConstant.ACTION_DOWNLOAD_PROGRESS);
        filter.addAction(UpgradeConstant.ACTION_UPGRADE_START);
        filter.addAction(UpgradeConstant.ACTION_UPGRADE_ERROR);
        filter.addAction(UpgradeConstant.ACTION_CLOSE_GESTURE);
        mContext.registerReceiver(new UpgradeStatusReceiver(),filter);
        UpgradeService.startService(mContext,UpgradeService.TYPE_CHECK);
        HaloLogger.logE("upgrade_info", "startService-check2 ");
        if(EndpointsConstants.UPGRADE_STATUS == UpgradeConstant.UPDATE_WAITING){
            mHandler.sendEmptyMessageDelayed(UPGRADE_START,5000);
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPGRADE_START:
                    UpgradeService.startService(mContext,UpgradeService.TYPE_UPGRADE);
                    HaloLogger.logE("upgrade_info", "startService-upgrade");
                    break;
                case CLOSE_GESTURE_POWER:
                    Intent intent = new Intent(HudIOConstants.ACTION_GESTURE_POWER);
                    intent.putExtra(HudIOConstants.ACTION_GESTURE_POWER,0);
                    mContext.sendBroadcast(intent);
                    mHandler.sendEmptyMessageDelayed(OPEN_GESTURE_POWER,2000);
                    break;
                case OPEN_GESTURE_POWER:
                    Intent intent1 = new Intent(HudIOConstants.ACTION_GESTURE_POWER);
                    intent1.putExtra(HudIOConstants.ACTION_GESTURE_POWER,1);
                    mContext.sendBroadcast(intent1);
                    break;
            }

        }
    };

    public  class UpgradeStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String action = intent.getAction();
                HaloLogger.logE("upgrade_info", "UpgradeStatusReceiver:"+action);
                switch (action) {
                    case UpgradeConstant.ACTION_CHECK_START:

                        break;
                    case UpgradeConstant.ACTION_CHECK_SUCCESS:

                        break;
                    case UpgradeConstant.ACTION_DOWNLOAD_PROGRESS:

                        break;
                    case UpgradeConstant.ACTION_DOWNLOAD_FINISHED:

                        break;
                    case UpgradeConstant.ACTION_UPGRADE_START:

                        break;
                    case UpgradeConstant.ACTION_CHECK_ERROR:

                        break;
                    case UpgradeConstant.ACTION_DOWNLOAD_ERROR:

                        break;
                    case UpgradeConstant.ACTION_UPGRADE_ERROR:

                        break;
                    case UpgradeConstant.ACTION_CLOSE_GESTURE:
                        mHandler.sendEmptyMessageDelayed(CLOSE_GESTURE_POWER,5000);
                        break;
                }
            }

        }
    }

}
