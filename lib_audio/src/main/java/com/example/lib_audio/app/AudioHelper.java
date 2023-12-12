package com.example.lib_audio.app;

import android.app.Activity;
import android.content.Context;

import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.core.MusicService;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.db.RoomHelper;
import com.example.lib_audio.mediaplayer.view.MusicPlayerActivity;

import java.util.ArrayList;

public class AudioHelper {
    // SDK 全局Context, 供子模块用
    private static Context mContext;
    public static void init(Context context) {
        mContext = context;
        RoomHelper.initDatabase();
    }
    public static Context getContext() {
        return mContext;
    }
    public static void startMusicService(ArrayList<AudioBean> audioBeans){
        MusicService.startMusicService(audioBeans);
    }
    public static void addAudio(Activity activity, AudioBean bean) {
        AudioController.getInstance().addAudio(bean);
        MusicPlayerActivity.start(activity);
    }
    public static void pauseAudio() {
        AudioController.getInstance().pause();
    }
    public static void resumeAudio(){
        AudioController.getInstance().resume();
    }

}
