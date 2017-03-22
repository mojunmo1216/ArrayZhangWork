package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;

import com.haloai.hud.carrecorderlibrary.bean.HudSDCardStates;
import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;
import com.haloai.hud.carrecorderlibrary.factory.HudRecorderControllerFactory;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.hudendpoint.HudStatusManager;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudCarcorderInfoQueryResp;
import com.haloai.hud.model.v2.HudDataDefine;
import com.haloai.hud.model.v2.HudNaviInfoQueryResp;
import com.haloai.hud.model.v2.HudQueryRequest;
import com.haloai.hud.model.v2.HudQueryResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.utils.EndpointsConstants;


/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/5/7 11:32
 * 修改人：zhang
 * 修改时间：2016/5/7 11:32
 * 修改备注：
 */
public class HudQueryRequestDispatcherimp implements IDataDispatcher {

    private Context mContext;
    private HudStatusManager mHudStatusManager;
    public HudQueryRequestDispatcherimp(Context context) {
        this.mContext = context;
        this.mHudStatusManager = HudStatusManager.getInstance();
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        HudQueryRequest hudQueryRequest = phone2HudMessages.getHudQueryRequest();
        if (null == hudQueryRequest) {
            return null;
        }
        HudQueryResponse hudQueryResponse = new HudQueryResponse();
        if (1 == (hudQueryRequest.getFeatureCarcorderInfo() & 0x0001)) {
            // 需要回复行车记录仪的消息

            HudCarcorderInfoQueryResp hudCarcorderInfoQueryResp = new HudCarcorderInfoQueryResp();

            IHudRecorderController hudCarcorderController = HudRecorderControllerFactory.getHudCarcorderController2(mContext);
            HudSDCardStates sdCardStates = hudCarcorderController.getSDCardStates();
            hudCarcorderInfoQueryResp.setHudTotalMemory((int) sdCardStates.mTotalSize);
            hudCarcorderInfoQueryResp.setHudUsedMemory((int) (sdCardStates.mTotalSize - sdCardStates.mUsbleSize));
            hudCarcorderInfoQueryResp.setRecorderVideoQuality(sdCardStates.mImageQuality);
            hudCarcorderInfoQueryResp.setLoopVideoNumber(hudCarcorderController.getVideoNumber(HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO));
            hudCarcorderInfoQueryResp.setLockVideoNumber(hudCarcorderController.getVideoNumber(HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO));

            hudCarcorderInfoQueryResp.setWonderfulVideoNumber(0);
            hudCarcorderInfoQueryResp.setEmergencyVideoNumber(0);

            hudQueryResponse.setCarcorderInfoQueryResp(hudCarcorderInfoQueryResp);
        }

        if (1 == (hudQueryRequest.getFeatureNaviInfo() & 0x0001)) {
            HudNaviInfoQueryResp hudNaviInfoQueryResp = new HudNaviInfoQueryResp();
            HudDataDefine define = new HudDataDefine();
            define.setHud_data_type(EndpointsConstants.HUD_SET_PATH);
            if(HudStatusManager.HudCurrentStatus== HudStatusManager.HudStatus.NAVIGATING){
                define.setBool_param(true);
                define.setExtra_param(mHudStatusManager.getHudPOIName());
            }else {
                define.setBool_param(false);
                define.setExtra_param("未发起导航");
            }
            hudNaviInfoQueryResp.setHudDataDefine(define);
            hudQueryResponse.setNaviInfoQueryResp(hudNaviInfoQueryResp);
        }
        return hudQueryResponse;
    }
}
