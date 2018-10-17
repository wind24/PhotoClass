package com.wind.photoclass;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wind.photoclass.base.BaseActivity;
import com.wind.photoclass.core.data.ImageFileHelper;
import com.wind.photoclass.core.data.StationSearchHelper;
import com.wind.photoclass.core.utils.LocationManager;
import com.wind.photoclass.core.utils.OnNotifyListener;
import com.wind.photoclass.core.view.CreateDirectDialog;
import com.wind.photoclass.module.album.ui.FolderDetailActivity;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity {

    private HomeFileListAdapter adapter;
    private CreateDirectDialog directDialog;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name) + "--" + getString(R.string.south_cmcc_design));
        initView();
        loadData();
    }

    @Override
    protected boolean isHome() {
        return true;
    }

    private void initView() {
        RecyclerView listView = findViewById(R.id.list_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new HomeFileListAdapter(this);
        adapter.setOnAddFileClickListener(addFileClickListener);
        adapter.setOnItemClickListener(itemClickListener);
        listView.setAdapter(adapter);
    }

    private void loadData() {
        LocationManager.getInstance().startLocation(this, null);
        search("");
    }

    private void search(String query) {
        StationSearchHelper.getInstance().search(query, new OnNotifyListener<List<File>>() {
            @Override
            public void onCallback(List<File> data) {
                adapter.setFileList(data);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mSearchView.getQuery().length() == 0) {
                    search("");
                    return true;
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocationManager.LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。
                    LocationManager.getInstance().startLocation(this, null);
                } else {
                    // 权限被用户拒绝了。
                    Toast.makeText(this, R.string.ask_location_permission, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    View.OnClickListener addFileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (directDialog == null) {
                directDialog = new CreateDirectDialog();
                directDialog.setDirectListener(createDirectListener);
            }
            directDialog.show(getSupportFragmentManager(), ImageFileHelper.getRootDir());
        }
    };

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof File) {
                File file = (File) v.getTag();
                Intent intent = new Intent(MainActivity.this, FolderDetailActivity.class);
                intent.putExtra("path", file.getAbsolutePath());
                startActivity(intent);
            }
        }
    };

    CreateDirectDialog.OnCreateDirectListener createDirectListener = new CreateDirectDialog.OnCreateDirectListener() {
        @Override
        public void onDirectCreated(File file) {
            if (file != null) {
                adapter.addFile(file);
                //添加文件的时候也添加搜索全文件缓存
                StationSearchHelper.getInstance().addFile(file);
            }
        }
    };
}
