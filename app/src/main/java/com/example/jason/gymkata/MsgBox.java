package com.example.jason.gymkata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by Jason on 2016-04-15.
 */
public class MsgBox extends DialogFragment implements Constants {
    public static final String OK_BUTTON = "OK";
    public static final String YES_NO_BUTTON = "YES_NO";
    public static final String DEFAULT_MSG = "NO MESSAGE SET";
    public static final String DEFAULT_TITLE = "GYMKATA";

    // Arg types
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";



    public String response = null;
    private String buttonType;


    public MsgBox() {
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    // use this instance of the interface to deliver acion events
    NoticeDialogListener mListener;

    // override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // verifiy that the host activity implements the callback interface
        try {
            // instantiate the listener so we can send events to host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            Log.e("MsgBox.onAtt", "Error: " + e.toString());
            // the activity doesn't implement the interface so throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String msg = args.getString("message", "");
        String title = args.getString("title", "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(MsgBox.this);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(MsgBox.this);
                    }

                }).create();
    }
/*
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
    */
}
