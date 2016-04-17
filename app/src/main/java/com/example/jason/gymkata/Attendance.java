package com.example.jason.gymkata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016-04-02.
 */
public class Attendance {

    private long id;
    private double attendDate;
    private long memberId;

    private SQLiteDatabase database;


    public Attendance() {
    }

    public Attendance(double attendDate, long memberId) {
        this.attendDate = attendDate;
        this.memberId = memberId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(double attendDate) {
        this.attendDate = attendDate;
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

    private static Attendance cursorToAttendance(Cursor cur) {
        Attendance att = new Attendance();
        try {
            att.setId(cur.getLong(0));
            att.setAttendDate(cur.getDouble(1));
            att.setId(cur.getLong(2));
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error setting values in cursorToAttendance: " + e.toString());
            e.printStackTrace();
        }
        return att;
    }
}
