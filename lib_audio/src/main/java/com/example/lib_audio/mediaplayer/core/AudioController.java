package com.example.lib_audio.mediaplayer.core;

import com.example.lib_audio.mediaplayer.db.RoomHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavoriteEvent;
import com.example.lib_audio.mediaplayer.events.AudioPlayModeEvent;
import com.example.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.example.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

public class AudioController {
    /**
     * 播放方式
     */
    public enum PlayMode {
        // 列表循环
        LOOP,
        // 随机
        RANDOM,
        // 单曲播放
        REPEAT
    }
    private AudioPlayer mAudioPlayer;
    // 存取所有播放歌曲
    private ArrayList<AudioBean> mQueue;
    // 当前播放歌曲的索引
    private int mQueueIndex;
    // 播放模式
    private PlayMode mPlayMode = PlayMode.LOOP;

    // 初始化变量
    private AudioController(){
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>():mQueue;
    }

    /**
     * 设置播放队列
     * @param queue
     */
    public void setQueue(ArrayList<AudioBean> queue){
        this.setQueue(queue, 0);
    }

    /**
     * 设置播放队列，并指定index
     * @param queue
     * @param queueIndex
     */
    public void setQueue(ArrayList<AudioBean> queue, int queueIndex){
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
        RoomHelper.selectAudioBeans(queue);
    }
    public void addAudio( AudioBean bean){
        this.addAudio(0, bean);
    }
    private int queueAudio(AudioBean bean) {
        return 0;
    }
    private void addCustomAudio(int index, AudioBean bean){

    }

    /**
     * 添加单一歌曲
     * @param index
     * @param bean
     */
    public void addAudio(int index, AudioBean bean){
        if(mQueue == null){
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
        // 判断当前列表是否已经存在当前歌曲
        int query = queueAudio(bean);
        if(query<=-1){
            // 没有添加过，添加进来
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean currentBean = getNowPlaying();
            if(!currentBean.id.equals(bean.id)){
                // 已经添加过且不在播放中
                setPlayIndex(query);
            }
        }
    }

    public PlayMode getPlayMode(){
        return mPlayMode;
    }

    /**
     * 对外提供设置播放模式
     * @param playMode
     */
    public void setPlayMode(PlayMode playMode){
        mPlayMode = playMode;
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }
    public void setPlayIndex(int index){
        if(mQueue == null){
            throw new AudioQueueEmptyException("当前播放列表为空，请先设置");
        }
        mQueueIndex = index;
        play();
    }

    public int getPlayIndex(){
        return mQueueIndex;
    }
    /**
     * 对外提供是否播放中的状态
     */
    public boolean isStartState(){
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 当前是不是pause
     */
    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }
    // 获取播放器当前状态
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }
    /**
     * 单例方法
     */
    public static AudioController getInstance() {
        return AudioController.SingletonHolder.instance;
    }
    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }
    private AudioBean getPlaying(int index) {
        if(mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else  {
            throw new AudioQueueEmptyException("当前播放队列为空，请先设置播放队列.");
        }
    }
    public AudioBean getNowPlaying(){
        return getPlaying(mQueueIndex);
    }
    private AudioBean getNextPlaying(){
        switch (mPlayMode){
            case LOOP:
                // 保证 mQueueIndex 的值一直在 mQueue.size()里
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying(mQueueIndex);
    }
    private AudioBean getPreviousPlaying(){
        switch (mPlayMode){
            case LOOP:
                // 保证 mQueueIndex 的值一直在 mQueue.size()里
                mQueueIndex = (mQueueIndex - 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying(mQueueIndex);
    }

    public void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }
    public void pause() {
        mAudioPlayer.pause();
    }
    public void resume() {
        mAudioPlayer.resume();
    }
    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    // 下一首
    public void next(){
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }
    // 上一首
    public void previous(){
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 添加/移除到收藏
     */
    public void  changeFavourite(){
        if(null != RoomHelper.selectFavourite(getNowPlaying())){
            // 已收藏，移除
            RoomHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavoriteEvent(false));
        } else {
            RoomHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavoriteEvent(true));
        }
    }
    /**
     * 自动切换播放/暂停
     */
    public void playOrPause(){
        if(isStartState()){
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }
}
