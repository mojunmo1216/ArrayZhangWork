package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudPrefSet{

    private HudDataDefine huddataDefine;

    public HudPrefSet() {
    }

    public HudPrefSet(Phone2HudMessagesProtoDef.HudPrefSetProto proto){
        if (proto.hasHudDataDefine()) {
            huddataDefine = new HudDataDefine(proto.getHudDataDefine());
        }
    }

    public Phone2HudMessagesProtoDef.HudPrefSetProto encapsulate() {
        Phone2HudMessagesProtoDef.HudPrefSetProto.Builder builder = Phone2HudMessagesProtoDef.HudPrefSetProto.newBuilder();
        if (huddataDefine != null) {
            builder.setHudDataDefine(huddataDefine.encapsulate());
        }
        return builder.build();
    }

    public HudDataDefine getHudPrefDefine() {
        return huddataDefine;
    }

    public void setHudDataDefine(HudDataDefine hudPrefDefine) {
        this.huddataDefine = hudPrefDefine;
    }
}
