package com.haloai.hud.hudendpoint.fragments.common;



import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.navi.model.NaviInfo;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.primarylauncher.HaloAMapNaviStartActivity;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HudSpeechPanelFragment extends Fragment {

    private static final String TAG = HudSpeechPanelFragment.class.getSimpleName();
    private static final String WSX = "wangshengxing";
    private RelativeLayout mMainView;

    private ImageView mSelectBgView;
    private ViewGroup mLastDisplayView;

    private RelativeLayout mCommonPanel;
    private ViewGroup mMainPanel,mNaviPanel,mNaviCallPanel,mNaviExitPanel,mCallPanel,mMusicPanel,mMusicControlPanel;

    private ImageView mCommonPanelImageView;

    //导航指示
    private ImageView mNaviInstructionImageview;
    private TextView mNaviDistanceTextView,mNaviRoadTextview,mNaviActionTextView;
    private RelativeLayout mNormalNavi,mNormalMusic,mNormalCall,mNoConnNavi,mNoConnMusic,mNoConnCall;
    private LinearLayout mNaviNormalMusic,mNaviNormalCall,mNaviNoConnMusic,mNaviNoConnCall,mNaviNoConnNavi,mNaviNormalNavi;

    private int curScreenAreaIndex = -1;

    private RelativeLayout mNaviInfoView;

    /**
     * 二级菜单下传入的手势坐标类型
     * */
    public enum GesturePointType{
        LeftGesturePointType,
        CenterGesturePointType,
        RightGesturePointType,
        DefaultGesturePointType
    }

    private GesturePointType curGesturePointType = GesturePointType.DefaultGesturePointType;


    /**
     * 语音菜单的枚举类型，请勿改变顺序
     * */
    public enum PanelType{
        //二级语音菜单
        MainPanelType,
        //三级语音菜单
        NaviPanelType,
        NaviPanelTypeCall,
        NaviExitType,
        CallPanelType,
        MusicPanelType,
        MusicControlPanelType
    }

    /**
     * 1、每个需要更改的内容的TextView，需要定义COMMON_TEXTVIEW_IDS中的ID号，这样可以随意组合其中的顺序
     * 2、另外在PANELS_CONTENT_NUMBERS中指令里面的TextView的个数
     * **/
//    private static final int[] COMMON_TEXTVIEW_IDS = new int[]{R.id.panel_commmon_textview_1,R.id.panel_commmon_textview_2,
//            R.id.panel_commmon_textview_3,R.id.panel_commmon_textview_4,R.id.panel_commmon_textview_5,R.id.panel_commmon_textview_6,
//            R.id.panel_commmon_textview_7/*,R.id.panel_commmon_textview_8,R.id.panel_commmon_textview_9*/};
    private static final int PANELS_NUBMER = PanelType.values().length;
    private static final int[] PANELS_CONTENT_NUMBERS = new int[] {6,6,5,4,2,2,6}; //每个菜单的可填写内容的数目，分别按PanelType的顺序排序
    private ViewGroup[] mPanelViews;    //不同菜单的布局
    private TextView[][] mPanelsTextViews;
    private String[][] mPanelsContents;//不同菜单的布局的内容，每次仅更新需要更改的内容

    private PanelType mCurrentPanelType = PanelType.MainPanelType;
    private HudStatusManager mHudStatusManager;

    Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    EndpointsConstants.IS_PANEL =false;
                    break;
                case 1:
                    EndpointsConstants.IS_PANEL =true;
                    break;
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = (RelativeLayout)inflater.inflate(R.layout.fragment_hud_speech_panel, container, false);
        mNaviInfoView = (RelativeLayout)inflater.inflate(R.layout.layout_common_naving_tips, container, false);
        initResource();
        initView();
//        upgradePanel(PanelType.MainPanelTypeNavi);
        upgradePanel(PanelType.CallPanelType,new String[]{"打电话给光晕","第二","第三","第四"},1,false);
        mHudStatusManager=HudStatusManager.getInstance();
        return mMainView;
    }
    /**
     * 初始化基本的类和资源
     * */
    private void initResource(){
        mPanelsTextViews = new TextView[PANELS_NUBMER][];
        mPanelsContents = new String[PANELS_NUBMER][];
        for (int i = 0; i <PANELS_NUBMER ; i++) {
            mPanelsTextViews[i] = new TextView[PANELS_CONTENT_NUMBERS[i]];
            mPanelsContents[i] = new String[PANELS_CONTENT_NUMBERS[i]];
        }
    }
    /**
     * 更新指定菜单的内容，超出的内容无效
     * @param offset
     *          内容的起始下标
     *@param override
     *          是否覆盖原有内容
     * */
    private void updateContent(PanelType type,String[] contents,final int offset,boolean override){
        int viewIndex = type.ordinal(); //！！！！可能会改变
        TextView[] textViews = mPanelsTextViews[viewIndex];
        if (textViews == null) {
            LogI("updateContent,textViews为空");
        }else {
            LogI("updateContent,textViews大小为："+textViews.length+"");
        }
        for (int i = 0; i <textViews.length ; i++) {
            String text = "";
            TextView textView = textViews[i];
            if (contents != null) {
                int textIndex = i-offset;
                if(offset>=0 && (textIndex)>=0 && (textIndex <contents.length)){ //合适的下标位置
                    text = contents[textIndex];
                    if (text != null && textView != null) {
                        textView.setText(text);
                        LogI("更新内容："+text);
                    }
                    if(text == null) LogI("更新内容为空");
                    if(textView == null) LogI("textView为空");

                }else if(override && textView != null){
                    textView.setText(text);
                }else {
                    //保持原来的内容
                }

            }else if(override && textView != null){
                textView.setText(text);
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            if (mNaviInfoView.getParent() != null){
                mMainView.removeView(mNaviInfoView);
            }
            mHandler.sendEmptyMessage(0);
        }else {
            mHandler.sendEmptyMessage(1);
        }
    }

    private void initView(){
        mMainPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_main_panel_viewgroup);
        mNaviExitPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_main_navi_panel_viewgroup);

        mNaviInstructionImageview = (ImageView) mNaviInfoView.findViewById(R.id.next_road_direction_imageview);
        mNaviDistanceTextView = (TextView) mNaviInfoView.findViewById(R.id.next_road_distance_textview);
        mNaviRoadTextview = (TextView) mNaviInfoView.findViewById(R.id.next_road_name_textview);
        mNaviActionTextView =  (TextView) mNaviInfoView.findViewById(R.id.road_name_prefix_textview);
