package com.example.lib_audio.mediaplayer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lib_audio.mediaplayer.model.Favourite;

@Dao
public interface FavouriteDao {
    @Insert
    void insert(Favourite bean);
    @Delete
    void delete(Favourite bean);
    @Query("SELECT * FROM favourite WHERE audioId = :audioId LIMIT 1")
    Favourite findByAudioId(String  audioId);
    @Query("DELETE FROM favourite WHERE audioId = :audioId")
    void deleteByAudioId(String audioId);
}
