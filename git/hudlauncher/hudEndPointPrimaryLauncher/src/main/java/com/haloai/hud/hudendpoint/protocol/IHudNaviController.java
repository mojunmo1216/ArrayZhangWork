package com.haloai.hud.hudendpoint.protocol;

import com.haloai.hud.model.v2.NaviRouteInfo;

/**
 * Created by zhangrui on 16/4/19.
 */
public interface IHudNaviController {
    void startNavigation(NaviRouteInfo naviRouteInfo);
    void stopNavigation();
}
