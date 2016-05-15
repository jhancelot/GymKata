package com.example.jason.gymkata;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Jason on 2016-05-14.
 */
public class ImportData extends AsyncTask<Void, Boolean, Boolean> {
    Context con;
    ProgressDialog dialog;
    String table;
    String path;
    String fileName = null;

    public ImportData(Context context, String tableName) {
        this.con = context;
        this.table = tableName;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File importFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        path = importFolder.getPath();
        Log.i("ExportData", "checking for exising export folder: " + path);
        if (!importFolder.exists()) {
            importFolder.mkdir();
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(MySqlHelper.DATE_FILE_NAME_FORMAT);
        String columns[] = null;
        if (table != null && table.equals(MySqlHelper.TABLE_MEMBER)) {
            fileName = "Members_" + formatter.format(c.getTime()) + ".csv";
            columns = MySqlHelper.MEMBER_COLS;
        } else if (table != null && table.equals(MySqlHelper.TABLE_ATTENDANCE)) {
            fileName = "Attendance_" + formatter.format(c.getTime()) + ".csv";
            columns = MySqlHelper.ATTEND_COLS;
        }


        File exportFile = new File(importFolder, fileName);
        try {
            exportFile.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(exportFile));
            csvWrite.writeNext(columns);
            DataHelper dataHelper = new DataHelper(con);
            if (table != null && table.equals(MySqlHelper.TABLE_MEMBER)) {
                dataHelper.openForRead();
                List<Member> memberList = dataHelper.getAllMembers(con);
                for (Member mem : memberList) {
                    String array[] = {mem.getId() + "", mem.getFirstName(), mem.getLastName(), mem.getDob(),
                            mem.getEmail(), mem.getPhoneNumber(), mem.getBeltLevel(), mem.getMemberSince()};
                    csvWrite.writeNext(array);
                }
                dataHelper.close();
            } else if (table != null && table.equals(MySqlHelper.TABLE_ATTENDANCE)) {
                dataHelper.openForRead();
                List<Attendance> attList = dataHelper.getAllAttendance(con);
                for (Attendance att : attList) {
                    String array[] = {att.getId() + "", att.getFormattedAttendDate(),att.getMemberId() + ""};
                    csvWrite.writeNext(array);
                }
                dataHelper.close();

            }

            return true;

        } catch (Exception e) {
            Log.e("ExportData", "Error exporting: " + e.getMessage(), e);
        }

        return false;

    }
}
