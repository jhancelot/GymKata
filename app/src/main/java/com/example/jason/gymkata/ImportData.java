package com.example.jason.gymkata;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Jason on 2016-05-14.
 * This class is used to export data to CSV format
 */
//public class ImportData extends AsyncTask<Void, Boolean, Boolean> implements Constants {
public class ImportData implements Constants {
    Context con;
    ProgressDialog dialog;
    String path;
    int reportType;
    String fileName = null;

    public ImportData(Context context, int reportType) {
        this.con = context;
        this.reportType = reportType;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //@Override
//    protected Boolean doInBackground(Void... params) {
      protected Boolean execute() {
        //File importFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        File importFolder = new File(this.path, "");
        // path = importFolder.getPath();
        Log.i("ImportData", "checking for existing import folder: " + path);
        if (!importFolder.exists() || (path == null || fileName == null)) {
            Log.w("ImportData", "Warning. Conditions are not ripe for an import."
                    + " importFolder=" + importFolder
                    + " path = " + ((path == null)?"null":path)
                    + " fileName = " + ((fileName == null)?"null":fileName));
            return false;
        }
        // should be good to go
        File importFile = new File(importFolder, fileName);
        Log.i("import", "preparing to import the file: " + importFile.getPath());
        DataHelper dataHelper = null;
        try {
            dataHelper = new DataHelper(con);
            dataHelper.open();
            CSVReader csvRead = new CSVReader(new FileReader(importFile));
            List<String[]> allLines = csvRead.readAll();
            Log.i("Import", "Number of elements in List: " + allLines.size());
            Log.i("Import", "reportType = " + reportType);
            if (reportType == MEMBER_REPORT || reportType == MEMBERS_PLUS_TOTALS) {
                Member mem = null;
                long currentMemberId = -1;
                int rowNumber = 0;
                Log.i("Import", "Starting to loop through rows...");
                for (String[] nextLine : allLines) {
                    // skip the header row
                    if (rowNumber > 0) {
                        mem = new Member();
                        Log.i("Import", "Starting to loop through columns...");
                        Log.i("importdata", "entering loop with column array length of " + nextLine.length);
                        for (int i = 0; i < nextLine.length; i++) { // .length is size of column array
                            switch (i) {
                                case 0:
                                    // No need to set the ID - it will be automatically set by Sqlite
                                    //mem.setId(Integer.parseInt(nextLine[i]));
                                    break;
                                case 1:
                                    mem.setFirstName(nextLine[i]);
                                    break;
                                case 2:
                                    mem.setLastName(nextLine[i]);
                                    break;
                                case 3:
                                    mem.setDob(nextLine[i]);
                                    break;
                                case 4:
                                    mem.setEmail(nextLine[i]);
                                    break;
                                case 5:
                                    mem.setPhoneNumber(nextLine[i]);
                                    break;
                                case 6:
                                    mem.setBeltLevel(nextLine[i]);
                                    break;
                                case 7:
                                    mem.setMemberSince(nextLine[i]);
                                    break;
                                case 8:
                                    // will only be the case when Attendance total is included
                                    // even if we bring it in, there's no place to store it in
                                    // the sqlite database at this time.
                                    mem.setAttendanceTotal(Integer.parseInt(nextLine[i]));
                                    break;

                            }
                        } // end of column iteration
                        Log.i("Import", "Adding member: " + mem.getLastName());
                        currentMemberId = dataHelper.createMember(mem);
                        if (currentMemberId > -1) {
                            // then success! but reset the Member object for the next one anyway
                            mem = null;
                        } else {
                            throw new Exception("Error writing to database. The currentMemberId is still -1");
                        }
                    }
                    rowNumber++;
                } // end of row iteration

            } else if (reportType == ATTENDANCE_REPORT) {
                Attendance att = null;
                long currentAttId = -1;
                int rowNumber = 0;
                for (String[] nextLine : allLines) {
                    // skip the header row
                    if (rowNumber > 0) {
                        att = new Attendance();
                        Log.i("importdata", "entering loop with column array length of " + nextLine.length);
                        for (int i = 0; i < nextLine.length; i++) { // .length is size of column array
                            Log.i("columnloop", "nextLine[i] = "+ nextLine[i]);
                            switch (i) {
                                case 0:
                                    // No need to set the ID - it will be automatically set by Sqlite
                                    //att.setId(Integer.parseInt(nextLine[i]));
                                    break;
                                case 1:
                                    att.setAttendDate(nextLine[i]);
                                    break;
                                case 2:
                                    att.setMemberId(Integer.parseInt(nextLine[i]));
                                    break;
                            }
                        } // end of column iteration
                        Log.i("Import", "Adding Attendance for member: " + att.getMemberId());
                        currentAttId = dataHelper.createAttend(att);
                        if (currentAttId > -1) {
                            // then success! but reset the Member object for the next one anyway
                            att = null;
                        } else {
                            throw new Exception("Error writing to database. The currentMemberId is still -1");
                        }
                    }
                    rowNumber++;
                } // end of row iteration

            }

            return true;

        } catch (Exception e) {
            Log.e("ExportData", "Error importing: " + e.getMessage(), e);
        } finally {
            if (dataHelper != null) dataHelper.close();
        }

        return false;

    }


}
