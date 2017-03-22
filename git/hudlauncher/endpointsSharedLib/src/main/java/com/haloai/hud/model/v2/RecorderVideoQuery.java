package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.CommonMessagesProtoDef.RecorderTypeProto;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef.RecorderVideoQueryProto;

/**
 * Created by wangshengxing on 16/4/8.
 */
public class RecorderVideoQuery {
    private boolean beginNewBatch;
    private RecorderTypeProto recorderType;
    private int videoIndex;
    private int videoNumber;
    private boolean videoThumbnailRequired;

    public RecorderVideoQuery(RecorderVideoQueryProto proto){
        this.beginNewBatch = proto.getBeginNewBatch();
        this.recorderType = proto.getRecorderType();
        this.videoIndex = proto.getVideoIndex();
        this.videoNumber = proto.getVideoNumber();
        this.videoThumbnailRequired = proto.getVideoThumbnailRequired();
    }
    public RecorderVideoQuery(){}
    public RecorderVideoQueryProto encapsulate() {
        RecorderVideoQueryProto.Builder builder = RecorderVideoQueryProto.newBuilder();
        builder.setBeginNewBatch(beginNewBatch);
        builder.setRecorderType(recorderType);
        builder.setVideoIndex(videoIndex);
        builder.setVideoNumber(videoNumber);
        builder.setVideoThumbnailRequired(videoThumbnailRequired);

        return builder.build();
    }

    public boolean isBeginNewBatch() {
        return beginNewBatch;
    }

    public void setBeginNewBatch(boolean beginNewBatch) {
        this.beginNewBatch = beginNewBatch;
    }

    public RecorderTypeProto getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(RecorderTypeProto recorderType) {
        this.recorderType = recorderType;
    }

    public int getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(int videoIndex) {
        this.videoIndex = videoIndex;
    }

    public int getVideoNumber() {
        return videoNumber;
    }

    public void setVideoNumber(int videoNumber) {
        this.videoNumber = videoNumber;
    }

    public boolean isVideoThumbnailRequired() {
        return videoThumbnailRequired;
    }

    public void setVideoThumbnailRequired(boolean videoThumbnailRequired) {
        this.videoThumbnailRequired = videoThumbnailRequired;
    }
}
