package com.example.jason.gymkata;

import java.util.Date;

/**
 * Created by Jason on 2016-04-02.
 */
public class Member {
    private long id;
    private String firstName;
    private String lastName;
    private double dob;
    private String phoneNumber;
    private String email;
    private String beltLevel;
    private double memberSince;

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

    public double getDob() {
        return dob;
    }

    public void setDob(double d) {
        this.dob = d;
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
    
    public double getMemberSince() { return memberSince;}

    public void setMemberSince(double memSince) {this.memberSince = memSince;}
}
