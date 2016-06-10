package com.example.jason.gymkata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason on 2016-04-02.
 * This class will hopefully be used to act as a data interface
 * It can be used to interact with a Sqlite database or with the shared_prefs
 * object.
 */
public class DataHelper {
    private SQLiteDatabase database;
    private MySqlHelper dbHelper;
/*
    private String[] member_cols = {MySqlHelper.MEMBER_COLUMN_ID, MySqlHelper.MEMBER_COLUMN_FIRSTNAME, MySqlHelper.MEMBER_COLUMN_LASTNAME,
       MySqlHelper.MEMBER_COLUMN_DOB, MySqlHelper.MEMBER_COLUMN_EMAIL, MySqlHelper.MEMBER_COLUMN_PHONE, MySqlHelper.MEMBER_COLUMN_BELT_LEVEL,
        MySqlHelper.MEMBER_MEMBERSINCE};

    private String[] attendance_cols = {MySqlHelper.ATTEND_COLUMN_ID, MySqlHelper.ATTEND_COLUMN_ATTEND_DATE, MySqlHelper.ATTEND_COLUMN_MEMBER_ID};
*/

    public DataHelper(Context context) {
        dbHelper = new MySqlHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void openForRead() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        // this call is to the superclass which closes all open databases
        if (dbHelper != null) dbHelper.close();
    }

    private ContentValues membertoContentValues(Member mem) {

        // prepare the phone number and the dates by stripping out non-digits
        /*
        // This is a good idea, but it seems to be interfering with my sql code
        // when I'm running date ranges in export/import
        // keep the "-" in the dates for now to see how the app and the sql code responds
        if (mem.getPhoneNumber() != null) mem.setPhoneNumber(mem.getPhoneNumber().replaceAll("\\D+",""));
        if (mem.getMemberSince() != null) mem.setMemberSince(mem.getMemberSince().replaceAll("\\D+", ""));
        if (mem.getDob() != null) mem.setDob(mem.getDob().replaceAll("\\D+", ""));
        */
        ContentValues values = new ContentValues();
        values.put(MySqlHelper.MEMBER_COLUMN_FIRSTNAME, mem.getFirstName());
        values.put(MySqlHelper.MEMBER_COLUMN_LASTNAME, mem.getLastName());
        values.put(MySqlHelper.MEMBER_COLUMN_DOB, mem.getDob());
        values.put(MySqlHelper.MEMBER_COLUMN_EMAIL, mem.getEmail());
        values.put(MySqlHelper.MEMBER_COLUMN_PHONE, mem.getPhoneNumber());
        values.put(MySqlHelper.MEMBER_COLUMN_BELT_LEVEL, mem.getBeltLevel());
        values.put(MySqlHelper.MEMBER_MEMBERSINCE, mem.getMemberSince());
        Log.i("membertoContentValues",
                "ID:" + mem.getId()
                        + "; First Name: " + mem.getFirstName()
                        + "; Last Name: " + mem.getLastName()
                        + "; Belt Level: " + mem.getBeltLevel()
                        + "; Phone: " + mem.getPhoneNumber()
                        + "; member since: " + mem.getMemberSince()
                        + "; dob: " + mem.getDob()
        );
        return values;
    }
    public long createMember(Member mem) {
        long insertId = -1;


        Log.i("database.isreadonly","ro=" + database.isReadOnly());
        try {
            ContentValues values = membertoContentValues(mem);
            // ensure we have a writable database
            this.open();
            insertId = database.insert(MySqlHelper.TABLE_MEMBER, null, values);
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error inserting into database: " + e.toString());
            e.printStackTrace();
        } finally {
            this.close();

        }
        return insertId;
    }
    public long updateMember(Member mem) {
        long insertId = -1;

        Log.i("DB.isreadonly", "ro=" + database.isReadOnly());
        try {
            // ensure we have a writable database
            this.open();
            ContentValues values = membertoContentValues(mem);
            String whereClause = MySqlHelper.MEMBER_COLUMN_ID + " =?";
            String[] whereArgs = new String[] {(mem.getId() + "")};
            insertId = database.update(MySqlHelper.TABLE_MEMBER, values, whereClause, whereArgs);

        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error UPDATING database: " + e.toString());
            e.printStackTrace();
        } finally {
            this.close();

        }
        return insertId;
    }

