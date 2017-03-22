package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudNaviInfoQueryRespProto;

/**
 * Created by wangshengxing on 16/4/8.
 */

public class HudNaviInfoQueryResp extends AHudResponse {

    private HudDataDefine hudDataDefine;

    public HudNaviInfoQueryResp(HudNaviInfoQueryRespProto proto) {
        if (proto.hasHudDataDefine()) {
            hudDataDefine = new HudDataDefine(proto.getHudDataDefine());
        }
    }

    public HudNaviInfoQueryResp() {
    }

    public HudNaviInfoQueryRespProto encapsulate() {
        HudNaviInfoQueryRespProto.Builder builder = HudNaviInfoQueryRespProto.newBuilder();
        if (hudDataDefine != null) {
            builder.setHudDataDefine(hudDataDefine.encapsulate());
        }
        return builder.build();
    }

    public HudDataDefine getHudDataDefine() {
        return hudDataDefine;
    }

    public void setHudDataDefine(HudDataDefine hudDataDefine) {
        this.hudDataDefine = hudDataDefine;
    }
}

