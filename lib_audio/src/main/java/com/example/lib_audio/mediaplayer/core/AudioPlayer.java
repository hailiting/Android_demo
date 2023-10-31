package com.example.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.example.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioProgressEvent;
import com.example.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;


/**
 * 1. 播放音频
 * 2. 对外发送各种类型的事件
 *  1. 监听系统时间 系统事件发送到 AudioPlayer，AudioPlayer接收到又发送出去
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnBufferingUpdateListener,
MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener,
        AudioFocusManager.AudioFocusListener{
    // 打日志用的
    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    // 核心 真正负责音频的播放
    private CustomMediaPlayer mMediaPlayer;
    // 后台保活能力
    private WifiManager.WifiLock mWifiLock;
    // 音频焦点的监听器
    private AudioFocusManager mAudioFocusManager;
    // 焦点获取失败，处于暂停状态
    private boolean isPauseByFocusLossTransient;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    // 暂停也要更新进度，防止UI不同步，只不过进度一直一样
                    if(getStatus() == CustomMediaPlayer.Status.STARTED || getStatus() == CustomMediaPlayer.Status.PAUSED) {
                        // UI类型处理事件
                        EventBus.getDefault()
                                .post(new AudioProgressEvent(getStatus(), getCurrentPosition(), getDuration()));
                    }
                    break;
            }
        }
    };
    public AudioPlayer() {
        init();
    }
    // 初始化
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // 初始化wifilock
        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }
    private void setVolume(float leftVol, float rightVol){
        if(mMediaPlayer != null) mMediaPlayer.setVolume(leftVol, rightVol);
    }
    // 内部开始播放
    private void start(){
        // 没有焦点，说明有其他播放器在播放
        if(!mAudioFocusManager.requestAudioFocus()){
            Log.e(TAG, "获取音频焦点失败");
        } else {
            // 正式进入播放状态
            mMediaPlayer.start();
            mWifiLock.acquire();
            // 对外发送start事件
            EventBus.getDefault().post(new AudioStartEvent());
        }
    }

    // 对外提供的加载，让外部去加载
    public void load(AudioBean audioBean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            // 异步准备过程 mMediaPlayer 在另一个线程加载 建立， 建立完后 自动调用 onPrepared
            mMediaPlayer.prepareAsync();
            // 对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (Exception e){
            // 对外发送Error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }
    // 设置音量



    /**
     * 对外提供暂停
     */
    public void pause(){
        // mMediaPlayer 状态
        if(getStatus() == CustomMediaPlayer.Status.STARTED){
            mMediaPlayer.pause();
            // 释放wifilock
            if(mWifiLock.isHeld()){
                mWifiLock.release();
            }
            // 释放音频焦点
            if(mAudioFocusManager != null){
                mAudioFocusManager.abandonAudioFocus();
            }
            // 发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 对外提供恢复
     */
    public void resume(){
        // mMediaPlayer 状态
        if(getStatus() == CustomMediaPlayer.Status.PAUSED){
            start();
        }
    }

    /**
     * 清空播放器占用资源
     */
    public  void release(){
        if(mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        if(mAudioFocusManager !=null){
            mAudioFocusManager.abandonAudioFocus();
        }
        if(mWifiLock.isHeld()){
            mWifiLock.release();
        }
        mAudioFocusManager = null;
        mWifiLock = null;
        // 发送release销毁事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**
     * 缓存进度回调
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    /**
     * 播放完毕回调
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // return false;
        // 播放出错回调
        EventBus.getDefault().post(new AudioErrorEvent());
        return true; // return true 代表发生 onError 就不会回调 onCompletion方法，执行处理了error方法
    }

    /**
     * onLoad完自动进入播放状态
     * @param mediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        start();
    }

    /**
     * 再次获得音频焦点，重新把音量设置在正常值
     */
    @Override
    public void audioFocusGrant() {
        // 让音量正常
        setVolume(1.0f, 1.0f);
        if(isPauseByFocusLossTransient){
            resume();
        }
        isPauseByFocusLossTransient = false;
    }

    /**
     * 永久失去焦点
     */
    @Override
    public void audioFocusLoss() {
        pause();
    }

    /**
     * 短暂失去焦点
     */
    @Override
    public void audioFocusLossTransient() {
        pause();
        isPauseByFocusLossTransient = true;
    }

    /**
     * 瞬间失去焦点，短信音乐或通知声音
     */
    @Override
    public void audioFocusLossDuck() {
        // 声音减半
        setVolume(0.5f, 0.5f);
    }

    /**
     * 获取播放器当前状态
     */
    public CustomMediaPlayer.Status getStatus() {
        if(mMediaPlayer != null) {
            return mMediaPlayer.getState();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    /**
     * 获取当前音乐总时长，更新进度用
     * @return
     */
    public int getDuration() {
        if(getStatus() == CustomMediaPlayer.Status.STARTED || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }
    public int getCurrentPosition() {
        if(getStatus() == CustomMediaPlayer.Status.STARTED || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }
}
