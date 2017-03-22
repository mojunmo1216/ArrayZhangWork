package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudCarcorderInfoQueryRespProto;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class HudCarcorderInfoQueryResp extends AHudResponse{

    private int hudTotalMemory;
    private int hudUsedMemory;
    private String recorderVideoQuality;
    private int loopVideoNumber;
    private int lockVideoNumber;
    private int wonderfulVideoNumber;
    private int emergencyVideoNumber;

    public HudCarcorderInfoQueryResp(HudCarcorderInfoQueryRespProto proto){
        this.hudTotalMemory = proto.getHudTotalMemory();
        this.hudUsedMemory = proto.getHudUsedMemory();
        this.recorderVideoQuality = proto.getRecorderVideoQuality();
        this.loopVideoNumber = proto.getLoopVideoNumber();
        this.lockVideoNumber = proto.getLockVideoNumber();
        this.wonderfulVideoNumber = proto.getWonderfulVideoNumber();
        this.emergencyVideoNumber = proto.getEmergencyVideoNumber();

    }
    public HudCarcorderInfoQueryResp(){}
    public HudCarcorderInfoQueryRespProto encapsulate(){
        HudCarcorderInfoQueryRespProto.Builder builder = HudCarcorderInfoQueryRespProto.newBuilder();
        builder.setHudTotalMemory(hudTotalMemory);
        builder.setHudUsedMemory(hudUsedMemory);
        builder.setRecorderVideoQuality(recorderVideoQuality);
        builder.setLoopVideoNumber(loopVideoNumber);
        builder.setLockVideoNumber(lockVideoNumber);
        builder.setWonderfulVideoNumber(wonderfulVideoNumber);
        builder.setEmergencyVideoNumber(emergencyVideoNumber);
        return  builder.build();
    }

    public int getHudTotalMemory() {
        return hudTotalMemory;
    }

    public void setHudTotalMemory(int hudTotalMemory) {
        this.hudTotalMemory = hudTotalMemory;
    }

    public int getHudUsedMemory() {
        return hudUsedMemory;
    }

    public void setHudUsedMemory(int hudUsedMemory) {
        this.hudUsedMemory = hudUsedMemory;
    }

    public String getRecorderVideoQuality() {
        return recorderVideoQuality;
    }

    public void setRecorderVideoQuality(String recorderVideoQuality) {
        this.recorderVideoQuality = recorderVideoQuality;
    }

    public int getLoopVideoNumber() {
        return loopVideoNumber;
    }

    public void setLoopVideoNumber(int loopVideoNumber) {
        this.loopVideoNumber = loopVideoNumber;
    }

    public int getLockVideoNumber() {
        return lockVideoNumber;
    }

    public void setLockVideoNumber(int lockVideoNumber) {
        this.lockVideoNumber = lockVideoNumber;
    }

    public int getWonderfulVideoNumber() {
        return wonderfulVideoNumber;
    }

    public void setWonderfulVideoNumber(int wonderfulVideoNumber) {
        this.wonderfulVideoNumber = wonderfulVideoNumber;
    }

    public int getEmergencyVideoNumber() {
        return emergencyVideoNumber;
    }

    public void setEmergencyVideoNumber(int emergencyVideoNumber) {
        this.emergencyVideoNumber = emergencyVideoNumber;
    }
}


