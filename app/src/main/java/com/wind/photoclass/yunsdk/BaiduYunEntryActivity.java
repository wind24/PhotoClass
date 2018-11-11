package com.wind.photoclass.yunsdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.netdisk.open.FileInfo;
import com.baidu.netdisk.sdk.NetDiskSDK;

import java.util.List;

public class BaiduYunEntryActivity extends Activity {
    private static final String TAG = "BaiduYunEntryActivity";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<FileInfo> parcelableArrayListExtra = NetDiskSDK.getInstance().handleIntent(getIntent());

        if (parcelableArrayListExtra != null) {
            Log.d(TAG, parcelableArrayListExtra.toString());
        }
    }
}
