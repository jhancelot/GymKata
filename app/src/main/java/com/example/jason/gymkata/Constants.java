package com.example.jason.gymkata;

/**
 * Created by Jason on 2016-05-10.
 * This is an interface used as a collection of CONSTANTS
 */
public interface Constants {

    static final String LOGIN_PREFS = "LoginPrefs";
    static final String LOGIN_STATE = "LoginState";
    static final String LOGGED_IN = "LoggedIn";
    static final String LOGGED_OUT = "LoggedOut";

    final static String EDIT_MODE = "EDIT_MODE";
    final static int VIEW_MODE = 0;
    final static int EDIT_EXISTING = 1;
    final static int EDIT_NEW = 2;
    final static int DELETE_EXISTING = 3;

    final static String ATTENDANCE_ID = "ATTENDANCE_ID";
    final static String MEMBER_ID = "MEMBER_ID";

    // REPORT TYPES
    final static int MEMBER_REPORT = 0;
    final static int MEMBERS_PLUS_TOTALS = 1;
    final static int ATTENDANCE_REPORT = 2;


    final static String DEFAULT_COUNTRY_ISO = "CA";

    static final String SHARED_PREFS = "SHARED_PREFS";

    // MsgBox Constants
    static final String MSGBOX_RESPONSE = "MSG_BOX_RESPONSE";
    static final String RESPONSE_YES = "YES";
    static final String RESPONSE_NO = "NO";
    static final String RESPONSE_CANCEL = "CANCEL";
    static final String RESPONSE_OK = "OK";


    static final String RESULT_SUCCESS = "SUCCESS";
    static final String RESULT_FAILED = "FAILED";
    static final String RESULT_CANCELLED = "CANCELLED";


}
