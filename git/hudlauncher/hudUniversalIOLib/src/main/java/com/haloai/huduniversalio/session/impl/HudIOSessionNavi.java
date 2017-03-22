package com.haloai.huduniversalio.session.impl;

import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.PoiItem;
import com.haloai.hud.model.v2.NaviRouteInfo;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter.RouteSearchData;
import com.haloai.hud.navigation.INaviPlatformInputer;
import com.haloai.hud.navigation.NavigationSDKAdapter;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.R;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.outputer.IHudOutputer.HudOutputContentType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionNaviCallback;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWords;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsListPage;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsNaviStrategy;
import com.haloai.huduniversalio.speech.ISpeechParserNavi;
import com.haloai.huduniversalio.speech.ISpeechParserNavi.DestinationInfo;

import java.util.ArrayList;
import java.util.List;

public class HudIOSessionNavi extends HudIOSession implements INaviPlatformInputer{ // implements
    // NavigationQueryCallback
    // {
    private String TAG = HudIOSessionNavi.class.getName();

    /*
     * 导航Session步骤
     *  1. 用户语音输入导航目的地地址，Session开始 —— SESSION_STATE_NAVI_START_SESSION；
     * 	2. 获得目的地址，等待外部调用导航SDK查询目的地 —— SESSION_STATE_NAVI_WAITING_FOR_NAVISDK_RETURN_POI_LIST
     *  3. 获得POI列表，等待用户说第几个 —— SESSION_STATE_NAVI_WAITING_FOR_CHOOSE_POI；
     *  4. 获得用户第几个输入，等待外部调用导航SDK查询路径策略 —— SESSION_STATE_NAVI_WAITING_FOR_NAVISDK_RETURN；
     *  5. 获得策略路径选项，等待用户说第几个 —— SESSION_STATE_NAVI_WAITING_FOR_CHOOSE_STRATEGY
     *  6. 获得策略选择，回调外部，Session —— SESSION_STATE_IDLE
     */
    private static final int SESSION_STATE_1_NAVI_START_SESSION = SESSION_STATE_BASE + 1;
    private static final int SESSION_STATE_2_NAVI_CHOOSE_POI = SESSION_STATE_BASE + 2;
    private static final int SESSION_STATE_3_NAVI_CHOOSE_STRATEGY = SESSION_STATE_BASE + 3;

    private NavigationSDKAdapter mNavAdapter;

    private IHudIOSessionNaviCallback mSessionCallback;
    private int mPoiListSize;
    private int mIndex =0;
    private static final int POI_MAX_NUM = 3;

    //新增
//    private List<String> mPOIList;
    private List<String> mPOIList;
    private int mCurrentPageIndex;
    private int mPageSize;

    //
    public NaviRouteInfo mPhoneRouteInfo;
    public boolean mPhoneInput = false;//是否为手机输入 默认为NO

    public HudIOSessionNavi(HudIOController hudIOController) {
        super(hudIOController);
        mIOSessionType = HudIOSessionType.NAVI;
        mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserNavi();

        mNavAdapter =  NavigationSDKAdapter.getNavigationAdapter(NavigationSDKAdapter.NAVIGATION_SDK_AMAP,hudIOController.getmContext());

        mNavAdapter.setNaviPlanNotifier(this);

    }

