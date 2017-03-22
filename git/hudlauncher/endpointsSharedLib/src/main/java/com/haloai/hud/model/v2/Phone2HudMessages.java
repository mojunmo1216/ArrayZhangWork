package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.RecorderVideoQueryProto;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.Phone2HudMessagesProto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Mo Bing(mobing@haloai.com) on 7/4/2016.
 */
public class Phone2HudMessages {
    private PhoneRequestId phoneRequestId;
    private PhoneInfoDeclaim phoneInfoDeclaim;
    private HudQueryRequest hudQueryRequest;
    private NaviRouteInfo naviRouteInfo;
    private HudPrefQueryRequest hudPrefQueryRequest;
    private HudPrefSet hudPrefSet;
    private HudRemoterCommand hudRemoterCommand;
    private List<RecorderVideoQuery> recorderVideoQueryList;
    private List<ThumbRequest> thumbRequestList;

    public Phone2HudMessages() {
    }

    public Phone2HudMessages(Phone2HudMessagesProto proto) {
        if (proto.hasPhoneInfoDeclaim()) {
            this.phoneInfoDeclaim = new PhoneInfoDeclaim(proto.getPhoneInfoDeclaim());
        }
        if (proto.hasHudQueryReq()) {
            this.hudQueryRequest = new HudQueryRequest(proto.getHudQueryReq());
        }
        if (proto.hasNaviPhoneInfo()) {
            this.naviRouteInfo = new NaviRouteInfo(proto.getNaviPhoneInfo());
        }
        if (proto.hasHudPrefSet()) {
            this.hudPrefSet = new HudPrefSet(proto.getHudPrefSet());
        }
        if (proto.hasHudPrefQuery()) {
            this.hudPrefQueryRequest = new HudPrefQueryRequest(proto.getHudPrefQuery());
        }
        if (proto.hasHudRemotCommand()) {
            this.hudRemoterCommand = new HudRemoterCommand(proto.getHudRemotCommand());
        }
        if (proto.getRecorderVideoQueryCount() > 0) {
            this.recorderVideoQueryList = new ArrayList<RecorderVideoQuery>();
            for (RecorderVideoQueryProto recorderVideoQueryProto : proto.getRecorderVideoQueryList()) {
                this.recorderVideoQueryList.add(new RecorderVideoQuery(recorderVideoQueryProto));
            }
        }
        if (proto.hasPhoneRequestId()) {
            this.phoneRequestId = new PhoneRequestId(proto.getPhoneRequestId());
        }
        if (proto.getThumbRequestCount() > 0) {
            this.thumbRequestList = new ArrayList<ThumbRequest>();
            for (Phone2HudMessagesProtoDef.ThumRequestProto recorderVideoQueryProto : proto.getThumbRequestList()) {
                this.thumbRequestList.add(new ThumbRequest(recorderVideoQueryProto));
            }
        }
    }

    public List<ThumbRequest> getThumbRequestList() {
        return thumbRequestList;
    }

    public void setThumbRequestList(List<ThumbRequest> thumbRequestList) {
        this.thumbRequestList = thumbRequestList;
    }

    public PhoneInfoDeclaim getPhoneInfoDeclaim() {
        return phoneInfoDeclaim;
    }

    public void setPhoneInfoDeclaim(PhoneInfoDeclaim phoneInfoDeclaim) {
        this.phoneInfoDeclaim = phoneInfoDeclaim;
    }

    public HudQueryRequest getHudQueryRequest() {
        return hudQueryRequest;
    }

    public void setHudQueryRequest(HudQueryRequest hudQueryRequest) {
        this.hudQueryRequest = hudQueryRequest;
    }

    public NaviRouteInfo getNaviRouteInfo() {
        return naviRouteInfo;
    }

    public void setNaviRouteInfo(NaviRouteInfo naviRouteInfo) {
        this.naviRouteInfo = naviRouteInfo;
    }

    public HudPrefSet getHudPrefSet() {
        return hudPrefSet;
    }

    public void setHudPrefSet(HudPrefSet hudPrefSet) {
        this.hudPrefSet = hudPrefSet;
    }

    public HudPrefQueryRequest getHudPrefQueryRequest() {
        return hudPrefQueryRequest;
    }

    public void setHudPrefQueryRequest(HudPrefQueryRequest hudPrefQueryRequest) {
        this.hudPrefQueryRequest = hudPrefQueryRequest;
    }

    public HudRemoterCommand getHudRemoterCommand() {
        return hudRemoterCommand;
    }

    public void setHudRemoterCommand(HudRemoterCommand hudRemoterCommand) {
        this.hudRemoterCommand = hudRemoterCommand;
    }

    public List<RecorderVideoQuery> getRecorderVideoQueryList() {
        return recorderVideoQueryList;
    }

    public void setRecorderVideoQueryList(List<RecorderVideoQuery> recorderVideoQueryList) {
        this.recorderVideoQueryList = recorderVideoQueryList;
    }

    public PhoneRequestId getPhoneRequestId() {
        return phoneRequestId;
    }

    public void setPhoneRequestId(PhoneRequestId phoneRequestId) {
        this.phoneRequestId = phoneRequestId;
    }

    public byte[] encapsulateHudP2HData() {
        Phone2HudMessagesProto.Builder builder = Phone2HudMessagesProto.newBuilder();

        if (phoneInfoDeclaim != null) {
            builder.setPhoneInfoDeclaim(phoneInfoDeclaim.encapsulate());
        }
        if (hudQueryRequest != null) {
            builder.setHudQueryReq(hudQueryRequest.encapsulate());
        }
        if (naviRouteInfo != null) {
            builder.setNaviPhoneInfo(naviRouteInfo.encapsulate());
        }
        if (hudPrefSet != null) {
            builder.setHudPrefSet(hudPrefSet.encapsulate());
        }
        if (hudPrefQueryRequest != null) {
            builder.setHudPrefQuery(hudPrefQueryRequest.encapsulate());
        }
        if (hudRemoterCommand != null) {
            builder.setHudRemotCommand(hudRemoterCommand.encapsulate());
        }
        if (recorderVideoQueryList != null && recorderVideoQueryList.size() > 0) {
            for (int i = 0; i < recorderVideoQueryList.size(); i++) {
                builder.addRecorderVideoQuery(i, recorderVideoQueryList.get(i).encapsulate());
            }
        }
        if (thumbRequestList != null && thumbRequestList.size() > 0) {
            for (int i = 0; i < thumbRequestList.size(); i++) {
                builder.addThumbRequest(i, thumbRequestList.get(i).encapsulate());
            }
        }
        if (phoneRequestId != null) {
            builder.setPhoneRequestId(phoneRequestId.encapsulate());
        }
        Phone2HudMessagesProtoDef.Phone2HudMessagesProto p2hMessages = builder.build();

        if (p2hMessages.getAllFields().size() == 0)
            throw new IllegalArgumentException("Doesn't include any valid child data.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            p2hMessages.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

}
