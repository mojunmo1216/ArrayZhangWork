package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.PhoneInfoDeclaimProto;

/**
 * @author Created by Mo Bing(mobing@haloai.com) on 7/4/2016.
 */
public class PhoneInfoDeclaim {
    private int protocolVersionCode;
    private Phone2HudMessagesProtoDef.PhonePlatformProto phonePlatform;
    private String phonePlatformVersion;
    private String phoneAppVersionStr;
    private int phoneAppVersionCode;
    private String phoneUniqueId;
    private String phoneNumber;

    public PhoneInfoDeclaim(Phone2HudMessagesProtoDef.PhonePlatformProto phonePlatform) {
        this.phonePlatform = phonePlatform;
    }
    public PhoneInfoDeclaim(){}
    public PhoneInfoDeclaim(PhoneInfoDeclaimProto proto) {
        this.protocolVersionCode = proto.getProtocolVersionCode();
        this.phonePlatform = proto.getPhonePlatformType();
        this.phonePlatformVersion = proto.getPhonePlatformVersion();
        this.phoneAppVersionStr = proto.getPhoneAppVersionStr();
        this.phoneUniqueId = proto.getPhoneUniqueId();
        this.phoneNumber = proto.getPhoneNumber();
    }

    public int getProtocolVersionCode() {
        return protocolVersionCode;
    }

    public void setProtocolVersionCode(int protocolVersionCode) {
        this.protocolVersionCode = protocolVersionCode;
    }

    public Phone2HudMessagesProtoDef.PhonePlatformProto getPhonePlatform() {
        return phonePlatform;
    }

    public void setPhonePlatform(Phone2HudMessagesProtoDef.PhonePlatformProto phonePlatform) {
        this.phonePlatform = phonePlatform;
    }

    public String getPhonePlatformVersion() {
        return phonePlatformVersion;
    }

    public void setPhonePlatformVersion(String phonePlatformVersion) {
        this.phonePlatformVersion = phonePlatformVersion;
    }

    public String getPhoneAppVersionStr() {
        return phoneAppVersionStr;
    }

    public void setPhoneAppVersionStr(String phoneAppVersionStr) {
        this.phoneAppVersionStr = phoneAppVersionStr;
    }

    public int getPhoneAppVersionCode() {
        return phoneAppVersionCode;
    }

    public void setPhoneAppVersionCode(int phoneAppVersionCode) {
        this.phoneAppVersionCode = phoneAppVersionCode;
    }

    public String getPhoneUniqueId() {
        return phoneUniqueId;
    }

    public void setPhoneUniqueId(String phoneUniqueId) {
        this.phoneUniqueId = phoneUniqueId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneInfoDeclaimProto encapsulate() {
        PhoneInfoDeclaimProto.Builder builder = PhoneInfoDeclaimProto.newBuilder();

        builder.setProtocolVersionCode(protocolVersionCode);
        builder.setPhonePlatformType(phonePlatform);
        builder.setPhonePlatformVersion(phonePlatformVersion);
        builder.setPhoneAppVersionStr(phoneAppVersionStr);
        builder.setPhoneAppVersionCode(phoneAppVersionCode);
        builder.setPhoneUniqueId(phoneUniqueId);
        builder.setPhoneNumber(phoneNumber);

        return builder.build();
    }
}