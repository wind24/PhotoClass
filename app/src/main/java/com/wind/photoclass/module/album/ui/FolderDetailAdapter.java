package com.wind.photoclass.module.album.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.wind.photoclass.R;
import com.wind.photoclass.core.utils.ImageLoader;
import com.wind.photoclass.module.album.logic.OnFolderDetailListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderDetailAdapter extends RecyclerView.Adapter<FolderDetailAdapter.ImageHolder> {

    private LayoutInflater inflater;
    private List<File> files;
    private boolean selectionMode;//是否在选择模式
    private List<String> selected;

    private OnFolderDetailListener detailListener;

    public FolderDetailAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        selected = new ArrayList<>();
    }

    public void setFiles(List<File> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void addFile(File file) {
        if (this.files == null) {
            this.files = new ArrayList<>();
        }

        this.files.add(0, file);
        notifyItemInserted(0);
    }

    public boolean removeAllSelected() {
        if (selected == null || selected.isEmpty()) {
            return false;
        }
        for (String path : selected) {
            deleteFile(path);
        }

        selected.clear();
        return true;
    }

    private void deleteFile(String path) {
        if (files == null) {
            return;
        }

        int position = -1;
        File file = null;
        int size = files.size();
        for (int i = size - 1; i >= 0; i--) {
            file = files.get(i);
            if (TextUtils.equals(file.getAbsolutePath(), path)) {
                boolean deleteSuccess = file.delete();
                if (deleteSuccess) {
                    files.remove(i);
                    position = i;
                }
                break;
            }
        }

        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (!selectionMode) {
            selected.clear();
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public List<String> getSelected() {
        return selected;
    }

    public void setDetailListener(OnFolderDetailListener detailListener) {
        this.detailListener = detailListener;
    }

    public List<File> getFiles() {
        return files;
    }

    @NonNull
    @Override
    public FolderDetailAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.holder_image_item, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderDetailAdapter.ImageHolder holder, int position) {
        holder.setData(files.get(position));
    }

    @Override
    public int getItemCount() {
        if (files == null)
            return 0;
        return files.size();
    }

    private void toggleImage(View v) {
        CheckBox selection = (CheckBox) v.getTag(R.id.holder_view);
        selection.toggle();
        String path = (String) v.getTag(R.id.holder_value);
        if (selection.isChecked()) {
            selected.add(path);
        } else {
            selected.remove(path);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectionMode) {
                //选择模式处理选中框
                toggleImage(v);
            } else {
                //不是选择模式
                String path = (String) v.getTag(R.id.holder_value);
                Intent intent = new Intent(v.getContext(), ImageWatcherActivity.class);
                intent.putExtra("path", path);
                v.getContext().startActivity(intent);
            }
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            setSelectionMode(true);
            toggleImage(v);
            if (detailListener != null) {
                detailListener.onSelectionShown();
            }
            return true;
        }
    };

    class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private CheckBox selection;

        ImageHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            selection = itemView.findViewById(R.id.selection);
            image.setOnClickListener(clickListener);
        }

        public void setData(File file) {
            String path = file.getAbsolutePath();
            image.setTag(R.id.holder_value, path);
            image.setTag(R.id.holder_view, selection);
            ImageLoader.loadLocalImageWithoutCache(file, image);

            if (selectionMode) {
                image.setOnLongClickListener(null);
                selection.setVisibility(View.VISIBLE);
            } else {
                image.setOnLongClickListener(longClickListener);
                selection.setVisibility(View.GONE);
            }

            selection.setChecked(selected.contains(path));
        }
    }
}
