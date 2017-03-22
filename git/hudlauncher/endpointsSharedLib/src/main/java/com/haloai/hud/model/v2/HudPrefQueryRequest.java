package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/12 21:22
 * 修改人：zhang
 * 修改时间：2016/6/12 21:22
 * 修改备注：
 */
public class HudPrefQueryRequest {

    private int hud_prefer_type = -1;

    public HudPrefQueryRequest(){}

    public HudPrefQueryRequest(Phone2HudMessagesProtoDef.HudPrefQueryRequestProto proto){
        hud_prefer_type = proto.getHudDataType();
    }

    public Phone2HudMessagesProtoDef.HudPrefQueryRequestProto encapsulate(){
        Phone2HudMessagesProtoDef.HudPrefQueryRequestProto.Builder builder = Phone2HudMessagesProtoDef.HudPrefQueryRequestProto.newBuilder();
        builder.setHudDataType(hud_prefer_type);
        return builder.build();
    }

    public int getHud_prefer_type() {
        return hud_prefer_type;
    }

    public void setHud_prefer_type(int hud_prefer_type) {
        this.hud_prefer_type = hud_prefer_type;
    }
}
