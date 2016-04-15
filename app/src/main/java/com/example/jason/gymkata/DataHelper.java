package com.example.jason.gymkata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private String[] member_cols = {MySqlHelper.MEMBER_COLUMN_ID, MySqlHelper.MEMBER_COLUMN_FIRSTNAME, MySqlHelper.MEMBER_COLUMN_LASTNAME,
       MySqlHelper.MEMBER_COLUMN_DOB, MySqlHelper.MEMBER_COLUMN_EMAIL, MySqlHelper.MEMBER_COLUMN_PHONE, MySqlHelper.MEMBER_COLUMN_BELT_LEVEL,
        MySqlHelper.MEMBER_MEMBERSINCE};

    public DataHelper(Context context) {
        dbHelper = new MySqlHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createMember(Member mem) {
        ContentValues values = new ContentValues();
        values.put(MySqlHelper.MEMBER_COLUMN_FIRSTNAME, mem.getFirstName());
        values.put(MySqlHelper.MEMBER_COLUMN_LASTNAME, mem.getLastName());
        values.put(MySqlHelper.MEMBER_COLUMN_DOB, mem.getDob());
        values.put(MySqlHelper.MEMBER_COLUMN_EMAIL, mem.getEmail());
        values.put(MySqlHelper.MEMBER_COLUMN_PHONE, mem.getPhoneNumber());
        values.put(MySqlHelper.MEMBER_COLUMN_BELT_LEVEL, mem.getBeltLevel());
        values.put(MySqlHelper.MEMBER_MEMBERSINCE, mem.getMemberSince());
        Log.e("createMember",
                "ID:" + mem.getId()
                        + "; First Name: " + mem.getFirstName()
                        + "; Last Name: " + mem.getLastName()
                        + "; Belt Level: " + mem.getBeltLevel());
        long insertId = 0;
        try {
            insertId = database.insert(MySqlHelper.TABLE_MEMBER, null, values);
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error inserting into database: " + e.toString());
        }
        Cursor cur = database.query(MySqlHelper.TABLE_MEMBER, member_cols, MySqlHelper.MEMBER_COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cur.moveToFirst();
        Member newMember = cursorToMember(cur);
        cur.close();

    }

    public void generateSampleData() {
        Member mem = new Member("Allan", "ADAMS", "WHITE");
        mem.setMemberSince(System.currentTimeMillis());
        createMember(mem);

        mem = new Member("Beatrice", "BONNER", "YELLOW");
        mem.setMemberSince(System.currentTimeMillis());
        createMember(mem);

        mem = new Member("Charles", "CARSON", "PURPLE");
        mem.setMemberSince(System.currentTimeMillis());
        createMember(mem);

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

    public void deleteMember(Member mem) {
        long id = mem.getId();
        Log.e("DataHelper.deleteMember", "Deleting member: " + mem.getId());
        database.delete(MySqlHelper.TABLE_MEMBER, MySqlHelper.MEMBER_COLUMN_ID + " = " + id, null);
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
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<Member>();
        Cursor cur = database.query(MySqlHelper.TABLE_MEMBER, member_cols, null, null, null, null, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            Member mem = cursorToMember(cur);
            members.add(mem);
            cur.moveToNext();
        }
        cur.close();
        return members;

    }

    public void listAllToLog() {
        String selectQuery = "SELECT * FROM " + MySqlHelper.TABLE_MEMBER;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        Log.e("DataHelper.listAll", "listing all members...");
        if (cur.moveToFirst()) {
            do {
                Log.e("DataHelper.listAll", "ID:" + cur.getString(0)
                        + "; First Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_FIRSTNAME))
                        + "; Last Name: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_LASTNAME))
                        + "; Belt Level: " + cur.getString(cur.getColumnIndex(MySqlHelper.MEMBER_COLUMN_BELT_LEVEL)));

            } while (cur.moveToNext());
        }
        Log.e("DataHelper.listAll", "end of list");
       // db.close();

    }

    private Member cursorToMember(Cursor cur) {
        Member mem = new Member();
        try {
            mem.setId(cur.getLong(0));
            mem.setFirstName(cur.getString(1));
            mem.setLastName(cur.getString(2));
            mem.setDob(cur.getDouble(3));
            mem.setPhoneNumber(cur.getString(4));
            mem.setEmail(cur.getString(5));
            mem.setBeltLevel(cur.getString(6));
            mem.setMemberSince(cur.getDouble(7));
        } catch (Exception e) {
            Log.e(DataHelper.class.getName(), "Error setting values in cursorToMember: " + e.toString());
        }
        return mem;
    }
}
