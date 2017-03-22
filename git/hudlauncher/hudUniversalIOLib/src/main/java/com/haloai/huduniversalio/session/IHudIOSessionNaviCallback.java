package com.haloai.huduniversalio.session;


import com.amap.api.services.core.PoiItem;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter.RouteSearchData;

import java.util.List;


public interface IHudIOSessionNaviCallback extends IHudIOSessionCallback {
	public static final String MESSAGE_ERROR_DESTINATION_NO_FOUND="未能找到您说的地名，请重试 ";
	public static final String FASTEST_STRATEGY="推荐";
	public static final String FASTEST="第一个";
	public static final String SHORTEST_STRATEGY="最快";
	public static final String SHORTEST="第二个";
	public static final String AVOID_CONGESTION_STRATEGY="最短";
	public static final String AVOID_CONGESTION="第三个";
	public static final String START_NAVI="开始导航";
	public static final String START="开始";
	public final static String MSG_SPEECH_ERROR = "不太明白，,请重说";
	public static final String [] STRATEGY_ARRAY = new String[]{"推荐","最快","最短"};

	public abstract void naviSessionPoiItemNextPage(List<String> currentPOIList ,int currentPageIndex);
	public abstract void naviSessionPoiItemPrevPage(List<String> currentPOIList ,int currentPageIndex);
	public abstract void naviSessionSetDestination(String poiNameToSearch,int index,boolean isLoading);
	public abstract void naviSessionChooseStrategy(int index);
	public abstract void naviSessionChoosePOI(int index);
	public abstract void naviSessionStart();

	public abstract void onNaviSessionPOIResult(List<String> poiList, String destination, int poiPageSize);
	public abstract void onPOISearchFailed(String poiNameToSearch, String error);
	public abstract void onCalculateRouteFailure(int errorInfo);
	public abstract void onCalculateRoute(RouteSearchData shortestDistance,RouteSearchData fastestTime, RouteSearchData defaultStrategy);
}
