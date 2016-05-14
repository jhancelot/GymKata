package com.example.jason.gymkata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jason on 2016-04-02.
 */
public class Attendance {

    private long id;
    private String attendDate;
    private long memberId;

    private SQLiteDatabase database;
    private MySqlHelper dbHelper;


    public Attendance() {
    }

    public Attendance(long memberId) {
        this.memberId = memberId;
    }

    public Attendance(long memberId, String attendDate) {
        this.attendDate = attendDate;
        this.memberId = memberId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAttendDate() {

        return attendDate;
    }

    public void setAttendDate(String attendDate) {
        this.attendDate = attendDate;
    }

    public String getFormattedAttendDate() {
        // use this code if you switch your dates to "long" data type and milliseconds
        //SimpleDateFormat dateFormat = new SimpleDateFormat(MySqlHelper.DATE_DISPLAY_FORMAT, Locale.getDefault());
        //Calendar cal = Calendar.getInstance();
        //cal.setTimeInMillis(this.attendDate);
       //return dateFormat.format(cal.getTime());
        String formattedDate = null;
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(MySqlHelper.DATE_DISPLAY_FORMAT, Locale.getDefault());
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT, Locale.getDefault());
        try {
            formattedDate = outputDateFormat.format(inputDateFormat.parse(this.attendDate));

        } catch (ParseException e) {
            Log.e("formatDate", "Error formatting date: " + e.toString());
            e.printStackTrace();
            formattedDate = this.attendDate;
        }
        Log.i("formatDate", "value of formattedDate: " + formattedDate + "... value of unformatted date: " + this.getAttendDate());
        return formattedDate;

    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    // This is apparently necessary to have the listView present the data properly
    // By doing it this way we can bind the Attendance object to the listview adapter
    // and the list will refresh properly
    @Override
    public String toString() {

        //return this.getMemberId() + ": " + this.getAttendDate();
        return this.getFormattedAttendDate();
    }

    public static List<Attendance> getAllAttendance(Context context) {
        // send an invalid memberId which will get them all
        return getAllAttendance(-1, context);

    }

    public static List<Attendance> getAllAttendance(long memberId, Context context) {
        Log.e("DataHelper.getAttend", "getting member attendance...");
        List<Attendance> attendanceList = null;
        MySqlHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cur = null;

        try {
            attendanceList = new ArrayList<Attendance>();
            dbHelper = new MySqlHelper(context);
            db = dbHelper.getReadableDatabase();
            if (memberId != 00 && memberId != -1) {
                String whereClause = MySqlHelper.ATTEND_COLUMN_MEMBER_ID + " =?";
                String[] whereArgs = new String[]{(memberId + "")};
                cur = db.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, whereClause, whereArgs, null, null, null);

            } else {
                cur = db.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, null, null, null, null, null);

            }

            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                Attendance att = cursorToAttendance(cur);
                attendanceList.add(att);
                cur.moveToNext();
            }
            cur.close();
        } catch (Exception e) {
            Log.e("getAttend", "Error getting attendance: " + e.toString());
            e.printStackTrace();
        }
        return attendanceList;

    }



    private static Attendance cursorToAttendance(Cursor cur) {
        Attendance att = new Attendance();
        try {
            att.setId(cur.getLong(0));
            att.setAttendDate(cur.getString(1));
            att.setId(cur.getLong(2));
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error setting values in cursorToAttendance: " + e.toString());
            e.printStackTrace();
        }
        return att;
    }
}
