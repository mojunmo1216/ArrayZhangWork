package com.haloai.hud.hudendpoint.fragments.amap;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

/**
 * Created by zhangrui on 16/10/26.
 */
public class HudAMapFragmentNative extends Fragment{
    private AMapNaviView mAMapNaviView;
    private RelativeLayout rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (RelativeLayout) View.inflate(getActivity(), R.layout.layout_native_amap, null);
     //   mAMapNaviView = (AMapNaviView) rootView.findViewById(R.id.native_amap_navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
  //      mAMapNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);// 正北模式，
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setNaviNight(true);                 // 夜间模式
        options.setCrossDisplayEnabled(false);      // 路口放大图
        options.setCrossDisplayShow(false);
        options.setMonitorCameraEnabled(false);     // 是否显示摄像头监控图标
        options.setTrafficBarEnabled(false);        // 右边光柱图
        options.setCompassEnabled(false);           // 是否显示罗盘

        options.setLayoutVisible(false);
        options.setAutoChangeZoom(true);
        options.setAutoDrawRoute(true);
        options.setLaneInfoShow(false);
        options.setTilt(40);
        //设置牵引线颜色,-1为不显示
        options.setLeaderLineEnabled(Color.WHITE);
        options.setAutoDrawRoute(true);

        options.setLaneInfoShow(false);
        options.setRouteListButtonShow(false);
        options.setSettingMenuEnabled(false);
        options.setTrafficLayerEnabled(false);

        mAMapNaviView.setViewOptions(options);
        removeNativeAMapView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAMapNaviView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行

    }


    public void removeNativeAMapView(){
        if (mAMapNaviView != null && mAMapNaviView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mAMapNaviView.getParent();
            parent.removeView(mAMapNaviView);
        }
    }

    public void addNativeAMapView(){
        if (mAMapNaviView != null) {
            removeNativeAMapView();
            rootView.addView(mAMapNaviView);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            removeNativeAMapView();
        } else {
            addNativeAMapView();
            showAMap();
        }
        HaloLogger.logE("speech_info","onHiddenChanged:"+hidden);
    }

    public void showAMap(){
        if(EndpointsConstants.HudStartPoint != null){
            AMap map = mAMapNaviView.getMap();
            //  CameraPosition cp = map.getCameraPosition();
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(EndpointsConstants.HudStartPoint,19f);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }
    }
}
