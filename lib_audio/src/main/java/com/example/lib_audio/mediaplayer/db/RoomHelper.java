package com.example.lib_audio.mediaplayer.db;

import android.content.Context;

import androidx.room.Room;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.room.FavouriteDao;
public class RoomHelper {

    private static final String DB_NAME = "music_db";
    private static AppDatabase mAppDatabase;
    public static void initDatabase() {
        Context context = AudioHelper.getContext();
        mAppDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                // 允许在主线程访问数据库
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // 将旧的删除，创建新的
                .build();
    }
    public static void addFavourite(AudioBean audioBean) {
        FavouriteDao dao = mAppDatabase.favouriteDao();
        dao.insert(audioBean);
    }
    public static void removeFavourite(AudioBean audioBean) {
        FavouriteDao dao = mAppDatabase.favouriteDao();
        dao.deleteByAudioId(audioBean.id);
    }
    public static AudioBean selectFavourite(AudioBean audioBean) {FavouriteDao dao = mAppDatabase.favouriteDao();
        return dao.findByAudioId(audioBean.id);
    }
}
