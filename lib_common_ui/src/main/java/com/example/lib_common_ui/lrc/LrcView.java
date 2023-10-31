package com.example.lib_common_ui.lrc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 歌词
 */
@SuppressLint("StaticFieldLeak")
public class LrcView extends View {
    public LrcView(Context context) {
        super(context);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
