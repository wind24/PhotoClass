package com.wind.photoclass.base;

import android.app.Application;
import android.os.StrictMode;

import com.wind.photoclass.core.data.ImageFileHelper;

public class PhotoClassApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        ImageFileHelper.initRootDir(this);
    }


}
