package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef;

/**
 * Created by zhangrui on 16/4/20.
 */
public class HudResponseId {
    private String hudResponseSerialNumber;

    public HudResponseId(){

    }
    public HudResponseId(Hud2PhoneMessagesProtoDef.HudResponseIdProto proto){
        if(proto.hasHudResponeseSerialNumer()){
            this.hudResponseSerialNumber=proto.getHudResponeseSerialNumer();
        }
    }

    public String getHudResponseSerialNumber() {
        return hudResponseSerialNumber;
    }

    public void setHudResponseSerialNumber(String hudResponseSerialNumber) {
        this.hudResponseSerialNumber = hudResponseSerialNumber;
    }

    public Hud2PhoneMessagesProtoDef.HudResponseIdProto encapsulate(){
        Hud2PhoneMessagesProtoDef.HudResponseIdProto.Builder builder= Hud2PhoneMessagesProtoDef.HudResponseIdProto.newBuilder();
        builder.setHudResponeseSerialNumer(hudResponseSerialNumber);
        return builder.build();
    }
}
