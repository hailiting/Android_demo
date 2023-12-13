package com.example.lib_video.videoplayer.core;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_video.videoplayer.core.view.CustomVideoView;
import com.example.lib_video.videoplayer.core.view.VideoFullDialog;
import com.example.lib_video.videoplayer.utils.Utils;
public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {
    private Context mContext;
    // ui
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;

    // data
    private String mXAdInstance;
    private SDKSlotListener mSlotListener;
    public VideoAdSlot(String adInstance, SDKSlotListener slotListener) {
        mXAdInstance = adInstance;
        mSlotListener = slotListener;
        mParentView = slotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }
    private void initVideoView(){
        mVideoView = new CustomVideoView(mContext);
        if(mXAdInstance != null){
            mVideoView.setDataSource(mXAdInstance);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }
    public void destroy(){
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }
    @Override
    public void onClickBackBtn() {
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        // 获取videoView在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(mVideoView);
        VideoFullDialog dialog = new VideoFullDialog(mContext, mVideoView, mXAdInstance, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }
            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
        dialog.setViewBundle(bundle); // 为Dialog设置播放器数据Bundle对象
        dialog.setSlotListener(mSlotListener);
        dialog.show();
        AudioHelper.pauseAudio();
    }
    private void backToSmallMode(int position) {
        if(mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); // 防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position);
        // 恢复音乐
        AudioHelper.resumeAudio();
    }
    private void bigPlayComplete() {
        if(mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0);
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
    }
    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if(mSlotListener!=null){
            mSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if(mSlotListener != null) {
            mSlotListener.onVideoFailed();
        }
    }

    @Override
    public void onAdVideoLoadComplete() {
        if(mSlotListener != null) {
            mSlotListener.onVideoComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    public interface SDKSlotListener {
        ViewGroup getAdParent();
        void onVideoLoadSuccess();
        void onVideoFailed();
        void onVideoComplete();
    }
}
