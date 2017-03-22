package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/7 11:15
 * 修改人：zhang
 * 修改时间：2016/6/7 11:15
 * 修改备注：
 */
public class HudPrefQueryResponse extends AHudResponse {
    private HudDataDefine huddataDefine;

    public HudPrefQueryResponse() {
    }

    public HudPrefQueryResponse(Hud2PhoneMessagesProtoDef.HudPrefQueryResponseProto proto){
        if (proto.hasHudDataDefine()) {
            huddataDefine = new HudDataDefine(proto.getHudDataDefine());
        }
    }

    public Hud2PhoneMessagesProtoDef.HudPrefQueryResponseProto encapsulate() {
        Hud2PhoneMessagesProtoDef.HudPrefQueryResponseProto.Builder builder = Hud2PhoneMessagesProtoDef.HudPrefQueryResponseProto.newBuilder();
        if (huddataDefine != null) {
            builder.setHudDataDefine(huddataDefine.encapsulate());
        }
        return builder.build();
    }

    public HudDataDefine getHudDataDefine() {
        return huddataDefine;
    }

    public void setHudPrefDefine(HudDataDefine hudPrefDefine) {
        this.huddataDefine = hudPrefDefine;
    }
}
