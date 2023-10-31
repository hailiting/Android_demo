package com.example.lib_audio.mediaplayer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lib_audio.mediaplayer.model.AudioBean;

@Dao
public interface FavouriteDao {
    @Insert
    void insert(AudioBean bean);
    @Delete
    void delete(AudioBean bean);
    @Query("SELECT * FROM favourite WHERE id = :audioId LIMIT 1")
    AudioBean findByAudioId(String  audioId);
    @Query("DELETE FROM favourite WHERE id = :audioId")
    void deleteByAudioId(String audioId);

}
