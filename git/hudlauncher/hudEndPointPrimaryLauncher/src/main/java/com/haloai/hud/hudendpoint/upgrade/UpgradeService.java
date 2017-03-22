package com.haloai.hud.hudendpoint.upgrade;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fota.iport.MobAgentPolicy;
import com.fota.iport.inter.ICheckVersionCallback;
import com.fota.iport.inter.IOnDownloadListener;
import com.fota.iport.service.DLService;
import com.fota.utils.Trace;
import com.haloai.hud.hudendpoint.upgrade.policy.PolicyInter;
import com.haloai.hud.hudendpoint.upgrade.policy.PolicyManager;
import com.haloai.hud.hudendpoint.utils.FileUtils;
import com.haloai.hud.hudendpoint.utils.NetworkUtils;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangrui on 16/12/13.
 */
public class UpgradeService extends IntentService{

    private static final String TAG = "UpgradeService";
    public static final String KEY_SERVICE_TYPE = "key_service_type";
    // 分别启动3类服务：检测，下载，升级
    public static final int TYPE_CHECK = 1;
    public static final int TYPE_DOWNLOAD = 2;
    public static final int TYPE_UPGRADE = 3;

    private static Context mContext;

    private PolicyInter mPolicyInter = new PolicyManager();

    public UpgradeService() {
        super("UpgradeService");
    }

