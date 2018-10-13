package com.wind.photoclass;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wind.photoclass.core.data.ImageFileHelper;
import com.wind.photoclass.core.view.DialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_ADD_FILE = 1;

    private LayoutInflater inflater;
    private List<File> fileList;

    private View.OnClickListener onAddFileClickListener;
    private View.OnClickListener onItemClickListener;

    public HomeFileListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    public void addFile(File file) {
        if (this.fileList == null) {
            this.fileList = new ArrayList<>();
        }
        this.fileList.add(0, file);
        notifyItemInserted(getHeaderCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD_FILE) {
            View fileView = inflater.inflate(R.layout.holder_add_file, parent, false);
            return new AddFileHolder(fileView);
        }
        View view = inflater.inflate(R.layout.holder_file_list, parent, false);
        return new FileDirHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_NORMAL) {
            FileDirHolder fileHolder = (FileDirHolder) holder;
            fileHolder.setData(fileList.get(position - getHeaderCount()));
            fileHolder.setListener(onItemClickListener);
        } else if (type == TYPE_ADD_FILE) {
            AddFileHolder addFileHolder = (AddFileHolder) holder;
            addFileHolder.setListener(onAddFileClickListener);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            //头部header的判断
            return TYPE_ADD_FILE;
        }

        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (fileList == null)
            return getHeaderCount();
        return fileList.size() + getHeaderCount();
    }

    public void setOnAddFileClickListener(View.OnClickListener onAddFileClickListener) {
        this.onAddFileClickListener = onAddFileClickListener;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private int getHeaderCount() {
        return 1;
    }

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final File file = (File) v.getTag();
            final int position = (int) v.getTag(R.id.holder_position) - getHeaderCount();
            DialogUtils.showAskDialog(v.getContext(), v.getResources().getString(R.string.ask_delete_folder, file.getName()), R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Disposable disposable = Single.create(new SingleOnSubscribe<Object>() {
                        @Override
                        public void subscribe(SingleEmitter<Object> emitter) throws Exception {
                            ImageFileHelper.deleteFile(file);
                            fileList.remove(file);
                            emitter.onSuccess(1);
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            notifyItemRemoved(position + getHeaderCount());
                        }
                    });
                }
            });

            return true;
        }
    };

    class FileDirHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView name;

        public FileDirHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.name);
        }

        public void setData(File file) {
            itemView.setTag(file);
            itemView.setTag(R.id.holder_position, getAdapterPosition());
            itemView.setOnLongClickListener(longClickListener);
            name.setText(file.getName());
        }

        public void setListener(View.OnClickListener clickListener) {
            itemView.setOnClickListener(clickListener);
        }
    }

    class AddFileHolder extends RecyclerView.ViewHolder {

        private View itemView;

        public AddFileHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setListener(View.OnClickListener clickListener) {
            itemView.setOnClickListener(clickListener);
        }
    }
}
