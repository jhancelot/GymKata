package com.example.jason.gymkata;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jason on 2016-04-02.
 */
public class Member {
    private long id = -1;
    private String firstName;
    private String lastName;
    private long dob;
    private String phoneNumber;
    private String email;
    private String beltLevel;
    private long memberSince;

    public Member() {
    }

    public Member(String firstName, String lastName, String beltLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.beltLevel = beltLevel;
        this.memberSince = System.currentTimeMillis();
    }

    // This is apparently necessary to have the listView present the data properly
    // By doing it this way we can bind the Member object to the listview adapter
    // and the list will refresh properly
    @Override
    public String toString() {
        return this.getLastName() + ", " + this.getFirstName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getDob() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MySqlHelper.DATE_FORMAT, Locale.getDefault());
        return dob; //dateFormat.format(dob);
    }

    public void setDob(long dob) {

        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBeltLevel() {
        return beltLevel;
    }

    public void setBeltLevel(String beltLevel) {
        this.beltLevel = beltLevel;
    }
    
    public long getMemberSince() { return memberSince;}

    public void setMemberSince(long memSince) {this.memberSince = memSince;}

    public long createMember(Context context) throws Exception {

        // long insertId = 0;
        Log.i("Member.createMember",
                "ID:" + this.getId()
                        + "; Date: " + this.getMemberSince()
                        + "; formatteddate: " + MySqlHelper.getFormattedDate(this.getMemberSince())
                        + "; Member ID: " + this.getId());
        DataHelper dataHelper = new DataHelper(context);

        return dataHelper.createMember(this);
    }
}
