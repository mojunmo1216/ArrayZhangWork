package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;
import android.util.Log;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.hudendpoint.protocol.HudPreferenceSetController;
import com.haloai.hud.hudendpoint.protocol.IHudPreferenceSetController;
import com.haloai.hud.hudendpoint.utils.WifiHostBiz;
import com.haloai.hud.lib.transportlayer.fm.FmManager;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudDataDefine;
import com.haloai.hud.model.v2.HudPrefQueryRequest;
import com.haloai.hud.model.v2.HudPrefQueryResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.utils.EndpointsConstants;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/13 11:35
 * 修改人：zhang
 * 修改时间：2016/6/13 11:35
 * 修改备注：
 */
public class HudPrefQueryRequestDispatcherImp implements IDataDispatcher {
    private IHudPreferenceSetController mHudSettingController;
    private Context mContext;

    public HudPrefQueryRequestDispatcherImp(Context context) {
        mHudSettingController = HudPreferenceSetController.getInstance();;
        this.mContext = context;
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        HudPrefQueryRequest hudPrefQueryRequest = phone2HudMessages.getHudPrefQueryRequest();
        if (hudPrefQueryRequest == null) {
            return null;
        }

        int hud_prefer_type = hudPrefQueryRequest.getHud_prefer_type();
        HudPrefQueryResponse response = new HudPrefQueryResponse();
        HudDataDefine define = new HudDataDefine();
        define.setHud_data_type(hud_prefer_type);
        switch (hud_prefer_type){
            case EndpointsConstants.HUD_SET_WIFI: // 获取wifi信息
                define.setExtra_param(mHudSettingController.getWifiApInfo(true));
                define.setExtra_param1(mHudSettingController.getWifiApInfo(false));
                define.setBool_param(new WifiHostBiz(mContext).isWifiApEnabled());
                break;
            case EndpointsConstants.HUD_SET_VOLUME: // 获取音量
                define.setInt_param(mHudSettingController.getVolumeValue());
                break;
            case EndpointsConstants.HUD_SET_BRIGHTNESS: // 获取亮度
                define.setInt_param(mHudSettingController.getBrightnessLevel());
                define.setBool_param(false);// TODO: 2016/6/22 获取机器是否自动调节
                break;
            case EndpointsConstants.HUD_SET_STORAGE:
                String storageInfo = mHudSettingController.getStorageInfo();
                String[] split = storageInfo.split("%");
                define.setExtra_param(split[0]);
                define.setExtra_param1(split[1]);
                break;
            case EndpointsConstants.HUD_SET_PATH:
                // TODO: 2016/10/10

                break;
            case EndpointsConstants.HUD_SET_FM:
                FmManager manager = FmManager.getSingleFmManagerInstance(mContext);
                boolean open = manager.isOpen();
                define.setBool_param(open);
                break;
        }

        response.setHudPrefDefine(define);
        return response;
    }
}
