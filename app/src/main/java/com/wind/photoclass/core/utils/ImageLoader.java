package com.wind.photoclass.core.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageLoader {

    public static void loadLocalImageWithoutCache(File file, ImageView view) {
        if (file == null || !file.exists()) {
            return;
        }

        if (view == null) {
            return;
        }

        RequestOptions options = new RequestOptions();
        options = options.centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(view).load(file).apply(options).into(view);
    }

}
