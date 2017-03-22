package com.haloai.hud.model.v2;

import com.google.protobuf.ByteString;
import com.haloai.hud.proto.v2.CommonMessagesProtoDef.RecorderTypeProto;
import com.haloai.hud.proto.v2.Hud2PhoneMessagesProtoDef.ThumbnailTransferRespProto;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/5/6.
 */
public class ThumbnailTransferResp extends AHudResponse{

    //TODO 缩略图数据byteString的处理
    private int thumbnailIndex;
    private List<byte[]> thumbnailBytesList;
    private int thumbnailNumber;
    private RecorderTypeProto recorderType;

    public ThumbnailTransferResp() {}
    public ThumbnailTransferResp(ThumbnailTransferRespProto proto) {
        this.thumbnailIndex = proto.getThumbnailIndex();

        if (proto.getThumbnailBytesCount()>0) {
            this.thumbnailBytesList = new LinkedList<byte[]>();
            for (ByteString byteString:proto.getThumbnailBytesList()) {
                this.thumbnailBytesList.add(byteString.toByteArray());
            }
        }
        this.thumbnailNumber = proto.getThumbnailNumber();
        this.recorderType = proto.getRecorderType();


    }

    public ThumbnailTransferRespProto encapsulate() {
        ThumbnailTransferRespProto.Builder builder = ThumbnailTransferRespProto.newBuilder();
        builder.setRecorderType(recorderType);
        builder.setThumbnailIndex(thumbnailIndex);
        builder.setThumbnailNumber(thumbnailNumber);
        if (thumbnailBytesList != null && thumbnailBytesList.size()>0){
            int i = 0;
            for(byte[] bytes:thumbnailBytesList){
                builder.addThumbnailBytes(ByteString.copyFrom(bytes));
//                        setThumbnailBytes(i,ByteString.copyFrom(bytes));
                i++;
            }
        }
        return builder.build();
    }

    public int getThumbnailIndex() {
        return thumbnailIndex;
    }

    public void setThumbnailIndex(int thumbnailIndex) {
        this.thumbnailIndex = thumbnailIndex;
    }

    public List<byte[]> getThumbnailBytesList() {
        return thumbnailBytesList;
    }

    public void setThumbnailBytesList(List<byte[]> thumbnailBytesList) {
        this.thumbnailBytesList = thumbnailBytesList;
    }

    public int getThumbnailNumber() {
        return thumbnailNumber;
    }

    public void setThumbnailNumber(int thumbnailNumber) {
        this.thumbnailNumber = thumbnailNumber;
    }

    public RecorderTypeProto getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(RecorderTypeProto recorderType) {
        this.recorderType = recorderType;
    }
}
