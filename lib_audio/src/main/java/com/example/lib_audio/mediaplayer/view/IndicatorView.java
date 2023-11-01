package com.example.lib_audio.mediaplayer.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.view.adpater.MusicPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class IndicatorView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    /**
     * view 相关
     */
    private Context mContext;
    private ImageView mImageView;
    private ViewPager mViewPager;//可以切换的viewpage
    private MusicPagerAdapter mAdapter;

    /**
     * data
     */
    private AudioBean mAudioBean; // 当前播放的歌曲
    private ArrayList<AudioBean> mQueue; // 播放队列

    public IndicatorView(Context context) {
        this(context, null);
    }
    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }
    private void initData(){
        mQueue = AudioController.getInstance().getQueue();
        mAudioBean = AudioController.getInstance().getNowPlaying();
    }
    private void initView(){
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_view, this);
        mImageView = rootView.findViewById(R.id.tip_view);
        mViewPager = rootView.findViewById(R.id.view_pager);
        // mQueue 歌曲列表
        mAdapter = new MusicPagerAdapter(mQueue, mContext);
        // 为viewpager绑定adapter
        mViewPager.setAdapter(mAdapter);
        showLoadView(false);
        // 要在UI初始化完，否则会多一次listener响应
        mViewPager.addOnPageChangeListener(this);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        AudioController.getInstance().setPlayIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                // 滑动结束，或没有滑动
                showPlayView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                // 在滑动中
                showPauseView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                // 惯性滚动时
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        mAudioBean = event.mAudioBean;
        // 更新viewpager为load状态
        showLoadView(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        // 更新viewpager为播放状态
        showPlayView();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        // 更新viewpager为暂停状态
        showPauseView();
    }

    private void showLoadView(boolean isSmooth){
        mViewPager.setCurrentItem(mQueue.indexOf(mAudioBean), isSmooth);
    }
    private void showPlayView(){
        // 恢复或启动动画
        Animator anim = mAdapter.getAnim(mViewPager.getCurrentItem());
        if(anim != null){
            if(anim.isPaused()){
                anim.resume();
            } else {
                anim.start();
            }
        }
    }
    private void showPauseView() {
        Animator anim = mAdapter.getAnim(mViewPager.getCurrentItem());
        if(anim != null){
            anim.pause();
        }
    }
}
