package com.example.lib_audio.mediaplayer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "favourite",
        foreignKeys = @ForeignKey(entity = AudioBean.class,
                parentColumns = "id",
                childColumns = "audioId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "audioId", unique = true)})
public class Favourite implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long favouriteId;

    @NonNull
    @ColumnInfo(name = "audioId")
    private String audioId;

    public Favourite(@NonNull String audioId) {
        this.audioId = audioId;
    }
    public Favourite() {
    }

    public Long getFavouriteId() {
        return favouriteId;
    }

    public void setFavouriteId(Long favouriteId) {
        this.favouriteId = favouriteId;
    }

    @NonNull
    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(@NonNull String audioId) {
        this.audioId = audioId;
    }
}