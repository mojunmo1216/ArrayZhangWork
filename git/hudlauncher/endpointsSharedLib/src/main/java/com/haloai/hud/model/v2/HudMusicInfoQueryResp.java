package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudMusicInfoQueryRespProto;

/**
 * Created by wangshengxing on 16/4/8.
 */

public class HudMusicInfoQueryResp extends AHudResponse {


    public HudMusicInfoQueryResp(HudMusicInfoQueryRespProto proto){

    }
    public HudMusicInfoQueryResp(){}
    public HudMusicInfoQueryRespProto encapsulate(){
        HudMusicInfoQueryRespProto.Builder builder = HudMusicInfoQueryRespProto.newBuilder();


        return  builder.build();
    }

}

