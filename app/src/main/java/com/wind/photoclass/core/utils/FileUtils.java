package com.wind.photoclass.core.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static void writeFile(File file, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (file == null) {
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String readFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int ret = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ret = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, ret);
            }

            byte[] data = baos.toByteArray();
            baos.flush();
            baos.close();
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
