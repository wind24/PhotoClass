package com.wind.photoclass.module.album.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wind.photoclass.R;
import com.wind.photoclass.core.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderDetailAdapter extends RecyclerView.Adapter<FolderDetailAdapter.ImageHolder> {

    private LayoutInflater inflater;
    private List<File> files;

    public FolderDetailAdapter(Context context) {
        inflater = LayoutInflater.from(context);
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

    class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        public void setData(File file) {
            ImageLoader.loadLocalImageWithoutCache(file, image);
        }
    }
}
