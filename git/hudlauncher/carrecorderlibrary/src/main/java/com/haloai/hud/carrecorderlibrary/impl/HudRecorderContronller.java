package com.haloai.hud.carrecorderlibrary.impl;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.haloai.hud.carrecorderlibrary.bean.HudSDCardStates;
import com.haloai.hud.carrecorderlibrary.bean.VideoBean;
import com.haloai.hud.carrecorderlibrary.controller.IHudRecorderController;
import com.haloai.hud.carrecorderlibrary.db.CarRecorderDBHelper;
import com.haloai.hud.carrecorderlibrary.utils.CarRecorderFileUtils;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.carrecorderlibrary.views.HudCarcorderSurfaceViewPlayController;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.io.File;
import java.util.List;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/9/2 11:30
 * 修改人：zhang
 * 修改时间：2016/9/2 11:30
 * 修改备注：
 */
public class HudRecorderContronller implements IHudRecorderController {

    private Context mContext;
    CarRecorderDBHelper helper;


    public HudRecorderContronller(Context context) {
        mContext = context;
        helper = CarRecorderDBHelper.getSingleCarRecorderDBHelper(context);
    }

    @Override
    public int getVideoNumber(HudCarcorderConstants.VideoTypeEnum videoType) {
        return helper.getVideoCount(getTypeString(videoType));
    }


    @Override
    public List<VideoBean> getHudVideoList(HudCarcorderConstants.VideoTypeEnum videoType, int startIndex, int listSize) {
        String typeString = getTypeString(videoType);
        return helper.pagesToFind(typeString, startIndex, listSize);
    }

    @Override
    public void defaultRecorderSetting() {
        try {
            Settings.System.putInt(mContext.getContentResolver(), HudCarcorderConstants.KEY_VIDEO_QUALITY, HudCarcorderConstants.DEFAULT_VIDEO_QUALITY);
            Settings.System.putInt(mContext.getContentResolver(), HudCarcorderConstants.KEY_VIDEO_RECORD_TIME, HudCarcorderConstants.DEFAULT_VIDEO_RECORD_TIME);
//            mContext.getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
            HaloLogger.postE(EndpointsConstants.RECORD_TAG,String.format("carrecorder info ,defaultRecorderSetting error "));
        }

    }

    /**
     * 机器默认为720p清晰度和1min时长
     *
     * @return SD卡状态bean
     */
    @Override
    public HudSDCardStates getSDCardStates() {
        HudSDCardStates bean = new HudSDCardStates();
        bean.mSDCardMounted = CarRecorderFileUtils.hasSdcard();
        File file = new File(HudCarcorderConstants.TF_VIDEOPATH);
        bean.mTotalSize = file.getTotalSpace() / 1024;
        bean.mUsbleSize = file.getUsableSpace() / 1024;

        switch (Settings.System.getInt(mContext.getContentResolver(), HudCarcorderConstants.KEY_VIDEO_QUALITY, -1)) {
            case 0:
                bean.mImageQuality = "1080p";
                break;
            case 1:
                bean.mImageQuality = "720p";
                break;
            default:
                bean.mImageQuality = "null";
        }
        switch (Settings.System.getInt(mContext.getContentResolver(), HudCarcorderConstants.KEY_VIDEO_RECORD_TIME, -1)) {
            case 0:
                bean.mVideoDuration = "1min";
                break;
            case 1:
                bean.mVideoDuration = "2min";
                break;
            case 2:
                bean.mVideoDuration = "5min";
                break;
            default:
                bean.mVideoDuration = "null";
        }
        return bean;
    }

    @Override
    public boolean addVideo(VideoBean bean) {
        boolean insert = false;
        Cursor cursor = helper.findVideoInfo(CarRecorderDBHelper.VIDEO_NAME, bean.videoName, BaseColumns._ID);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            Log.e("addVideo", "数据库已存在该视频，id为：" + id);
            cursor.close();
        } else {
            cursor.close();
            long l = helper.insert(bean.videoName, bean.videoSize, bean.videoDuration, bean.startTime, bean.endTime, bean.thumbBytes, bean.videoPath, bean.videoType);
            insert =  l > 0;
        }
        return insert;
    }

    /**
     * 数据库逻辑删除，文件删除请另行操作
     *
     * @param id 视频id
     * @return 是否成功删除
     */
    @Override
    public boolean deleteVideo(Long id) {
        return helper.delete(id);
    }

    @Override
    public void lockVideo(Long id) {
        helper.update(BaseColumns._ID, id, CarRecorderDBHelper.VIDEO_TYPE, CarRecorderDBHelper.LOCK_VIDEO);
    }

    @Override
    public Cursor findVideoInfomation(String fieldName, Object fieldValue, String destfieldName) {
        return helper.findVideoInfo(fieldName, fieldValue, destfieldName);
    }

    @Override
    public View getRecorderSurfaceView() {
        return HudCarcorderSurfaceViewPlayController.getRecorderSurfaceView(mContext);
    }

    @Override
    public IHudCarrecordPlayController getRecorderPlayer() {
        return HudCarcorderSurfaceViewPlayController.getRecorderSurfaceView(mContext);
    }

    private String getTypeString(HudCarcorderConstants.VideoTypeEnum videoType) {
        String type = null;
        if (videoType == HudCarcorderConstants.VideoTypeEnum.LOOPINGVIDEO) {
            type = CarRecorderDBHelper.LOOP_VIDEO;
        } else if (videoType == HudCarcorderConstants.VideoTypeEnum.LOCKEDVIDEO) {
            type = CarRecorderDBHelper.LOCK_VIDEO;
        }
        return type;
    }
}
