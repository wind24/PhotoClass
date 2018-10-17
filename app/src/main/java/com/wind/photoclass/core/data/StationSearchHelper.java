package com.wind.photoclass.core.data;

import android.text.TextUtils;

import com.wind.photoclass.core.model.ProjectInfoModel;
import com.wind.photoclass.core.utils.FileUtils;
import com.wind.photoclass.core.utils.GsonUtils;
import com.wind.photoclass.core.utils.OnNotifyListener;
import com.wind.photoclass.core.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StationSearchHelper {

    private static StationSearchHelper instance = new StationSearchHelper();

    public static StationSearchHelper getInstance() {
        if (instance == null) {
            instance = new StationSearchHelper();
        }

        return instance;
    }

    private Disposable searchDisposable;
    private List<File> allFiles;

    public void search(final String query, final OnNotifyListener<List<File>> listener) {
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }

        searchDisposable = Single.create(new SingleOnSubscribe<List<File>>() {
            @Override
            public void subscribe(SingleEmitter<List<File>> emitter) throws Exception {
                if (allFiles == null) {
                    allFiles = ImageFileHelper.loadAllFile();
                }

                if (TextUtils.isEmpty(query)) {
                    emitter.onSuccess(allFiles);
                } else {
                    List<File> result = new ArrayList<>();
                    for (File file : allFiles) {
                        File desc = new File(file, ImageFileHelper.DESC_FILE_NAME);
                        if (!desc.exists()) {
                            continue;
                        }
                        String json = FileUtils.readFile(desc);
                        if (!TextUtils.isEmpty(json)) {
                            if (json.contains(query)) {
                                result.add(file);
                            }
                        }
                    }

                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<File>>() {
            @Override
            public void accept(List<File> files) throws Exception {
                if (listener != null) {
                    listener.onCallback(files);
                }
                searchDisposable.dispose();
                searchDisposable = null;
            }
        });
    }

    public void addFile(File file) {
        if (allFiles == null) {
            allFiles = new ArrayList<>();
        }

        allFiles.add(file);
    }

}
