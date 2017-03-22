package com.haloai.hud.hudendpoint.protocol;

import android.content.Context;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.model.v2.NaviRouteInfo;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/7 16:17
 * 修改人：zhang
 * 修改时间：2016/6/7 16:17
 * 修改备注：
 */
public interface IHudPreferenceSetController {
    void init(Context context);
    void setWifiApInfo(String name, String password);
    String getWifiApInfo(boolean isName);
    void openWifiAp(boolean flag);

    void setVolumeValue(int value);
    void upDownVolumeValue(boolean isUp);
    void volumeValueChange(boolean isMedia,boolean isUp);
    int getVolumeValue();
    int getMaxVolume();

    void setBrightnessLevel(int value, boolean isAuto);
    void upDownBrightnessLevel(boolean isUp);
    int getBrightnessLevel();

    void setNaviRoutInfo(String destinationName, double longitude, double latitude, HudEndPointConstants.PathEnumType type);
    NaviRouteInfo getRoutInfo(boolean isHome);

    String getStorageInfo();
}
