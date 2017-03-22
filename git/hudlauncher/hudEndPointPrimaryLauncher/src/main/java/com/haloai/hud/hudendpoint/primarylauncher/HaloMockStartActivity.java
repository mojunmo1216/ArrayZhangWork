package com.haloai.hud.hudendpoint.primarylauncher;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.fragments.amap.HudAMapFragmentMain;
import com.haloai.hud.hudendpoint.fragments.amap.HudAMapFragmentNavi;
import com.haloai.hud.hudendpoint.fragments.common.HudAMapFragmentOutgoing;
import com.haloai.hud.hudendpoint.fragments.common.HudDialogFragment;
import com.haloai.hud.hudendpoint.fragments.common.HudStatusBarFragment;
import com.haloai.hud.hudendpoint.fragments.mock.MockARwayOpenGLFragment;
import com.haloai.hud.hudendpoint.fragments.mock.MockHudCarRecorderFragment;
import com.haloai.hud.hudendpoint.fragments.mock.MockHudSpeechPanelFragment;
import com.haloai.hud.hudendpoint.fragments.mock.MockHudSpeechReceiverFragment;
import com.haloai.hud.navigation.AMapNavigationSDKAdapter;
import com.haloai.hud.navigation.NavigationSDKAdapter;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.gesture.GestureCode;
import com.haloai.huduniversalio.speech.ISpeechParserMusic;

import java.util.List;

public class HaloMockStartActivity extends HaloBaseActvity implements NavigationSDKAdapter.NavigationNotifier {

    private AMapNavigationSDKAdapter mAMapNaviAdapter;

    private ProgressBar    mLoadingProgressBar;
    private ViewGroup      mLoadingProgressBarParent;
    private RelativeLayout mMainView;

