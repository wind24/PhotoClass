package com.wind.photoclass.module.camera;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wind.photoclass.R;
import com.wind.photoclass.base.BaseActivity;
import com.wind.photoclass.core.utils.ImageLoader;
import com.wind.photoclass.core.view.AutoFitTextureView;
import com.wind.photoclass.core.view.CameraView;

import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends BaseActivity implements View.OnClickListener {

    private AutoFitTextureView previewLayout;
    private CameraView cameraView;
    private View capture;
    private ImageView imagePreview;
    private View sure;

    private File dir;
    private ArrayList<File> pictures;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        hideTitle();
        initView();
        initAction();
    }

    private void initView() {
        previewLayout = findViewById(R.id.preview_layout);
        capture = findViewById(R.id.capture);
        capture.setOnClickListener(this);
        imagePreview = findViewById(R.id.image_preview);
        sure = findViewById(R.id.sure);
        sure.setOnClickListener(this);
    }

    private void initAction() {
        String dirPath = getIntent().getStringExtra("dirPath");
        if (dirPath == null) {
            Toast.makeText(this, R.string.target_dir_cannot_empty, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dir = new File(dirPath);
        if (!dir.exists()) {
            Toast.makeText(this, R.string.target_dir_cannot_empty, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cameraView = new CameraView(previewLayout);
        cameraView.setPictureTakenListener(pictureTakenListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (previewLayout.isAvailable()) {
            openCamera();
        } else {
            previewLayout.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }

    private void openCamera() {
        int state = cameraView.initCamera(this, previewLayout.getWidth(), previewLayout.getHeight());
        if (state == CameraView.STATE_CAMERA_DISABLE) {
            //相机不可用，直接退出
            Toast.makeText(this, "相机不可用", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cameraView.configureTransform(CameraActivity.this, previewLayout.getWidth(), previewLayout.getHeight());
        cameraView.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.close();
    }

    @Override
    public void onClick(View v) {
        if (v == capture) {
            cameraView.takePicture();
        } else if (v == sure) {
            Intent intent = new Intent();
            intent.putExtra("result", pictures);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            cameraView.configureTransform(CameraActivity.this, width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraView.OnPictureTakenListener pictureTakenListener = new CameraView.OnPictureTakenListener() {
        @Override
        public File getTargetDir() {
            return dir;
        }

        @Override
        public void onPictureTaken(final File file) {
            if (pictures == null) {
                pictures = new ArrayList<>();
            }

            pictures.add(file);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imagePreview.setVisibility(View.VISIBLE);
                    ImageLoader.loadLocalImageWithoutCache(file, imagePreview);
                }
            });
        }
    };

}