    public static void startService(Context context, int type) {
        mContext = context;
        //启动服务
        Intent intent = new Intent(context, UpgradeService.class);
        intent.putExtra(KEY_SERVICE_TYPE, type);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            dispatchServiceType(intent.getIntExtra(KEY_SERVICE_TYPE, -1));
        }
    }

    private void dispatchServiceType(int type) {
        switch (type) {
            case TYPE_CHECK:
                checkVersion();
                break;
            case TYPE_DOWNLOAD:
                downloadFile();
                break;
            case TYPE_UPGRADE:
                recoveryUpgrade();
                break;
        }
    }

    /**
     * SDK 检测版本接口
     */
    private void checkVersion() {
        log(TAG, "checkVersion ");
        MobAgentPolicy.checkVersion(mContext, new ICheckVersionCallback() {
            @Override
            public void onCheckSuccess(int status) {
                log(TAG, "onCheckSuccess [status] " + status);
                sendStatusBroadcast(UpgradeConstant.ACTION_CHECK_SUCCESS, null);
                //如果当前状态为空闲，开始下载升级包
                if(EndpointsConstants.UPGRADE_STATUS == UpgradeConstant.UPDATE_IDILE) UpgradeService.startService(mContext, TYPE_DOWNLOAD);
            }

            @Override
            public void onCheckFail(int status, String errorMsg) {
                log(TAG, "onCheckFail() [status, errorMsg] " + errorMsg);
                sendStatusBroadcast(UpgradeConstant.ACTION_CHECK_ERROR, errorMsg);
                if(EndpointsConstants.UPGRADE_STATUS == UpgradeConstant.UPDATE_START){
                    EndpointsConstants.UPGRADE_STATUS = UpgradeConstant.setUpgradeCode(UpgradeConstant.UPDATE_IDILE);//配置参数 更新完成
                }
            }

            @Override
            public void onInvalidDate() {
                log(TAG, "onInvalidDate()");
                sendStatusBroadcast(UpgradeConstant.ACTION_CHECK_ERROR, "no network or response data is exception.");
            }
        });

    }

    /**
     * 下载升级包接口
     */
    private void downloadFile() {
        if (mPolicyInter.is_request_wifi()) {
            if (!NetworkUtils.isWifi(mContext)) {//中文
                sendStatusBroadcast(UpgradeConstant.ACTION_DOWNLOAD_ERROR, "配置了wifi网络下载，但不在wifi下");
                return;
            }
        }

        File down_path = new File(FileUtils.getUpgradeFilePath(mContext));
        //获取后台配置路径
        String configPath = mPolicyInter.get_storage_path();
        if (!TextUtils.isEmpty(configPath)) {
            down_path = new File(configPath);
        }
        // 检查内存空间
        if (!mPolicyInter.is_storage_space_enough(down_path.getAbsolutePath())) {
            sendStatusBroadcast(UpgradeConstant.ACTION_DOWNLOAD_ERROR, "配置了最小内存空间，但空间不足");
            return;
        }


        //启动SDK下载接口
        DLService.start(mContext,
                MobAgentPolicy.getVersionInfo().deltaUrl,//下载url
                new File(down_path.getParent()),// 文件父目录
                down_path.getName(),// 文件名
                new IOnDownloadListener() {
                    @Override
                    public void onDownloadProgress(String tmpPath, int totalSize, int downloadedSize) {
                        log(TAG, "onDownloadProgress [tmpPath, totalSize, downloadedSize] " + totalSize + "," + downloadedSize);
                        //发送广播给主界面通知下载进度
                        sendStatusBroadcast(UpgradeConstant.ACTION_DOWNLOAD_PROGRESS, String.valueOf(downloadedSize * 100 / totalSize));
                    }

                    @Override
                    public void onDownloadFinished(int state, File file) {
                        log(TAG, "onDownloadFinished [state, file] " + state);
                        sendStatusBroadcast(UpgradeConstant.ACTION_DOWNLOAD_FINISHED, null);
                        if (mPolicyInter.is_auto_upgrade()) {
                            log(TAG, "onDownloadFinished auto upgrade");
                            UpgradeService.startService(mContext, TYPE_UPGRADE);
                        } else {
                            EndpointsConstants.UPGRADE_STATUS = UpgradeConstant.setUpgradeCode(UpgradeConstant.UPDATE_WAITING);
                            log(TAG, "onDownloadFinished not auto upgrade UPDATE_WAITING");
                        }
                    }

                    @Override
                    public void onDownloadError(int error) {
                        log(TAG, "onDownloadError [error] " + error);
                        //根据错误码，对错误做出对应的处理
                        sendStatusBroadcast(UpgradeConstant.ACTION_DOWNLOAD_ERROR, String.valueOf(error));
                    }
                }
        );
    }

    //广播通知状态变化
    private void sendStatusBroadcast(String action, String arg0) {
        Intent intent = new Intent(action);
        if (arg0 != null) {
            intent.putExtra(UpgradeConstant.KEY_INTENT_ARG0, arg0);
        }
        mContext.sendBroadcast(intent);
    }

    /**
     * 升级接口
     */
    private void recoveryUpgrade() {
        log(TAG, "recoveryUpgrade() ");
        //升级开始
        sendStatusBroadcast(UpgradeConstant.ACTION_UPGRADE_START, null);
        //电量检查
        if (!mPolicyInter.is_battery_enough(mContext)) {
            sendStatusBroadcast(UpgradeConstant.ACTION_UPGRADE_ERROR, "配置了最小电量，但电量不足");
            return;
        }
        File down_path = new File(FileUtils.getUpgradeFilePath(mContext));
        //获取后台配置路径
        String configPath = mPolicyInter.get_storage_path();
        if (!TextUtils.isEmpty(configPath)) {
            down_path = new File(configPath);
        }
        if(!down_path.exists()){
            EndpointsConstants.UPGRADE_STATUS = UpgradeConstant.setUpgradeCode(UpgradeConstant.UPDATE_IDILE);//配置参数 开始更新
            return;
        }
        try {
            log(TAG, "upgrade reboot path:"+down_path.getAbsolutePath());
            EndpointsConstants.UPGRADE_STATUS = UpgradeConstant.setUpgradeCode(UpgradeConstant.UPDATE_START);//配置参数 开始更新
            MobAgentPolicy.rebootUpgrade(mContext, FileUtils.getUpgradeFilePath(mContext));
        } catch (IOException e) {
            HaloLogger.uploadCatchException(e);
            log(TAG, "recoveryUpgrade error:"+e.getMessage());
            e.printStackTrace();
            sendStatusBroadcast(UpgradeConstant.ACTION_UPGRADE_ERROR, e.getMessage());
        }
    }

    private void log(String tag, String msg){
        HaloLogger.logE("upgrade_info",msg);
        Trace.d(tag,msg);
    }


}
