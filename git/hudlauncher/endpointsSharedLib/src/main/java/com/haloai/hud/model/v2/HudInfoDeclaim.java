package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef;


/**
 * @author Created by zhangrui on 16/4/14.
 */
public class HudInfoDeclaim  extends AHudResponse{

    private int protocolVersionCode;
    private String firmwareVersion;
    private String launcherAppVersionStar;
    private int  launcherAppVersionCode;
    private String hudUniqueId;
    private String hudSimId;

    public HudInfoDeclaim(Hud2PhoneMessagesProtoDef.HudInfoDeclaimProto proto){
        this.protocolVersionCode =proto.getProtocolVersionCode();
        this.firmwareVersion =proto.getFirmwareVersion();
        this.launcherAppVersionStar=proto.getLauncherAppVersionStr();
        this.launcherAppVersionCode=proto.getLauncherAppVersionCode();
        this.hudUniqueId=proto.getHudUniqueId();
        this.hudSimId=proto.getHudSimIccid();
    }
    public HudInfoDeclaim(){}
    public int getProtocolVersionCode() {
        return protocolVersionCode;
    }

    public void setProtocolVersionCode(int protocolVersionCode) {
        this.protocolVersionCode = protocolVersionCode;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getLauncherAppVersionStar() {
        return launcherAppVersionStar;
    }

    public void setLauncherAppVersionStar(String launcherAppVersionStar) {
        this.launcherAppVersionStar = launcherAppVersionStar;
    }

    public int getLauncherAppVersionCode() {
        return launcherAppVersionCode;
    }

    public void setLauncherAppVersionCode(int launcherAppVersionCode) {
        this.launcherAppVersionCode = launcherAppVersionCode;
    }

    public String getHudUniqueId() {
        return hudUniqueId;
    }

    public void setHudUniqueId(String hudUniqueId) {
        this.hudUniqueId = hudUniqueId;
    }

    public String getHudSimId(){
        return hudSimId;
    }

    public void setHudSimId(String hudSimId){
        this.hudSimId = hudSimId;
    }

    public Hud2PhoneMessagesProtoDef.HudInfoDeclaimProto encapsulate(){
        Hud2PhoneMessagesProtoDef.HudInfoDeclaimProto.Builder builder = Hud2PhoneMessagesProtoDef.HudInfoDeclaimProto.newBuilder();

        builder.setProtocolVersionCode(protocolVersionCode);
        builder.setFirmwareVersion(firmwareVersion);
        builder.setLauncherAppVersionCode(launcherAppVersionCode);
        builder.setLauncherAppVersionStr(launcherAppVersionStar);
        builder.setHudUniqueId(hudUniqueId);
        builder.setHudSimIccid(hudSimId);
        return builder.build();
    }
}
