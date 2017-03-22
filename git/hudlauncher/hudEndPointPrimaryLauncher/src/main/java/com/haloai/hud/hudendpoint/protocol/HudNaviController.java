package com.haloai.hud.hudendpoint.protocol;

import android.content.Context;
import android.content.Intent;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.model.v2.NaviRouteInfo;

/**
 * Created by zhangrui on 16/4/18.
 */
public class HudNaviController implements IHudNaviController {

    private Context mContext;

    public  HudNaviController(Context context){
        this.mContext=context;
    }

    @Override
    public void startNavigation(NaviRouteInfo naviRouteInfo) {
        if(naviRouteInfo!=null){
            Intent intent=new Intent();
            intent.setAction(HudEndPointConstants.PHONE_REQUEST_NAGATION);
            intent.putExtra(HudEndPointConstants.PHONE_REQUEST_TYPE,HudEndPointConstants.PHONE_START_NAGATION);
            intent.putExtra(HudEndPointConstants.PHONE_REQUEST_NAGATION,naviRouteInfo);
            mContext.sendBroadcast(intent);
        }

    }

    @Override
    public void stopNavigation() {
        Intent intent=new Intent();
        intent.setAction(HudEndPointConstants.PHONE_REQUEST_NAGATION);
        intent.putExtra(HudEndPointConstants.PHONE_REQUEST_TYPE,HudEndPointConstants.PHONE_STOP_NAGATION);
        mContext.sendBroadcast(intent);
    }

}
