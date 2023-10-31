package com.example.lib_audio.mediaplayer.core;


import static com.example.lib_audio.mediaplayer.view.NotificationHelper.NOTIFICATION_ID;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavoriteEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 音乐后台服务，并更新 notification 状态
 */
public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {
    /**
     * 常量
     */
    private static String DATA_AUDIOS = "DATA_AUDIOS";
    private static String ACTION_START = "ACTION_START";

    /**
     * data
     */
    private ArrayList<AudioBean> mAudioBeans;
    private NotificationReceiver mReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNotificationInit() {
        // 绑定notification与service，并使服务成为前台服务
        startForeground(NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        // 更新notification状态为加载态
//        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        // 更新notification状态为play态
        NotificationHelper.getInstance().showPlayStatus();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        // 更新notification状态为pause态
        NotificationHelper.getInstance().showPauseStatus();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavoriteEvent event) {
        // 更新notification状态为pause态
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioBeans = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
        if(intent.getAction().equals(ACTION_START)){
            // 播放音乐
            playMusic();
            // 初始化notification
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void playMusic() {
        AudioController.getInstance().setQueue(mAudioBeans);
        AudioController.getInstance().play();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }
    private void unRegisterBroadcastReceiver() {
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
    private void registerBroadcastReceiver(){
        if(mReceiver == null){
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }
    public static void startMusicService(ArrayList<AudioBean> audioBeans) {
        // 通过 intent 对象创建一个意图（Intent），指定启动服务为 MusicService
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        // setAction 方法设置意图动作为 ACTION_START ，用于标识服务启动动作
        intent.setAction(ACTION_START);
        // 使用putExtra 方法将 audioBeans 数据作为附加信息传递给 intent， DATA_AUDIOS用于标识传递的附加信息键值
        intent.putExtra(DATA_AUDIOS, audioBeans);
        // AudioHelper.getContext() 获得上下文
        // startService 启动指定服务
        AudioHelper.getContext().startService(intent);
    }
    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR = AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null || TextUtils.isEmpty(intent.getAction())){
                return;
            }
            String action = intent.getStringExtra(EXTRA);
            switch (action) {
                case EXTRA_PLAY:
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous();
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break; case EXTRA_FAV:
                        // 收藏广播处理
                    AudioController.getInstance().changeFavourite();
                    break;

            }
        }
    }
}
