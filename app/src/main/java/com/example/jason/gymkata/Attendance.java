package com.example.jason.gymkata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(MySqlHelper.DATE_DISPLAY_FORMAT, Locale.getDefault());
        Date d = new Date(this.attendDate);
        return dateFormat.format(d);

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
        return this.getMemberId() + ": " + this.getAttendDate();
    }

    public static List<Attendance> getAllAttendance(Context context) {
        List<Attendance> attendanceList = new ArrayList<Attendance>();
        MySqlHelper dbHelper = new MySqlHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, null, null, null, null, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            Attendance att = cursorToAttendance(cur);
            attendanceList.add(att);
            cur.moveToNext();
        }
        cur.close();
        return attendanceList;

    }

    public long createAttend(Context context) throws Exception {

       // long insertId = 0;
        Log.e("Attend.createAttend",
                "ID:" + this.getId()
                        + "; Date: " + this.getAttendDate()
                        + "; formatteddate: " + this.getFormattedAttendDate()
                        + "; Member ID: " + this.getMemberId());
        DataHelper dataHelper = new DataHelper(context);

        return dataHelper.createAttend(this);
        /*
        ContentValues values = new ContentValues();
        values.put(MySqlHelper.ATTEND_COLUMN_ATTEND_DATE, this.getAttendDate());
        values.put(MySqlHelper.ATTEND_COLUMN_MEMBER_ID, this.getMemberId());
        try {
            // YOU NEED TO FIGURE OUT HOW TO PASS THE CONTEXT TO THIS CLASS OR MOVE THE SQL STUFF BACK TO DATAHELPER
          //  dbHelper = new MySqlHelper(context);
            dbHelper = new MySqlHelper(context);
            database = dbHelper.getWritableDatabase();
            insertId = database.insert(MySqlHelper.TABLE_ATTENDANCE, null, values);
        } catch (Exception e) {
            Log.e(Attendance.class.getName(), "Error inserting into database: " + e.toString());
            throw e;
        }
        */
        /*
        Cursor cur = database.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, MySqlHelper.ATTEND_COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cur.moveToFirst();
        Attendance newAttend = cursorToAttendance(cur);
        cur.close();
        */
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
