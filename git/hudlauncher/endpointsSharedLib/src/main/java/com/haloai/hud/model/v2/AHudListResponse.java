package com.haloai.hud.model.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/5/9 12:00
 * 修改人：zhang
 * 修改时间：2016/5/9 12:00
 * 修改备注：
*/
public class AHudListResponse extends AHudResponse {
    private List<RecorderVideoQueryResp> recorderVideoQueryRespList = new ArrayList<RecorderVideoQueryResp>();

    public List<ThumbnailTransferResp> getThumbnailTransferRespList() {
        return thumbnailTransferRespList;
    }

    public void setThumbnailTransferRespList(List<ThumbnailTransferResp> thumbnailTransferRespList) {
        this.thumbnailTransferRespList = thumbnailTransferRespList;
    }


    private List<ThumbnailTransferResp> thumbnailTransferRespList = new ArrayList<ThumbnailTransferResp>();

    public List<RecorderVideoQueryResp> getRecorderVideoQueryRespList() {
        return recorderVideoQueryRespList;
    }

    public void setRecorderVideoQueryRespList(List<RecorderVideoQueryResp> recorderVideoQueryRespList) {
        this.recorderVideoQueryRespList = recorderVideoQueryRespList;
    }
}
