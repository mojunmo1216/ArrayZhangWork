package com.haloai.hud.music;

import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangrui on 17/2/23.
 */
public class MediaInfo{

    private long id;
    private long albumId;
    private String kind;//可选“song,radio,track”
    private String title;
    private String tag;
    private String imageUrl;
    private String artist;
    private String mediaUrl;
    private long duration;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public static List<MediaInfo> getListByXMTracks(List<Track> trackList){
        List<MediaInfo> list = new ArrayList<>();
        for (Track track : trackList){
            MediaInfo mediaInfo = new MediaInfo();
            mediaInfo.setId(track.getDataId());
            mediaInfo.setTag(track.getTrackTags());
            mediaInfo.setImageUrl(track.getCoverUrlLarge());
            mediaInfo.setArtist(track.getAnnouncer().getNickname());
            mediaInfo.setDuration(track.getDuration());
            mediaInfo.setKind(track.getKind());
            mediaInfo.setTitle(track.getTrackTitle());
            mediaInfo.setMediaUrl(track.getPlayUrl64());
            mediaInfo.setAlbumId(track.getAlbum().getAlbumId());
            list.add(mediaInfo);
        }
        return list;
    }

    public static List<MediaInfo> getListByRadios(List<Radio> radioList){
        List<MediaInfo> list = new ArrayList<>();
        for (Radio radio : radioList){
            MediaInfo mediaInfo = new MediaInfo();
            mediaInfo.setId(radio.getDataId());
            mediaInfo.setImageUrl(radio.getCoverUrlLarge());
            mediaInfo.setArtist(radio.getRadioDesc());
            mediaInfo.setKind(radio.getKind());
            mediaInfo.setTitle(radio.getKind());
            mediaInfo.setMediaUrl(radio.getRate64AacUrl());
            list.add(mediaInfo);
        }
        return list;
    }



}
