package com.example.lib_video.videoplayer.core.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.example.lib_video.R;
import com.example.lib_video.videoplayer.core.VideoAdSlot;
import com.example.lib_video.videoplayer.utils.Utils;

import java.util.List;

/**
 * 全屏显示视频
 */
public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {
    private static final String TAG = VideoFullDialog.class.getSimpleName();
    private CustomVideoView mVideoView;
    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private int mPosition;
    private FullToSmallListener mListener;
    private boolean isFirst = true;
    // 动画要执行的平移值
    private int deltaY;
    private VideoAdSlot.SDKSlotListener mSlotListener;
    private Bundle mStartBundle;
    private Bundle mEndBundle; // 用于Dialog出入场动画

    public VideoFullDialog(Context context, CustomVideoView videoView, String instance, int position) {
        super(context, R.style.dialog_full_screen);
        mPosition = position;
        mVideoView = videoView;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_layout);
        initVideoView();
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }
    public void setListener(FullToSmallListener listener) {
        this.mListener = listener;
    }

    public void setSlotListener(VideoAdSlot.SDKSlotListener slotListener) {
        this.mSlotListener = slotListener;
    }
    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mRootView = findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);
        mVideoView.setListener(this);
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                        prepareScene();
                        runEnterAnimation();
                        return true;
                    }
                });
    }


    // 准备动画所需要的数据
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);
        /**
         * 将desationview移动到originalView位置处
         */
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP) - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    /**
     * 准备入场动画
     */
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                }).start();
    }

    /**
     * 准备出场动画
     */
    private void runExitAnimator() {
        mVideoView.animate().setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                dismiss();
                if (mListener != null) {
                    mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                }
            }
        }).start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mVideoView.isShowFullBtn(false); // 防止第一次，有些手机仍然显示全屏按钮
        if(!hasFocus) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            if(isFirst) {
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        isFirst = false;
    }

    @Override
    public void dismiss() {
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); // 禁止返回键本身关闭功能，转为自己的关闭效果
        onClickBackBtn();
    }
    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickPlay() {

    }
    @Override
    public void onAdVideoLoadSuccess() {
        if(mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {
        dismiss();
        if(mListener != null) {
            mListener.playComplete();
        }
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public interface FullToSmallListener {
        void getCurrentPlayPosition(int position);

        void playComplete(); // 全屏播放结束时间回调
    }
    @Override
    public void onClickBackBtn(){
        runExitAnimator();
    }
}
