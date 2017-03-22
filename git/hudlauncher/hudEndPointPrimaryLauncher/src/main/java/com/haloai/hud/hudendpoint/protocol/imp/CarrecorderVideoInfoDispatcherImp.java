package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.haloai.hud.carrecorderlibrary.bean.VideoBean;
import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;
import com.haloai.hud.carrecorderlibrary.db.CarRecorderDBHelper;
import com.haloai.hud.carrecorderlibrary.factory.HudRecorderControllerFactory;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudListResponse;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudCarcorderVideoInfoResp;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.model.v2.RecorderVideoQuery;
import com.haloai.hud.model.v2.RecorderVideoQueryResp;
import com.haloai.hud.proto.v2.CommonMessagesProtoDef;
import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/5/7 15:16
 * 修改人：zhang
 * 修改时间：2016/5/7 15:16
 * 修改备注：
 */
public class CarrecorderVideoInfoDispatcherImp implements IDataDispatcher {

    private Context mContext;
    private List<HudCarcorderVideoInfoResp> mVideoInfoList = new ArrayList<>();


    public CarrecorderVideoInfoDispatcherImp(Context context) {
        this.mContext = context;
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        List<RecorderVideoQuery> recorderVideoQueryList = phone2HudMessages.getRecorderVideoQueryList();
        if (recorderVideoQueryList == null || recorderVideoQueryList.size() <= 0) {
            return null;
        }
        // 需要返回信息
        IHudRecorderController hudCarcorderController = HudRecorderControllerFactory.getHudCarcorderController2(mContext);
        AHudListResponse aHudListResponse = new AHudListResponse();
        List<RecorderVideoQueryResp> recorderVideoQueryRespList = aHudListResponse.getRecorderVideoQueryRespList();

        for (RecorderVideoQuery recorderVideoQuery : recorderVideoQueryList) {
            // boolean beginNewBatch = recorderVideoQuery.isBeginNewBatch();
            CommonMessagesProtoDef.RecorderTypeProto recorderType = recorderVideoQuery.getRecorderType();
            final int videoIndex = recorderVideoQuery.getVideoIndex();
            int videoNumber = recorderVideoQuery.getVideoNumber();
            boolean thumbnailRequired = recorderVideoQuery.isVideoThumbnailRequired();

            String path;
            switch (recorderType) {
                case LOOP_VIDEO:
                    path = HudCarcorderConstants.LOOPINGVIDEOPATH;
                    break;
                case LOCK_VIDEO:
                    path = HudCarcorderConstants.LOCKEDVIDEOPATH;
                    break;
                default:
                    path = null;
            }
            RecorderVideoQueryResp recorderVideoQueryResp = new RecorderVideoQueryResp();
            setVideoInfoList(hudCarcorderController, videoIndex, videoNumber, thumbnailRequired, path);
            recorderVideoQueryResp.setBeginVideoIndex(videoIndex);
            recorderVideoQueryResp.setVideoNumber(videoNumber);
            recorderVideoQueryResp.setRecorderType(recorderType);
            recorderVideoQueryResp.setHudCarcorderVideoInfoRespList(mVideoInfoList);
            recorderVideoQueryRespList.add(recorderVideoQueryResp);
        }

        return aHudListResponse;
    }

    private void setVideoInfoList(IHudRecorderController mController, int videoIndex, int videoNumber, boolean thumbnailRequired, String path) {
        mVideoInfoList.clear();
        HudCarcorderConstants.VideoTypeEnum type;
        if (path.contains("protect")) {
            type = HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO;
        } else {
            type = HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO;
        }

        List<VideoBean> hudVideoList = mController.getHudVideoList(type, videoIndex, videoNumber);
        if (hudVideoList == null || hudVideoList.size() <= 0) {
            HaloLogger.logE("setVideoInfoList", "路径错误或视频个数为0");
            return;
        }

        for (VideoBean videoInfo : hudVideoList) {
            HudCarcorderVideoInfoResp hudCarcorderVideoInfoResp = new HudCarcorderVideoInfoResp();
            hudCarcorderVideoInfoResp.setVideoCreateTime(videoInfo.startTime);
            hudCarcorderVideoInfoResp.setVideoName(videoInfo.videoName);
            hudCarcorderVideoInfoResp.setVideoPath(videoInfo.videoPath);
            hudCarcorderVideoInfoResp.setVideoSize((int) videoInfo.videoSize);
            hudCarcorderVideoInfoResp.setVideoTimeLength((int) videoInfo.videoDuration);
            if (thumbnailRequired) {
                Cursor cursor = mController.findVideoInfomation(BaseColumns._ID, videoInfo.id, CarRecorderDBHelper.THUMB_BYTE_ARRAY);
                if (cursor.moveToNext()) {
                    byte[] imagebytes = cursor.getBlob(cursor.getColumnIndex(CarRecorderDBHelper.THUMB_BYTE_ARRAY));
                    hudCarcorderVideoInfoResp.setThumbnailBytes(imagebytes);
                } else {
                    HaloLogger.logE("setVideoInfoList", "返回手机的获取图片失败");
                }
                cursor.close();
            }
            mVideoInfoList.add(hudCarcorderVideoInfoResp);
        }
    }
}