    @Override
    public void onWakeupWord(String wakeupWord) {
        if (mCurrentSpeechWakeupWords == null) {
            // TODO 错误播报
            return;
        }

        int res = mCurrentSpeechWakeupWords.getWakeupResult(wakeupWord);
        if (this.mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI) {
            if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
                if (res <= SpeechWakeupWordsListPage.WAKEUP_RESULT_ITEM_3 && res >= SpeechWakeupWordsListPage.WAKEUP_RESULT_ITEM_1) {
                    if ((res - 1 + mCurrentPageIndex * POI_MAX_NUM) < mPoiListSize){
                        // TODO 函数名不恰当，该方法并不会开始导航，而是进行路线规划
                        mNavAdapter.startNavigate(res-1 + mCurrentPageIndex * POI_MAX_NUM);
                        String poiNameToSearch = mPOIList.get(res - 1 + mCurrentPageIndex * POI_MAX_NUM);
                        mSessionCallback.naviSessionSetDestination(poiNameToSearch,res-1,true);
                        HaloLogger.postI(EndpointsConstants.NAVI_TAG,"speech POIindex:"+res+" POIName"+poiNameToSearch);
                    }

                }else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_EXIT) {
                    endIOSession(true);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"speech exit_poi");
                }/*else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_NEXT_PAGE) {
                    mCurrentPageIndex = mCurrentPageIndex + 1> mPageSize - 1 ? 0 : mCurrentPageIndex + 1;
                    mSessionCallback.naviSessionPoiItemNextPage(getCurrentPageList(mPOIList,mCurrentPageIndex),mCurrentPageIndex);
                } else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_PREV_PAGE) {
                    mCurrentPageIndex = mCurrentPageIndex-1 < 0 ? mPageSize - 1 : mCurrentPageIndex-1;
                    mSessionCallback.naviSessionPoiItemPrevPage(getCurrentPageList(mPOIList,mCurrentPageIndex),mCurrentPageIndex);
                }*/
            } else {
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_poi_pick), HudOutputContentType.CONTENT_TYPE_CUSTOM, true);
            }
        } else if (this.mSessionState == SESSION_STATE_3_NAVI_CHOOSE_STRATEGY) {
            if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
                if (res == SpeechWakeupWordsNaviStrategy.WAKEUP_RESULT_FASTEST_STRATEGY || res == SpeechWakeupWordsNaviStrategy.WAKEUP_FASTEST){
                    mIndex =0;
                    mNavAdapter.setStrategy(AMapNavi.DrivingDefault);
                    mSessionCallback.naviSessionChooseStrategy(mIndex);
                }else if (res == SpeechWakeupWordsNaviStrategy.WAKEUP_SHORTEST || res == SpeechWakeupWordsNaviStrategy.WAKEUP_RESULT_SHORTEST_STRATEGY){
                    mIndex =1;
                    mNavAdapter.setStrategy(AMapNavi.DrivingFastestTime);
                    mSessionCallback.naviSessionChooseStrategy(mIndex);
                }else if (res == SpeechWakeupWordsNaviStrategy.WAKEUP_AVOID_CONGESTION || res == SpeechWakeupWordsNaviStrategy.WAKEUP_RESULT_AVOID_CONGESTION_STRATEGY){
                    mIndex =2;
                    mNavAdapter.setStrategy(AMapNavi.DrivingShortDistance);
                    mSessionCallback.naviSessionChooseStrategy(mIndex);
                }else if (res == SpeechWakeupWordsNaviStrategy.WAKEUP_START_NAVI || res == SpeechWakeupWordsNaviStrategy.WAKEUP_START) {
                    endIOSession(false);
                    mSessionCallback.naviSessionStart();
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"speech 开始导航 !!!");
                } else if (res == SpeechWakeupWordsNaviStrategy.WAKEUP_RESULT_EXIT){
                    endIOSession(true);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"speech exit_strategy");
                }
            } else {
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        mCurrentSpeechWakeupWords.getPromptText(), HudOutputContentType.CONTENT_TYPE_CUSTOM, true);
            }
        }

    }

    @Override
    public void onProtocol(String protocol) {
        ISpeechParserNavi speechParserNavi = (ISpeechParserNavi) mSpeechParser;
        int res = speechParserNavi.parseSpeechText(protocol);
        if (res == ISpeechParserNavi.PARSE_RES_CODE_NAVI_DESTINATION_NAME) {
            DestinationInfo destiInfo = speechParserNavi.getParseResultDestinationInfo();
            String category = speechParserNavi.getParseResultRoundCategory();
            this.mSessionState = SESSION_STATE_1_NAVI_START_SESSION;
            HaloLogger.logE("speech_info", " category: " + category);
            if (category == null) {
                if (destiInfo != null && destiInfo.name != null && !destiInfo.name.isEmpty()) {
                    //mSessionCallback.naviSessionSearchDestination(destiInfo.city, destiInfo.name);
                    mNavAdapter.startSearchDestination(destiInfo.city,destiInfo.name);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"目的地搜索:"+destiInfo.name);
                }
            } else if (category != null) {
                mNavAdapter.startSearchDestinnationRound(category);
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"周边搜索:"+category);
            }

        } else {
            this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                    SpeechResourceManager.getInstanse().getString(R.string.tts_session_map_no_found), HudOutputContentType.CONTENT_TYPE_CUSTOM, true);
            endIOSession(false);
        }
    }

    @Override
    public boolean onRemoteKey(int keyCode) {
        if (mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI && mPoiListSize > POI_MAX_NUM) {
            /*if (keyCode == HudIOConstants.INPUTER_REMOTER_LEFT) {
                mCurrentPageIndex = mCurrentPageIndex-1 < 0 ? mPageSize - 1 : mCurrentPageIndex-1;
                mSessionCallback.naviSessionPoiItemPrevPage(getCurrentPageList(mPOIList,mCurrentPageIndex),mCurrentPageIndex);
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"remote prev_poi");
                return true;
            } else if (keyCode == HudIOConstants.INPUTER_REMOTER_RIGHT) {
                mCurrentPageIndex = mCurrentPageIndex + 1> mPageSize - 1 ? 0 : mCurrentPageIndex + 1;
                mSessionCallback.naviSessionPoiItemNextPage(getCurrentPageList(mPOIList,mCurrentPageIndex),mCurrentPageIndex);
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"remote next_poi");
                return true;
            }*/
        }
        return false;
    }

    @Override
    public boolean onGestureKey(int gestureCode) {
        if(mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI){
            if(gestureCode == HudIOConstants.INPUTER_GESTURE_V){
                if (mPoiListSize>0) {
                    mNavAdapter.startNavigate(mIndex);
                    String poiNameToSearch = mPOIList.get(mIndex);
                    mSessionCallback.naviSessionSetDestination(poiNameToSearch,mIndex,true);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_v poi");
                }
                return true;
            }else if(gestureCode == HudIOConstants.INPUTER_GESTURE_PALM){
                endIOSession(true);
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_plam exit_poi");
                return true;
            }
           /* if(mPoiListSize>POI_MAX_NUM){
                if(type == HudIOConstants.INPUTER_GESTURE_SLIDE){
                    mCurrentPageIndex = mCurrentPageIndex + 1> mPageSize - 1 ? 0 : mCurrentPageIndex + 1;
                    mSessionCallback.naviSessionPoiItemNextPage(getCurrentPageList(mPOIList,mCurrentPageIndex),mCurrentPageIndex);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_slide next_poi");
                    return true;
                }
            }else {
                if(type == HudIOConstants.INPUTER_GESTURE_SLIDE){
                    endIOSession(true);
                    HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_slide exit_poi");
                    return true;
                }
            }*/
        }else if(mSessionState == SESSION_STATE_3_NAVI_CHOOSE_STRATEGY){
           /* if(type == HudIOConstants.INPUTER_GESTURE_SLIDE){
                mIndex++;
                mIndex = mIndex > 2 ? 0: mIndex;
                mSessionCallback.naviSessionChooseStrategy(mIndex);
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_slide next_strategy");
                return true;
            }else*/
            if(gestureCode == HudIOConstants.INPUTER_GESTURE_V){
                endIOSession(false);
                mSessionCallback.naviSessionStart();
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"Gesture_V 开始导航 !!!");
                return true;
            }else if(gestureCode == HudIOConstants.INPUTER_GESTURE_PALM){
                endIOSession(true);

            }
        }
        return false;
    }

    @Override
    public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
        if(mSessionState == SESSION_STATE_3_NAVI_CHOOSE_STRATEGY){
            int res = result.getAreaIndex();
            if(res != mIndex && res >= 0 && res <3){
                mSessionCallback.naviSessionChooseStrategy(res);
                mIndex = res;
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_point STRATEGY");
                return true;
            }
        }else if(mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI){
            int res = result.getAreaIndex();
            if(res != mIndex && res >= 0 && res <mPoiListSize){
                mSessionCallback.naviSessionChoosePOI(res);
                mIndex = res;
                HaloLogger.postI(EndpointsConstants.NAVI_TAG,"gesture_point POI");
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryGesturePointArea() {
        if(mSessionState == SESSION_STATE_3_NAVI_CHOOSE_STRATEGY){
            return 3;
        }else if(mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI){
            return mPoiListSize;
        }
        return -1;
    }

    @Override
    public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
        if (!(sessionCallback instanceof IHudIOSessionNaviCallback)) {
            throw new IllegalArgumentException(
                    "Requirst the IHudIOSessionNaviCallback");
        }
        this.mSessionCallback = (IHudIOSessionNaviCallback) sessionCallback;
    }

    @Override
    public void continueSession() {
        if (mSessionState == SESSION_STATE_1_NAVI_START_SESSION) {
            mSessionState = SESSION_STATE_2_NAVI_CHOOSE_POI;
        } else if (mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI) {
            mSessionState = SESSION_STATE_3_NAVI_CHOOSE_STRATEGY;
        }
        setWakeupWordsForCurrentState(mSessionState);

    }

    private void setWakeupWordsForCurrentState(int currentState) {
        if (currentState == SESSION_STATE_2_NAVI_CHOOSE_POI) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsListPage();
        } else if (currentState == SESSION_STATE_3_NAVI_CHOOSE_STRATEGY) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsNaviStrategy();
        }

        if (mCurrentSpeechWakeupWords != null) {
            mSpeechEngine.setWakupWords(getWakeupWordsWithPOISize());
        }
    }

    private List<String> getWakeupWordsWithPOISize() {
        if (mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI) {
            List<String> wakeUpList = new ArrayList<String>();
            if (mPoiListSize <= POI_MAX_NUM && mPoiListSize > 0) {
                wakeUpList.addAll(mCurrentSpeechWakeupWords.getAllWakeupWords());
                for (int i = mPoiListSize + 1; i <= SpeechWakeupWordsListPage.WAKEUP_RESULT_PREV_PAGE; i++) {
                    wakeUpList.remove(wakeUpList.size() - 1);
                }
                return wakeUpList;
            }
        }
        return mCurrentSpeechWakeupWords.getAllWakeupWords();
    }


    public void callNaviSDKSearchRoute(NaviRouteInfo routeInfo){  //phoneinputer 调用
        mPhoneInput = true;
        mPhoneRouteInfo = routeInfo;
        int driveMode = AMapNavi.DrivingDefault;
        if (mPhoneRouteInfo.getNaviDrivingStrategy() == Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.DEFAULT) {
            driveMode = AMapNavi.DrivingDefault;
        } else if (mPhoneRouteInfo.getNaviDrivingStrategy() == Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.FASTEST_TIME) {
            driveMode = AMapNavi.DrivingFastestTime;
        } else if (mPhoneRouteInfo.getNaviDrivingStrategy() == Phone2HudMessagesProtoDef.NaviDrivingStrategyProto.SHORT_DISTANCE) {
            driveMode = AMapNavi.DrivingShortDistance;
        }
        double lat = mPhoneRouteInfo.getNaviDestinationLat();
        double lng = mPhoneRouteInfo.getNaviDestinationLng();
        String name = mPhoneRouteInfo.getNaviDestinationPoi();
        String speechText = "导航到"+name;
        this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(speechText,HudOutputContentType.CONTENT_TYPE_CUSTOM,false);
        mSessionCallback.naviSessionSetDestination(name,-1,false);
        mNavAdapter.startNavigate(lat,lng,name);
        mNavAdapter.setStrategy(driveMode);
        endIOSession(false);
        mSessionCallback.naviSessionStart();
        HaloLogger.postI(EndpointsConstants.PHONE_TAG,"手机导航:"+name+" strategy:"+driveMode);
    }




    protected List<String> getCurrentPageList(List<String> POIList,int currentPageIndex){
        List<String> tempList = new ArrayList<String>();
        for (int i = 0; i < POI_MAX_NUM; i++) {
            if (currentPageIndex * POI_MAX_NUM + i < mPoiListSize) {
                tempList.add(POIList.get(currentPageIndex * POI_MAX_NUM
                        + i));
            } else {
                break;
            }
        }
        return tempList;
    }


    @Override
    public void onPOIListData(String poiNameToSearch, List<String> poiList) {
        if (mSessionState == SESSION_STATE_1_NAVI_START_SESSION && poiList.size() >0) {
            mIndex = 0;
            mPOIList = poiList;
            mPoiListSize = poiList.size();
            mPageSize = mPoiListSize % 3 == 0 ? mPoiListSize / 3 : mPoiListSize / 3 + 1;
            mCurrentPageIndex = 0;
          //  mSessionCallback.onNaviSessionPOIResult(getCurrentPageList(mPOIList,mCurrentPageIndex), poiNameToSearch,mPageSize);
            mSessionCallback.onNaviSessionPOIResult(mPOIList, poiNameToSearch,mPageSize);
            continueSession();
            HaloLogger.postI(EndpointsConstants.NAVI_TAG,"POI列表:"+poiList.toString());
        }
    }

    @Override
    public void onDetailPOIListData(String poiNameToSearch, List<PoiItem> poiList) {
        /*if (mSessionState == SESSION_STATE_1_NAVI_START_SESSION) {
            mPOIList = poiList;
            mPoiListSize = poiList.size();
            mPageSize = mPoiListSize % POI_MAX_NUM == 0 ? mPoiListSize / POI_MAX_NUM : mPoiListSize / POI_MAX_NUM + 1;
            mCurrentPageIndex = 0;
            mSessionCallback.onNaviSessionPOIResult(getCurrentPageList(mPOIList,mCurrentPageIndex), poiNameToSearch,mPageSize);
            continueSession();
            HaloLogger.postI(EndpointsConstants.NAVI_TAG,"POI列表:"+poiList.toString());
        }*/
    }

    @Override
    public void onPOISearchFailed(String poiNameToSearch, String error) {
        if (mSessionState == SESSION_STATE_1_NAVI_START_SESSION) {
            mSessionCallback.onPOISearchFailed(poiNameToSearch, error);
            endIOSession(false);
            HudIOSessionDispatcher hudIOSessionDispatcher = (HudIOSessionDispatcher)mHudIOController.beginHudIOSession(HudIOSessionType.DISPATCHER);
            if(hudIOSessionDispatcher != null){
                hudIOSessionDispatcher.createNaviSessionDispatcher();
            }
            HaloLogger.postE(EndpointsConstants.NAVI_TAG,"POIFailed:"+poiNameToSearch+" Error:"+error);
        }
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        if (mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI) {
            mSessionCallback.onCalculateRouteFailure(errorInfo);
            HaloLogger.postE(EndpointsConstants.NAVI_TAG,"StrategyFailure:"+errorInfo);
        }else if (mSessionState == SESSION_STATE_IDLE) {
            if (mPhoneInput) {
                endIOSession(false);
                // （以下想添加的代码）发送消息回手机端 告诉手机端路线规划失败
            }
        }
    }

    @Override
    public void onCalculateRouteSuccess(RouteSearchData shortestDistance,
                                        RouteSearchData fastestTime, RouteSearchData defaultStrategy){
        if (mSessionState == SESSION_STATE_2_NAVI_CHOOSE_POI){
            mIndex = 0;
            mSessionCallback.onCalculateRoute(shortestDistance,fastestTime,defaultStrategy);
            mNavAdapter.setStrategy(AMapNavi.DrivingDefault);
            continueSession();
            HaloLogger.postI(EndpointsConstants.MAIN_TAG,"策略选择");
        }
    }

}
