package com.haloai.hud.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.LocationManager;
import android.os.Process;

import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class NavigationSDKAdapter {
	public static String NAVIGATION_SDK_BAIDU = "navigation_sdk_baidu";
	public static String NAVIGATION_SDK_BAIDU_INNER = "navigation_sdk_baidu_inner";
	public static String NAVIGATION_SDK_AMAP = "navigation_sdk_amap";
	public static String NAVIGATION_SDK_CARELAND = "navigation_sdk_careland";

	private static NavigationSDKAdapter currentNavigationAdapter;

	private static NavigationSDKAdapter mAMapNavigationAdapter;
    private static NavigationSDKAdapter mBaiduNavigatingAdapter;

	protected INaviPlatformInputer mNaviPlanNotifier;
	protected NavigationNotifier mNaviNotifier;
	protected String currentSearchingPoi;
	protected int mSatelliteNum;
	protected Listener mGpsStatusListener;
	protected LocationManager mLocationManager;
	
	public void setNaviNotifier(NavigationNotifier naviNotifier) {
		this.mNaviNotifier = naviNotifier;
		updateSatelliteInfo();
	}

	public void setNaviPlanNotifier(INaviPlatformInputer naviPlanNotifier){
		this.mNaviPlanNotifier = naviPlanNotifier;
		updateSatelliteInfo();
	}

    /*
	public static void launchNavigationSDK(String navigationSdkName,
			Context appContext) {
		if (currentNavigationAdapter != null) {
			HaloLogger.logE("NavigationSDKAdapter", "Launch Navigation SDK duplicatedly wrongly.");
			return;
		}
		HaloLogger.logE("NavigationSDKAdapter", "launchNavigationSDK enter");
		if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_BAIDU)) {
			// currentNavigationAdapter = new BaiduNavigationSDKAdapter();
		} else if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_CARELAND)) {
			// currentNavigationAdapter = new CarelandNavigationSDKAdapter();
		} else if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_AMAP)) {
			currentNavigationAdapter = new AMapNavigationSDKAdapter(appContext);
		} else if (navigationSdkName
				.equalsIgnoreCase(NAVIGATION_SDK_BAIDU_INNER)) {
			// currentNavigationAdapter = new InnerBaiduNavigationSDKAdapter();
		} else {
			throw new IllegalArgumentException(String.format(
					"Doesn't support '%s' SDK", navigationSdkName));
		}

		HaloLogger.logE("NavigationSDKAdapter", "launchNavigationSDK leave with currentNaivigationAdapter=" + currentNavigationAdapter);
	}

	public static NavigationSDKAdapter getCurrentNavigationAdapter() {
		HaloLogger.logE("NavigationSDKAdapter", "currentNaivigationAdapter=" + currentNavigationAdapter);
		return currentNavigationAdapter;
	}
    */
	public static NavigationSDKAdapter getNavigationAdapter(String navigationSdkName,
																   Context appContext) {
		NavigationSDKAdapter navigationSDKAdapter = null;
		if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_BAIDU)) {
			// currentNavigationAdapter = new BaiduNavigationSDKAdapter();
		} else if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_CARELAND)) {
			// currentNavigationAdapter = new CarelandNavigationSDKAdapter();
		} else if (navigationSdkName.equalsIgnoreCase(NAVIGATION_SDK_AMAP)) {
			if (mAMapNavigationAdapter == null) {
				mAMapNavigationAdapter = new AMapNavigationSDKAdapter(appContext);
			}
			navigationSDKAdapter = mAMapNavigationAdapter;
		} else if (navigationSdkName
				.equalsIgnoreCase(NAVIGATION_SDK_BAIDU_INNER)) {
			// currentNavigationAdapter = new InnerBaiduNavigationSDKAdapter();
		} else {
			throw new IllegalArgumentException(String.format(
					"Doesn't support '%s' SDK", navigationSdkName));
		}
		return navigationSDKAdapter;
	}




	private void updateSatelliteInfo() {
		if(mGpsStatusListener != null){
			return;
		}
		mGpsStatusListener = new Listener() {
			private List<GpsSatellite> mNumSatelliteList = new ArrayList<GpsSatellite>();

			@Override
			public void onGpsStatusChanged(int event) {
				GpsStatus status = mLocationManager.getGpsStatus(null);
				mNumSatelliteList.clear();
				if (status != null
						&& event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
					// 获取最大的卫星数（这个只是一个预设值）
					int maxSatellites = status.getMaxSatellites();
					Iterator<GpsSatellite> it = status.getSatellites()
							.iterator();
					mNumSatelliteList.clear();
					// 记录实际的卫星数目
					int count = 0;
					while (it.hasNext() && count <= maxSatellites) {
						// 保存卫星的数据到一个队列，用于刷新界面
						GpsSatellite s = it.next();
						count++;
						if(s.usedInFix()){
							mNumSatelliteList.add(s);
						}
					}
				}
				mSatelliteNum = mNumSatelliteList.size();
				if (mNaviNotifier != null) {
					mNaviNotifier.onSatelliteFound(mSatelliteNum);
				}
			}
		};
		mLocationManager.addGpsStatusListener(mGpsStatusListener);
	}

	// Navigation Query functions
	/*public abstract void searchLocationName(String province, String city,
			String area, String locationName);*/

	// Navigation invoke functions


	// 策略导航
	public abstract void startSearchDestination(String city,String destinName);

	public abstract void startSearchDestinnationRound(String category);


	public abstract void startNavigate(int poiIndex);

	public abstract void startNavigate(double lat, double lng, String name);

	public abstract void startNavigateSimulately();

	public abstract void stopNavigate();

	public abstract void  setStrategy(int driveMode);



	public abstract void getNavigationStatus(); // 查询当前导航状态：导航中;巡航中;导航服务未开启

	public interface NavigationNotifier {
		enum NaviTextType {
			UnknownText,
			NaivInfoText,
			FrontTrafficText,
			WholeTrafficText
		}
/*
		public void onPOIListData(String poiNameToSearch, List<String> poiList);

		public void onDetailPOIListData(String poiNameToSearch, List<PoiItem> poiList);

		public void onPOISearchFailed(String poiNameToSearch, String error);

		public void onCalculateRouteFailure(int errorInfo); //路径计算失败
*/


		public void onNaviCalculateRouteFailure(int errorInfo); //导航路径计算失败


		public void onNaviText(NaviTextType textType, String text); // 导航中的文字提示，可用于语音播报,

		public void onYawStart(); // 偏航

		public void onYawEnd(); // 偏航，且已经重新规划导航

		public void onReCalculateRouteForTrafficJam(); //自动躲避拥堵后规划新的导航路径成功

		public void onCrossShow(Bitmap cross); // 路口放大图显示

		public void onCrossHide(); // 路口放大图隐藏

		public void onNaviInfo(NaviInfo info); // 导航信息更新

		public void onSpeedUpgraded(float speed); // 速度更新

		public void onLocationChanged(Object location); // 当前GPS位置更新，为适配不同导航SDK，参数为Object

		public void onPathCalculated(Object aMapNavi); // 路径计算完成，为适配不同导航SDK，参数为Object

		public void onSatelliteFound(int satelliteNum); // 返回当前GPS搜星结果
		
		public void onArriveDestination(); //到达目的地
		
		public void onEndEmulatorNavi(); //模拟导航到达目的地

		public void onStartNavi(int type);//导航开始

	}

}
