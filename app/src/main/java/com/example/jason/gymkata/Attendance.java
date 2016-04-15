package com.example.jason.gymkata;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jason on 2016-04-02.
 */
public class Attendance {

    private long id;
    private double attendDate;
    private long memberId;
    private SQLiteDatabase database;
    private MySqlHelper dbHelper;

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


}
