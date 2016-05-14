package com.example.jason.gymkata;

import android.app.AlertDialog;
import android.content.Context;
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

    public static final String RESPONSE_YES = "YES";
    public static final String RESPONSE_NO = "NO";
    public static final String RESPONSE_CANCEL = "CANCEL";
    public static final String RESPONSE_OK = "OK";

    public String response = null;
    private String msg;
    private String buttonType;


    public MsgBox() {
    }

    public MsgBox(String msg, Context con) {
        buttonType = YES_NO_BUTTON;
        this.msg(msg, con);
    }

    public MsgBox(String msg, Context con, String btn) {
        this.buttonType = btn;
        this.msg(msg, con);
    }
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    private void msg(String msg, Context con) {
        AlertDialog.Builder builder = new AlertDialog.Builder(con);
        builder.setMessage(msg).setCancelable(false);
        if (buttonType != null && buttonType.equals(OK_BUTTON)) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    response = RESPONSE_OK;
                    dialog.cancel();
                }
            });

        } else if (buttonType != null && buttonType.equals(YES_NO_BUTTON)) {
            Log.i("msgBox", "buttonType=" + buttonType);
            builder.setPositiveButton(RESPONSE_YES, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //MainActivity.this.finish();
                    response = RESPONSE_YES;
                    Log.i("msg", "Pos Button response=" + response);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(RESPONSE_NO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    response = RESPONSE_NO;
                    Log.i("msg", "Neg Button response=" + response);
                    dialog.cancel();
                }
            });

        } else {
            builder.setNeutralButton(OK_BUTTON, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    response = RESPONSE_OK;
                    dialog.cancel();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();

    }
}
