package com.haloai.hud.hudendpoint.mappath;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;

/**
 * Created by wangshengxing on 16/5/24.
 */
public class AmapPathUtils {
    public static final String ROAD_STATUS_JAM = "拥堵";
    public static final String ROAD_STATUS_SLOWLY = "缓行";
    public static final String ROAD_STATUS_GO = "畅通";
    public static final String ROAD_STATUS_UNKNOWN = "未知";
    /**
     * latLonPoint to LatLng
     *
     * @param latLonPoint
     * @return latLng
     */
    public static LatLng latLonPoint2LatLng(LatLonPoint latLonPoint) {
        return latLonPoint == null ? null : new LatLng(latLonPoint.getLatitude(),
                latLonPoint.getLongitude());
    }

    /**
     * NaviLatLng to LatLng
     *
     * @param naviLatLng
     * @return latLng
     */
    public static LatLng naviLatLng2LatLng(NaviLatLng naviLatLng) {
        return naviLatLng == null ? null : new LatLng(naviLatLng.getLatitude(),
                naviLatLng.getLongitude());
    }

    /**
     * LatLng to NaviLatLng
     *
     * @param latLng
     * @return naviLatLng
     */
    public static NaviLatLng latLng2NaviLatLng(LatLng latLng) {
        return latLng == null ? null : new NaviLatLng(latLng.latitude,
                latLng.longitude);
    }

    public static String latLonPoint2LonlatStr(LatLonPoint latLonPoint){
        return (latLonPoint == null)?null:(latLonPoint.getLongitude()+","+latLonPoint.getLatitude());
    }
}
