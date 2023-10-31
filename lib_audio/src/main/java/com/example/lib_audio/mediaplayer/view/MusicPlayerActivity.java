package com.example.lib_audio.mediaplayer.view;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_image_loader.app.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 播放音乐的Activity
 */
public class MusicPlayerActivity extends BaseActivity {

    private RelativeLayout mBgView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private ImageView mFavouriteView;


    private SeekBar mProgressView;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;


    private ImageView mPlayModeView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mPreviousView;

    private Animator animator;


    /**
     * data
     */
    private AudioBean mAudioBean; // 当前正在播放的歌曲
    private AudioController.PlayMode mPlayMode; // 当前播放模式



    public static void start(Activity context) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        ActivityCompat.startActivity(context, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(
                    TransitionInflater.from(this)
                            .inflateTransition(
                                    R.transition.transition_bottom2top
                            )
            );
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_service_layout);
        initData();
        initView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void initData(){
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode = AudioController.getInstance().getPlayMode();
    }
    private void initView(){
        mBgView = findViewById(R.id.root_layout);
        // 给背景添加模糊效果
        ImageLoaderManager.getInstance().displayImageForViewGroup(
                mBgView, mAudioBean.albumPic
        );
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                shareMusic(mAudioBean.mUrl, mAudioBean.name);
            }
        });

        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 弹出歌单列表dialog
                MusicListDialog dialog = new MusicListDialog(MusicPlayerActivity.this);
                dialog.show();
            }
        });
        mInfoView = findViewById(R.id.album_view);
        mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus(); // 跑马灯效果焦点获取
        mAuthorView = findViewById(R.id.author_view);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView = findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AudioController.getInstance().changeFavourite();
            }
        });

    }

    /**
     * 分享给好友
     * @param url
     * @param name
     */
    private void shareMusic(String url, String name){

    }
}
