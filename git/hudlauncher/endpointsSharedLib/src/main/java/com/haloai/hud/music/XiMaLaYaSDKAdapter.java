package com.haloai.hud.music;

import android.content.Context;

import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.track.BatchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.haloai.hud.music.IPlayStatusListenter.PlayStatEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangrui on 17/2/22.
 */
public class XiMaLaYaSDKAdapter extends MusicSDKAdapter{

    private final static String key = "84cbae69225ca3c4ff06fed3b9910d76";//XiMaLaYa SDK key
    private Context mContext;
    private XmPlayerManager mPlayerManager;
    private List<Track> mTracks;
    private List<Radio> mRadios;
    private int mIndex;
    private int mRIndex;

    public XiMaLaYaSDKAdapter (Context context){
        mContext = context;
        CommonRequest.getInstanse().init(context, key);
        mPlayerManager = XmPlayerManager.getInstance(mContext);
    }

    public void init(){
        CommonRequest.getInstanse().setDefaultPagesize(MusicConstants.PAGE_COUNT);
        mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(statusListener);
    }

    public void release(){
        if(mPlayerManager != null){
            mPlayerManager.clearPlayCache();
            XmPlayerManager.release();
            mPlayerManager.removePlayerStatusListener(statusListener);
        }
    }

    private IXmPlayerStatusListener statusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onPlayStatus(PlayStatEnum.PLAY_START);
            }
        }

        @Override
        public void onPlayPause() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onPlayStatus(PlayStatEnum.PLAY_PAUSE);
            }
        }

        @Override
        public void onPlayStop() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onPlayStatus(PlayStatEnum.PLAY_STOP);
            }
        }

        @Override
        public void onSoundPlayComplete() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onPlayStatus(PlayStatEnum.PLAY_COMPLETE);
            }
        }

        @Override
        public void onSoundPrepared() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onSoundPrepared();
            }
        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

        }

        @Override
        public void onBufferingStart() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onBufferStatus(false);
            }
        }

        @Override
        public void onBufferingStop() {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onBufferStatus(true);
            }
        }

        @Override
        public void onBufferProgress(int i) {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onBufferProgress(i);
            }
        }

        @Override
        public void onPlayProgress(int i, int i1) {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onPlayProgress(i, i1);
            }
        }

        @Override
        public boolean onError(XmPlayerException e) {
            if(mPlayStatusListenter != null){
                mPlayStatusListenter.onError(e.getMessage());
            }
            return false;
        }
    };

    @Override
    public void getCategories() {
        HaloLogger.logE("xmly_info","getCategories");
        CommonRequest.getCategories(null, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                HaloLogger.logE("xmly_info",categoryList.getCategories().toString());
                if(mMusicSDKNotifier != null && categoryList.getCategories() != null){
                    List<CategoryInfo> list = CategoryInfo.getListByXM(categoryList);
                    mMusicSDKNotifier.onCategoriesList(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                HaloLogger.logE("xmly_info",i+s);
                if(mMusicSDKNotifier != null){
                    mMusicSDKNotifier.onError(i,s);
                }
            }
        });
    }

    @Override
    public void listAlbum(Map<String, String> params) {
        HaloLogger.logE("xmly_info","listAlbum");
        //通过内容，风格 获取专辑列表
        if(params.get(MusicConstants.CATEGORY_ID) != null){
            CommonRequest.getAlbumList(params, new IDataCallBack<AlbumList>() {
                @Override
                public void onSuccess(AlbumList albumList) {
                    if(mMusicSDKNotifier != null && albumList.getAlbums() != null){
                        List<AlbumInfo> list = AlbumInfo.getListByXM(albumList.getAlbums());
                        mMusicSDKNotifier.onAlbumsList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }

        //通过批量的专辑ids，获取专辑列表
        if(params.get(MusicConstants.ALBUM_IDS) != null){
            HaloLogger.logE("xmly_info","listAlbum1");
            CommonRequest.getBatch(params, new IDataCallBack<BatchAlbumList>() {
                @Override
                public void onSuccess(BatchAlbumList batchAlbumList) {
                    HaloLogger.logE("xmly_info","listAlbum:"+batchAlbumList.getAlbums().toString());
                    if(mMusicSDKNotifier != null && batchAlbumList.getAlbums() != null){
                        List<AlbumInfo> list = AlbumInfo.getListByXM(batchAlbumList.getAlbums());
                        mMusicSDKNotifier.onAlbumsList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }


    }

    @Override
    public void listMedia(Map<String, String> params, int startIndex) {
        mIndex = startIndex;
        //根据专辑获取媒体列表
        if(params.get(MusicConstants.ALBUM_ID) != null){
            CommonRequest.getTracks(params, new IDataCallBack<TrackList>() {
                @Override
                public void onSuccess(TrackList trackList) {
                    if(mMusicSDKNotifier != null && trackList.getTracks().size() > 0){
                        mTracks = trackList.getTracks();
                        List<MediaInfo> list = MediaInfo.getListByXMTracks(trackList.getTracks());
                        playTrcks();
                        mMusicSDKNotifier.onMediaList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }
        //根据分类和标签获取热门声音列表
        if(params.get(MusicConstants.CATEGORY_ID) != null){
            CommonRequest.getHotTracks(params, new IDataCallBack<TrackHotList>() {
                @Override
                public void onSuccess(TrackHotList trackHotList) {
                    if(mMusicSDKNotifier != null && trackHotList.getTracks().size() > 0){
                        mTracks = trackHotList.getTracks();
                        List<MediaInfo> list = MediaInfo.getListByXMTracks(trackHotList.getTracks());
                        playTrcks();
                        mMusicSDKNotifier.onMediaList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }
        //根据批量id获取声音列表
        if(params.get(MusicConstants.TRACK_IDS) != null){
            CommonRequest.getBatchTracks(params, new IDataCallBack<BatchTrackList>() {
                @Override
                public void onSuccess(BatchTrackList batchTrackList) {
                    if(mMusicSDKNotifier != null && batchTrackList.getTracks().size() > 0){
                        mTracks = batchTrackList.getTracks();
                        List<MediaInfo> list = MediaInfo.getListByXMTracks(batchTrackList.getTracks());
                        playTrcks();
                        mMusicSDKNotifier.onMediaList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }


    }

    @Override
    public void listRadio(Map<String, String> params, int startIndex) {
        //通过id批量获取电台节目
        if(params.get(MusicConstants.RADIO_IDS) != null){
            CommonRequest.getRadiosByIds(params, new IDataCallBack<RadioListById>() {
                @Override
                public void onSuccess(RadioListById radioListById) {
                    if(mMusicSDKNotifier != null && radioListById.getRadios().size() >0){
                        mRadios = radioListById.getRadios();
                        List<MediaInfo> list = MediaInfo.getListByRadios(radioListById.getRadios());
                        playRadio(mRadios.get(0));
                        mRIndex = 0;
                        mMusicSDKNotifier.onMediaList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }
    }

    @Override
    public void serachMedia(Map<String, String> keyWord, int startIndex) {

        if(keyWord.get(MusicConstants.SEARCH_KEY) == null){
            HaloLogger.postE(EndpointsConstants.MUSIC_TAG,"search Media keyWord is null");
            return;
        }
        mIndex = startIndex;
        if(keyWord.get(MusicConstants.RADIO_CATEGORY_ID) == null){
            CommonRequest.getSearchedTracks(keyWord, new IDataCallBack<SearchTrackList>() {
                @Override
                public void onSuccess(SearchTrackList searchTrackList) {
                    if(mMusicSDKNotifier != null && searchTrackList.getTracks().size() > 0){
                        mTracks = searchTrackList.getTracks();
                        List<MediaInfo> list = MediaInfo.getListByXMTracks(searchTrackList.getTracks());
                        playTrcks();
                        mMusicSDKNotifier.onMediaList(list);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }

        if(keyWord.get(MusicConstants.RADIO_CATEGORY_ID) != null){
            CommonRequest.getSearchedRadios(keyWord, new IDataCallBack<RadioList>() {
                @Override
                public void onSuccess(RadioList radioList) {
                    if(radioList.getTotalCount() > 0){
                        mRadios = radioList.getRadios();
                        Radio radio = radioList.getRadios().get(0);
                        playRadio(radio);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(mMusicSDKNotifier != null){
                        mMusicSDKNotifier.onError(i,s);
                    }
                }
            });
        }

    }

    @Override
    public void getMedia(Map<String, String> ids) {
    }

    @Override
    public void playMediaById(long mediaId) {

    }

    @Override
    public void playMediaByIndex(int index) {

    }

    @Override
    public void playAlbum(long albumId) {
        //根据专辑获取媒体列表
        mIndex = 0;
        Map<String,String> params = new HashMap<>();
        params.put(MusicConstants.ALBUM_ID,albumId+"");
        CommonRequest.getTracks(params, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if(mMusicSDKNotifier != null && trackList.getTracks() != null){
                    mTracks = trackList.getTracks();
                    playTrcks();
                }
            }

            @Override
            public void onError(int i, String s) {
                if(mMusicSDKNotifier != null){
                    mMusicSDKNotifier.onError(i,s);
                }
            }
        });


    }

    @Override
    public void playManager(PlayStatEnum playStatEnum) {
        if (mPlayerManager == null){
            return;
        }
        switch (playStatEnum){
            case PLAY_START:
                mPlayerManager.play();
                break;
            case PLAY_PAUSE:
                mPlayerManager.pause();
                break;
            case PLAY_STOP:
                mPlayerManager.stop();
                break;
            case PLAY_NEXT:
                if(mRadios.size() > 0 && PlayableModel.KIND_RADIO.equals(mPlayerManager.getCurrSound().getKind())){
                    mRIndex++;
                    mRIndex = mRIndex > mRadios.size()-1 ? 0 : mRIndex;
                    playRadio(mRadios.get(mRIndex));
                }else {
                    mPlayerManager.playNext();
                }
                break;
            case PLAY_PRE:
                if(mRadios.size() > 0 && PlayableModel.KIND_RADIO.equals(mPlayerManager.getCurrSound().getKind())){
                    mRIndex--;
                    mRIndex = mRIndex < 0 ? mRadios.size()-1 : mRIndex;
                    playRadio(mRadios.get(mRIndex));
                }else {
                    mPlayerManager.playPre();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean getPlayerStat(MediaStatEnum statEnum) {
        switch (statEnum){
            case IS_PLAYING:
                return mPlayerManager.isPlaying();
            case HAS_NEXT:
                return mPlayerManager.hasNextSound();
            case HAS_PRE:
                return mPlayerManager.hasPreSound();
            default:
                break;
        }
        return false;
    }

    private void playTrcks(){
        if(mTracks != null && mIndex > MusicConstants.DEFAUT_INDEX){
            mPlayerManager.playList(mTracks,mIndex);
        }
    }

    private void playRadio(Radio radio){
        if(mIndex > MusicConstants.DEFAUT_INDEX){
            mPlayerManager.playLiveRadioForSDK(radio,-1,0);
        }
    }


}
