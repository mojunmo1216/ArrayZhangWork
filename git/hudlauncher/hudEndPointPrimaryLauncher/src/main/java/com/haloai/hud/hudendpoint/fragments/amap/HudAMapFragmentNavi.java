package com.haloai.hud.hudendpoint.fragments.amap;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.services.route.DrivePath;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.mappath.MapPathManager;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter;
import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangrui on 16/6/13.
 */
public class HudAMapFragmentNavi extends Fragment {
    private static final String TAG = "HudAMapFragmentNavi";
    private static final boolean IS_MAP_VIEW = false;

    //导航相关
    private AMapNaviView mAmapNaviView;
    private AMap mAMap;

    private DrivePath mDrivePath;
    private boolean updatingRoutePath = false;
    private boolean amapInited = false;


    private TextView mPoiTitle;


    private TextView mDefaultTime, mTimeTime, mNojamTime, mStrategyTimeDownNum;
    private TextView mDefaultDist, mTimeDist, mNojamDist,mDefaultTitle, mTimeTitle, mNojamTitle;
    private FrameLayout mMainView;
    private RelativeLayout mPoiView, mPoiListView, mNewStrategyView;
    private AMapNavigationSDKAdapter.RouteSearchData mShortestDistance = null;
    private AMapNavigationSDKAdapter.RouteSearchData mFastestTime = null;
    private AMapNavigationSDKAdapter.RouteSearchData mDefaultStrategy = null;
  //  private LinearLayout mPointsView;


    protected static final int MAX_ITEMS_COUNT = 3;

    private List<View> poiList, poiOldList;
    private List<String> mPOIList;
    private ImageView mStrategyRouteView, mStrategyRouteViewleft, mStrategySelectView ,mPOISelectView;
    private List<ImageView> mStrategyRouteViewList = new ArrayList<ImageView>();
    private MapPathManager mMapPathManager;
    private AMapNavigationSDKAdapter.RouteSearchData[] mRouteSearchDatas;
    private boolean mStrategyRouteImageViewAnimatingFlag = false;
    private AnimatorSet mStrategyRouteViewAnimtionSet = null;
    private int mCurrentRequestStragetyIndex = -1;
    private int mRepeatLoadingTimes = 0;
    private int offset = 0;
    private Bitmap mFastestStrategyBitmap, mShortestStrategyBitmap, mAvoidCongestionStrategyBitmap;

    private int curStregy = -1;


    private void initPathManager() {
        mMapPathManager = new MapPathManager(getActivity(), mStrategyRouteView);
        mMapPathManager.setOnCreateStrategyBitmaListener(new MapPathManager.OnCreateStrategyBitmapListener() {
            @Override
            public void onCreateStrategyBitmap(Bitmap bitmap) {
                switch(mCurrentRequestStragetyIndex){
                    case 0:
                        mFastestStrategyBitmap = Bitmap.createBitmap(bitmap);
                        mStrategyRouteViewList.get(1).setImageBitmap(mFastestStrategyBitmap);
                        mCurrentRequestStragetyIndex++;
                        updateRoutePath(mRouteSearchDatas[mCurrentRequestStragetyIndex].driveRouteResult.getPaths().get(0));
                        break;
                    case 1:
                        mAvoidCongestionStrategyBitmap = Bitmap.createBitmap(bitmap);
                        mCurrentRequestStragetyIndex++;
                        updateRoutePath(mRouteSearchDatas[mCurrentRequestStragetyIndex].driveRouteResult.getPaths().get(0));
                        break;
                    case 2:
                        mShortestStrategyBitmap = Bitmap.createBitmap(bitmap);
                        mCurrentRequestStragetyIndex = -1;
                        break;
                    default:
                        HaloLogger.logE("HudAMapFragmentNavi", "图片回调异常");
                }
                if (curStregy != -1 && curStregy <= mCurrentRequestStragetyIndex){
                    startStrategyViewAnimation(curStregy);
                }
            }
        });
    }

