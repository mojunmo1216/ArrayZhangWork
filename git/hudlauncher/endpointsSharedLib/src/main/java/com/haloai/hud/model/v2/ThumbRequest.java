package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.proto.v2.CommonMessagesProtoDef.RecorderTypeProto;

/**
 * Created by wangshengxing on 16/5/6.
 */
public class ThumbRequest {

    private RecorderTypeProto recorderType;		//视频记录类型
    private int thumbnailIndex;  //
    private int thumbnailNumber;  //请求缩略图的个数

    public ThumbRequest() {
    }
    public ThumbRequest(Phone2HudMessagesProtoDef.ThumRequestProto proto) {
        this.recorderType = proto.getRecorderType();
        this.thumbnailIndex = proto.getThumbnailIndex();
        this.thumbnailNumber = proto.getThumbnailNumber();
    }
    public Phone2HudMessagesProtoDef.ThumRequestProto encapsulate() {
        Phone2HudMessagesProtoDef.ThumRequestProto.Builder builder = Phone2HudMessagesProtoDef.ThumRequestProto.newBuilder();
        builder.setRecorderType(recorderType);
        builder.setThumbnailIndex(thumbnailIndex);
        builder.setThumbnailNumber(thumbnailNumber);
        return builder.build();
    }

    public RecorderTypeProto getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(RecorderTypeProto recorderType) {
        this.recorderType = recorderType;
    }

    public int getThumbnailIndex() {
        return thumbnailIndex;
    }

    public void setThumbnailIndex(int thumbnailIndex) {
        this.thumbnailIndex = thumbnailIndex;
    }

    public int getThumbnailNumber() {
        return thumbnailNumber;
    }

    public void setThumbnailNumber(int thumbnailNumber) {
        this.thumbnailNumber = thumbnailNumber;
    }

}
