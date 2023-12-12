package com.example.lib_common_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.lib_common_ui.utils.StatusBarUtil;
import com.example.lib_image_loader.app.ImageLoaderManager;

import java.util.List;

public class MultiImageViewLayout extends LinearLayout {
    public static int MAX_WIDTH = 0;
    // 照片URL列表
    private List<String> imagesList;

    /**
     * 长度
     */
    private int pxMoreWandH = 0; // 多张图的宽高
    private int pxImagePadding = StatusBarUtil.dip2px(getContext(), 3); // 图片间的间距
    private int MAX_PER_ROW_COUNT = 3; // 每行显示最大数

    private LayoutParams onePicPara;
    private LayoutParams morePara, moreParaColumnFirst;
    private LayoutParams rowPara;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    public MultiImageViewLayout(Context context) {
        super(context);
    }

    public MultiImageViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public void setList(List<String> lists) throws IllegalArgumentException {
        if(lists == null){
            throw new IllegalArgumentException("imageList is Null...");
        }
        imagesList = lists;
        if(MAX_WIDTH > 0) {
            pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) /3; //
            initImageLayoutParams();
        }
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if(width>0){
                MAX_WIDTH = width;
                if(imagesList!=null && imagesList.size() >0 ) {
                    setList(imagesList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private int measureWidth(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if(specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    private void initImageLayoutParams(){
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;
        onePicPara = new LayoutParams(wrap, wrap);
        moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
        morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
        rowPara = new LayoutParams(match, wrap);
    }

    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if(MAX_WIDTH == 0){
            addView(new View(getContext()));
            return;
        }
        if(imagesList == null || imagesList.size() == 0){
            return;
        }
        if(imagesList.size() == 1){
            addView(createImageView(0, false));
        } else {
            int allCount = imagesList.size();
            if(allCount == 4){
                MAX_PER_ROW_COUNT = 2;
            } else {
                MAX_PER_ROW_COUNT = 3;
            }
            int rowCount = allCount / MAX_PER_ROW_COUNT + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 :0); // 行数
            for(int rowCursor = 0; rowCursor < rowCount; rowCursor++){
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setLayoutParams(rowPara);
                if (rowCount != 0){
                    rowLayout.setPadding(0, pxImagePadding, 0, 0);
                }
                int columnCount = allCount % MAX_PER_ROW_COUNT == 0?MAX_PER_ROW_COUNT : allCount % MAX_PER_ROW_COUNT; // 每行的数列
                if(rowCount != 0){
                    rowLayout.setPadding(0, pxImagePadding, 0,0);
                }
                addView(rowLayout);
                int rowOffset = rowCursor * MAX_PER_ROW_COUNT; // 偏移量
                for(int columnCursor = 0; columnCursor < columnCount; columnCursor++){
                    int position = columnCursor + rowOffset;
                    rowLayout.addView(createImageView(position, true));
                }
             }
        }
    }
    private ImageView createImageView(int position, final boolean isMultiImage) {
        String photoInfo = imagesList.get(position);
        ImageView imageView = new ImageView(getContext());
        if(isMultiImage) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position%MAX_PER_ROW_COUNT == 0? moreParaColumnFirst: morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            // 一张图，根据图的大小wrap
            imageView.setLayoutParams(onePicPara);
        }
        imageView.setId(photoInfo.hashCode());
        imageView.setOnClickListener(new ImageOnClickListener(position));
        ImageLoaderManager.getInstance().displayImageForView(imageView, photoInfo);
        return imageView;
    }
    private class ImageOnClickListener implements OnClickListener {
        private int position;
        public ImageOnClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, position);
            }
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
