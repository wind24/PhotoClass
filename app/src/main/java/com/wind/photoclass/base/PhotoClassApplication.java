package com.wind.photoclass.base;

import android.app.Application;

import com.wind.photoclass.core.data.ImageFileHelper;

public class PhotoClassApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageFileHelper.initRootDir(this);
    }


}
