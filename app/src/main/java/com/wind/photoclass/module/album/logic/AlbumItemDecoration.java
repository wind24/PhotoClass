package com.wind.photoclass.module.album.logic;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AlbumItemDecoration extends RecyclerView.ItemDecoration {

    private int columnCount;
    private int padding;

    public AlbumItemDecoration(int columnCount, int padding) {
        this.columnCount = columnCount;
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        outRect.left = padding / 2;
        outRect.right = padding / 2;
        outRect.bottom = padding;
        if (position / columnCount == 0) {
            outRect.top = padding;
        }
    }
}