    public long createAttend(Attendance attend) {

        long insertId = -1;
        // strip non-digits out of the Attendance Date
        //attend.setAttendDate(attend.getAttendDate().replaceAll("\\D+", ""));

        ContentValues values = this.attendToContentValues(attend);
        try {
            this.open(); // ensure we have a writable database
            insertId = database.insert(MySqlHelper.TABLE_ATTENDANCE, null, values);
            this.close();
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error inserting into database: " + e.toString());
            e.printStackTrace();
        }
        return insertId;

    }
    public long updateAttend(Attendance attend) {

        long insertId = -1;
        ContentValues values = this.attendToContentValues(attend);
        try {
            //insertId = database.insert(MySqlHelper.TABLE_ATTENDANCE, null, values);
            String whereClause = MySqlHelper.ATTEND_COLUMN_ID + " =?";
            String[] whereArgs = new String[] {(attend.getId() + "")};
            insertId = database.update(MySqlHelper.TABLE_ATTENDANCE, values, whereClause, whereArgs );
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error UPDATING database: " + e.toString());
            e.printStackTrace();
        }
        return insertId;

    }
    public long checkForExistingAttend(Attendance proposedAttend) {
        long attendanceId = -1;
        Cursor cur = null;
        try {
            String whereClause = MySqlHelper.ATTEND_COLUMN_MEMBER_ID + " =?";
            String[] whereArgs = new String[] {(proposedAttend.getMemberId() + "")};
            cur = database.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, whereClause, whereArgs, null, null, null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                Attendance att = cursorToAttendance(cur);
                Log.i("checkExistAtt", "att.getAttDate=" + att.getAttendDate() + " and propAtt.getAttDate=" + proposedAttend.getAttendDate());
                if (att != null && att.getAttendDate() != null && att.getAttendDate().equals(proposedAttend.getAttendDate())) {
                    attendanceId = att.getId();
                    Log.w("checkexistAtt", "Warning - the member " + proposedAttend.getMemberId() + " has already signed in on " + proposedAttend.getAttendDate());
                    break;
                } else {
                    Log.i("checkExistAtt", "The member " + att.getMemberId() + " has not signed in today.");
                }

                cur.moveToNext();
            }


        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error UPDATING database: " + e.toString());
            e.printStackTrace();
        } finally {
            if (cur != null) cur.close();
        }
        return attendanceId;

    }
    public void deleteAttendance(long attendId) throws SQLException {
        Log.e("DataHelper.deleteMember", "Deleting attendance id: " + attendId);
        try {
            database.delete(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLUMN_ID + " = " + attendId, null);
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteAttendance: " + e.toString());
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    public ContentValues attendToContentValues(Attendance attend) {
        ContentValues values = new ContentValues();
        values.put(MySqlHelper.ATTEND_COLUMN_ATTEND_DATE, attend.getAttendDate());
        values.put(MySqlHelper.ATTEND_COLUMN_MEMBER_ID, attend.getMemberId());

        Log.e("attToCV",
                "ID:" + attend.getId()
                        + "; Date: " + attend.getAttendDate()
                        + "; Member ID: " + attend.getMemberId());
        return values;
    }

    public static String getTodaysDate(String format) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    public void generateSampleData(Context context) {
        // this line is only necessary because we're calling local methods without instantiating
        // this class first (which is where the context is normally passed in)
        if (dbHelper == null) dbHelper = new MySqlHelper(context);
        DateFormat dateFormat = new SimpleDateFormat(MySqlHelper.DATE_DISPLAY_FORMAT);
        String curDate = dateFormat.format(new Date());
        long memberId = -1;
        Member mem = new Member("Allan", "ADAMS", "WHITE");

        mem.setMemberSince(curDate);
        try {
            // ensure we have a writable database
            this.open();
            memberId = -1;
            memberId = this.createMember(mem);
            Log.i("genSample", "mem.getMemSince: " + mem.getMemberSince());
            Attendance attend = new Attendance(memberId, mem.getMemberSince());
            this.createAttend(attend);

            memberId = -1;
            mem = new Member("Beatrice", "BONNER", "YELLOW");
            mem.setMemberSince(curDate);
            memberId = this.createMember(mem);
            attend = new Attendance(memberId, mem.getMemberSince());
            this.createAttend(attend);

            memberId = -1;
            mem = new Member("Charles", "CARSON", "PURPLE");
            mem.setMemberSince(curDate);
            memberId = this.createMember(mem);
            attend = new Attendance(memberId, mem.getMemberSince());
            this.createAttend(attend);

        } catch (Exception e) {
            Log.e("GenMembers", "Error generating members: " + e.getMessage(), e);
        } finally {
           this.close();
        }
    }

    public int countMembers() {
        int i = 0;
        /*
        // Here's the lazy-man approach... copy the getAllMembers method
        Cursor cur = database.query(MySqlHelper.TABLE_MEMBER, member_cols, null, null, null, null, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            i++;
            cur.moveToNext();
        }
        cur.close();
        Log.e(DataHelper.class.getName(), "Value of i after while loop: " + i);
        */

        // Now let's execute the SELECT COUNT command
        Log.e(DataHelper.class.getName(), "Sql Statement: " + "SELECT * FROM " + MySqlHelper.TABLE_MEMBER);
       Cursor countCursor = database.rawQuery("SELECT * FROM " + MySqlHelper.TABLE_MEMBER, null);
        try {
            i = countCursor.getCount();
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in countMembers: " + e.toString());
        }
        Log.e(DataHelper.class.getName(), "Value of SELECT COUNT statement: " + i);
        if (i > 0) {
            if (countCursor.moveToFirst()) {
                do {
                    Log.e("countMembers",
                            "ID:" + countCursor.getString(0)
                                    + "; First Name: " + countCursor.getString(1)
                                    + "; Last Name: " + countCursor.getString(2)
                                    + "; Belt Level: " + countCursor.getString(3));
                } while (countCursor.moveToNext());
            }
        }
        countCursor.close();
        return i;
    }

    public void deleteMember(long memberId) throws SQLException {
        Log.e("DataHelper.deleteMember", "Deleting member: " + memberId);
        database.beginTransaction();
        try {
            database.delete(MySqlHelper.TABLE_MEMBER, MySqlHelper.MEMBER_COLUMN_ID + " = " + memberId, null);
            database.delete(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLUMN_MEMBER_ID + " = " + memberId, null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteMember: " + e.toString());
            e.printStackTrace();
            throw new SQLException(e);

        } finally {
            database.endTransaction();
        }
    }

    public void deleteAllMembers() throws SQLException {
        // Now let's execute the SELECT COUNT command
        Log.e(DataHelper.class.getName(), "Sql Statement: " + "DELETE FROM " + MySqlHelper.TABLE_MEMBER);
        Cursor countCursor = null;
        try {
            countCursor = database.rawQuery("DELETE FROM " + MySqlHelper.TABLE_MEMBER, null);
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteAllMembers: " + e.toString());
            e.printStackTrace();
            throw new SQLException(e);
        }

        int i;
        try {
            i = countCursor.getCount();
            if (i > 0) {
                throw new SQLException("Error. Still " + i + " rows left in the " + MySqlHelper.TABLE_MEMBER + " table.");
            }
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteAllMembers: " + e.toString());
            e.printStackTrace();
        }
        countCursor.close();

    }
    public List<Member> getAllMembers(Context context) {
        // this line is only necessary because we're calling local methods without instantiating
        // this class first (which is where the context is normally passed in)
        if (dbHelper == null) dbHelper = new MySqlHelper(context);
        List<Member> members = new ArrayList<Member>();
        if (database == null || !database.isReadOnly()) database = dbHelper.getReadableDatabase();
        // This will retrieve all data in the Member table and order by LASTNAME
        Cursor cur = database.query(MySqlHelper.TABLE_MEMBER, MySqlHelper.MEMBER_COLS, null, null, null, null, MySqlHelper.MEMBER_COLUMN_LASTNAME);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            Member mem = cursorToMember(cur);
            members.add(mem);
            cur.moveToNext();
        }
        cur.close();
        database.close();
        return members;

    }
    public List<Member> getMembersWithTotals(String startDate, String endDate, Context context) {
        // this line is only necessary because we're calling local methods without instantiating
        // this class first (which is where the context is normally passed in)
        if (dbHelper == null) dbHelper = new MySqlHelper(context);
        List<Member> members = new ArrayList<Member>();
        if (database == null || !database.isReadOnly()) database = dbHelper.getReadableDatabase();
        // This will retrieve all data in the Member table and order by LASTNAME
        String selectQuery = "SELECT " + MySqlHelper.TABLE_MEMBER + ".*, Count(*) as 'Total'"
            + " FROM " + MySqlHelper.TABLE_MEMBER + " INNER JOIN " + MySqlHelper.TABLE_ATTENDANCE
            + " WHERE " + MySqlHelper.TABLE_MEMBER + "." + MySqlHelper.MEMBER_COLUMN_ID
                + " = " + MySqlHelper.TABLE_ATTENDANCE + "." + MySqlHelper.ATTEND_COLUMN_MEMBER_ID
                + " AND Date(" + MySqlHelper.ATTEND_COLUMN_ATTEND_DATE + ") BETWEEN "
                + " Date('" + startDate + "') AND Date('" + endDate + "') "
                + "GROUP BY " + MySqlHelper.TABLE_MEMBER + "." + MySqlHelper.MEMBER_COLUMN_ID + ";";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i("getMemTot", "Executing raw query: " + selectQuery);
        Cursor cur = db.rawQuery(selectQuery, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            Member mem = cursorToMember(cur);
            // This is a specialized case where we are appending the count via a select statement
            mem.setAttendanceTotal(cur.getInt(8));
            members.add(mem);
            cur.moveToNext();
        }
        cur.close();
        database.close();
        return members;

    }

    public void listMembersToLog(Context context) {
        // this line is only necessary because we're calling local methods without instantiating
        // this class first (which is where the context is normally passed in)
        if (dbHelper == null) dbHelper = new MySqlHelper(context);
        String selectQuery = "SELECT * FROM " + MySqlHelper.TABLE_MEMBER;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        Log.e("DataHelper.listAll", "listing all members...");
        if (cur.moveToFirst()) {
            do {
                Log.e("DataHelper.listAll", "ID:" + cur.getString(0)
                        + "; First Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_FIRSTNAME))
                        + "; Last Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_LASTNAME))
                        + "; Dob: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_DOB))
                                + "; Phone: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_PHONE))
                                + "; Email: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_EMAIL))
                        + "; Belt Level: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_BELT_LEVEL))
                        + "; memberSince: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_MEMBERSINCE))
                );

            } while (cur.moveToNext());
        }
        Log.e("DataHelper.listAll", "end of list");
       // db.close();

    }

    public Member getMember(long memberId) {
        Member mem = new Member();
        // initialize to -1
        mem.setId(-1);
        Log.e("DataHelper.getMember", "getting member...");
        //SQLiteDatabase db = dbHelper.getReadableDatabase();
        String whereClause = MySqlHelper.MEMBER_COLUMN_ID + " =?";
        String[] whereArgs = new String[] {(memberId + "")};
        Cursor cur = database.query(MySqlHelper.TABLE_MEMBER, MySqlHelper.MEMBER_COLS, whereClause, whereArgs, null, null, null);
        if (cur.moveToFirst()) {
            Log.e("DataHelper.listAll", "ID:" + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_ID))
                            + "; First Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_FIRSTNAME))
                            + "; Last Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_LASTNAME))
                            + "; Dob: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_DOB))
                            + "; Phone: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_PHONE))
                            + "; Email: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_EMAIL))
                            + "; Belt Level: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_BELT_LEVEL))
                            + "; memberSince: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_MEMBERSINCE))
            );
            mem = cursorToMember(cur);

        }
        return mem;
        // db.close();

    }
    public Attendance getAttendanceRecord(long attendanceId) {
        Attendance att = new Attendance();

        // initialize to -1
        att.setId(-1);
        Log.e("DataHelper.getAtt", "getting attendance...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String whereClause = MySqlHelper.ATTEND_COLUMN_ID + " =?";
        String[] whereArgs = new String[] {(attendanceId + "")};
        Cursor cur = database.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, whereClause, whereArgs, null, null, null);
        if (cur.moveToFirst()) {
            Log.e("getAttRec", "ID:" + cur.getString(cur.getColumnIndex(MySqlHelper.ATTEND_COLUMN_ID))
                            + "; Att Date: " + cur.getString(cur.getColumnIndex(MySqlHelper.ATTEND_COLUMN_ATTEND_DATE))
                            + "; memberId: " + cur.getString(cur.getColumnIndex(MySqlHelper.ATTEND_COLUMN_MEMBER_ID))
            );
            att = cursorToAttendance(cur);

        }
        return att;
        // db.close();

    }

    private Member cursorToMember(Cursor cur) {
        Member mem = new Member();
        try {
            mem.setId(cur.getLong(0));
            mem.setFirstName(cur.getString(1));
            mem.setLastName(cur.getString(2));
            mem.setDob(cur.getString(3));
            mem.setEmail(cur.getString(4));
            mem.setPhoneNumber(cur.getString(5));
            mem.setBeltLevel(cur.getString(6));
            mem.setMemberSince(cur.getString(7));
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error setting values in cursorToMember: " + e.toString());
        }
        return mem;
    }


    private Attendance cursorToAttendance(Cursor cur) {
        Attendance attend = new Attendance();
        try {
            attend.setId(cur.getLong(0));
            attend.setAttendDate(cur.getString(1));
            attend.setMemberId(cur.getLong(2));

        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error setting values in cursorToMember: " + e.toString());
        }
        return attend;
    }

    public List<Attendance> getAllAttendance(Context context) {
        // send an invalid memberId which will get them all
        return getAllAttendance(-1, context);

    }

    public List<Attendance> getAllAttendance(long memberId, Context context) {
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
                cur = db.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, whereClause, whereArgs,
                        null, null, MySqlHelper.ATTEND_COLUMN_ATTEND_DATE);

            } else {
                cur = db.query(MySqlHelper.TABLE_ATTENDANCE, MySqlHelper.ATTEND_COLS, null, null,
                        null, null, null);

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

    public List<Attendance> getAllAttendance(String startDate, String endDate, Context context) {
        Log.e("DataHelper.getAttend", "getting member attendance between " + startDate + " and " + endDate);
        List<Attendance> attendanceList = null;
        MySqlHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cur = null;

        try {
            attendanceList = new ArrayList<Attendance>();
            dbHelper = new MySqlHelper(context);
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM "
                    + MySqlHelper.TABLE_ATTENDANCE
                    + " WHERE "
                    + "Date(" + MySqlHelper.ATTEND_COLUMN_ATTEND_DATE + ") BETWEEN Date(" + startDate
                    + ") AND Date(" + MySqlHelper.ATTEND_COLUMN_ATTEND_DATE + ")";
            Log.i("getAllAtt", "query: " + query);
            cur = db.rawQuery(query, null);



            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                Attendance att = cursorToAttendance(cur);
                attendanceList.add(att);
                cur.moveToNext();
            }
        } catch (Exception e) {
            Log.e("getAttend", "Error getting attendance: " + e.toString());
            e.printStackTrace();
        } finally {
            if (cur != null) cur.close();

        }
        return attendanceList;

    }

    public void listAttendanceToLog() {
        String selectQuery = "SELECT * FROM " + MySqlHelper.TABLE_ATTENDANCE;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        Log.e("DataHelper.listAll", "listing all attendance...");
        if (cur.moveToFirst()) {
            do {
                Log.e("DataHelper.listAttend", "ID:" + cur.getString(0)
                                + "; Date: " + cur.getString(cur.getColumnIndex(MySqlHelper.ATTEND_COLUMN_ATTEND_DATE))
                        + "; MBR ID: " + cur.getString(cur.getColumnIndex(MySqlHelper.ATTEND_COLUMN_MEMBER_ID))
                );

            } while (cur.moveToNext());
        }
        Log.e("DataHelper.listAll", "end of list");
        // db.close();

    }
    public void deleteAllAttendance() throws SQLException {
        // Now let's execute the SELECT COUNT command
        Log.e(DataHelper.class.getName(), "Sql Statement: " + "DELETE FROM " + MySqlHelper.TABLE_ATTENDANCE);
        Cursor countCursor = null;
        try {
            countCursor = database.rawQuery("DELETE FROM " + MySqlHelper.TABLE_ATTENDANCE, null);
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteAllAttendance: " + e.toString());
            e.printStackTrace();
            throw new SQLException(e);
        }

        int i;
        try {
            i = countCursor.getCount();
            if (i > 0) {
                throw new SQLException("Error. Still " + i + " rows left in the " + MySqlHelper.TABLE_ATTENDANCE + " table.");
            }
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error in deleteAllAttendance: " + e.toString());
            e.printStackTrace();
        } finally {
            if (countCursor != null) countCursor.close();
        }


    }
    public boolean validateLogin(SystemUser user) {
        boolean loginSuccessful = false;
        Log.d("validateLogin()a: ", user.get_name() + " " + user.get_password());
        if (database == null || !database.isReadOnly()) database = dbHelper.getReadableDatabase();
        // Something is wrong with this query, so comment out for now (use below RAW query instead)
        /*
        Cursor cur = database.query(MySqlHelper.TABLE_SYSTEM_USER, new String[]{
                        MySqlHelper.SYSTEM_USER_COLUMN_ID, MySqlHelper.SYSTEM_USER_COLUMN_NAME, MySqlHelper.SYSTEM_USER_COLUMN_PASS},
                MySqlHelper.SYSTEM_USER_COLUMN_NAME + "=?",
                new String[]{String.valueOf(user.get_name())}, null, null, null, null);
        */
        // code to test if the above code is even working...
        String selectQuery = "SELECT * FROM " + MySqlHelper.TABLE_SYSTEM_USER;
        Cursor cur = database.rawQuery(selectQuery, null);
        /* Only need this code to compare to above query
        if (cur2 != null && cur2.getCount() > 0) {
            cur2.moveToFirst();
            cur2.moveToFirst();
            Log.e("cur 2 Total Rows: ", cur2.getCount() + "");

            Log.e("cur 2", "cur values: " + cur2.getInt(0) + ";" + cur2.getString(1) + ";" + cur2.getString(2));
            cur2.close();
        } else {
            Log.e("ValidateLogin", "cur 2 is null or 0");
        }
        */

        Log.d("validateLogin()b: ", user.get_name() + " " + user.get_password());
        SystemUser existingUser = null;
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            Log.d("Total Rows: ", cur.getCount() + "");
            Log.d("validateLogin()c: ", user.get_name() + " " + user.get_password());
            existingUser = new SystemUser(cur.getInt(0), cur.getString(1), cur.getString(2));
            cur.close();
            Log.d("validateLogin()ccid: ", existingUser.get_id() + " " + existingUser.get_name() + " " + existingUser.get_password());
            if (user.get_name().equals(existingUser.get_name()) && user.get_password().equals(existingUser.get_password())) {
                loginSuccessful = true;
            } else {
                loginSuccessful = false;
            }
        } else {
            // No user found matching user input
            Log.w("validateLogin", "Warning - no user on SYSTEM_USER table that matches user input");
            loginSuccessful = false;
        }
        database.close();
        return loginSuccessful;
    }

    public void upgradeDb(Context con) {
        if (database == null || database.isReadOnly()) database = dbHelper.getWritableDatabase();
        MySqlHelper msql = new MySqlHelper(con);
        msql.onUpgrade(database, 1, 1);
        database.close();

    }
}
