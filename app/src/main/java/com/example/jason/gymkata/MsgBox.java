package com.example.jason.gymkata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

/**
 * Created by Jason on 2016-04-15.
 */
public class MsgBox {
    public static final String OK_BUTTON = "OK";
    public static final String YES_NO_BUTTON = "YES_NO";
    public static final String DEFAULT_MSG = "NO MESSAGE SET";
    private String msg;
    private String buttonType;

    public MsgBox() {
    }

    public MsgBox(String msg, View view) {
        buttonType = YES_NO_BUTTON;
        this.msg(msg, view);
    }

    public MsgBox(String msg, View view, String btn) {
        this.buttonType = btn;
        this.msg(msg, view);
    }

    private void msg(String msg, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(msg).setCancelable(false);
        if (buttonType != null && buttonType.equals(OK_BUTTON)) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        } else if (buttonType.equals(YES_NO_BUTTON)) {
            Log.i("msgBox", "buttonType=" + buttonType);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //MainActivity.this.finish();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        } else {
            builder.setNeutralButton(OK_BUTTON, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();

    }
}
