package com.wind.photoclass.core.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

public class GsonUtils {

    private static Gson gson;

    public static String toJson(Object obj) {
        if (gson == null) {
            gson = new Gson();
        }

        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        if (TextUtils.isEmpty(json) || cls == null) {
            return null;
        }
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(json, cls);
    }

}
