package com.wind.photoclass.core.data;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wind.photoclass.R;
import com.wind.photoclass.core.model.LocationModel;
import com.wind.photoclass.core.model.ProjectInfoModel;
import com.wind.photoclass.core.utils.FileUtils;
import com.wind.photoclass.core.utils.GsonUtils;
import com.wind.photoclass.core.utils.LocationManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ImageFileHelper {

    public static final String DESC_FILE_NAME = "desc.txt";
    private static File rootDir;

    public static void initRootDir(Context context) {
        rootDir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    public static File getRootDir() {
        return rootDir;
    }

    public static void loadHomeDirects(final OnImageDirLoadListener listener) {
        Disposable disposable = Single.create(new SingleOnSubscribe<List<File>>() {
            @Override
            public void subscribe(SingleEmitter<List<File>> emitter) throws Exception {
                emitter.onSuccess(loadAllFile());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<File>>() {
            @Override
            public void accept(List<File> files) throws Exception {
                if (listener != null) {
                    listener.onDirLoad(files);
                }
            }
        });
    }

    public static List<File> loadAllFile() {
        List<File> result = new ArrayList<>();
        File[] files = rootDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static File createDirect(File dir, String name) {
        if (dir == null || !dir.exists()) {
            return null;
        }

        if (TextUtils.isEmpty(name)) {
            return null;
        }

        File file = new File(dir, name);
        boolean ret = file.mkdir();
        if (ret) {
            //创建工程的描述文件
            createDescTxt(file, name);
            return file;
        }

        return null;
    }

    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        File[] files = file.listFiles();
        int length = files.length;
        for (int i = 0; i < length; i++) {
            File child = files[i];
            if (child.isDirectory()) {
                deleteFile(child);
            } else {
                child.delete();
            }
        }
        file.delete();
    }

    public static void createDescTxt(File folder, String name) {
        File descFile = new File(folder, DESC_FILE_NAME);
        ProjectInfoModel model = new ProjectInfoModel();
        model.setStationName(name);
        model.setCreateTime(System.currentTimeMillis());
        if (LocationManager.getInstance().getCurrLocationModel() != null) {
            LocationModel location = LocationManager.getInstance().getCurrLocationModel();
            model.setLatitude(location.getLatitude());
            model.setLongitude(location.getLongitude());
            model.setAddress(location.getAddress());
        }
        String json = GsonUtils.toJson(model);
        FileUtils.writeFile(descFile, json);
    }

    /**
     * 加载指定目录文件，只加载文件，忽略目录
     *
     * @param dir
     * @return
     */
    public static List<File> loadFiles(File dir) {
        if (dir == null || !dir.exists()) {
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        List<File> result = new ArrayList<>();
        int length = files.length;
        for (int i = 0; i < length; i++) {
            File file = files[i];
            if (file.isFile() && file.getName().contains(".jpg")) {
                result.add(file);
            }
        }

        Collections.sort(result, timeComp);
        return result;
    }

    private static Comparator<File> timeComp = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            long modify1 = o1.lastModified();
            long modify2 = o2.lastModified();
            if (modify1 > modify2) {
                return 1;
            } else if (modify1 < modify2) {
                return -1;
            }
            return 0;
        }
    };

}
