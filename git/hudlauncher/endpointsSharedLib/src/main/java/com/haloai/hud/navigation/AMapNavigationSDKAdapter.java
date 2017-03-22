package com.haloai.hud.navigation;

import android.content.Context;
import android.location.LocationManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.haloai.hud.navigation.NavigationSDKAdapter.NavigationNotifier.NaviTextType;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.List;

public class AMapNavigationSDKAdapter extends NavigationSDKAdapter implements
		OnPoiSearchListener, OnRouteSearchListener {

	private String mSearchName;
	private static final String TAG = "AMapNavigationSDKAdapter";
	private static final int SIMULATION_NAVI = 0;// 执行模拟导航操作
	private static final int CALCULATE_ROUTE = 1;// 执行计算线路操作
	private static final int GPS_CANNOT_USE = 0;// 使用我的位置进行计算、GPS定位还未成功状态
	private static final int CALCULATE_ERROR = 1;// 启动路径计算失败状态
	private static final int CALCULATE_SUCCESS = 2;// 启动路径计算成功状态
	private static final int MAX_POI_LIST_SIZE = 3;// 兴趣点返回的一页的长度

	private static final int AMAP_MAP_CODE_SUCCESS = 0;//POI搜索成功
	private static final int AMAP_MAP_CODE_KEY_ERROR = 32;//Key验证错误
	private static final int AMAP_MAP_CODE_TIME_OUT = 1802;//socket 连接超时
	private static final int AMAP_MAP_CODE_UNKNOW_HOST = 1804;//未知主机
	private static final int AMAP_MAP_CODE_NET_ERROR = 27;//http或socket连接失败

	private boolean mHasEndpoint = false;// 记录是否得到终点坐标
	private boolean mHasGetGPS = false;// 记录GPS定位是否成功

	private static final int DRIVING_MODE_NONE = -99;
	//单位km/h
	protected static final int SIMULATION_SPEED = 120;
	private int mDriveMode = DRIVING_MODE_NONE;

	private int routeSearchAndCalculateCount = 0;

	// 导航点
	private List<NaviLatLng> mStartPoints = new ArrayList<>();
	private NaviLatLng mStartPointNaviLatLng = new NaviLatLng();
	private List<NaviLatLng> mEndPoints = new ArrayList<>();
	private NaviLatLng mEndPointNaviLatLng = new NaviLatLng();
	private List<NaviLatLng> mWayPoints = new ArrayList<>();
	private List<PoiItem> mPoiList = null;
	private int mChoosePoiIndex;

	private PoiSearch.Query mEndSearchQuery;
	private LatLonPoint mEndPointLatLon = null;
	private LatLonPoint mStartPointLatLon = null;

	// 三种策略的返回数据
	private RouteSearchData mShortestDistance;
	private RouteSearchData mFastestTime;
	private RouteSearchData mDefaultStrategy;

	// choose strategy callback
	//private ChooseNaviStrategyCallback mChooseNaviStrategyCallback;

	// 导航核心类及其监听
	private AMapNavi mAMapNavi;
	private AMapNaviListener mAmapNaviListener;
	protected String mCurrentProvince;
	protected String mCurrentCity="深圳市";
	protected String mCurrentCityCode = "0755";
	protected String mCurrentArea;

	private Context mAppContext;
	
	private boolean mIsYaw = false;
	private boolean mIsSimulatelyNavi = false;
	private boolean mIsStartNavi = false; //是否初次开始导航
	private boolean mIsPOIFailed = false;//POI失败后是否重新搜索

	private void clearNaviStatus() {
		mSearchName = null;
	//	mDriveMode = DRIVING_MODE_NONE;
		routeSearchAndCalculateCount = 0;
		/*mStartPoints.clear();
		mEndPoints.clear();*/
		if(mWayPoints != null) mWayPoints.clear();
		if(mPoiList != null) mPoiList.clear();
		mChoosePoiIndex = 0;
		mHasEndpoint = false;
		mIsSimulatelyNavi = false;
		mSatelliteNum = -1;
	}

	// 定位类及其监听
	private AMapLocationClient mLocationClient;
	private AMapLocationListener mLocationListener = new AMapLocationListener() {

		@Override
		public void onLocationChanged(AMapLocation location) {
			if (location != null && location.getErrorCode() == 0) {
				mHasGetGPS = true;
				mStartPointNaviLatLng = new NaviLatLng(location.getLatitude(),location.getLongitude());
				mStartPoints.clear();
				mStartPoints.add(mStartPointNaviLatLng);
				mCurrentProvince = location.getProvince();
				/*AMapNavigationSDKAdapter.this.mCurrentCity = location.getCity();
				AMapNavigationSDKAdapter.this.mCurrentCityCode = location
						.getCityCode();*/
				AMapNavigationSDKAdapter.this.mCurrentArea = location
						.getDistrict();
				AMapNavigationSDKAdapter.this.mStartPointLatLon = new LatLonPoint(
						location.getLatitude(), location.getLongitude());
				EndpointsConstants.HudStartPoint = new LatLng(location.getLatitude(),location.getLongitude());

				String cityName = location.getCity();
				String cityCode = location.getCityCode();
				if(!TextUtils.isEmpty(cityName) && !mCurrentCity.equals(cityName)){
					AMapNavigationSDKAdapter.this.mCurrentCity = cityName;
					HaloLogger.postE(EndpointsConstants.NAVI_TAG,"设置城市名："+mCurrentCity);
				}
				if(!TextUtils.isEmpty(cityCode) && !mCurrentCityCode.equals(cityCode)){
					AMapNavigationSDKAdapter.this.mCurrentCityCode = cityCode;
					HaloLogger.postE(EndpointsConstants.NAVI_TAG,"设置城市码："+cityCode);
				}

			} else {
				//location is error , so set mHasGetGPS is false
				mHasGetGPS = false;
			}
		}
	};

	public AMapNavigationSDKAdapter(Context appContext) {
		super();
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
		initAMapNavi();
		startLocationAndInit();
	}

	private void initAMapNavi() {
		mAMapNavi = AMapNavi.getInstance(mAppContext);
		mAMapNavi.setAMapNaviListener(getAMapNaviListener());
	}

	private void startLocationAndInit() {
		if (this.mLocationClient == null) {
			this.mLocationClient = new AMapLocationClient(mAppContext);
			AMapLocationClientOption option = new AMapLocationClientOption();
			option.setGpsFirst(true);
			option.setLocationMode(AMapLocationMode.Hight_Accuracy);
			option.setInterval(5000);
			option.setNeedAddress(false);
			this.mLocationClient.setLocationOption(option);
			this.mLocationClient.setLocationListener(mLocationListener);
			AMapNavigationSDKAdapter.this.mLocationClient.startLocation();
		}
	}

	public void startSearchDestination(String city,String destinName){
		if (destinName != null) {
			mIsPOIFailed = true;
			this.setLocationNameAndSearchPOI(destinName);
		}
	}

	public void startSearchDestinnationRound(String category) {
		if (category != null) {
			mIsPOIFailed = true;
			this.setLocationNameAndRoundSearchPOI(category);
		}
	}
	public void setLocationNameAndSearchPOI(String locationName){
		clearNaviStatus();
		mSearchName=locationName;
		endSearchResult();
	}

	public void setLocationNameAndRoundSearchPOI(String category){
		clearNaviStatus();
		mSearchName=category;
		roundSearchResult();
	}
	@Override
	public void startNavigate(int poiIndex) {
		this.mChoosePoiIndex = poiIndex;
		if (mPoiList == null || mPoiList.size() <= 0) {
			return;
		}
		mEndPointLatLon = mPoiList.get(mChoosePoiIndex).getLatLonPoint();
		mSearchName = mPoiList.get(mChoosePoiIndex).getTitle();
		// TODO 选择POI后获取路线规划，在路线规划的结果回调中进入选择策略的界面
		routeSearch();
	}

	// choose navigation strategy

	/*
	private void chooseNaviStrategy() {
		if (mChooseNaviStrategyCallback != null) {
			mChooseNaviStrategyCallback.chooseNaviStrategy();
		}
	}
    */
	@Override
	public void setStrategy(int driveMode) {
		mDriveMode = driveMode;
	}

	// TODO --------------------------choose navi strategy callback start-------

	/*
	public void setChooseNaviStrategyCallback(
			ChooseNaviStrategyCallback chooseNaviStrategyCallback) {
		this.mChooseNaviStrategyCallback = chooseNaviStrategyCallback;
	}


	public interface ChooseNaviStrategyCallback {
		public void chooseNaviStrategy();
	}
    */
	// TODO --------------------------navigation info return callback


	// 获取三种策略的返回的结果
	private void routeSearch() {
		RouteSearch routeSearch = new RouteSearch(mAppContext);
		FromAndTo ft = new FromAndTo(mStartPointLatLon, mEndPointLatLon);
		// strategy 1:least time
		// route search ,default is speed first
		int mode = AMapNavi.DrivingDefault;
		DriveRouteQuery query = new DriveRouteQuery(ft, mode, null, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// strategy 2:shortest distance
		// route search ,short distance
		mode = AMapNavi.DrivingShortDistance;
		query = new DriveRouteQuery(ft, mode, null, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// strategy 3:avoid congestion
		// route search ,time first ,avoid
		mode = AMapNavi.DrivingFastestTime;
		query = new DriveRouteQuery(ft, mode, null, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// calculate route

  		/*!!!!! debug 业务需求不更新给绿灯，规划路径时，不用导航类规划路径，开始导航时再设置导航策略**/
		//configurateNaviInfo();
	}

	//TODO 之后删除
	private void configurateNaviInfo(int strategy) {
		mEndPoints.clear();
		mEndPointNaviLatLng = new NaviLatLng(mEndPointLatLon.getLatitude(),
				mEndPointLatLon.getLongitude());
		mEndPoints.add(mEndPointNaviLatLng);
		if (!mHasGetGPS) {
			startLocationAndInit();
			return;
		}
		if (!mHasEndpoint) {
			endSearchResult();
			return;
		}
		mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, mWayPoints, strategy);
	}

/*public void getRouteSearchResult(RouteSearchData shortestDistance,
									 RouteSearchData fastestTime, RouteSearchData defaultStrategy) {
		shortestDistance.naviPath = mShortestDistance.naviPath;
		shortestDistance.driveRouteResult = mShortestDistance.driveRouteResult;
		shortestDistance.distance = mShortestDistance.distance;
		shortestDistance.duration = mShortestDistance.duration;
		shortestDistance.charge = mShortestDistance.charge;
		shortestDistance.roads = mShortestDistance.roads;
		shortestDistance.trafficLights = mShortestDistance.trafficLights;

		fastestTime.naviPath = mFastestTime.naviPath;
		fastestTime.driveRouteResult = mFastestTime.driveRouteResult;
		fastestTime.distance = mFastestTime.distance;
		fastestTime.duration = mFastestTime.duration;
		fastestTime.charge = mFastestTime.charge;
		fastestTime.roads = mFastestTime.roads;
		fastestTime.trafficLights = mFastestTime.trafficLights;

		defaultStrategy.naviPath = mDefaultStrategy.naviPath;
		defaultStrategy.driveRouteResult = mDefaultStrategy.driveRouteResult;
		defaultStrategy.distance = mDefaultStrategy.distance;
		defaultStrategy.duration = mDefaultStrategy.duration;
		defaultStrategy.charge = mDefaultStrategy.charge;
		defaultStrategy.roads = mDefaultStrategy.roads;
		defaultStrategy.trafficLights = mDefaultStrategy.trafficLights;


	}
*/
	/**
	 * 正式开始导航
	 */
	public void startNavigate() {
		go2Navi();
	}

	// start navigation with poiIndex and strategy
	private void go2Navi() {
		HaloLogger.logE("speech_info","go2Navi called");
		mIsStartNavi = true;
		configurateNaviInfo(mDriveMode);
		// 开启真实导航，需要在有路径规划成功回调那里调用
//		mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
	}

	public void startGpsNavi(){
		mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
	}

	public void reCalculate(){
		mAMapNavi.reCalculateRoute(mDriveMode);
		HaloLogger.logE("speech_info","reCalculate:"+mDriveMode);
	}

	@Override
	public void startNavigate(double lat, double lng, String name) {
		mHasEndpoint = true;
		mEndPointLatLon = new LatLonPoint(lat,lng);
		mSearchName = name;
		mPoiList = new ArrayList<>();

	}

	@Override
	public void startNavigateSimulately() {
		HaloLogger.logE("speech_info","startNavigateSimulately");
		// 设置模拟速度
		mAMapNavi.setEmulatorNaviSpeed(SIMULATION_SPEED);
		// 开启模拟导航
		mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
		mIsSimulatelyNavi = true;
		HaloLogger.logE("speech_info","SIMULATION_SPEED:"+SIMULATION_SPEED);
	}

	@Override
	public void stopNavigate() {
		// Intent intent = new Intent(mAppContext,
		// HaloAMapNaviStartActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// mAppContext.startActivity(intent)

		// amapNaviEmulatorFragmentListener.onStopAMapNaviEmulator();
		HaloLogger.logE("speech_info","stopNavigate");
		mIsSimulatelyNavi = false;
	//	clearNaviStatus();
		if (mAMapNavi != null) {
			mAMapNavi.stopNavi();
		}
		mLocationManager.removeGpsStatusListener(mGpsStatusListener);
	}

	@Override
	public void getNavigationStatus() {

	}

	public void endSearchResult() {
		mEndSearchQuery = new PoiSearch.Query(mSearchName, "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
				"住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施", mCurrentCityCode);
		// mEndSearchQuery = new PoiSearch.Query("广州市汇峰苑", "",
		// mCurrentCityCode);
		mEndSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
		mEndSearchQuery.setPageSize(MAX_POI_LIST_SIZE);// 设置每页返回多少条数据
		PoiSearch poiSearch = new PoiSearch(mAppContext, mEndSearchQuery);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
		HaloLogger.postE(EndpointsConstants.NAVI_TAG,"城市名："+mCurrentCity +"城市码："+mCurrentCityCode);
	}


	public void roundSearchResult(){

		if(mStartPointLatLon!=null){
			mEndSearchQuery = new PoiSearch.Query(mSearchName,"汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
					"住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施",mCurrentCityCode);
			mEndSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			mEndSearchQuery.setPageSize(MAX_POI_LIST_SIZE);// 设置每页返回多少条数据
			PoiSearch poiSearch = new PoiSearch(mAppContext,mEndSearchQuery);
			poiSearch.setBound(new PoiSearch.SearchBound(mStartPointLatLon, 2000));//设置周边搜索的中心点以及区域
			poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
			poiSearch.searchPOIAsyn();//开始搜索

		}

	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		if (mNaviPlanNotifier==null){
			return;
		}
		if (rCode == AMAP_MAP_CODE_SUCCESS) {// 返回成功
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
				if (result.getQuery().equals(mEndSearchQuery)) {
					mHasEndpoint = true;
					mPoiList = result.getPois();
					List<String> poiList = new ArrayList<String>();
					for (int i = 0; i < mPoiList.size(); i++) {
						PoiItem poiItem = mPoiList.get(i);
						String poiName = poiItem.getTitle();

						poiList.add(poiName);
						if (i >= MAX_POI_LIST_SIZE - 1)
							break;
					}
					AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onDetailPOIListData(
							mSearchName, result.getPois());
					AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onPOIListData(
							mSearchName, poiList);
				}
			} else {
				HaloLogger.postE(EndpointsConstants.NAVI_TAG,"PoiSearched Failed:"+mCurrentCity+mSearchName+mCurrentCityCode);
				if(mIsPOIFailed){
					mIsPOIFailed = false;
					setLocationNameAndSearchPOI(mCurrentCity+mSearchName);
					HaloLogger.postE(EndpointsConstants.NAVI_TAG,"PoiSearched_Failed:"+mCurrentCity+mSearchName+mCurrentCityCode);
					return;
				}
				AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onPOISearchFailed(
						mSearchName, "搜索不到目的地，请详细再说一遍");
			}
		}else {
			HaloLogger.postE(EndpointsConstants.NAVI_TAG,"PoiSearched Failed2:"+rCode);
			if(mIsPOIFailed){
				mIsPOIFailed = false;
				setLocationNameAndSearchPOI(mCurrentCity+mSearchName);
				return;
			}
			if (rCode == AMAP_MAP_CODE_NET_ERROR) {
				AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onPOISearchFailed(
						mSearchName, "搜索不到目的地，请检查网络");
			} else if (rCode == AMAP_MAP_CODE_KEY_ERROR) {
				AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onPOISearchFailed(
						mSearchName, "搜索不到目的地，请检查Key");
			} else {
				AMapNavigationSDKAdapter.this.mNaviPlanNotifier.onPOISearchFailed(
						mSearchName, "搜索不到目的地，请详细再说一遍或加上所在城市名");
			}
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

	}


	// private int calculateDriverRoute(int drivingMode) {
	// int code = CALCULATE_ERROR;
	// if (mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, mWayPoints,
	// drivingMode)) {
	// code = CALCULATE_SUCCESS;
	// }
	// return code;
	// }
	//
	// private int calculateDriverRoute() {
	// return calculateDriverRoute(mDriveMode);
	// }

	// 导航的监听回调
	private AMapNaviListener getAMapNaviListener() {
		if (mAmapNaviListener == null) {
			mAmapNaviListener = new AMapNaviListener() {
				@Override
				public void onTrafficStatusUpdate() {
					// 交通状态更新
				}

				@Override
				public void onStartNavi(int type) {
					HaloLogger.logE("speech_info","onStartNavi called:"+type);
					if (mNaviNotifier != null) {
						mNaviNotifier.onStartNavi(type);
					}
				}

				@Override
				public void onReCalculateRouteForYaw() {
					// 偏航后重新设置path
					if (mNaviNotifier != null) {
						mNaviNotifier.onYawStart();
					}
					mIsYaw = true;
					HaloLogger.postI(EndpointsConstants.ARWAY_TAG,"已偏航");
				}

				@Override
				public void onReCalculateRouteForTrafficJam() {
					if (mNaviNotifier != null) {
						mNaviNotifier.onReCalculateRouteForTrafficJam();
					}
				}

				@Override
				public void onLocationChange(AMapNaviLocation location) {
					if(mIsSimulatelyNavi){
						location.setMatchStatus(1);
					}

					HaloLogger.logE(TAG,String.format("onLocationChanged: Bearing %s , Coord %s",location.getBearing(),location.getCoord()));

					if(true){//location.isMatchNaviPath()
						if (mNaviNotifier != null) {
							mNaviNotifier.onLocationChanged(location);
							mNaviNotifier.onSpeedUpgraded(location.getSpeed());
						}
					}
				}

				@Override
				public void onInitNaviSuccess() {
					HaloLogger.logE("speech_info", "onInitNaviSuccess");
				}

				@Override
				public void onInitNaviFailure() {
					HaloLogger.logE("speech_info","onInitNaviFailure");
				}

				@Override
				public void onGetNavigationText(int type, String text) {
					if (mNaviNotifier != null) {
						if (type == AMapNavi.NaviInfoText)
							mNaviNotifier.onNaviText(NaviTextType.NaivInfoText, text);
						else if (type == AMapNavi.FrontTrafficText)
							mNaviNotifier.onNaviText(NaviTextType.FrontTrafficText, text);
						else if (type == AMapNavi.WholeTrafficText)
							mNaviNotifier.onNaviText(NaviTextType.WholeTrafficText, text);
					}
				}

				@Override
				public void onEndEmulatorNavi() {
					if (mNaviNotifier != null) {
						mNaviNotifier.onEndEmulatorNavi();
					}
					mIsSimulatelyNavi = false;
					clearNaviStatus();
				}

				@Override
				public void onCalculateRouteSuccess() {
					//if current is yaw state , so go to set the path
					// FIXME: 16/6/24 处理偏航时是否会回调
					HaloLogger.logE("speech_info","mIsStartNavi is "+mIsStartNavi+" ,strategy is "+mAMapNavi.getNaviPath().getStrategy());
					HaloLogger.logE("speech_info","path size:"+mAMapNavi.getNaviPath().getSteps().size()+"path length:"+mAMapNavi.getNaviPath().getAllLength());
				    if(mIsStartNavi){
						mIsStartNavi=false;
						if (mNaviNotifier != null) {

							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									mNaviNotifier.onPathCalculated(mAMapNavi);
									mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
									HaloLogger.postI("speech_info","startNavi GPSNaviMode");
								}
							};
//							runnable.run();
							Thread thread = new Thread(runnable,"amap_onCalculateRouteSuccess");
							thread.setPriority(1);
							thread.start();
						}

						return;
				    }else if (mIsYaw) {
						mIsYaw = false;
						if (mNaviNotifier != null) {
							mNaviNotifier.onYawEnd();
							mNaviNotifier.onPathCalculated(mAMapNavi);
						}
						HaloLogger.postI(EndpointsConstants.ARWAY_TAG,"偏航结束");
						return;
					}else {
						// TODO: 2016/9/30
						if (mNaviNotifier != null) {
							mNaviNotifier.onYawStart();
							mNaviNotifier.onPathCalculated(mAMapNavi);
							mNaviNotifier.onYawEnd();
						}
						HaloLogger.postI("speech_info","no start navi , no yaw navi ,but calc success");
					}
					/***!!!!! debug 业务需求不更新给绿灯，规划路径时，不用导航类规划路径，开始导航时再设置导航策略***/
					// if current is route search step,do not to start navi.
					/*if (mDriveMode == DRIVING_MODE_NONE) {
						RouteSearchData data = new RouteSearchData();
						AMapNaviPath path = mAMapNavi.getNaviPath();
						data.naviPath = path;
						int strategy = path.getStrategy();
						if (strategy == AMapNavi.DrivingDefault) {
							if (mDefaultStrategy == null) {
								mDefaultStrategy = new RouteSearchData();
							}
							mDefaultStrategy.setNaviPath(path);
							mDefaultStrategy.trafficLights = 0;
							List<AMapNaviStep> steps = path.getSteps();
							for (AMapNaviStep step : steps) {
								List<AMapNaviLink> links = step.getLinks();
								for (AMapNaviLink link : links) {
									if (link.getTrafficLights()) {
										mDefaultStrategy.trafficLights++;
									}
								}
							}
//							routeSearchAndCalculateCount++;
//							go2ChooseNaviStrategy();
						} else if (strategy == AMapNavi.DrivingFastestTime) {
							if (mFastestTime == null) {
								mFastestTime = new RouteSearchData();
							}
							mFastestTime.setNaviPath(path);
							mFastestTime.trafficLights = 0;
							List<AMapNaviStep> steps = path.getSteps();
							for (AMapNaviStep step : steps) {
								List<AMapNaviLink> links = step.getLinks();
								for (AMapNaviLink link : links) {
									if (link.getTrafficLights()) {
										mFastestTime.trafficLights++;
									}
								}
							}
//							routeSearchAndCalculateCount++;
							mAMapNavi.calculateDriveRoute(mStartPoints,
									mEndPoints, mWayPoints,
									AMapNavi.DrivingShortDistance);
						} else if (strategy == AMapNavi.DrivingShortDistance) {
							if (mShortestDistance == null) {
								mShortestDistance = new RouteSearchData();
							}
							mShortestDistance.setNaviPath(path);
							mShortestDistance.trafficLights = 0;
							List<AMapNaviStep> steps = path.getSteps();
							for (AMapNaviStep step : steps) {
								List<AMapNaviLink> links = step.getLinks();
								for (AMapNaviLink link : links) {
									if (link.getTrafficLights()) {
										mShortestDistance.trafficLights++;
									}
								}
							}
//							routeSearchAndCalculateCount++;
							mAMapNavi.calculateDriveRoute(mStartPoints,
									mEndPoints, mWayPoints,
									AMapNavi.DrivingDefault);
						}
					} else {
						mNaviNotifier.onNaviStarted();
					}*/
				}


				@Override
				public void onCalculateRouteFailure(int errorInfo) {
					if (mNaviNotifier != null) {
						AMapNavigationSDKAdapter.this.mNaviNotifier.onNaviCalculateRouteFailure(errorInfo);
					}
					HaloLogger.logE("speech_info","onCalculateRouteFailure"+errorInfo);
				}

				@Override
				public void onArrivedWayPoint(int arg0) {
					HaloLogger.logE("speech_info","onArrivedWayPoint");
				}

				@Override
				public void onArriveDestination() {
					clearNaviStatus();
					if (mNaviNotifier != null) {
						mNaviNotifier.onArriveDestination();
					}
				}


				@Override
				public void onGpsOpenStatus(boolean arg0) {
				}

				@Override
				public void onNaviInfoUpdate(NaviInfo info) {
					if (mNaviNotifier != null) {
						mNaviNotifier.onNaviInfo(info);
					}
					if (mIsSimulatelyNavi) {
						// TODO 模拟导航中调用onLocationChange来达到绘制HUDWay效果
						AMapNaviLocation location = new AMapNaviLocation();
						NaviLatLng naviLatLng = new NaviLatLng(info.getCoord()
								.getLatitude(), info.getCoord().getLongitude());
						location.setCoord(naviLatLng);
						location.setSpeed(SIMULATION_SPEED);
						location.setBearing(info.getDirection());
						location.setTime(System.currentTimeMillis());
						onLocationChange(location);
					}
				}

				@Override
				@Deprecated
				public void OnUpdateTrafficFacility(
						TrafficFacilityInfo trafficInfo) {

				}

				@Override
				public void OnUpdateTrafficFacility(
						AMapNaviTrafficFacilityInfo trafficFacilityInfo) {

				}

				@Override
				public void hideLaneInfo() {

				}

				@Override
				public void onCalculateMultipleRoutesSuccess(int[] arg0) {

				}



				@Override
				public void showCross(AMapNaviCross amapNaviCross) {
					if (amapNaviCross != null) {
						if (mNaviNotifier != null) {
							mNaviNotifier.onCrossShow(amapNaviCross.getBitmap());
						}
					}
				}

				@Override
				public void hideCross() {
					if (mNaviNotifier != null) {
						mNaviNotifier.onCrossHide();
					}
				}

				@Override
				public void showLaneInfo(AMapLaneInfo[] infos, byte[] arg1,
						byte[] arg2) {
				}

				@Override
				@Deprecated
				public void onNaviInfoUpdated(AMapNaviInfo arg0) {
				}
			};
		}
		return mAmapNaviListener;
	}

	// --------------------------------------route search callback start----------------------------------------//
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode!=AMAP_MAP_CODE_SUCCESS || result == null || result.getPaths() == null
				|| result.getPaths().size() == 0) {
			if(mAmapNaviListener!=null){
				mNaviPlanNotifier.onCalculateRouteFailure(rCode);
			}
			return;
		}
		DrivePath path = result.getPaths().get(0);
		RouteSearchData data = new RouteSearchData();
		// 距离，耗时，收费总额，策略名，途径路段，--红绿灯个数
		data.driveRouteResult = result;
		data.distance = path.getDistance();
		data.duration = path.getDuration();
		data.charge = path.getTolls();
		data.strategy = path.getStrategy();
		data.roads = new ArrayList<String>();
		data.trafficLights = 0;
		for (DriveStep step : path.getSteps()) {
			String road = step.getRoad();
			if (!data.roads.contains(road)) {
				data.roads.add(road);
			}
		}
		// 距离最短shortestdistance，速度最快default，参考交通信息最快fastesttime
		String strategy = path.getStrategy();
		if ("速度最快".equalsIgnoreCase(strategy)) {
			if (mDefaultStrategy == null) {
				// route search first,caculated second
				mDefaultStrategy = data;
			} else {
				mDefaultStrategy.setRouteSearchResult(data);
			}
		} else if ("参考交通信息最快".equalsIgnoreCase(strategy)) {
			if (mFastestTime == null) {
				// route search first,caculated second
				mFastestTime = data;
			} else {
				mFastestTime.setRouteSearchResult(data);
			}
		} else if ("距离最短".equalsIgnoreCase(strategy)) {
			if (mShortestDistance == null) {
				// route search first,caculated second
				mShortestDistance = data;
			} else {
				mShortestDistance.setRouteSearchResult(data);
			}
		}
		routeSearchAndCalculateCount++;
		go2ChooseNaviStrategy();
	}

	// TODO 什么时候展示路径数据比较合适，共有6处回调需要处理之后才到展示界面
	private void go2ChooseNaviStrategy() {
		if (routeSearchAndCalculateCount == 3) {
			routeSearchAndCalculateCount = 0;
			if (mNaviPlanNotifier != null) {
				mNaviPlanNotifier.onCalculateRouteSuccess(mShortestDistance, mFastestTime, mDefaultStrategy);
			}
			//chooseNaviStrategy();
		}
	}


	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
	}

	public static class RouteSearchData {
		public AMapNaviPath naviPath;
		public DriveRouteResult driveRouteResult;
		public Float distance;
		public Long duration;
		// TODO: sen 16/6/1 onCalculateRouteSuccess 中的交通灯可能失效，后期更新高德搜索服务jar包后，在新API中增加此方法
		public Integer trafficLights;
		public Float charge;
		public String strategy;
		public List<String> roads;

		public void setRouteSearchResult(RouteSearchData data) {
//			if (data.naviPath != null) {
//				this.naviPath = data.naviPath;
//			}
			if (data.driveRouteResult != null) {
				this.driveRouteResult = data.driveRouteResult;
			}
			if (data.distance != null) {
				this.distance = data.distance;
			}
			if (data.duration != null) {
				this.duration = data.duration;
			}
			if (data.trafficLights != null) {
				this.trafficLights = data.trafficLights;
			}
			if (data.charge != null) {
				this.charge = data.charge;
			}
			if (data.strategy != null) {
				this.strategy = data.strategy;
			}
			if (data.roads != null) {
				this.roads = data.roads;
			}
		}

		public void setNaviPath(AMapNaviPath naviPath) {
			this.naviPath = naviPath;
		}
	}

