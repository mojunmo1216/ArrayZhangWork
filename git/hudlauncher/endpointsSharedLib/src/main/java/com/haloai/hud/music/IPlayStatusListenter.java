package com.haloai.hud.music;

/**
 * Created by zhangrui on 17/3/1.
 * 播放器播放状态的监听
 */
public interface IPlayStatusListenter {

    enum  PlayStatEnum{
        PLAY_START,
        PLAY_STOP,
        PLAY_PAUSE,
        PLAY_NEXT,
        PLAY_PRE,
        PLAY_COMPLETE
    }

    void onPlayStatus(PlayStatEnum stat);

    //播放器准备完成
    void onSoundPrepared();

    //切歌
    void onSoundSwicth(int last, int cur);

    //缓存开始或者结束
    void onBufferStatus(boolean isEnd);

    //缓存进度
    void onBufferProgress(int percent);

    //播放进度
    void onPlayProgress(int currPos, int duration);

    //播放器报错
    void onError(String error);

}
