package com.example.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 维护一个状态 MediaPlayer
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {
    public enum Status {
        IDEL, INITIALIZED, STARTED, PAUSED, STOPPED, COMPLETED;
    }
    private OnCompletionListener mCompletionListener;
    private Status mStates;
    public CustomMediaPlayer(){
        super();
        mStates = Status.IDEL;
        super.setOnCompletionListener(this);
    }
    @Override
    public void reset(){
        super.reset();
        mStates = Status.IDEL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStates = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStates = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStates = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStates = Status.STOPPED;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mStates = Status.COMPLETED;
    }

    //
    public Status getState() {
        return mStates;
    }
    //
    public boolean isComplete(){
        return mStates == Status.COMPLETED;
    }
    //
    public void setCompleteListener(OnCompletionListener listener){
        mCompletionListener = listener;
    }
}
