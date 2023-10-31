package com.example.lib_audio.mediaplayer.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_image_loader.app.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BottomMusicView extends RelativeLayout {
    public Context mContext;

    /**
     * View
     */
    private ImageView mLeftView;
    private TextView mTitleView;
    private TextView mAlbumView;
    private ImageView mPlayView;
    private ImageView mRightView;

    /**
     * 播放实体
     */
    private AudioBean mAudioBean;
    public BottomMusicView(Context context){
        this(context, null);
    }
    public BottomMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BottomMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 完成构造逻辑
        mContext = context;
        EventBus.getDefault().register(this);
        initView();
    }
    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bottom_view, this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                 MusicPlayerActivity.start((Activity) mContext);
            }
        });
        mLeftView = rootView.findViewById(R.id.album_view);
        // 动画，让左侧mLeftView不停的旋转 暂停  动画也暂停  播放  动画也播放
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, View.ROTATION.getName(), 0f, 360);
        // 10s 转一圈
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();
        // 动画 end
        mTitleView = rootView.findViewById(R.id.audio_name_view);
        mAlbumView = rootView.findViewById(R.id.audio_album_view);
        mPlayView = rootView.findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dddd", "111");
                // 处理播放暂停事件
                AudioController.getInstance().playOrPause();
            }
        });
        mRightView = rootView.findViewById(R.id.show_list_view);
        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示音乐列表对话框
            }
        });
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    // 实现逻辑
    // 接收 audio 并处理

    /**
     * 监听加载事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        mAudioBean = event.mAudioBean;
        showLoadingView();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        showPlayView();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
    }

    private void showLoadingView(){
        if(mAudioBean != null) {
            ImageLoaderManager.getInstance().displayImageForCircle(mLeftView, mAudioBean.albumPic);
            mTitleView.setText(mAudioBean.name);
            mAlbumView.setText(mAudioBean.album);
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }
    private void showPlayView(){
        if(mAudioBean != null){
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }
    private void showPauseView(){
        if(mAudioBean != null){
            mPlayView.setImageResource(R.mipmap.note_btn_play_white);
        }
    }
}
