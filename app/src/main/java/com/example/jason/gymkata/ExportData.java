package com.example.jason.gymkata;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Jason on 2016-05-14.
 */
public class ExportData implements Constants {
    Context con;
    ProgressDialog dialog;
    int reportType;
    String path;
    String fileName = null;
    String startDate = null;
    String endDate = null;



    public ExportData(Context context, int tableName) {
        this.con = context;
        this.reportType = tableName;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }
    /*
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(con);
        dialog.setTitle("Exporting members...");
        dialog.setMessage("initializing...");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.show();

    }
    */
    protected Boolean execute() {
        File exportFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        path = exportFolder.getPath();
        Log.i("ExportData", "checking for existing export folder: " + path);
        if (!exportFolder.exists()) {
            exportFolder.mkdir();
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(MySqlHelper.DATE_FILE_NAME_FORMAT);
        String columns[] = null;
        if (reportType == MEMBER_REPORT) {
            fileName = "Members_" + formatter.format(c.getTime()) + ".csv";
            columns = MySqlHelper.MEMBER_COLS;
        } else if (reportType == ATTENDANCE_REPORT) {
            fileName = "Attendance_" + formatter.format(c.getTime()) + ".csv";
            columns = MySqlHelper.ATTEND_COLS;
        } else if (reportType == MEMBERS_PLUS_TOTALS) {
            fileName = "Members_withTotals_" + formatter.format(c.getTime()) + ".csv";
            columns = MySqlHelper.MEMBER_COLS_INCLUDING_TOTALS;

        }


        File exportFile = new File(exportFolder, fileName);
        DataHelper dataHelper = null;
        try {
            exportFile.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(exportFile));
            csvWrite.writeNext(columns);
            dataHelper = new DataHelper(con);
            if (reportType == MEMBER_REPORT) {
                dataHelper.openForRead();
                List<Member> memberList = dataHelper.getAllMembers(con);
                Log.i("ExportData", "Total Members for export: " + memberList.size());
                for (Member mem : memberList) {
                    String array[] = {mem.getId() + "", mem.getFirstName(), mem.getLastName(), mem.getDob(),
                            mem.getEmail(), mem.getPhoneNumber(), mem.getBeltLevel(), mem.getMemberSince()};
                    csvWrite.writeNext(array);
                }
            } else if (reportType == ATTENDANCE_REPORT) {
                dataHelper.openForRead();
                List<Attendance> attList = dataHelper.getAllAttendance(this.startDate, this.endDate, con);
                Log.i("ExportData", "Total Attendance for export: " + attList.size());
                for (Attendance att : attList) {
                    String array[] = {att.getId() + "", att.getAttendDate(),att.getMemberId() + ""};
                    csvWrite.writeNext(array);
                }
                dataHelper.close();

            } else if (reportType == MEMBERS_PLUS_TOTALS){
                dataHelper.openForRead();
                List<Member> memList = dataHelper.getMembersWithTotals(this.startDate, this.endDate, con);
                Log.i("ExportData", "Total Members for export: " + memList.size());
                for (Member mem : memList) {
                    String array[] = {mem.getId() + "", mem.getFirstName(), mem.getLastName(), mem.getDob(),
                            mem.getEmail(), mem.getPhoneNumber(), mem.getBeltLevel(), mem.getMemberSince(),
                        mem.getAttendanceTotal() + ""};
                    csvWrite.writeNext(array);
                }
                dataHelper.close();

            }
            csvWrite.close();
            return true;

        } catch (Exception e) {
            Log.e("ExportData", "Error exporting: " + e.getMessage(), e);
        } finally {
            if (dataHelper != null) dataHelper.close();

        }

        return false;
    }

/*
    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()) dialog.dismiss();

        if (result)
            Log.i("ExportData", "Members successfully exported to file " + path + "\\" + fileName);
        else
            Log.i("ExportData", "Members export failed.");
         //   Snackbar.make(con, "Members successfully exported to folder " + Environment.getExternalStorageDirectory(),
         //           Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }
    */
}
