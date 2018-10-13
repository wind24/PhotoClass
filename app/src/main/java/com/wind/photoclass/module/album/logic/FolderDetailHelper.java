package com.wind.photoclass.module.album.logic;

import com.wind.photoclass.core.data.ImageFileHelper;
import com.wind.photoclass.core.utils.OnNotifyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FolderDetailHelper {

    public static void loadData(File folder, final OnNotifyListener<List<File>> listener) {
        if (folder == null || !folder.exists()) {
            return;
        }


        Disposable disposable = Observable.just(folder).map(new Function<File, List<File>>() {
            @Override
            public List<File> apply(File file) throws Exception {
                List<File> result = ImageFileHelper.loadFiles(file);

                if (result == null) {
                    result = new ArrayList<>();
                }
                return result;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<File>>() {
            @Override
            public void accept(List<File> files) throws Exception {
                if (listener != null) {
                    listener.onCallback(files);
                }
            }
        });


    }

}
