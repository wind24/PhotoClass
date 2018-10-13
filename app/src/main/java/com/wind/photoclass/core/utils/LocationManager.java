package com.wind.photoclass.core.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wind.photoclass.core.model.LocationModel;

public class LocationManager {

    public static final int LOCATION_CODE = 1919;
    private static LocationManager instance = new LocationManager();

    public static LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }

        return instance;
    }

    private LocationModel currLocationModel;

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public LocationModel getCurrLocationModel() {
        return currLocationModel;
    }

    public void startLocation(Activity activity, final OnNotifyListener<LocationModel> listener) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {// 没有权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                // 申请授权。
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);

            }
        }
        mlocationClient = new AMapLocationClient(activity);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    int errorCode = aMapLocation.getErrorCode();
                    String errorInfo = aMapLocation.getErrorInfo();
                    Log.d("LocationManager", "onLocationChanged errorCode=" + errorCode + ",info=" + errorInfo);
                    if (errorCode == 0) {
                        //定位成功回调信息，设置相关消息
                        currLocationModel = new LocationModel();
                        currLocationModel.setLatitude(aMapLocation.getLatitude());
                        currLocationModel.setLongitude(aMapLocation.getLongitude());
                        currLocationModel.setAddress(aMapLocation.getAddress());
                        if (listener != null) {
                            listener.onCallback(currLocationModel);
                        }
                    } else {
                        if (listener != null) {
                            listener.onCallback(null);
                        }
                    }
                }
            }
        });
        //启动定位
        mlocationClient.startLocation();
    }

}
