package com.haloai.hud.carrecorderlibrary.utils;

public interface HudCarcorderConstants {
    String TF_VIDEOPATH = "/mnt/sdcard2/DCIM/camera";
    String LOOPINGVIDEOPATH = TF_VIDEOPATH + "/front";
    String LOCKEDVIDEOPATH = TF_VIDEOPATH + "/protect";
    String FILEPREFIX = "Recfront"; // 所能读取的文件的前缀。

    enum VideoTypeEnum {
        LOOPINGVIDEO, LOCKEDVIDEO
    }

    //录像质量 value = 0 / 1  (1080p/720p)
    String KEY_VIDEO_QUALITY = "key_video_quality";
    int DEFAULT_VIDEO_QUALITY = 1;
    //录像时间 value = 0 / 1 / 2 (1min, 2min, 5min)
    String KEY_VIDEO_RECORD_TIME = "key_video_record_time";
    int DEFAULT_VIDEO_RECORD_TIME = 2;

    int CARERECORDER_PLAYING_IMAGE_WIDTH = 341;
    int CARERECORDER_PLAYING_IMAGE_HEIGHT = 192;
}
