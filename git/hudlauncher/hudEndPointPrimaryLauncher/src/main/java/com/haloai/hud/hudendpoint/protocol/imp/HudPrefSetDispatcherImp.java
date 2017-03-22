package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.hudendpoint.protocol.HudPreferenceSetController;
import com.haloai.hud.hudendpoint.protocol.IHudPreferenceSetController;
import com.haloai.hud.lib.transportlayer.fm.FmManager;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudNobodyResponse;
import com.haloai.hud.model.v2.HudDataDefine;
import com.haloai.hud.model.v2.HudPrefSet;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.music.MusicConstants;
import com.haloai.hud.music.MusicSDKAdapter;
import com.haloai.hud.music.XiMaLaYaSDKAdapter;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.HudConstants;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.outputer.IHudOutputer;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/7 14:00
 * 修改人：zhang
 * 修改时间：2016/6/7 14:00
 * 修改备注：
 */
public class HudPrefSetDispatcherImp implements IDataDispatcher {
    private IHudPreferenceSetController mHudSettingController;
    private FmManager manager;
    private Context mContext;
    private IHudOutputer mHudOutputer;
    private XiMaLaYaSDKAdapter mXiMaLaYaAdapter;

    public HudPrefSetDispatcherImp(Context context,IHudOutputer hudOutputer) {
        mHudSettingController = HudPreferenceSetController.getInstance();
        mContext = context;
        mHudOutputer = hudOutputer;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(HudEndPointConstants.FM_STATUS_ACTION);
            switch (msg.what){
                case HudEndPointConstants.OPEN_FM:
                    intent.putExtra(HudEndPointConstants.FM_STATUS_ACTION,true);
                    manager.openFm();
                    break;
                case HudEndPointConstants.CLOSE_FM:
                    intent.putExtra(HudEndPointConstants.FM_STATUS_ACTION,false);
                    manager.closeFm();
                    break;
            }
            mContext.sendBroadcast(intent);
        }
    };

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        HudPrefSet set = phone2HudMessages.getHudPrefSet();
        if (set == null) {
            return null;
        }

        HudDataDefine hudDataDefine = set.getHudPrefDefine();
        if (hudDataDefine == null) {
            return null;
        }

        switch (hudDataDefine.getHud_data_type()) {
            case EndpointsConstants.HUD_SET_WIFI:
                int int_param = hudDataDefine.getInt_param();
                if (int_param == 1) {
                    mHudSettingController.openWifiAp(hudDataDefine.getBool_param());
                } else if (int_param == 2) {
                    // 设置wifi名及密码
                    mHudSettingController.setWifiApInfo(hudDataDefine.getExtra_param(), hudDataDefine.getExtra_param1());
                }
                break;
            case EndpointsConstants.HUD_SET_VOLUME: // 音量设置
                mHudSettingController.setVolumeValue(hudDataDefine.getInt_param());
                break;
            case EndpointsConstants.HUD_SET_BRIGHTNESS: // 亮度设置
                mHudSettingController.setBrightnessLevel(hudDataDefine.getInt_param(), hudDataDefine.getBool_param());
                break;
            case EndpointsConstants.HUD_SET_FM:
                manager = FmManager.getSingleFmManagerInstance(mContext);
                String extra_param = hudDataDefine.getExtra_param();
                if (extra_param == null) {
                    manager.setRate((float) hudDataDefine.getDouble_param());
                } else {
                    switch (hudDataDefine.getExtra_param()) {
                        case "switch":
                            // 去打开FM
                            if (hudDataDefine.getBool_param()) {
                                mHudOutputer.output("已打开FM声音发射", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,EndpointsConstants.IS_PANEL);
                                mHandler.sendEmptyMessageDelayed(HudEndPointConstants.OPEN_FM,4000);
                            } else {
                                mHandler.sendEmptyMessage(HudEndPointConstants.CLOSE_FM);
                                mHudOutputer.output("已关闭FM声音发射", IHudOutputer.HudOutputContentType.CONTENT_TYPE_CUSTOM,EndpointsConstants.IS_PANEL);
                            }
                            break;
                        default:
                            manager.setRate((float) hudDataDefine.getDouble_param());
                            break;
                    }
                }
                break;
            case EndpointsConstants.HUD_SET_PATH: // 回家、去公司的路径设置。
                setPath(hudDataDefine.getExtra_param(), hudDataDefine.getExtra_param1());
                break;
            case EndpointsConstants.HUD_SET_DEBUG: // 设置手势开关
                if(hudDataDefine.getInt_param()==EndpointsConstants.HUD_DEBUG_GESTURE && HudConstants.IS_GESTURE){
                    Intent intent = new Intent(HudIOConstants.GESTURE_CONNECT_DEBUG);
                    intent.putExtra(HudIOConstants.GESTURE_CONNECT_DEBUG,!HudStatusManager.HudGesture);
                    mContext.sendBroadcast(intent);
                }
                break;
            case EndpointsConstants.HUD_SET_MUSIC:
                saveMusicInfo(hudDataDefine.getExtra_param(),hudDataDefine.getExtra_param1());
                break;
            default:
                break;
        }
        return new HudNobodyResponse();
    }

    /**
     * 路径传输规则：“aa:bb:cc”中aa代表地名，bb代表经度，cc表示纬度。
     *
     * @param homeString
     * @param companyString
     */
    public void setPath(String homeString, String companyString) {
        if(!TextUtils.isEmpty(homeString)){
            if(homeString.contains("clear")){
                mHudSettingController.setNaviRoutInfo(null, 0, 0, HudEndPointConstants.PathEnumType.PATH_TO_HOME);
                return;
            }
            String[] homeStrings = homeString.split(":");
            if (homeStrings.length == 3 && homeStrings[0].length() > 0 && homeStrings[1].length() > 0 && homeStrings[2].length() > 0) {
                mHudSettingController.setNaviRoutInfo(homeStrings[0], Double.parseDouble(homeStrings[1]), Double.parseDouble(homeStrings[2]), HudEndPointConstants.PathEnumType.PATH_TO_HOME);
            }
        }
        if(!TextUtils.isEmpty(companyString)){
            if(companyString.contains("clear")){
                mHudSettingController.setNaviRoutInfo(null, 0, 0, HudEndPointConstants.PathEnumType.PATH_TO_COMPANY);
                return;
            }
            String[] companyStrings = companyString.split(":");
            if (companyStrings.length == 3 && companyStrings[0].length() > 0 && companyStrings[1].length() > 0 && companyStrings[2].length() > 0) {
                mHudSettingController.setNaviRoutInfo(companyStrings[0], Double.parseDouble(companyStrings[1]), Double.parseDouble(companyStrings[2]), HudEndPointConstants.PathEnumType.PATH_TO_COMPANY);
            }
        }

    }

    private void listAlbum(String string){
        mXiMaLaYaAdapter = (XiMaLaYaSDKAdapter) MusicSDKAdapter.getMusicSDKAdapter(mContext,MusicSDKAdapter.MUSIC_SDK_XIMALAYA);
        Map<String,String> ids = new HashMap<>();
        ids.put(MusicConstants.ALBUM_IDS,string);
        HaloLogger.logE("xmly_info",""+string);
        mXiMaLaYaAdapter.listAlbum(ids);
    }

    private void saveMusicInfo(String string, String string1){
        SharedPreferences sp = mContext.getSharedPreferences(MusicSDKAdapter.MUSIC_SDK_XIMALAYA,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(!TextUtils.isEmpty(string1)){
            editor.putString(MusicConstants.ALBUM_IDS,string1);
        }
        if(!TextUtils.isEmpty(string)){
            String[] strings = string.split(";");
            if(strings[0].length() > 0){
                editor.putString(MusicConstants.CATEGORY_ID,strings[0]);
            }
            if(strings[1].length() > 0){
                editor.putString(MusicConstants.RADIO_IDS,strings[1]);
            }
        }
        editor.commit();
    }
}
