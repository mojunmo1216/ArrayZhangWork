package com.haloai.hud.hudendpoint.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fota.iport.Const;
import com.haloai.hud.hudendpoint.utils.FileUtils;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.io.File;

/**
 * Created by zhangrui on 16/12/22.
 */
public class UpgradeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            String action = intent.getAction();
            if(action.equals(UpgradeConstant.ACTION_UPGRADE_SUCCES)){
                HaloLogger.logE("upgrade_info",UpgradeConstant.ACTION_UPGRADE_SUCCES);
                HaloLogger.uploadHaloLog("升级成功");
                boolean flag = intent.getBooleanExtra(Const.KEY_UPGRADE_SUCCESS,false);
                if(flag){
                    EndpointsConstants.UPGRADE_STATUS = UpgradeConstant.setUpgradeCode(UpgradeConstant.UPDATE_IDILE);
                    File file = new File(FileUtils.getUpgradeFilePath(context));
                    if(file.exists()) file.delete();
                    Intent intent1 = new Intent(UpgradeConstant.ACTION_CLOSE_GESTURE);
                    context.sendBroadcast(intent1);
                }

            }

        }
    }
}
