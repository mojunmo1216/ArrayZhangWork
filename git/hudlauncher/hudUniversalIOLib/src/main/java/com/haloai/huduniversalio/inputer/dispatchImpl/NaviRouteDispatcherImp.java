package com.haloai.huduniversalio.inputer.dispatchImpl;

import android.util.Log;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudNobodyResponse;
import com.haloai.hud.model.v2.NaviRouteInfo;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.huduniversalio.inputer.IPhoneInputerNotifer;

/**
 * Created by zhangrui on 16/4/18.
 */
public class NaviRouteDispatcherImp implements IDataDispatcher {
    private IPhoneInputerNotifer mPhoneInputerNotifer;

    public NaviRouteDispatcherImp(IPhoneInputerNotifer phoneInputerNotifer){
        this.mPhoneInputerNotifer=phoneInputerNotifer;
    }
    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        NaviRouteInfo naviRouteInfo=phone2HudMessages.getNaviRouteInfo();
        if(naviRouteInfo != null){
            Log.e("NaviRouteDispatcherImp", naviRouteInfo.getNaviDestinationPoi());
            mPhoneInputerNotifer.startNavigation(naviRouteInfo);
            return new HudNobodyResponse();
        }
        return null;
    }
}
