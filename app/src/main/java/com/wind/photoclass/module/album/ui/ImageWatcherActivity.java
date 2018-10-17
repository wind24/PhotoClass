package com.wind.photoclass.module.album.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.wind.photoclass.R;
import com.wind.photoclass.base.BaseActivity;
import com.wind.photoclass.core.utils.ImageLoader;

import java.io.File;

public class ImageWatcherActivity extends BaseActivity {

    private PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_watcher);
        initView();
        post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    private void initView() {
        photoView = findViewById(R.id.photo_view);
    }

    private void initData() {
        String path = getIntent().getStringExtra("path");
        File file = new File(path);
        ImageLoader.loadLocalImageWithoutCache(file, photoView);
    }
}
