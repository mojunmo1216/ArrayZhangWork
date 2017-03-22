package com.haloai.hud.hudendpoint.fragments.mock;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.hudendpoint.arwaylib.arway.INaviDisplayPresenter;
import com.haloai.hud.hudendpoint.arwaylib.arway.INaviUpdater;
import com.haloai.hud.hudendpoint.arwaylib.arway.IStateContoller;
import com.haloai.hud.hudendpoint.arwaylib.draw.impl_opengl.NaviAnimationNotifer;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.navigation.NavigationSDKAdapter;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.IDisplay;

/**
 * A simple {@link Fragment} subclass.
 */
public class MockARwayOpenGLFragment extends Fragment implements IDisplay, AMap.OnMapLoadedListener, AMap.OnCameraChangeListener, IStateContoller, INaviUpdater, INaviDisplayPresenter, NaviAnimationNotifer {


    public MockARwayOpenGLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mock_arway_open_gl, container, false);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void prepareNavingStartView() {

    }

    @Override
    public void onNavingStartView() {

    }

    @Override
    public void onNavingView() {

    }

    @Override
    public void onNavingEndView() {

    }

    @Override
    public void onNavingStopView() {

    }

    @Override
    public void onYawStartView() {

    }

    @Override
    public void onYawEndView() {

    }

    @Override
    public void onNavingContextChangedView() {

    }

    @Override
    public void onNaviStarted() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void updateYawStart() {

    }

    @Override
    public void updateYawEnd() {

    }

    @Override
    public void onNaviCalculateRouteFailure(int i) {

    }

    @Override
    public void onGpsStatusChanged(boolean b) {

    }

    @Override
    public void onNetworkStatusChanged(boolean b) {

    }

    @Override
    public void showCrossImage(Bitmap bitmap) {

    }

    @Override
    public void hideCrossImage() {

    }

    @Override
    public void updateLocation(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void updateNaviText(NavigationSDKAdapter.NavigationNotifier.NaviTextType naviTextType, String s) {

    }

    @Override
    public void updatePath(AMapNavi aMapNavi) {

    }

    @Override
    public void updateNaviInfo(NaviInfo naviInfo) {

    }

    @Override
    public void onSpeedUpgraded(float v) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void prepareARWayStart() {

    }

    @Override
    public void onARWayStart() {

    }

    @Override
    public void stopDrawHudway() {

    }

    @Override
    public void onNaviStartAnimation(long l) {

    }

    @Override
    public ISurfaceRenderer createRenderer() {
        return null;
    }

    public void updateRouteInfo(int iconType, int curStepRetainDistance) {
    }

    public void updateTimeAndDistance(int pathRetainTime, int pathRetainDistance) {
    }

    public void updateCurPointIndex(int curPoint, int curStep) {
    }

    public void showHideBlockView(boolean isShow) {

    }
}