//        mNaviInstructionImageview = (ImageView) mMainNavingPanel.findViewById(R.id.direction_indicate_imageview);
//        mNaviDistanceTextView = (TextView) mMainNavingPanel.findViewById(R.id.distance_indicate_textview);
//        mNaviRoadTextview = (TextView) mMainNavingPanel.findViewById(R.id.next_road_indicate_textview);
        mCommonPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_common_panel_viewgroup);
        mCommonPanelImageView = (ImageView) mCommonPanel.findViewById(R.id.common_panel_imageview);
        mCallPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_call_panel_viewgroup);
        mNaviPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_navi_panel_viewgroup);
        mMusicPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_music_panel_viewgroup);
        mMusicControlPanel = (RelativeLayout)mMainView.findViewById(R.id.speech_music_control_viewgroup);
        initMainPanel();
        removeView(mCommonPanel);
        mPanelViews = new ViewGroup[]{mMainPanel,mNaviPanel,mNaviCallPanel,mNaviExitPanel,mCallPanel,mMusicPanel,mMusicControlPanel};
        //移除所有view，默认不显示
        removeView(mCommonPanel);
        for (int i = 0; i <mPanelViews.length ; i++) {
            removeView(mPanelViews[i]);
        }
        //获取所有菜单中的TextView
