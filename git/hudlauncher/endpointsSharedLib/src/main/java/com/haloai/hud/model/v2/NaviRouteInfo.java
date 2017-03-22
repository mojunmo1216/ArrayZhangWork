package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.NaviDrivingStrategyProto;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.NaviRouteInfoProto;

import java.io.Serializable;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class NaviRouteInfo implements Serializable{

    private String naviDestinationPoi;
    private double naviDestinationLat;
    private double naviDestinationLng;
    private NaviDrivingStrategyProto naviDrivingStrategy;

    public NaviRouteInfo(NaviRouteInfoProto naviRouteInfoProto){
        this.naviDestinationPoi = naviRouteInfoProto.getNaviDestinationPoi();
        this.naviDestinationLat = naviRouteInfoProto.getNaviDestinationLat();
        this.naviDestinationLng = naviRouteInfoProto.getNaviDestinationLng();
        this.naviDrivingStrategy = naviRouteInfoProto.getNaviDrivingStrategy();
    }
    public NaviRouteInfo(){}

    public NaviRouteInfoProto encapsulate(){
        NaviRouteInfoProto.Builder builder = NaviRouteInfoProto.newBuilder();
        builder.setNaviDestinationPoi(naviDestinationPoi);
        builder.setNaviDestinationLat(naviDestinationLat);
        builder.setNaviDestinationLng(naviDestinationLng);
        builder.setNaviDrivingStrategy(naviDrivingStrategy);
        return builder.build();
    }
    public String getNaviDestinationPoi() {
        return naviDestinationPoi;
    }

    public void setNaviDestinationPoi(String naviDestinationPoi) {
        this.naviDestinationPoi = naviDestinationPoi;
    }

    public double getNaviDestinationLat() {
        return naviDestinationLat;
    }

    public void setNaviDestinationLat(double naviDestinationLat) {
        this.naviDestinationLat = naviDestinationLat;
    }

    public double getNaviDestinationLng() {
        return naviDestinationLng;
    }

    public void setNaviDestinationLng(double naviDestinationLng) {
        this.naviDestinationLng = naviDestinationLng;
    }

    public NaviDrivingStrategyProto getNaviDrivingStrategy() {
        return naviDrivingStrategy;
    }

    public void setNaviDrivingStrategy(NaviDrivingStrategyProto naviDrivingStrategy) {
        this.naviDrivingStrategy = naviDrivingStrategy;
    }
}
