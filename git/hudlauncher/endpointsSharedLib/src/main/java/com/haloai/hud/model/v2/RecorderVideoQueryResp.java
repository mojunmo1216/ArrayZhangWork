package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.CommonMessagesProtoDef.RecorderTypeProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudCarcorderVideoInfoRespProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.RecorderVideoQueryRespProto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class RecorderVideoQueryResp extends AHudListResponse {

    private int beginVideoIndex;
    private List<HudCarcorderVideoInfoResp> hudCarcorderVideoInfoRespList = new ArrayList<>();
    private int videoNumber;
    private RecorderTypeProto recorderType;

    public RecorderVideoQueryResp(RecorderVideoQueryRespProto proto){
        this.beginVideoIndex = proto.getBeginVideoIndex();
        if (proto.getVideosInfoCount()>0){
            hudCarcorderVideoInfoRespList = new LinkedList<>();
            for (HudCarcorderVideoInfoRespProto videoInfoRespProto:proto.getVideosInfoList()) {
                hudCarcorderVideoInfoRespList.add(new HudCarcorderVideoInfoResp(videoInfoRespProto));
            }
        }
        this.videoNumber = proto.getVideoNumber();
        this.recorderType = proto.getRecorderType();
    }
    public RecorderVideoQueryResp(){}
    public RecorderVideoQueryRespProto encapsulate(){
        RecorderVideoQueryRespProto.Builder builder = RecorderVideoQueryRespProto.newBuilder();
        builder.setBeginVideoIndex(beginVideoIndex);
        if(hudCarcorderVideoInfoRespList!= null && hudCarcorderVideoInfoRespList.size()>0){
            for (HudCarcorderVideoInfoResp hudCarcorderVideoInfoResp:hudCarcorderVideoInfoRespList){
                builder.addVideosInfo(hudCarcorderVideoInfoResp.encapsulate());
            }
        }
        builder.setVideoNumber(videoNumber);
        builder.setRecorderType(recorderType);
        return  builder.build();
    }

    public int getBeginVideoIndex() {
        return beginVideoIndex;
    }

    public void setBeginVideoIndex(int beginVideoIndex) {
        this.beginVideoIndex = beginVideoIndex;
    }

    public List<HudCarcorderVideoInfoResp> getHudCarcorderVideoInfoRespList() {
        return hudCarcorderVideoInfoRespList;
    }

    public void setHudCarcorderVideoInfoRespList(List<HudCarcorderVideoInfoResp> hudCarcorderVideoInfoRespList) {
        this.hudCarcorderVideoInfoRespList.clear();
        this.hudCarcorderVideoInfoRespList.addAll(hudCarcorderVideoInfoRespList);
    }

    public int getVideoNumber() {
        return videoNumber;
    }

    public void setVideoNumber(int videoNumber) {
        this.videoNumber = videoNumber;
    }

    public RecorderTypeProto getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(RecorderTypeProto recorderType) {
        this.recorderType = recorderType;
    }
}

