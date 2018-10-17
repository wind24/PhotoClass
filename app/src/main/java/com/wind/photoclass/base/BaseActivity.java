package com.wind.photoclass.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wind.photoclass.R;

public class BaseActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private Toolbar toolbar;

    private View containerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_activity);
        initBaseView();
    }

    private void initBaseView() {
        rootView = findViewById(R.id.root);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (!isHome()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void removeOldContainer() {
        if (containerView != null && containerView.getParent() == rootView) {
            rootView.removeView(containerView);
        }
    }

    protected boolean isHome() {
        return false;
    }

    @Override
    public void setTitle(int titleId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setContentView(View view) {
        removeOldContainer();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(view, params);
        containerView = view;
    }


    @Override
    public void setContentView(int layoutResID) {
        removeOldContainer();

        View view = getLayoutInflater().inflate(layoutResID, rootView, false);
        containerView = view;
        rootView.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        removeOldContainer();

        containerView = view;
        rootView.addView(view, params);
    }

    public void hideTitle() {
        toolbar.setVisibility(View.GONE);
    }

    public void post(Runnable runnable) {
        rootView.post(runnable);
    }
}
