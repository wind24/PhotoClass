package com.wind.photoclass.module.album.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class AlbumImageView extends AppCompatImageView {
    public AlbumImageView(Context context) {
        super(context);
    }

    public AlbumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
