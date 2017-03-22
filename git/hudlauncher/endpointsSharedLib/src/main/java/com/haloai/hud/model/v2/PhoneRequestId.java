package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;

/**
 * Created by zhangrui on 16/4/20.
 */
public class PhoneRequestId {
    private String phoneRequestSerialNumber;

    public PhoneRequestId(){}

    public PhoneRequestId(Phone2HudMessagesProtoDef.PhoneRequestIdProto proto){
        if(proto.hasPhoneRequestSerialNumber()){
            this.phoneRequestSerialNumber=proto.getPhoneRequestSerialNumber();
        }
    }

    public String getPhoneRequestSerialNumber() {
        return phoneRequestSerialNumber;
    }

    public void setPhoneRequestSerialNumber(String phoneRequestSerialNumber) {
        this.phoneRequestSerialNumber = phoneRequestSerialNumber;
    }

    public Phone2HudMessagesProtoDef.PhoneRequestIdProto encapsulate(){
        Phone2HudMessagesProtoDef.PhoneRequestIdProto.Builder builder=Phone2HudMessagesProtoDef.PhoneRequestIdProto.newBuilder();
        builder.setPhoneRequestSerialNumber(phoneRequestSerialNumber);
        return builder.build();
    }
}
