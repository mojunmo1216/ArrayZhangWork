package com.haloai.hud.carrecorderlibrary.controller;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.view.View;

import com.haloai.hud.carrecorderlibrary.bean.HudSDCardStates;
import com.haloai.hud.carrecorderlibrary.bean.VideoBean;
import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;

import java.util.List;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/9/2 10:38
 * 修改人：zhang
 * 修改时间：2016/9/2 10:38
 * 修改备注：
 */
public interface IHudRecorderController {

    int getVideoNumber(HudCarcorderConstants.VideoTypeEnum videoType);

    List<VideoBean> getHudVideoList(HudCarcorderConstants.VideoTypeEnum videoType, int startIndex, int listSize);

    HudSDCardStates getSDCardStates();

    boolean addVideo(VideoBean bean);

    boolean deleteVideo(Long id);

    void lockVideo(Long id);

    Cursor findVideoInfomation(String fieldName, Object fieldValue, String destfieldName);

    View getRecorderSurfaceView();

    IHudCarrecordPlayController getRecorderPlayer();

    interface IHudCarrecordPlayController {

        void playVideo(String videoPath);

        void pauseVideo();

        void resumeVideo();

        void stopVideo();

        void fastForward();

        void fastBackward();

        MediaPlayer getMedia();
    }

    void defaultRecorderSetting();
}
