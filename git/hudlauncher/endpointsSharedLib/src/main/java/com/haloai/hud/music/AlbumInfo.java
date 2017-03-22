package com.haloai.hud.music;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.category.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangrui on 17/2/23.
 */
public class AlbumInfo {

    private long id;
    private int categoryId;
    private String kind;//默认“album”
    private String title;
    private String tag;
    private String imageUrl;
    private String artist;
    private int count;
    private List<MediaInfo> mediaList;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public List<MediaInfo> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaInfo> mediaList) {
        this.mediaList = mediaList;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static List<AlbumInfo> getListByXM(List<Album> albumList){
        List<AlbumInfo> list = new ArrayList<>();
        for(Album album : albumList){
            AlbumInfo albumInfo = new AlbumInfo();
            albumInfo.setId(album.getId());
            albumInfo.setCategoryId(album.getCategoryId());
            albumInfo.setTag(album.getAlbumTags());
            albumInfo.setKind(MusicConstants.ALBUM_KIND);
            albumInfo.setTitle(album.getAlbumTitle());
            albumInfo.setArtist(album.getAnnouncer().getNickname());
            albumInfo.setCount((int)album.getIncludeTrackCount());
            albumInfo.setImageUrl(album.getCoverUrlLarge());
            list.add(albumInfo);
        }
        return list;
    }
}
