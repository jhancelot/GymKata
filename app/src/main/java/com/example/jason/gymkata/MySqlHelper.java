package com.example.jason.gymkata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jason on 2016-04-02.
 */
public class MySqlHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GymKata.db";
    private static final int DATABASE_VERSION = 1; //"1.0";

    public MySqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Here's a public constant for dates:
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // MEMBER TABLE
    public static final String TABLE_MEMBER = "Member";
    public static final String MEMBER_COLUMN_ID = "_id";
    public static final String MEMBER_COLUMN_FIRSTNAME = "FirstName";
    public static final String MEMBER_COLUMN_LASTNAME = "LastName";
    public static final String MEMBER_COLUMN_DOB = "DateOfBirth";
    public static final String MEMBER_COLUMN_EMAIL = "Email";
    public static final String MEMBER_COLUMN_PHONE = "PhoneNumber";
    public static final String MEMBER_COLUMN_BELT_LEVEL = "Level";
    public static final String MEMBER_MEMBERSINCE = "MemberSince";

    private static final String MEMBER_TABLE_CREATE = "create table "
            + TABLE_MEMBER + "(" + MEMBER_COLUMN_ID
            + " integer primary key autoincrement, "
            + MEMBER_COLUMN_FIRSTNAME + " text not null,"
            + MEMBER_COLUMN_LASTNAME + " text not null,"
            + MEMBER_COLUMN_DOB + " REAL,"
            + MEMBER_COLUMN_EMAIL + " text,"
            + MEMBER_COLUMN_PHONE + " text,"
            + MEMBER_COLUMN_BELT_LEVEL + " text,"
            + MEMBER_MEMBERSINCE + " REAL);";

    // BELT LEVEL TABLE
    public static final String TABLE_BELT = "BeltLevel";
    public static final String BELT_COLUMN_ID = "_id";
    public static final String BELT_COLUMN_LEVEL = "Level";
    public static final String BELT_COLUMN_DESCRIPTION = "Description";

    private static final String BELT_TABLE_CREATE = "create table "
            + TABLE_BELT + "(" + BELT_COLUMN_ID
            + " integer primary key autoincrement, "
            + BELT_COLUMN_LEVEL + " text not null,"
            + BELT_COLUMN_DESCRIPTION + " text);";

    // ATTENDANCE TABLE
    public static final String TABLE_ATTENDANCE = "Attendance";
    public static final String ATTEND_COLUMN_ID = "_id";
    public static final String ATTEND_COLUMN_ATTEND_DATE = "AttendanceDate";
    public static final String ATTEND_COLUMN_MEMBER_ID = "MemberId";

    private static final String ATTEND_TABLE_CREATE = "create table "
            + TABLE_ATTENDANCE + "(" + ATTEND_COLUMN_ID
            + " integer primary key autoincrement, "
            + ATTEND_COLUMN_ATTEND_DATE + " integer not null,"
            + ATTEND_COLUMN_MEMBER_ID + " text not null);";

    public static final String[] ATTEND_COLS = {MySqlHelper.ATTEND_COLUMN_ID, MySqlHelper.ATTEND_COLUMN_ATTEND_DATE, MySqlHelper.ATTEND_COLUMN_MEMBER_ID};

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_MEMBER);
        Log.e(MySqlHelper.class.getName(), "Sql syntax: " + MEMBER_TABLE_CREATE);
        db.execSQL(MEMBER_TABLE_CREATE);

        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_BELT);
        Log.e(MySqlHelper.class.getName(), "Sql syntax: " + BELT_TABLE_CREATE);
        db.execSQL(BELT_TABLE_CREATE);

        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_ATTENDANCE);
        Log.e(MySqlHelper.class.getName(), "Sql syntax: " + ATTEND_TABLE_CREATE);
        db.execSQL(ATTEND_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySqlHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ".");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BELT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        this.onCreate(db);
    }
}
