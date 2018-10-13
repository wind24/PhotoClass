package com.wind.photoclass.core.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogUtils {

    public static void showAskDialog(Context context, String ask, int okRes, int cancelRes, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(ask);
        builder.setNegativeButton(cancelRes, null);
        builder.setPositiveButton(okRes, okListener);
        builder.show();
    }

}
