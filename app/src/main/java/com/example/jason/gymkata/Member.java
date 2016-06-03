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
    private String dob;
    private String phoneNumber;
    private String email;
    private String beltLevel;
    private String memberSince;
    private int attendanceTotal;



    public Member() {
    }

    public Member(String firstName, String lastName, String beltLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.beltLevel = beltLevel;
        //this.memberSince = System.currentTimeMillis();
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

    public String getDob() {
        return dob;
    }
    public String getFormattedDob() {
        return MySqlHelper.getFormattedDate(this.dob);
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFormattedPhone() {
        return MySqlHelper.getFormattedPhone(this.phoneNumber);
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
    
    public String getMemberSince() { return memberSince;}

    public String getFormattedMemberSince() {
        //String formattedDate = this.memberSince.substring(0,3) + MySqlHelper.DATE_DELIMITER
        //            + this.memberSince.substring(4,5) + MySqlHelper.DATE_DELIMITER
        //            + this.memberSince.substring(6,7);
        //return formattedDate;
        return MySqlHelper.getFormattedDate(this.memberSince);

    }
    public void setMemberSince(String memSince) {this.memberSince = memSince;}

    // although not part of the table, we need these to store the total attendance
    // when we JOIN to the Attendance table
    public int getAttendanceTotal() {
        return attendanceTotal;
    }

    public void setAttendanceTotal(int attendanceTotal) {
        this.attendanceTotal = attendanceTotal;
    }

}