    private HudStatusManager              mHudStatusManager;
    private HudAMapFragmentMain           mMainFragment;
    private MockARwayOpenGLFragment       mNavingFragment;
    private HudAMapFragmentOutgoing       mOutgoingFragment;
    private HudDialogFragment             mDialogFragment;
    private MockHudCarRecorderFragment    mCarRecorderFragment;
    private MockHudSpeechPanelFragment    mSpeechPanelFragment;
    private MockHudSpeechReceiverFragment mSpeechReceiverFragment;
    private HudStatusBarFragment          mStatusBarFragment;
    private HudAMapFragmentNavi           mNaviFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halo_mock_start);
        /*View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //判断当前屏幕是否需要横屏
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels < dm.heightPixels){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

        mAMapNaviAdapter = (AMapNavigationSDKAdapter) NavigationSDKAdapter
                .getNavigationAdapter(NavigationSDKAdapter.NAVIGATION_SDK_AMAP, this);

        mAMapNaviAdapter.setNaviNotifier(this);

        mMainView = (RelativeLayout)this.findViewById(R.id.amap_main);
        mMainFragment = (HudAMapFragmentMain) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_main);
        mNavingFragment = (MockARwayOpenGLFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_naving);
        mOutgoingFragment = (HudAMapFragmentOutgoing) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_outgoing);
        mDialogFragment = (HudDialogFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_dialog);
        mCarRecorderFragment = (MockHudCarRecorderFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_carrecorder);
        mNaviFragment = (HudAMapFragmentNavi) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_navi);
        mSpeechPanelFragment = (MockHudSpeechPanelFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_speechpanel);
        mSpeechReceiverFragment = (MockHudSpeechReceiverFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_receiver);
        mStatusBarFragment = (HudStatusBarFragment) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_status);
     *//*   mNativeFragment = (HudAMapFragmentNative) this.getFragmentManager()
                .findFragmentById(R.id.amap_fragment_native_amap);*//*

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.show(mStatusBarFragment);
        ft.hide(mNavingFragment);
        ft.hide(mOutgoingFragment);
        ft.hide(mCarRecorderFragment);
        ft.hide(mSpeechPanelFragment);
        ft.show(mSpeechReceiverFragment);
        ft.hide(mDialogFragment);
        ft.hide(mNaviFragment);
        //       ft.hide(mNativeFragment);
        ft.commitAllowingStateLoss();*/

       /* mHudStatusManager = HudStatusManager.getInstance();
        mHudStatusManager.create(getApplicationContext(), this, mHudSpeechOutputer);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        initMainLayout();

        IntentFilter sDCardFilter = new IntentFilter();
        sDCardFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        sDCardFilter.addDataScheme("file");
        registerReceiver(mSDCardReceiver, sDCardFilter);
        CrashReport.setUserSceneTag(getApplicationContext(), HudEndPointConstants.BUGLY_TAG_MAIN);//主界面Bugly_TAG
        HaloLogger.postI(EndpointsConstants.NAVI_TAG,"HUD is start! ,thread is "+Thread.currentThread());*/
    }


    @Override
    public boolean onGestureCommand(int gestureType) {
        return false;
    }

    @Override
    public void onGestureShow(GestureCode gestureCode, boolean keyEvent) {

    }

    @Override
    public boolean onGesturePoint(int areaIndex) {
        return false;
    }

    @Override
    public void onPhoneOnclickCommand(int command) {

    }

    @Override
    public void onRemoterKey(int keyCode) {

    }

    @Override
    public void onSpeechEngineReady() {

    }

    @Override
    public void onWakeup() {

    }

    @Override
    public void onSpeechVolumeChanged(int volume) {

    }

    @Override
    public void onSpeechRecognizeStart() {

    }

    @Override
    public void onSpeechTTSBegin() {

    }

    @Override
    public void onSpeechTTSEnd() {

    }

    @Override
    public void onSpeechUnRecogized(String errorMessage) {

    }

    @Override
    public void onSpeechEngineError(SpeechEngineErrorCode errorCode, String errorMsg) {

    }

    @Override
    public void onSpeechText(String text) {

    }

    @Override
    public void onWakeupPrompt(HudIOConstants.HudInputerType hudInputerType) {

    }

    @Override
    public void commandSessionPlay(HudCommandType commandType) {

    }

    @Override
    public void dispatcherSessionPromat(HudIOConstants.HudPromatType hudPromatType) {

    }

    @Override
    public void dispatcherSessionAdjustLight(boolean isUp) {

    }

    @Override
    public void dispatcherSessionGestureChoose(int index) {

    }

    @Override
    public void onSessionCallHangUp() {

    }

    @Override
    public void onSessionIncomingCallRinging(String contactName) {

    }

    @Override
    public void musicSessionSearchSong(List<ISpeechParserMusic.MusicInfo> mMusicInfoList, String tag) {

    }

    @Override
    public void musicSessionPlayMusic() {

    }

    @Override
    public void naviSessionSetDestination(String poiNameToSearch, int index, boolean isLoading) {

    }

    @Override
    public void naviSessionChooseStrategy(int index) {

    }

    @Override
    public void naviSessionChoosePOI(int index) {

    }

    @Override
    public void naviSessionStart() {

    }

    @Override
    public void onNaviSessionPOIResult(List<String> poiList, String destination, int poiPageSize) {

    }

    @Override
    public void onPOISearchFailed(String poiNameToSearch, String error) {

    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {

    }

    @Override
    public void onCalculateRoute(AMapNavigationSDKAdapter.RouteSearchData shortestDistance, AMapNavigationSDKAdapter.RouteSearchData fastestTime, AMapNavigationSDKAdapter.RouteSearchData defaultStrategy) {

    }

    @Override
    public void onSessionOutgoingCallContactList(List<String> contactList, int pageCount) {

    }

    @Override
    public void onSessionOutgoingCallNextPage() {

    }

    @Override
    public void onSessionOutgoingCallPrevPage() {

    }

    @Override
    public void onSessionOutgoingCalling(String name, String phone, boolean isHud) {

    }

    @Override
    public void onSessionOutgoingCallPhoneNumList(String name, List<String> phoneList) {

    }

    @Override
    public void onSessionOutgoingCallEnd(boolean isCancelled) {

    }

    @Override
    public void onSessionRecorderOpen() {

    }

    @Override
    public void onSessionRecorderBrowseVideo(HudRecorderVideoType hudRecorderVideoType) {

    }

    @Override
    public void onSessionRecorderClose() {

    }

    @Override
    public void onSessionRecorderCommand(HudRecorderCommandType hudRecorderCommandType) {

    }

    @Override
    public void onSessionRecorderPhonePlay(String path, int index) {

    }

    @Override
    public void onNaviCalculateRouteFailure(int errorInfo) {

    }

    @Override
    public void onNaviText(NaviTextType textType, String text) {

    }

    @Override
    public void onYawStart() {

    }

    @Override
    public void onYawEnd() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onCrossShow(Bitmap cross) {

    }

    @Override
    public void onCrossHide() {

    }

    @Override
    public void onNaviInfo(NaviInfo info) {

    }

    @Override
    public void onSpeedUpgraded(float speed) {

    }

    @Override
    public void onLocationChanged(Object location) {

    }

    @Override
    public void onPathCalculated(Object aMapNavi) {

    }

    @Override
    public void onSatelliteFound(int satelliteNum) {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onStartNavi(int type) {

    }
}
