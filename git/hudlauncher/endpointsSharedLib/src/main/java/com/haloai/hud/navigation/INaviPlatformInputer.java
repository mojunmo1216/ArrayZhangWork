package com.haloai.hud.navigation;

import com.amap.api.services.core.PoiItem;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter.RouteSearchData;
import java.util.List;

/**
 * Created by ylq on 16/7/29.
 */
public interface INaviPlatformInputer {

    public void onPOIListData(String poiNameToSearch, List<String> poiList);

    public void onDetailPOIListData(String poiNameToSearch, List<PoiItem> poiList);

    public void onPOISearchFailed(String poiNameToSearch, String error);

    public void onCalculateRouteFailure(int errorInfo); //路径计算失败

    public void onCalculateRouteSuccess(RouteSearchData shortestDistance,
                                        RouteSearchData fastestTime, RouteSearchData defaultStrategy);//策略路径计算完毕

}
