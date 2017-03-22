package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.HudQueryRequestProto;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudQueryRequest {
    private int hudStatus;
    private int hudMiscInfo;
    private int featureMusicInfo;
    private int featureNaviInfo;
    private int featureCarcorderInfo;


    public HudQueryRequest(HudQueryRequestProto proto){
        this.hudStatus = proto.getHudStatus();
        this.hudMiscInfo = proto.getHudMiscInfo();
        this.featureMusicInfo = proto.getFeatureMusicInfo();
        this.featureNaviInfo = proto.getFeatureNaviInfo();
        this.featureCarcorderInfo = proto.getFeatureCarcorderInfo();

    }

    public HudQueryRequest(){}
    public HudQueryRequestProto encapsulate(){
        HudQueryRequestProto.Builder builder = HudQueryRequestProto.newBuilder();
        builder.setHudStatus(hudStatus);
        builder.setHudMiscInfo(hudMiscInfo);
        builder.setFeatureMusicInfo(featureMusicInfo);
        builder.setFeatureNaviInfo(featureNaviInfo);
        builder.setFeatureCarcorderInfo(featureCarcorderInfo);

        return  builder.build();
    }

    public int getHudStatus() {
        return hudStatus;
    }

    public void setHudStatus(int hudStatus) {
        this.hudStatus = hudStatus;
    }

    public int getHudMiscInfo() {
        return hudMiscInfo;
    }

    public void setHudMiscInfo(int hudMiscInfo) {
        this.hudMiscInfo = hudMiscInfo;
    }

    public int getFeatureMusicInfo() {
        return featureMusicInfo;
    }

    public void setFeatureMusicInfo(int featureMusicInfo) {
        this.featureMusicInfo = featureMusicInfo;
    }

    public int getFeatureNaviInfo() {
        return featureNaviInfo;
    }

    public void setFeatureNaviInfo(int featureNaviInfo) {
        this.featureNaviInfo = featureNaviInfo;
    }

    public int getFeatureCarcorderInfo() {
        return featureCarcorderInfo;
    }

    public void setFeatureCarcorderInfo(int featureCarcorderInfo) {
        this.featureCarcorderInfo = featureCarcorderInfo;
    }

}
