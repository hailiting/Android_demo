package com.example.lib_audio.mediaplayer.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.Favourite;
import com.example.lib_audio.mediaplayer.model.room.AudioBeanDao;
import com.example.lib_audio.mediaplayer.model.room.FavouriteDao;

/**
 * 操作db数据库帮助类
 */
@Database(entities = {AudioBean.class, Favourite.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AudioBeanDao audioBeanDao();
    public abstract FavouriteDao favouriteDao();

}