    /**
     * 切换路线图动画
     *
     * @param index 目标路线角标 0：最快，1：躲避拥堵， 2：最短
     */
    public void startStrategyViewAnimation(final int index) {
        // 根据index，设置bitmap
        Bitmap bitmap;
        switch (index) {
            case 0:
                bitmap = mFastestStrategyBitmap;
                break;
            case 1:
                bitmap = mAvoidCongestionStrategyBitmap;
                break;
            case 2:
                bitmap = mShortestStrategyBitmap;
                break;
            default:
                return;
        }

        if (mRepeatLoadingTimes == 15) {
            mRepeatLoadingTimes = 0;
            HaloLogger.logE("HudAMapFragmentNavi","what happend？？？");
            return;
        }


        if (mStrategyRouteImageViewAnimatingFlag) {
            if (mStrategyRouteViewAnimtionSet != null) {
                mStrategyRouteViewAnimtionSet.end();
            }
        }
        final ImageView imageView = mStrategyRouteViewList.get(1);
        mStrategyRouteViewAnimtionSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "TranslationX", imageView.getTranslationX(), imageView.getTranslationX() + DisplayUtil.dip2px(getActivity(), 294.66f)).setDuration(500);

        ImageView imageViewleft = mStrategyRouteViewList.get(0);

        if (bitmap == null) {
            HaloLogger.logE("HudAMapFragmentNavi", "图片未加载完成，startStrategyViewAnimation延迟执行");
          /*  new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(200);
                    startStrategyViewAnimation(index);
                    mRepeatLoadingTimes ++;
                }
            }).start();*/
            curStregy = index;
        }else {
            imageViewleft.setImageBitmap(Bitmap.createBitmap(bitmap));
        }
        ObjectAnimator animatorleft = ObjectAnimator.ofFloat(imageViewleft, "TranslationX", imageViewleft.getTranslationX(), imageViewleft.getTranslationX() + DisplayUtil.dip2px(getActivity(), 294.66f)).setDuration(500);

