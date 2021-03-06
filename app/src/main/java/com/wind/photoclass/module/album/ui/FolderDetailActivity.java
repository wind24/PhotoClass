package com.wind.photoclass.module.album.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wind.photoclass.BuildConfig;
import com.wind.photoclass.R;
import com.wind.photoclass.base.BaseActivity;
import com.wind.photoclass.core.data.ImageFileHelper;
import com.wind.photoclass.core.model.LocationModel;
import com.wind.photoclass.core.model.ProjectInfoModel;
import com.wind.photoclass.core.utils.FileUtils;
import com.wind.photoclass.core.utils.GsonUtils;
import com.wind.photoclass.core.utils.LocationManager;
import com.wind.photoclass.core.utils.OnNotifyListener;
import com.wind.photoclass.core.utils.TimeUtils;
import com.wind.photoclass.module.album.logic.AlbumItemDecoration;
import com.wind.photoclass.module.album.logic.FolderDetailHelper;

import java.io.File;
import java.util.List;

public class FolderDetailActivity extends BaseActivity implements View.OnClickListener {

    File folderFile;
    File tempFile;//临时文件，拍照时先保存到这，成功后转移到目标目录
    ProjectInfoModel descModel;

    FolderDetailAdapter adapter;
    View infoLayout;
    View overlay;
    TextView createTimeInput;
    EditText projectNameInput;
    EditText stationNameInput;
    TextView addressInput;
    View address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);
        initView();
        post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    private void initView() {
        createTimeInput = findViewById(R.id.create_time_input);
        projectNameInput = findViewById(R.id.project_name_input);
        stationNameInput = findViewById(R.id.station_name_input);
        projectNameInput.addTextChangedListener(projectWatcher);
        stationNameInput.addTextChangedListener(stationWatcher);
        address = findViewById(R.id.address);
        address.setOnClickListener(this);
        addressInput = findViewById(R.id.address_input);
        infoLayout = findViewById(R.id.info_layout);
        overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(this);

        RecyclerView listView = findViewById(R.id.list_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        listView.setLayoutManager(layoutManager);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        listView.addItemDecoration(new AlbumItemDecoration(3, padding));
        adapter = new FolderDetailAdapter(this);
        listView.setAdapter(adapter);
    }

    private void initData() {
        tempFile = new File(ImageFileHelper.getRootDir(), ".tmp.jpg");
        String path = getIntent().getStringExtra("path");
        if (TextUtils.isEmpty(path)) {
            finish();
            return;
        }

        folderFile = new File(path);
        setTitle(folderFile.getName());

        File desc = new File(folderFile, ImageFileHelper.DESC_FILE_NAME);
        if (!desc.exists()) {
            ImageFileHelper.createDescTxt(folderFile, folderFile.getName());
        }
        String json = FileUtils.readFile(desc);
        if (!TextUtils.isEmpty(json)) {
            descModel = GsonUtils.fromJson(json, ProjectInfoModel.class);
            if (descModel == null) {
                descModel = new ProjectInfoModel();
                descModel.setCreateTime(System.currentTimeMillis());
                descModel.setProjectName(folderFile.getName());
            }
            createTimeInput.setText(TimeUtils.parseTimeToYMDTime(descModel.getCreateTime()));
            projectNameInput.setText(descModel.getProjectName());
            if (!TextUtils.isEmpty(descModel.getStationName())) {
                stationNameInput.setText(descModel.getStationName());
            }

            if (!TextUtils.isEmpty(descModel.getAddress())) {
                addressInput.setText(descModel.getAddress());
            }
        }


        loadData();
    }

    private void saveDesc() {
        String json = GsonUtils.toJson(descModel);
        File desc = new File(folderFile, ImageFileHelper.DESC_FILE_NAME);
        FileUtils.writeFile(desc, json);
    }

    private void loadData() {
        FolderDetailHelper.loadData(folderFile, new OnNotifyListener<List<File>>() {
            @Override
            public void onCallback(List<File> data) {
                adapter.setFiles(data);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile)); //Uri.fromFile(tempFile)
                startActivityForResult(intent, 1);
                break;
            case R.id.info:
                infoLayout.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDesc();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File out = new File(folderFile, String.valueOf(System.currentTimeMillis()) + ".jpg");
            boolean ret = tempFile.renameTo(out);
            if (ret) {
                adapter.addFile(out);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == overlay) {
            infoLayout.setVisibility(View.GONE);
        } else if (v == address) {
            LocationManager.getInstance().startLocation(this, new OnNotifyListener<LocationModel>() {
                @Override
                public void onCallback(LocationModel data) {
                    if (data != null) {
                        descModel.setLatitude(data.getLatitude());
                        descModel.setLongitude(data.getLongitude());
                        descModel.setAddress(data.getAddress());

                        post(new Runnable() {
                            @Override
                            public void run() {
                                addressInput.setText(descModel.getAddress());
                            }
                        });
                    }
                }
            });
        }
    }

    private TextWatcher projectWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            descModel.setProjectName(projectNameInput.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher stationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            descModel.setStationName(stationNameInput.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