//        for (int i = 0; i < PANELS_NUBMER; i++) {
//            ViewGroup panelViewGroup = mPanelViews[i];
//            TextView[] textViews = mPanelsTextViews[i];
//            if (panelViewGroup != null) {
//                final int TOTAL_VIEW = PANELS_CONTENT_NUMBERS[i];
//                for (int j = 0; j <TOTAL_VIEW; j++) {
//                    View view = panelViewGroup.findViewById(COMMON_TEXTVIEW_IDS[j]);
//                    if (view != null && view instanceof TextView){
//                        textViews[j] = (TextView)view;//
////                        LogI("面板 "+PanelType.values()[i]+"找到Textview"+view);
//                    }
//                }
//                /*int viewCnt = 0;
//                int childCount = panelViewGroup.getChildCount();
//                final int TOTAL_VIEW = PANELS_CONTENT_NUMBERS[i];
//                LogI("面板 "+PanelType.values()[i]+",子view的个数为："+childCount);
//                for (int j = 0; j <childCount; j++) {
//                    View view = panelViewGroup.getChildAt(j);
//                    if (view instanceof TextView){
//                        textViews[viewCnt] = (TextView)view;//
//                        LogI("面板 "+PanelType.values()[i]+"找到Textview");
//                        if(++viewCnt>=TOTAL_VIEW){
//                            break;
//                        }
//                    }
//
//                }*/
//
//            }
//        }

    }
    private void initMainPanel(){
        mNormalNavi=(RelativeLayout) mMainPanel.findViewById(R.id.panel_navi_normal_layout);
        mNormalMusic=(RelativeLayout)mMainPanel.findViewById(R.id.panel_music_normal_layout);
        mNormalCall=(RelativeLayout)mMainPanel.findViewById(R.id.panel_call_normal_layout);
//        mNoConnNavi=(RelativeLayout)mMainPanel.findViewById(R.id.panel_navi_noconnect_layout);
//        mNoConnMusic=(RelativeLayout)mMainPanel.findViewById(R.id.panel_music_noconnect_layout);
//        mNoConnCall=(RelativeLayout)mMainPanel.findViewById(R.id.panel_call_noconnect_layout);
//        mNaviNormalMusic=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_music_normal_layout);
//        mNaviNormalCall=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_call_normal_layout);
//        mNaviNoConnMusic=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_music_noconnect_layout);
//        mNaviNoConnCall=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_call_noconnect_layout);
//        mNaviNoConnNavi=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_navi_norconn_layout);
//        mNaviNormalNavi=(LinearLayout)mMainNavingPanel.findViewById(R.id.navi_navi_normal_layout);
    }

    private void updateMainPanel(){
        if(HudStatusManager.IS_NETWORK_CONNECTED){
            mNormalNavi.setVisibility(View.VISIBLE);
            mNormalMusic.setVisibility(View.VISIBLE);
            mNoConnNavi.setVisibility(View.GONE);
            mNoConnMusic.setVisibility(View.GONE);
        }else{
            mNoConnNavi.setVisibility(View.VISIBLE);
            mNoConnMusic.setVisibility(View.VISIBLE);
            mNormalNavi.setVisibility(View.GONE);
            mNormalMusic.setVisibility(View.GONE);
        }
        if(mHudStatusManager.getHudBTContactsStatus()==HudStatusManager.BLUETOOTH_CONTACTS_COMPLED){
            mNormalCall.setVisibility(View.VISIBLE);
            mNoConnCall.setVisibility(View.GONE);
        }else{
            mNoConnCall.setVisibility(View.VISIBLE);
            mNormalCall.setVisibility(View.GONE);
        }
        /*
        mNormalNavi.setVisibility(View.VISIBLE);
        mNormalMusic.setVisibility(View.VISIBLE);
        mNoConnNavi.setVisibility(View.GONE);
        mNoConnMusic.setVisibility(View.GONE);
        mNormalCall.setVisibility(View.VISIBLE);
        mNoConnCall.setVisibility(View.GONE);

        if(HudStatusManager.IS_NETWORK_CONNECTED){
            isNaviUsable = true;
            isMusicUsable = true;
        }else {
            isNaviUsable = false;
            isMusicUsable = false;
        }
        if (mHudStatusManager.getHudBTContactsStatus()==HudStatusManager.BLUETOOTH_CONTACTS_COMPLED){
            isPhoneUsable = true;
        }else {
            isPhoneUsable = false;
        }
        */
    }

    private void updateNavingMainPanel(){
        if(HudStatusManager.IS_NETWORK_CONNECTED){
            mNaviNormalMusic.setVisibility(View.VISIBLE);
            mNaviNoConnMusic.setVisibility(View.GONE);
            mNaviNormalNavi.setVisibility(View.VISIBLE);
            mNaviNoConnNavi.setVisibility(View.GONE);
        }else{
            mNaviNormalMusic.setVisibility(View.GONE);
            mNaviNoConnMusic.setVisibility(View.VISIBLE);
            mNaviNormalNavi.setVisibility(View.GONE);
            mNaviNoConnNavi.setVisibility(View.VISIBLE);
        }
        if(mHudStatusManager.getHudBTContactsStatus()==HudStatusManager.BLUETOOTH_CONTACTS_COMPLED){
            mNaviNormalCall.setVisibility(View.VISIBLE);
            mNaviNoConnCall.setVisibility(View.GONE);
        }else{
            mNaviNoConnCall.setVisibility(View.VISIBLE);
            mNaviNormalCall.setVisibility(View.GONE);
        }



    }
    /**
     * 移除指定的View
     * */
    private void removeView(View view ){
        if (view != null) {
            mMainView.removeView(view);
        }
    }


    /**
     * 更新导航的指示信息
     * */
    public void updateNaviInfo(NaviInfo naviInfo){
        int  instruction = naviInfo.getIconType();
        int distance = naviInfo.getCurStepRetainDistance();
        String roadname = naviInfo.getCurrentRoadName();

        if (mNaviInstructionImageview != null) {
            int resouce = getInstructionDrawable(instruction);
            if (resouce > 0) {
                mNaviInstructionImageview.setImageResource(resouce);
            }
        }

        if (distance>=0) {
            String distanceText;
            if(distance<1000){
                distanceText=distance+"米";
            }else {
                distanceText=String.format("%.1f%s",distance/1000.0,"公里");
            }
            mNaviDistanceTextView.setText(distanceText);
        }else {
            mNaviDistanceTextView.setText("");
        }

        if (roadname !=null) {
            mNaviRoadTextview.setText(roadname);
            if (roadname == "目的地"){
                mNaviActionTextView.setText("到达");
            }else {
                mNaviActionTextView.setText("进入");
            }
        }

    }



    /**
     * 获取指示资源
     * */
    public int getInstructionDrawable(int instruction){
        int result = 0;
        switch (instruction) {
            case 15:
                result = R.drawable.greenline_direction__icon_turn_dest;
                break;
            case 13:
                result = R.drawable.greenline_direction__icon_turn_service_area;
                break;
            case 14:
                result = R.drawable.greenline_direction__icon_turn_tollgate;
                break;
            case 16:
                result = R.drawable.greenline_direction__icon_turn_tunnel;
                break;
            case 10:
                result = R.drawable.greenline_direction__icon_turn_passing_point;
                break;
            case 24:
                // TODO 通过索道
                break;
            case 26:
                // TODO 通过通道、建筑物穿越通道
                break;
            case 17:
                // TODO 通过人行横道
                break;
            case 28:
                // TODO 通过游船路线
                break;
            case 1:
                // TODO 自车图标
                break;
            case 11:
                result = R.drawable.greenline_direction__icon_turn_ring;
                break;
            case 31:
                // TODO 通过阶梯
                break;
            case 2:
                result = R.drawable.greenline_direction__icon_turn_left;
                break;
            case 6:
                result = R.drawable.greenline_direction__icon_turn_left_back;
                break;
            case 4:
                result = R.drawable.greenline_direction__icon_turn_left_front;
                break;
            case 8:
                result = R.drawable.greenline_direction__icon_turn_back;
                break;
            case 23:
                // TODO 通过直梯
                break;
            case 0:
                // TODO 无定义
                break;
            case 12:
                result = R.drawable.greenline_direction__icon_turn_ring_out;
                break;
            case 18:
                // TODO 通过过街天桥
                break;
            case 21:
                // TODO 通过公园
                break;
            case 3:
                result = R.drawable.greenline_direction__icon_turn_right;
                break;
            case 7:
                result = R.drawable.greenline_direction__icon_turn_right_back;
                break;
            case 5:
                result = R.drawable.greenline_direction__icon_turn_right_front;
                break;
            case 29:
                // TODO 通过观光车路线
                break;
            case 25:
                // TODO 通过空中通道
                break;
            case 30:
                // TODO 通过滑道
                break;
            case 20:
                // TODO 通过广场
                break;
            case 22:
                // TODO 通过扶梯
                break;
            case 9:
                result = R.drawable.greenline_direction__icon_turn_front;
                break;
            case 19:
                // TODO 通过地下通道
                break;
            case 27:
                // TODO 通过行人道路
                break;
        }
        return result;
    }
    /**
     * 更新对应指示菜单类型的界面，并相关的显示文本内容
     * */
    public void upgradePanel(PanelType panelType, List<String> textList){

    }
    /**
     * 更新仅指示菜单类型的界面
     * @param offset
     *          内容的起始下标
     *@param override
     *          是否覆盖原有内容
     * */
    public void upgradePanel(PanelType panelType,String[] contents,int offset,boolean override) {
        mCurrentPanelType = panelType;
        updateContent(panelType,contents,offset,override);
        upgradePanel(panelType);
    }

    /**
     * 更新仅指示菜单类型的界面
     * */

    public void upgradePanel(PanelType panelType) {
        mCurrentPanelType = panelType;
        curScreenAreaIndex = -1;
        switch (panelType){
            case MainPanelType:
                //updateMainPanel();
                refreshView(mMainPanel);
                curGesturePointType = GesturePointType.DefaultGesturePointType;
                updateTwoLevelPanel(curGesturePointType);
                break;
//            case MainPanelTypeNavi:
//                updateNavingMainPanel();
//                refreshView(mMainNavingPanel);
//                break;
            case CallPanelType:
                refreshView(mCallPanel);
                updateThreeLevelPanel(curScreenAreaIndex);
                break;
            case NaviPanelType:
                refreshView(mNaviPanel);
                updateThreeLevelPanel(curScreenAreaIndex);
                break;
            case MusicPanelType:
                refreshView(mMusicPanel);
                updateThreeLevelPanel(curScreenAreaIndex);
                break;
            case NaviExitType:
                refreshView(mNaviExitPanel);
                updateThreeLevelPanel(curScreenAreaIndex);
                break;
            case MusicControlPanelType:
                refreshView(mMusicControlPanel);
                updateThreeLevelPanel(curScreenAreaIndex);
                if (HudStatusManager.HudMusicStatus==HudStatusManager.MUSIC_PLAY){
                    ImageView imageView = (ImageView)mMusicControlPanel.findViewById(R.id.music_play_select_imageview);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.music_pause_select));
                    TextView textView = (TextView) mMusicControlPanel.findViewById(R.id.music_play_select_textview);
                    textView.setText("暂停");
                    imageView = (ImageView) mMusicControlPanel.findViewById(R.id.music_play_unselect_imageview);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.music_pause_unselect));
                    textView = (TextView)mMusicControlPanel.findViewById(R.id.music_play_unselect_textview);
                    textView.setText("暂停");
                    Log.e("music","音乐正在播放");
                }else {
                    ImageView imageView = (ImageView)mMusicControlPanel.findViewById(R.id.music_play_select_imageview);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.music_play_select));
                    TextView textView = (TextView) mMusicControlPanel.findViewById(R.id.music_play_select_textview);
                    textView.setText("播放");
                    imageView = (ImageView) mMusicControlPanel.findViewById(R.id.music_play_unselect_imageview);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.music_play_unselect));
                    textView = (TextView)mMusicControlPanel.findViewById(R.id.music_play_unselect_textview);
                    textView.setText("播放");
                    Log.e("music","音乐正在停止");
                }

        }
    }

    /**
     * 更新面板显示的View,移除上一个显示的View
     * */
    private void refreshView(ViewGroup view){
        if (mLastDisplayView != null) {
            mMainView.removeView(mLastDisplayView);
            mLastDisplayView = null;
        }
        if (view != null) {
            mMainView.addView(view);
            mLastDisplayView = view;
            view.setVisibility(View.VISIBLE);
            view.bringToFront();
        }
        if (mNaviInfoView.getParent() != null){
            mMainView.removeView(mNaviInfoView);
        }
        if (mCurrentPanelType != PanelType.MainPanelType && mCurrentPanelType != PanelType.NaviPanelType){
            if(HudStatusManager.HudCurrentStatus == HudStatusManager.HudStatus.NAVIGATING){
                mMainView.addView(mNaviInfoView);
                mNaviInfoView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取当前的语音菜单类型
     * */
    public PanelType getCurrentPanelType() {
        return mCurrentPanelType;
    }

    /**
     * 根据手势Location点更新UI
     * */
    public boolean updateUIAccordingGesturePointChange(GesturePointType type){
        boolean res = false;
        if (curGesturePointType != type && mCurrentPanelType == PanelType.MainPanelType){
            curGesturePointType = type;
            updateTwoLevelPanel(type);
            res = true;
        }
        return res;
    }

    private void updateTwoLevelPanel(GesturePointType type){
        switch (type){
            case LeftGesturePointType:
                mNormalNavi.setAlpha(1);
                mNormalMusic.setAlpha(0);
                mNormalCall.setAlpha(0);
                break;
            case CenterGesturePointType:
                mNormalNavi.setAlpha(0);
                mNormalMusic.setAlpha(1);
                mNormalCall.setAlpha(0);
                break;
            case RightGesturePointType:
                mNormalNavi.setAlpha(0);
                mNormalMusic.setAlpha(0);
                mNormalCall.setAlpha(1);
                break;
            case DefaultGesturePointType:
                mNormalNavi.setAlpha(0);
                mNormalMusic.setAlpha(0);
                mNormalCall.setAlpha(0);
                break;
        }
    }





    /*
    NaviPanelType,
        NaviPanelTypeCall,
        NaviExitType,
        CallPanelType,
        MusicPanelType
     */

    public boolean gesturePointChoose(int areaIndex){
        boolean res = false;
        if (curScreenAreaIndex!=areaIndex) {
            curScreenAreaIndex = areaIndex;
            updateThreeLevelPanel(areaIndex);
            res = true;
        }
        return res;

    }


    private void updateThreeLevelPanel(int areaIndex){
        switch (mCurrentPanelType) {
            case NaviPanelType:
                switch (areaIndex){
                    case -1:
                        mNaviPanel.findViewById(R.id.panel_nav_go_home).setVisibility(View.GONE);
                        mNaviPanel.findViewById(R.id.panel_nav_go_company).setVisibility(View.GONE);
                        break;
                    case 0:
                        mNaviPanel.findViewById(R.id.panel_nav_go_home).setVisibility(View.VISIBLE);
                        mNaviPanel.findViewById(R.id.panel_nav_go_company).setVisibility(View.GONE);
                        break;
                    case 1:
                        mNaviPanel.findViewById(R.id.panel_nav_go_home).setVisibility(View.GONE);
                        mNaviPanel.findViewById(R.id.panel_nav_go_company).setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case NaviPanelTypeCall:
                break;
            case NaviExitType:
                switch (areaIndex){
                    case -1:
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit_go_home).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_go_exit_company).setVisibility(View.GONE);
                        break;
                    case 0:
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit).setVisibility(View.VISIBLE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit_go_home).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_go_exit_company).setVisibility(View.GONE);
                        break;
                    case 1:
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit_go_home).setVisibility(View.VISIBLE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_go_exit_company).setVisibility(View.GONE);
                        break;
                    case 2:
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_exit_go_home).setVisibility(View.GONE);
                        mNaviExitPanel.findViewById(R.id.panel_nav_go_exit_company).setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case CallPanelType:
                switch (areaIndex){
                    case -1:
                        mCallPanel.findViewById(R.id.panel_call_first).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_second).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_third).setVisibility(View.GONE);
                        break;
                    case 0:
                        mCallPanel.findViewById(R.id.panel_call_first).setVisibility(View.VISIBLE);
                        mCallPanel.findViewById(R.id.panel_call_second).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_third).setVisibility(View.GONE);
                        break;
                    case 1:
                        mCallPanel.findViewById(R.id.panel_call_first).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_second).setVisibility(View.VISIBLE);
                        mCallPanel.findViewById(R.id.panel_call_third).setVisibility(View.GONE);
                        break;
                    case 2:
                        mCallPanel.findViewById(R.id.panel_call_first).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_second).setVisibility(View.GONE);
                        mCallPanel.findViewById(R.id.panel_call_third).setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case MusicPanelType:
                switch (areaIndex){
                    case -1:
                        mMusicPanel.findViewById(R.id.panel_music_local).setVisibility(View.GONE);
                        mMusicPanel.findViewById(R.id.panel_music_net).setVisibility(View.GONE);
                        break;
                    case 0:
                        mMusicPanel.findViewById(R.id.panel_music_local).setVisibility(View.VISIBLE);
                        mMusicPanel.findViewById(R.id.panel_music_net).setVisibility(View.GONE);
                        break;
                    case 1:
                        mMusicPanel.findViewById(R.id.panel_music_local).setVisibility(View.GONE);
                        mMusicPanel.findViewById(R.id.panel_music_net).setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case MusicControlPanelType:
                switch (areaIndex){
                    case -1:
                        mMusicControlPanel.findViewById(R.id.panel_music_play).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_next).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_stop).setVisibility(View.GONE);
                        break;
                    case 0:
                        mMusicControlPanel.findViewById(R.id.panel_music_play).setVisibility(View.VISIBLE);
                        mMusicControlPanel.findViewById(R.id.panel_music_next).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_stop).setVisibility(View.GONE);
                        break;
                    case 1:
                        mMusicControlPanel.findViewById(R.id.panel_music_play).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_next).setVisibility(View.VISIBLE);
                        mMusicControlPanel.findViewById(R.id.panel_music_stop).setVisibility(View.GONE);
                        break;
                    case 2:
                        mMusicControlPanel.findViewById(R.id.panel_music_play).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_next).setVisibility(View.GONE);
                        mMusicControlPanel.findViewById(R.id.panel_music_stop).setVisibility(View.VISIBLE);
                        break;
                }
                break;
        }
    }


    private void LogI(String msg){
        HaloLogger.logI(WSX,msg);
    }
}
