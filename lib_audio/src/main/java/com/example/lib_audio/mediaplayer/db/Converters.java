package com.example.lib_audio.mediaplayer.db;

import androidx.room.TypeConverters;

import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Converters {
    @TypeConverters
    public static List<AudioBean> fromString(String value) {
        return new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
    }
    @TypeConverters
    public static String fromList(List<String> list) {
        return new Gson().toJson(list);
    }
}