        mStrategyRouteViewAnimtionSet.playTogether(animator, animatorleft);
        mStrategyRouteViewAnimtionSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mStrategyRouteImageViewAnimatingFlag = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator.ofFloat(imageView, "TranslationX", imageView.getTranslationX(), imageView.getTranslationX() - 2 * DisplayUtil.dip2px(getActivity(), 294.66f)).setDuration(0).start();
                ImageView remove = mStrategyRouteViewList.remove(1);
                mStrategyRouteViewList.add(0, remove);
                mStrategyRouteImageViewAnimatingFlag = false;
            }
        });
        mStrategyRouteViewAnimtionSet.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initView(inflater, savedInstanceState);
        initPathManager();
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAmapNaviView != null){
            mAmapNaviView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAmapNaviView != null) {
            mAmapNaviView.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mAmapNaviView != null) {
            mAmapNaviView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAmapNaviView != null) {
            mAmapNaviView.onDestroy();
        }
    }

    private void initView(LayoutInflater inflater, Bundle savedInstanceState) {
        mMainView = (FrameLayout) View.inflate(getActivity(),
                R.layout.fragment_amap_navi, null);
        mPoiView = (RelativeLayout) mMainView
                .findViewById(R.id.amap_poilist_layout);

        mStrategySelectView = (ImageView) mMainView
                .findViewById(R.id.route_selection_image);
        mNewStrategyView = (RelativeLayout) mMainView
                .findViewById(R.id.amap_new_strategy_layout);
        mDefaultTime = (TextView) mMainView
                .findViewById(R.id.amap_new_deatfual_num);
        mTimeTime = (TextView) mMainView
                .findViewById(R.id.amap_new_time_num);
        mNojamTime = (TextView) mMainView
                .findViewById(R.id.amap_new_avoid_num);
        mStrategyTimeDownNum = (TextView) mMainView
                .findViewById(R.id.amap_new_strategy_time_num);
        mStrategyRouteView = (ImageView) mMainView
                .findViewById(R.id.amap_strategy_route_image);
        mStrategyRouteViewleft = (ImageView) mMainView
                .findViewById(R.id.amap_strategy_route_image_left);

        mPoiListView = (RelativeLayout) mMainView
                .findViewById(R.id.amap_poilist_layout_base);
       /* mPointsView = (LinearLayout) mMainView
                .findViewById(R.id.amap_poilist_points);*/

        mPOISelectView = (ImageView) mMainView
                .findViewById(R.id.poi_select_bg);
        mDefaultDist = (TextView) mMainView
                .findViewById(R.id.amap_new_deatfual_dist);
        mTimeDist = (TextView) mMainView
                .findViewById(R.id.amap_new_time_dist);
        mNojamDist = (TextView) mMainView
                .findViewById(R.id.amap_new_avoid_dist);

        mDefaultTitle = (TextView) mMainView
                .findViewById(R.id.amap_new_deatfual_title);
        mTimeTitle = (TextView) mMainView
                .findViewById(R.id.amap_new_time_title);
        mNojamTitle = (TextView) mMainView
                .findViewById(R.id.amap_new_avoid_title);
        /*mPoiTitle = (TextView) mMainView.findViewById(R.id.amap_poilist_title);*/

        mStrategyRouteViewList.add(mStrategyRouteViewleft);
        mStrategyRouteViewList.add(mStrategyRouteView);

        initMap(savedInstanceState);
    }

    private void initMap(Bundle savedInstanceState) {
//        mAmapNaviView = (AMapNaviView) mMainView.findViewById(R.id.main_amap_view);
        if(mAmapNaviView != null){
            mAmapNaviView.onCreate(savedInstanceState);// 此方法必须重写
            mAmapNaviView.setVisibility(View.INVISIBLE);
            mAMap = mAmapNaviView.getMap();
            mAMap.setOnMapLoadedListener(mOnMapLoadedListener);
            mAmapNaviView.getMap().setOnCameraChangeListener(mOnCameraChangeListener);
            initAMapNaviView();
            HaloLogger.logI(TAG, "正在初始化地图....");
        }

    }

  /*  public void removeAMapNaviView() {
        if (mAmapNaviView != null && mAmapNaviView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mAmapNaviView.getParent();
            parent.removeView(mAmapNaviView);
        }
    }

    public void addAMapNaviView() {
        if (mAmapNaviView != null) {
            removeAMapNaviView();
            mMainView.addView(mAmapNaviView);
        }
    }*/


    public void initAMapNaviView() {

        if (mAmapNaviView != null) {
            AMapNaviViewOptions viewOptions = mAmapNaviView.getViewOptions();
            viewOptions.setNaviNight(true);
       //     viewOptions.setNaviViewTopic(AMapNaviViewOptions.BLUE_COLOR_TOPIC);
            viewOptions.setCrossDisplayShow(false);
            viewOptions.setLayoutVisible(false);
            viewOptions.setAutoChangeZoom(true);
            mAmapNaviView.setViewOptions(viewOptions);
            mAmapNaviView.openNorthMode();
        }
    }

    AMap.OnCameraChangeListener mOnCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            initAMapNaviView();
        }

        @Override
        public void onCameraChangeFinish(CameraPosition arg0) {
            HudStatusManager instance = HudStatusManager.getInstance();
            if (updatingRoutePath) {
                if (mDrivePath != null && mMapPathManager != null) {
                    mMapPathManager.displayRoutePath(mDrivePath, instance.getHudPOIName());
                }
                updatingRoutePath = false;
                mDrivePath = null;
                HaloLogger.logI(TAG, "onCameraChangeFinish called");
            }
        }
    };

    private AMap.OnMapLoadedListener mOnMapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
            if (!amapInited) {
                amapInited = true;
                mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                HaloLogger.logI(TAG, "地图初始化成功");
//                mMapPathManager.setAMapProjection(mAMap.getProjection());
                mAmapNaviView.displayOverview();
            }

        }
    };



    public void updateRoutePath(final DrivePath drivePath) {
        if (drivePath != null && mMapPathManager != null) {
            HudStatusManager instance = HudStatusManager.getInstance();
            mMapPathManager.displayRoutePath(drivePath, instance.getHudPOIName());
        }
    }

    public void choosePOI(int index){
        if(mPOIList != null){
            int y = DisplayUtil.dip2px(this.getActivity(), 13) + (index + 1) * (offset - 4) + index * DisplayUtil.dip2px(this.getActivity(), 26);
            mPOISelectView.setY(y);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void displayPoiList(String searchingPoiName, List<String> lsPoiList,int currentPOIListIndex, int pageCount, int pageFlag) {
        List<View> poi = new ArrayList<View>();
        int[] imageIcon = {R.drawable.poi_number_first,
                R.drawable.poi_number_second,
                R.drawable.poi_number_third,
                R.drawable.poi_number_forth};
        int poiCount = lsPoiList.size() > MAX_ITEMS_COUNT ? MAX_ITEMS_COUNT
                : lsPoiList.size();
        mPOIList = lsPoiList;
        int imageX = DisplayUtil.dip2px(this.getActivity(), 200 + 330 * pageFlag);//425,
        int topY = DisplayUtil.dip2px(this.getActivity(), 17);// 10;
        offset = DisplayUtil.dip2px(this.getActivity(), Math.round((195 - (33 * poiCount)) / (poiCount + 1) / 1.5));//(195 - (30 * poiCount)) / (poiCount + 1);
        choosePOI(0);
        int imageHeight = DisplayUtil.dip2px(this.getActivity(), 26);//30;
        int mLayoutParamsW = DisplayUtil.dip2px(this.getActivity(), 16);//35;
        int mLayoutParamsH = DisplayUtil.dip2px(this.getActivity(), 2);//15;
        int mLayoutMargin = DisplayUtil.dip2px(this.getActivity(), 1);//10
        int textX = imageX + imageHeight + imageHeight / 2;
        int distanceX = imageX + imageHeight + imageHeight / 2 + DisplayUtil.dip2px(this.getActivity(), 220);
        mNewStrategyView.setVisibility(View.GONE);
     /*   mPointsView.removeAllViews();
        for (int i = 0; i < pageCount; i++) {
            View point = new View(getActivity());
            if (i == currentPOIListIndex) {
                point.setBackgroundResource(R.drawable.poi_page_checked);
            } else {
                point.setBackgroundResource(R.drawable.poi_page_unchecked);
            }
            android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mLayoutParamsW, mLayoutParamsH);
            params.setMargins(mLayoutMargin, 0, mLayoutMargin, 0);
            point.setLayoutParams(params);
            mPointsView.addView(point);
        }*/
        if (poiOldList != null) {
            for (View view : poiOldList) {
                mPoiListView.removeView(view);
            }
        }
       /* if (poiCount == 0) {
            mPoiTitle.setText(String.format("无法找到:\"%s\"", searchingPoiName));
        } else {*/
            for (int i = 0; i < poiCount; i++) {
                TextView poiText = (TextView) View.inflate(getActivity(),
                        R.layout.amap_poi_textview, null);
                ImageView poiImg = (ImageView) View.inflate(getActivity(),
                        R.layout.amap_poi_imageview, null);
                if (Build.VERSION.SDK_INT >= 21) {
                    poiText.setLetterSpacing(0.06f);
                }
                poiText.setX(textX);
                poiText.setY(topY + (i + 1) * (offset - 4)  + i * imageHeight);
                poiText.setText(lsPoiList.get(i));
                poiImg.setX(imageX);
                poiImg.setY(topY + (i + 1) * (offset - 4) + i * imageHeight);
                poiImg.setImageResource(imageIcon[i]);
                mPoiListView.addView(poiText);
                mPoiListView.addView(poiImg);
                poi.add(poiText);
                poi.add(poiImg);
               /* int dis = lsPoiList.get(i).getDistance();//TODO 暂时不做显示POI距离
                if(dis > 0){
                    String distance = dis > 1000 ? (float) (Math.round((dis / 1000.0) * 10)) / 10 +"公里" : dis+"米";
                    poiDistance.setText(distance);
                    mPoiListView.addView(poiDistance);
                    poiDistance.setX(distanceX);
                    poiDistance.setY(topY + (i + 1) * (offset - 4)  + i * imageHeight);
                    poi.add(poiDistance);
                }*/

            }
      //  }
        mPoiView.setVisibility(View.VISIBLE);
        if (pageFlag != 0) {
            if (poiList != null) {
                for (View view : poiList) {
                    HudAMapAnimation.startPoiViewAnimation(view, DisplayUtil.dip2px(this.getActivity(), -330 * pageFlag), 500);
                }
            }
            if (poi != null) {
                for (View view : poi) {
                    HudAMapAnimation.startPoiViewAnimation(view, DisplayUtil.dip2px(this.getActivity(), -330 * pageFlag), 500);
                }
            }
        } else {
            if (poiList != null) {
                for (View view : poiList) {
                    mPoiListView.removeView(view);
                }
            }
        }
        this.poiOldList = poiList;
        this.poiList = poi;
        mPoiView.bringToFront();
    }


    public void dislayStrategyView(AMapNavigationSDKAdapter.RouteSearchData shortestDistance,
                                   AMapNavigationSDKAdapter.RouteSearchData fastestTime, AMapNavigationSDKAdapter.RouteSearchData defaultStrategy) {
        this.mShortestDistance = shortestDistance;
        this.mFastestTime = fastestTime;
        this.mDefaultStrategy = defaultStrategy;
        mRouteSearchDatas = new AMapNavigationSDKAdapter.RouteSearchData[]{mDefaultStrategy, mFastestTime, mShortestDistance};
        setStrategyRouteInfo(mDefaultTime, mDefaultStrategy.duration,mDefaultDist,mDefaultStrategy.distance);
        setStrategyRouteInfo(mTimeTime, mFastestTime.duration,mTimeDist,mFastestTime.distance);
        setStrategyRouteInfo(mNojamTime, mShortestDistance.duration,mNojamDist,mShortestDistance.distance);
        mPoiView.setVisibility(View.GONE);
        mNewStrategyView.setVisibility(View.VISIBLE);
        mCurrentRequestStragetyIndex = 0;
        curStregy = -1;
        mShortestStrategyBitmap = null;
        mFastestStrategyBitmap = null;
        mAvoidCongestionStrategyBitmap = null;
        mStrategyRouteViewleft.setImageBitmap(null);
        mStrategyRouteView.setImageBitmap(null);
        displayChooseStrategy(0);
        updateRoutePath(mDefaultStrategy.driveRouteResult.getPaths().get(0));
        mNewStrategyView.bringToFront();
    }

    public void displayChooseStrategy(int index) {
        setCountDown(9);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mStrategySelectView.getLayoutParams();
        params.leftMargin = DisplayUtil.dip2px(getActivity(), 12 + index * 75);
        mStrategySelectView.setLayoutParams(params);
        int d_color = 0 == index ? Color.WHITE : Color.GRAY;
        int t_color = 1 == index ? Color.WHITE : Color.GRAY;
        int n_color = 2 == index ? Color.WHITE : Color.GRAY;

        float d_size = 0 == index ? 19f : 18f;
        float t_size = 1 == index ? 19f : 18f;
        float n_size = 2 == index ? 19f : 18f;

        mDefaultTitle.setTextColor(d_color);
        mDefaultTime.setTextColor(d_color);
        mDefaultDist.setTextColor(d_color);
        mDefaultTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,d_size);
        mDefaultTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,d_size-2f);
        mDefaultDist.setTextSize(TypedValue.COMPLEX_UNIT_SP,d_size-4f);

        mTimeTitle.setTextColor(t_color);
        mTimeTime.setTextColor(t_color);
        mTimeDist.setTextColor(t_color);
        mTimeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,t_size);
        mTimeTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,t_size-2f);
        mTimeDist.setTextSize(TypedValue.COMPLEX_UNIT_SP,t_size-4f);

        mNojamTitle.setTextColor(n_color);
        mNojamTime.setTextColor(n_color);
        mNojamDist.setTextColor(n_color);
        mNojamTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,n_size);
        mNojamTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,n_size-2f);
        mNojamDist.setTextSize(TypedValue.COMPLEX_UNIT_SP,n_size-4f);

    }

    private void setStrategyRouteInfo(TextView text, long duration,TextView dist,float distance) {
        long min = duration / 60;
        String string;
        if (min < 60) {
            string = min + "min";
        } else if (min >= 60 && min < 600) {
            string = (float) (Math.round((min / 60.0) * 10)) / 10 + "h";
        } else {
            string = min / 60 + "h";
        }
        text.setText(string);
        String distText ;
        if(distance < 1000){
            distText = (int)distance+"m";
        }else if(distance>= 1000 && distance <100000){
            distText = (float) (Math.round((distance / 1000.0) * 10)) / 10 +"km";
        }else{
            distText = (int)distance / 1000 +"km";
        }
        dist.setText(distText);
    }


    public void setCountDown(int time) {
        if (time > -1 && time < 10) {
            mStrategyTimeDownNum.setText(time + "");
        }
    }

}
