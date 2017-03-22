package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.*;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudQueryResponse extends AHudResponse{
    private HudStatusQueryResp	statusQueryResp;
    private HudMiscInfoQueryResp  miscInfoQueryResp;
    private HudMusicInfoQueryResp musicInfoQueryResp;
    private HudNaviInfoQueryResp  naviInfoQueryResp;
    private HudCarcorderInfoQueryResp carcorderInfoQueryResp;

    public HudQueryResponse(){}

    public HudQueryResponse(HudQueryResponseProto proto){
        if (proto.hasStatusQueryResp()){
            this.statusQueryResp = new HudStatusQueryResp(proto.getStatusQueryResp());
        }
        if (proto.hasMiscInfoQueryResp()){
            this.miscInfoQueryResp = new HudMiscInfoQueryResp(proto.getMiscInfoQueryResp());
        }
        if (proto.hasMusicInfoQueryResp()){
            this.musicInfoQueryResp = new HudMusicInfoQueryResp(proto.getMusicInfoQueryResp());
        }
        if (proto.hasNaviInfoQueryResp()){
            this.naviInfoQueryResp = new HudNaviInfoQueryResp(proto.getNaviInfoQueryResp());
        }
        if (proto.hasCarcorderInfoQueryResp()){
            this.carcorderInfoQueryResp = new HudCarcorderInfoQueryResp(proto.getCarcorderInfoQueryResp());
        }
    }

    public HudQueryResponseProto encapsulate(){
        HudQueryResponseProto.Builder builder = HudQueryResponseProto.newBuilder();

        if (statusQueryResp != null) {
            builder.setStatusQueryResp(statusQueryResp.encapsulate());
        }
        if(miscInfoQueryResp != null){
            builder.setMiscInfoQueryResp(miscInfoQueryResp.encapsulate());
        }
        if (musicInfoQueryResp != null){
            builder.setMusicInfoQueryResp(musicInfoQueryResp.encapsulate());
        }
        if (naviInfoQueryResp != null){
            builder.setNaviInfoQueryResp(naviInfoQueryResp.encapsulate());
        }
        if (carcorderInfoQueryResp != null){
            builder.setCarcorderInfoQueryResp(carcorderInfoQueryResp.encapsulate());
        }

        return builder.build();
    }

    public HudStatusQueryResp getStatusQueryResp() {
        return statusQueryResp;
    }

    public void setStatusQueryResp(HudStatusQueryResp statusQueryResp) {
        this.statusQueryResp = statusQueryResp;
    }

    public HudMiscInfoQueryResp getMiscInfoQueryResp() {
        return miscInfoQueryResp;
    }

    public void setMiscInfoQueryResp(HudMiscInfoQueryResp miscInfoQueryResp) {
        this.miscInfoQueryResp = miscInfoQueryResp;
    }

    public HudMusicInfoQueryResp getMusicInfoQueryResp() {
        return musicInfoQueryResp;
    }

    public void setMusicInfoQueryResp(HudMusicInfoQueryResp musicInfoQueryResp) {
        this.musicInfoQueryResp = musicInfoQueryResp;
    }

    public HudNaviInfoQueryResp getNaviInfoQueryResp() {
        return naviInfoQueryResp;
    }

    public void setNaviInfoQueryResp(HudNaviInfoQueryResp naviInfoQueryResp) {
        this.naviInfoQueryResp = naviInfoQueryResp;
    }

    public HudCarcorderInfoQueryResp getCarcorderInfoQueryResp() {
        return carcorderInfoQueryResp;
    }

    public void setCarcorderInfoQueryResp(HudCarcorderInfoQueryResp carcorderInfoQueryResp) {
        this.carcorderInfoQueryResp = carcorderInfoQueryResp;
    }
}
