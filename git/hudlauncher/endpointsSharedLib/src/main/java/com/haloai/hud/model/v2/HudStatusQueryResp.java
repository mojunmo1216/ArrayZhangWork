package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudStatusQueryRespProto;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudStatusQueryResp extends AHudResponse{


    public HudStatusQueryResp(HudStatusQueryRespProto proto){

    }
    public HudStatusQueryResp(){}
    public HudStatusQueryRespProto encapsulate(){
        HudStatusQueryRespProto.Builder builder = HudStatusQueryRespProto.newBuilder();


        return  builder.build();
    }

}
