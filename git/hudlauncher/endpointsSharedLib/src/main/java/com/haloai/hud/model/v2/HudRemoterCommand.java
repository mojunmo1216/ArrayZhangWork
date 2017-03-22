package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.HudRemoterCommandProto;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudRemoterCommand extends AHudResponse{
    private int hudRemoterCommandId;
    private int hudCommandInt;
    private int hudCommandInt1;
    private int hudCommandInt2;
    private String hudCommandString;


    public HudRemoterCommand(HudRemoterCommandProto proto){
        this.hudRemoterCommandId=proto.getHudRemoterCommandId();
        this.hudCommandInt=proto.getIntParam();
        this.hudCommandInt1=proto.getIntParam1();
        this.hudCommandInt2=proto.getIntParam2();
        this.hudCommandString=proto.getStringParam();

    }
    public HudRemoterCommand(){}

    public int getHudRemoterCommandId() {
        return hudRemoterCommandId;
    }

    public void setHudRemoterCommandId(int hudRemoterCommandId) {
        this.hudRemoterCommandId = hudRemoterCommandId;
    }

    public int getHudCommandInt() {
        return hudCommandInt;
    }

    public void setHudCommandInt(int hudCommandInt) {
        this.hudCommandInt = hudCommandInt;
    }

    public int getHudCommandInt1() {
        return hudCommandInt1;
    }

    public void setHudCommandInt1(int hudCommandInt1) {
        this.hudCommandInt1 = hudCommandInt1;
    }

    public int getHudCommandInt2() {
        return hudCommandInt2;
    }

    public void setHudCommandInt2(int hudCommandInt2) {
        this.hudCommandInt2 = hudCommandInt2;
    }

    public String getHudCommandString() {
        return hudCommandString;
    }

    public void setHudCommandString(String hudCommandString) {
        this.hudCommandString = hudCommandString;
    }

    public HudRemoterCommandProto encapsulate(){
        HudRemoterCommandProto.Builder builder = HudRemoterCommandProto.newBuilder();
        builder.setHudRemoterCommandId(hudRemoterCommandId);
        builder.setIntParam(hudCommandInt);
        builder.setIntParam1(hudCommandInt1);
        builder.setIntParam2(hudCommandInt2);
        builder.setStringParam(hudCommandString);
        return builder.build();
    }
}
