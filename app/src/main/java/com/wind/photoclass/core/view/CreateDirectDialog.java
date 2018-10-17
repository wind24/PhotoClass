package com.wind.photoclass.core.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.wind.photoclass.R;
import com.wind.photoclass.core.data.ImageFileHelper;

import java.io.File;

public class CreateDirectDialog extends DialogFragment {

    private View cancel;
    private EditText name;
    private View ok;

    /**
     * 父目录，空的话就是root
     */
    private File parent;

    private OnCreateDirectListener directListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_create_folder, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Dialog dialog = getDialog();
            if (dialog != null) {
                DisplayMetrics dm = new DisplayMetrics();
                //设置弹框的占屏宽
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void show(FragmentManager manager, File parent) {
        this.parent = parent;
        super.show(manager, parent.getAbsolutePath());
    }

    private void initView(View view) {
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);
        name = view.findViewById(R.id.name);
        name.addTextChangedListener(watcher);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = name.getText().toString().trim();
                if (parent == null) {
                    parent = ImageFileHelper.getRootDir();
                }
                File temp = new File(parent, fileName);
                if (temp.exists()) {
                    Toast.makeText(getContext(), R.string.station_already_exists, Toast.LENGTH_SHORT).show();
                    if (directListener != null) {
                        directListener.onDirectCreated(null);
                    }
                    return;
                }
                File file = ImageFileHelper.createDirect(parent, fileName);
                if (file != null) {
                    if (directListener != null) {
                        directListener.onDirectCreated(file);
                    }
                }
                dismiss();
            }
        });
    }

    public void setDirectListener(OnCreateDirectListener directListener) {
        this.directListener = directListener;
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ok.setEnabled(name.getText().toString().trim().length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public interface OnCreateDirectListener {
        void onDirectCreated(File file);
    }

}
