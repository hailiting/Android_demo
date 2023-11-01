package com.example.lib_audio.mediaplayer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.Favourite;

@Dao
public interface AudioBeanDao {
    @Insert
    void insert(AudioBean bean);
    @Delete
    void delete(AudioBean bean);
    @Query("SELECT * FROM audioBean WHERE id = :audioId LIMIT 1")
    AudioBean findByAudioId(String  audioId);
    @Query("DELETE FROM audioBean WHERE id = :audioId")
    void deleteByAudioId(String audioId);
}
