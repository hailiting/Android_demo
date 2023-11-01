package com.example.lib_audio.mediaplayer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import java.io.Serializable;
// Entity -> AudioBean 这个类生成一个表
@Entity(tableName = "audioBean",
    indices = {@Index(value = "mUrl", unique = true)})
public class AudioBean implements Serializable {
    private static final long serialVersionUID = -8849228294348905620L;

    public AudioBean(String id, @NonNull String mUrl, @NonNull String name, @NonNull String author,
                     @NonNull String album, @NonNull String albumInfo, @NonNull String albumPic,
                     @NonNull String totalTime) {
        this.id = id;
        this.mUrl = mUrl;
        this.name = name;
        this.author = author;
        this.album = album;
        this.albumInfo = albumInfo;
        this.albumPic = albumPic;
        this.totalTime = totalTime;
    }
    public AudioBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMUrl() {
        return this.mUrl;
    }

    public void setMUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumPic() {
        return this.albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    public String id;
    @NonNull
    @ColumnInfo(name="mUrl")
    public String mUrl;
    @NonNull
    public String name;
    @NonNull
    public String author;
    @NonNull
    public String album;
    @NonNull
    public String albumInfo;
    @NonNull
    public String albumPic;
    @NonNull
    public String totalTime;

    @Override
    public boolean equals(Object other) {
        if(other == null){
            return false;
        }
        if(!(other instanceof AudioBean)){
            return false;
        }
        return ((AudioBean) other).id.equals(this.id);
    }
}
