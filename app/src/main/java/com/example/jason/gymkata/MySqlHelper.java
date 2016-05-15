package com.example.jason.gymkata;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    //public static final String DATE_DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_DISPLAY_FORMAT = "yyyy-MM-dd";
    public static final String DATE_SQL_FORMAT = "yyyyMMdd";
    public static final String DATE_FILE_NAME_FORMAT = "yyyy-MM-dd-HHmmss";
    public static final String DATE_DELIMITER = "-";

    public static final String PHONE_DISPLAY_FORMAT = "";
    public static final String PHONE_DELIMITER = "-";

    // SYSTEMUSER TABLE
    public static final String SYSTEM_USER_COLUMN_ID = "_id";
    public static final String SYSTEM_USER_COLUMN_NAME = "Name";
    public static final String SYSTEM_USER_COLUMN_PASS = "Password";
    public static final String TABLE_SYSTEM_USER = "SystemUser";
    public static final String SYSTEM_USER_TABLE_CREATE = "CREATE TABLE " + TABLE_SYSTEM_USER
            + " (" + SYSTEM_USER_COLUMN_ID + " integer primary key autoincrement, "
            + SYSTEM_USER_COLUMN_NAME + " text not null, "
            + SYSTEM_USER_COLUMN_PASS + " text not null);";
    public static final String DEFAULT_USER_NAME = "cbc@uniserve.com";
    public static final String DEFAULT_USER_PASSWORD = "pass123";

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
            + MEMBER_COLUMN_DOB + " text,"
            + MEMBER_COLUMN_EMAIL + " text,"
            + MEMBER_COLUMN_PHONE + " text,"
            + MEMBER_COLUMN_BELT_LEVEL + " text,"
            + MEMBER_MEMBERSINCE + " text);";

    public static final String[] MEMBER_COLS = {MEMBER_COLUMN_ID, MEMBER_COLUMN_FIRSTNAME,
            MEMBER_COLUMN_LASTNAME, MEMBER_COLUMN_DOB,MEMBER_COLUMN_EMAIL, MEMBER_COLUMN_PHONE,
            MEMBER_COLUMN_BELT_LEVEL, MEMBER_MEMBERSINCE};

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
            + ATTEND_COLUMN_ATTEND_DATE + " text,"
            + ATTEND_COLUMN_MEMBER_ID + " text not null);";

    public static final String[] ATTEND_COLS = {ATTEND_COLUMN_ID,
            ATTEND_COLUMN_ATTEND_DATE, ATTEND_COLUMN_MEMBER_ID};

    public static String getFormattedDate(String date) {
        String formattedDate = null;
        if (date != null && date.length() >= 8) {
            Log.i("getFormattedDate", "date=" + date);
            Log.i("getFormattedDate", "date.substring(4, 6)=" + date.substring(4, 6));
            Log.i("getFormattedDate", "date.substring(6, 8)=" + date.substring(6, 8));
            // first strip out all non-digits
            date = date.replaceAll("\\D+","");
            formattedDate = date.substring(0, 4) + DATE_DELIMITER
                    + date.substring(4, 6) + DATE_DELIMITER
                    + date.substring(6, 8);
        }
        return formattedDate;

    }
    public static String getFormattedPhone(String phoneNumber) {
        String formattedPhone = null;
        if (phoneNumber != null && phoneNumber.length() >=10) {
            Log.i("getFormattedPhone", "phoneNumber=" + phoneNumber);
            Log.i("getFormattedPhone", "phoneNumber.substring(3, 6)=" + phoneNumber.substring(3, 6));
            Log.i("getFormattedPhone", "phoneNumber.substring(6, 10)=" + phoneNumber.substring(6, 10));
            // first strip out all non-digits
            phoneNumber = phoneNumber.replaceAll("\\D+","");
            formattedPhone = phoneNumber.substring(0, 3) + PHONE_DELIMITER
                    + phoneNumber.substring(3, 6) + PHONE_DELIMITER
                    + phoneNumber.substring(6, 10);
        }
        return formattedPhone;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_SYSTEM_USER);
        Log.i(MySqlHelper.class.getName(), "Sql syntax: " + SYSTEM_USER_TABLE_CREATE);
        db.execSQL(SYSTEM_USER_TABLE_CREATE);
        // Pre-populate the SYSTEM_USER table with a single admin user
        ContentValues values = new ContentValues();
        values.put(SYSTEM_USER_COLUMN_NAME, DEFAULT_USER_NAME);
        values.put(SYSTEM_USER_COLUMN_PASS, DEFAULT_USER_PASSWORD);
        db.insert(TABLE_SYSTEM_USER, null, values);

        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_MEMBER);
        Log.i(MySqlHelper.class.getName(), "Sql syntax: " + MEMBER_TABLE_CREATE);
        db.execSQL(MEMBER_TABLE_CREATE);

        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_BELT);
        Log.i(MySqlHelper.class.getName(), "Sql syntax: " + BELT_TABLE_CREATE);
        db.execSQL(BELT_TABLE_CREATE);

        Log.w(MySqlHelper.class.getName(), "Creating table " + TABLE_ATTENDANCE);
        Log.i(MySqlHelper.class.getName(), "Sql syntax: " + ATTEND_TABLE_CREATE);
        db.execSQL(ATTEND_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySqlHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ".");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYSTEM_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BELT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        this.onCreate(db);
    }
}