/*	private int parseStrategy(int old){
		int mode = 0;
		try {
			if(old == AMapNavi.DrivingDefault){
				mode = mAMapNavi.strategyConvert(false,false,false,false,false);
			}else if(old == AMapNavi.DrivingShortDistance){
				mode = mAMapNavi.strategyConvert(false,true,false,false,false);
			}else if(old == AMapNavi.DrivingFastestTime){
				mode = mAMapNavi.strategyConvert(true,false,false,false,false);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		HaloLogger.logE("speech_info","strategy old:"+old+"new:"+mode);
		return mode;
	}*/

/*	// 获取三种策略的返回的结果
	private void routeSearch() {
		RouteSearch routeSearch = new RouteSearch(mAppContext);
		List<LatLonPoint> passByPoint = new ArrayList<>();
		//TODO : 测试在北京演示的导航数据
		//TODO : from:MeePark(116.494914,39.989676) through:北京市朝阳区彩虹路6号(116.498246,39.990673) to:望京SOHO(116.480665,39.996404)
		HaloLogger.logE("speech_info","test start");
		mStartPointLatLon = new LatLonPoint(39.989676,116.494914);
		mEndPointLatLon = new LatLonPoint(39.996404,116.480665);
		//TODO : 测试在北京演示的导航数据 end

		FromAndTo ft = new FromAndTo(mStartPointLatLon, mEndPointLatLon);
		// strategy 1:least time
		// route search ,default is speed first
		int mode = AMapNavi.DrivingDefault;
		DriveRouteQuery query = new DriveRouteQuery(ft, mode, passByPoint, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// strategy 2:shortest distance
		// route search ,short distance
		mode = AMapNavi.DrivingShortDistance;
		query = new DriveRouteQuery(ft, mode, passByPoint, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// strategy 3:avoid congestion
		// route search ,time first ,avoid
		mode = AMapNavi.DrivingFastestTime;
		query = new DriveRouteQuery(ft, mode, passByPoint, null, null);
		routeSearch.setRouteSearchListener(this);
		routeSearch.calculateDriveRouteAsyn(query);

		// calculate route
		*//***!!!!! debug 业务需求不更新给绿灯，规划路径时，不用导航类规划路径，开始导航时再设置导航策略***//*
		//configurateNaviInfo();
	}

	private void configurateNaviInfo(int strategy) {
		PoiItem endpoiItem = mPoiList.get(mChoosePoiIndex);
		mEndPointLatLon = endpoiItem.getLatLonPoint();
		mSearchName = endpoiItem.getTitle();
		mEndPoints.clear();
		mEndPointNaviLatLng = new NaviLatLng(mEndPointLatLon.getLatitude(), mEndPointLatLon.getLongitude());
		mEndPoints.add(mEndPointNaviLatLng);
		if (!mHasGetGPS) {
			startLocationAndInit();
			return;
		}
		if (!mHasEndpoint) {
			endSearchResult();
			return;
		}

		//TODO : 测试在北京演示的导航数据 start
		//TODO : from:MeePark(116.494914,39.989676) to:望京SOHO(116.480665,39.996404)
		//TODO : through:西门(116.495082,39.992747)
		//TODO : through:停车场(国风上观东南)(116.485589,39.990745)
		//TODO : through:停车场(卡特彼勒大厦B座西)(116.48109,39.993777)
		//TODO : through:停车场(望京SOHO中心P2西)(116.47893,39.997114)
		mStartPoints = new ArrayList<>();
		mStartPoints.add(new NaviLatLng(39.9789383918,116.4924464486));
		mWayPoints = new ArrayList<>();
	*//*	mWayPoints.add(new NaviLatLng(39.992747,116.495082));
		mWayPoints.add(new NaviLatLng(39.990745,116.485589));
		mWayPoints.add(new NaviLatLng(39.993777,116.48109));
		mWayPoints.add(new NaviLatLng(39.997114,116.47893));
		mEndPoints = new ArrayList<>();*//*
		mEndPoints.add(new NaviLatLng(39.996404,116.480665));
		//TODO : 测试在北京演示的导航数据 end 116.498246,39.990673

		boolean calSuccess = mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, mWayPoints, strategy);
		HaloLogger.logE("speech_info","configurateNaviInfo called, calculateDriveRoute = "+calSuccess);
	}*/

	// --------------------------------------route search callback
	// end------------------------------------------//

	// TODO test location change and navi info update start
	public void testLocationChange(AMapNaviLocation location) {
		if (mAmapNaviListener != null) {
			mAmapNaviListener.onLocationChange(location);
		}
	}

	public void testNaviInfoUpdate(NaviInfo info) {
		if (mAmapNaviListener != null) {
			mAmapNaviListener.onNaviInfoUpdate(info);
		}
	}
	// test location change and navi info update end

}
