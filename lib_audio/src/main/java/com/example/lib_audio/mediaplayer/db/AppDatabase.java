package com.example.lib_audio.mediaplayer.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.room.FavouriteDao;

/**
 * 操作db数据库帮助类
 */
@Database(entities = {AudioBean.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavouriteDao favouriteDao();

}

