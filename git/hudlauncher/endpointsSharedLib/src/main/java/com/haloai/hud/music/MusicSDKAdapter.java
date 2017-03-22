package com.haloai.hud.music;

import android.content.Context;
import com.haloai.hud.music.IPlayStatusListenter.PlayStatEnum;


import java.util.List;
import java.util.Map;

/**
 * Created by zhangrui on 17/2/22.
 *  Music SDK 的抽象类
 */
public abstract class MusicSDKAdapter {

    public static  final String MUSIC_SDK_XIMALAYA = "hud_music_ximalaya_fm"; // 喜马拉雅FM
    public static  final String MUSIC_SDK_KAOLA = "hud_music_kaola_fm"; // 考拉FM
    public static  final String MUSIC_SDK_XIAMI = "hud_music_xiami_fm"; // 虾米音乐

    private static MusicSDKAdapter mXiMaLaYaSDKAdapter;

    protected IPlayStatusListenter mPlayStatusListenter;
    protected IMusicSDKNotifier mMusicSDKNotifier;

    public static MusicSDKAdapter getMusicSDKAdapter(Context context, String musicSDK){
        MusicSDKAdapter musicSDKAdapter = null;
        if(MUSIC_SDK_XIMALAYA.equalsIgnoreCase(musicSDK)){
            if(mXiMaLaYaSDKAdapter == null){
                mXiMaLaYaSDKAdapter = new XiMaLaYaSDKAdapter(context);
            }
            musicSDKAdapter = mXiMaLaYaSDKAdapter;
        }
        return musicSDKAdapter;
    }

    public void setPlayStatusListenter(IPlayStatusListenter playStatusListenter){
        this.mPlayStatusListenter = playStatusListenter;
    }

    public void setMusicSDKNotifier(IMusicSDKNotifier musicSDKNotifier){
        this.mMusicSDKNotifier = musicSDKNotifier;
    }

    public abstract void getCategories();

    /**
     *
     * @param params category_id/ids, tag_name, calc_dimension(算法), page, count
     *  可以根据风格tag,内容id,获取指定长度的专辑列表
     */
    public abstract void listAlbum(Map<String, String> params);

    /**
     *
     * @param params radio_category_id/album_id/(category_id,tag_name 默认热门), sort, page, count
     * @param startIndex 默认-1不播放列表，>-1 按照角标播放歌曲列表
     *  可以根据 内容 风格,专辑id,获取媒体列表
     */
    public abstract void listMedia(Map<String, String> params, int startIndex);

    /**
     *
     * @param params radio_category_id/album_id/(category_id,tag_name 默认热门), sort, page, count
     * @param startIndex 默认-1不播放列表，>-1 按照角标播放歌曲列表
     *  可以根据 内容 风格,专辑id,获取媒体列表
     */
    public abstract void listRadio(Map<String, String> params, int startIndex);

    /**
     *
     * @param keyWord 搜索媒体资源的关键字
     *  歌手名，歌曲名，专辑名，进行搜索
     * @param startIndex 默认-1不播放列表，>-1 按照角标播放歌曲列表
     */
    public abstract void serachMedia(Map<String, String> keyWord, int startIndex);

    /**
     *
     * @param ids ids
     *  可以根据批量ids 获取对应的专辑
     */
    public abstract void getMedia(Map<String, String> ids);

    public abstract void playMediaById(long mediaId);

    public abstract void playMediaByIndex(int index);

    public abstract void playAlbum(long albumId);

    public abstract void playManager(PlayStatEnum playStatEnum);

    public abstract boolean getPlayerStat(MediaStatEnum statEnum);


    public interface IMusicSDKNotifier{

        void onCategoriesList(List<CategoryInfo> list);

        void onAlbumsList(List<AlbumInfo> list);

        void onMediaList(List<MediaInfo> list);

        void onMediaInfo(MediaInfo mediaInfo);

        void onMediaSearchResult(MediaInfo mediaInfo);

        void onError(int code, String error);

    }

    enum MediaStatEnum {
        IS_PLAYING,
        HAS_NEXT,
        HAS_PRE
    }

}
