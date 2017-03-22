package com.haloai.hud.model.v2;

import com.google.protobuf.ByteString;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.HudCarcorderVideoInfoRespProto;

/**
 * Created by wangshengxing on 16/5/6.
 */
public class HudCarcorderVideoInfoResp {
    //TODO 缩略图数据byteString的处理
    private String videoName;
    private String videoPath;
    private long videoCreateTime;
    private int videoTimeLength;
    private int videoSize;
    private byte[] thumbnailBytes;

    public HudCarcorderVideoInfoResp() {

    }
    public HudCarcorderVideoInfoResp(HudCarcorderVideoInfoRespProto proto) {
        this.videoName = proto.getVideoName();
        this.videoPath = proto.getVideoPath();
        this.videoCreateTime = proto.getVideoCreateTime();
        this.videoTimeLength = proto.getVideoTimeLength();
        this.videoSize = proto.getVideoSize();
        if (proto.hasThumbnailBytes()) {
            thumbnailBytes = proto.getThumbnailBytes().toByteArray();
        }
    }

    public HudCarcorderVideoInfoRespProto encapsulate() {
        HudCarcorderVideoInfoRespProto.Builder builder = HudCarcorderVideoInfoRespProto.newBuilder();
        builder.setVideoName(videoName);
        builder.setVideoPath(videoPath);
        builder.setVideoCreateTime(videoCreateTime);
        builder.setVideoTimeLength(videoTimeLength);
        builder.setVideoSize(videoSize);
        if (thumbnailBytes != null) {
            builder.setThumbnailBytes(ByteString.copyFrom(thumbnailBytes));
        }
        return builder.build();
    }


    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public long getVideoCreateTime() {
        return videoCreateTime;
    }

    public void setVideoCreateTime(long videoCreateTime) {
        this.videoCreateTime = videoCreateTime;
    }

    public int getVideoTimeLength() {
        return videoTimeLength;
    }

    public void setVideoTimeLength(int videoTimeLength) {
        this.videoTimeLength = videoTimeLength;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public byte[] getThumbnailBytes() {
        return thumbnailBytes;
    }

    public void setThumbnailBytes(byte[] thumbnailBytes) {
        this.thumbnailBytes = thumbnailBytes;
    }
}
