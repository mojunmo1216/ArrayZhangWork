package com.haloai.hud.model.v2;


import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudMiscInfoQueryRespProto;

/**
 * Created by wangshengxing on 16/4/8.
 */

public class HudMiscInfoQueryResp extends AHudResponse {


    public HudMiscInfoQueryResp(HudMiscInfoQueryRespProto proto){

    }
    public HudMiscInfoQueryResp(){}
    public HudMiscInfoQueryRespProto encapsulate(){
        HudMiscInfoQueryRespProto.Builder builder = HudMiscInfoQueryRespProto.newBuilder();


        return  builder.build();
    }

}
