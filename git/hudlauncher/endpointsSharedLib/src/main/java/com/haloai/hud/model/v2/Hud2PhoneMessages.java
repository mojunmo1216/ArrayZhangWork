package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudStatusQueryRespProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudInfoDeclaimProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudResponseIdProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.ThumbnailTransferRespProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudQueryResponseProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.RecorderVideoQueryRespProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.Hud2PhoneMessagesProto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class Hud2PhoneMessages {
    private HudResponseId hudResponseId;
    private HudQueryResponse hudQueryResponse;
    private List<RecorderVideoQueryResp> recorderVideoQueryRespList;
    private HudInfoDeclaim hudInfoDeclaim;
    private HudStatusQueryResp hudStatusQueryResp;
    private List<ThumbnailTransferResp> thumbnailTransferRespList;
    private HudPrefQueryResponse hudPrefQueryResponse;

    public Hud2PhoneMessages(Hud2PhoneMessagesProto proto) {
        if (proto.hasHudQueryResp()) {
            hudQueryResponse = new HudQueryResponse(proto.getHudQueryResp());
        }
        if (proto.getRecorderVideoQueryRespCount() > 0) {
            recorderVideoQueryRespList = new LinkedList<>();
            for (RecorderVideoQueryRespProto videoQueryRespProto : proto.getRecorderVideoQueryRespList()) {
                recorderVideoQueryRespList.add(new RecorderVideoQueryResp(videoQueryRespProto));
            }
        }
        if (proto.hasHudInfoDeclaim()) {
            hudInfoDeclaim = new HudInfoDeclaim(proto.getHudInfoDeclaim());
        }
        if (proto.hasHudStatusQueryResp()) {
            hudStatusQueryResp = new HudStatusQueryResp(proto.getHudStatusQueryResp());
        }
        if (proto.hasHudResponeId()) {
            hudResponseId = new HudResponseId(proto.getHudResponeId());
        }
        if (proto.getThumbnailTransferRespCount() > 0) {
            thumbnailTransferRespList = new LinkedList<>();
            for (ThumbnailTransferRespProto thumbnailTransferRespProto : proto.getThumbnailTransferRespList()) {
                thumbnailTransferRespList.add(new ThumbnailTransferResp(thumbnailTransferRespProto));
            }
        }
        if (proto.hasHudPrefQueryResp()) {
            hudPrefQueryResponse = new HudPrefQueryResponse(proto.getHudPrefQueryResp());
        }
    }

    public Hud2PhoneMessages() {
    }

    public byte[] encapsulateHudH2PData() {
        Hud2PhoneMessagesProto.Builder builder = Hud2PhoneMessagesProto.newBuilder();

        if (hudQueryResponse != null) {
            builder.setHudQueryResp(hudQueryResponse.encapsulate());
        }
        if (recorderVideoQueryRespList != null && recorderVideoQueryRespList.size() > 0) {
            for (RecorderVideoQueryResp videoQueryResp : recorderVideoQueryRespList) {
                builder.addRecorderVideoQueryResp(videoQueryResp.encapsulate());
            }
        }
        if (hudInfoDeclaim != null) {
            builder.setHudInfoDeclaim(hudInfoDeclaim.encapsulate());
        }
        if (hudStatusQueryResp != null) {
            builder.setHudStatusQueryResp(hudStatusQueryResp.encapsulate());
        }
        if (hudResponseId != null) {
            builder.setHudResponeId(hudResponseId.encapsulate());
        }
        if (thumbnailTransferRespList != null && thumbnailTransferRespList.size() > 0) {
            for (ThumbnailTransferResp thumbnailTransferResp : thumbnailTransferRespList) {
                builder.addThumbnailTransferResp(thumbnailTransferResp.encapsulate());
            }
        }
        if (hudPrefQueryResponse != null) {
            builder.setHudPrefQueryResp(hudPrefQueryResponse.encapsulate());
        }

        Hud2PhoneMessagesProto h2pMessages = builder.build();

        if (h2pMessages.getAllFields().size() == 0)
            throw new IllegalArgumentException("Doesn't include any valid child data.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            h2pMessages.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public HudQueryResponse getHudQueryResponse() {
        return hudQueryResponse;
    }

    public void setHudQueryResponse(HudQueryResponse hudQueryResponse) {
        this.hudQueryResponse = hudQueryResponse;
    }

    public List<RecorderVideoQueryResp> getRecorderVideoQueryRespList() {
        return recorderVideoQueryRespList;
    }

    public void setRecorderVideoQueryRespList(List<RecorderVideoQueryResp> recorderVideoQueryRespList) {
        this.recorderVideoQueryRespList = recorderVideoQueryRespList;
    }

    public HudInfoDeclaim getHudInfoDeclaim() {
        return hudInfoDeclaim;
    }

    public void setHudInfoDeclaim(HudInfoDeclaim hudInfoDeclaim) {
        this.hudInfoDeclaim = hudInfoDeclaim;
    }

    public HudStatusQueryResp getHudStatusQueryResp() {
        return hudStatusQueryResp;
    }

    public void setHudStatusQueryResp(HudStatusQueryResp hudStatusQueryResp) {
        this.hudStatusQueryResp = hudStatusQueryResp;
    }

    public HudResponseId getHudResponseId() {
        return hudResponseId;
    }

    public void setHudResponseId(HudResponseId hudResponseId) {
        this.hudResponseId = hudResponseId;
    }

    public List<ThumbnailTransferResp> getThumbnailTransferRespList() {
        return thumbnailTransferRespList;
    }

    public void setThumbnailTransferRespList(List<ThumbnailTransferResp> thumbnailTransferRespList) {
        this.thumbnailTransferRespList = thumbnailTransferRespList;
    }

    public HudPrefQueryResponse getHudPrefQueryResponse() {
        return hudPrefQueryResponse;
    }

    public void setHudPrefQueryResponse(HudPrefQueryResponse hudPrefQueryResponse) {
        this.hudPrefQueryResponse = hudPrefQueryResponse;
    }


    public void setAHudResponse(AHudResponse aHudResponse) {
        if (aHudResponse instanceof HudInfoDeclaim) {
            setHudInfoDeclaim((HudInfoDeclaim) aHudResponse);
        } else if (aHudResponse instanceof HudQueryResponse) {
            setHudQueryResponse((HudQueryResponse) aHudResponse);
        } else if (aHudResponse instanceof AHudListResponse) {
            List<RecorderVideoQueryResp> recorderVideoQueryRespList = ((AHudListResponse) aHudResponse).getRecorderVideoQueryRespList();
            if (recorderVideoQueryRespList != null && recorderVideoQueryRespList.size() > 0) {
                setRecorderVideoQueryRespList(recorderVideoQueryRespList);
            }

            List<ThumbnailTransferResp> thumbnailTransferRespList = ((AHudListResponse) aHudResponse).getThumbnailTransferRespList();
            if (thumbnailTransferRespList != null && thumbnailTransferRespList.size() > 0) {
                setThumbnailTransferRespList(thumbnailTransferRespList);
            }
        } else if (aHudResponse instanceof HudPrefQueryResponse) {
            setHudPrefQueryResponse((HudPrefQueryResponse) aHudResponse);
        } else if (aHudResponse instanceof HudNobodyResponse) {
            return;
        }
    }
}
