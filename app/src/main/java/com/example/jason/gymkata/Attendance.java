package com.example.jason.gymkata;

/**
 * Created by Jason on 2016-04-02.
 */
public class Attendance {

    private long id;
    private long attendDate;
    private long memberId;

    public Attendance() {
    }

    public Attendance(long attendDate, long memberId) {
        this.attendDate = attendDate;
        this.memberId = memberId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(long attendDate) {
        this.attendDate = attendDate;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
