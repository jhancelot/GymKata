package com.example.jason.gymkata;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity implements Constants {
    private long currentAttendanceId;
    private long currentMemberId;

    private TextView mFullName;
    private TextView mAttendanceId;
    private EditText mAttendanceDate;
    private Menu mainMenu;

    private int editMode = Constants.VIEW_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        // Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFullName = (TextView) findViewById(R.id.tvMemberFullName);
        mAttendanceId = (TextView) findViewById(R.id.tvAttendId);
        mAttendanceDate = (EditText) findViewById(R.id.etAttendanceDate);

        // Populate fields
        Intent i = getIntent();
        // CHECK THE VALUE OF EDIT_MODE... IF ITS NULL THEN DEFAULT TO "NEW" MODE
        editMode = i.getIntExtra(EDIT_MODE, EDIT_NEW);
        Log.i("AttendActivity", "editMode=" + editMode);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentAttendanceId = i.getLongExtra(ATTENDANCE_ID, -1);
        Log.i("AttendActivity", "currentAttendanceId=" + currentAttendanceId);
        if (currentAttendanceId > -1) {
            try {
                DataHelper dataHelper = new DataHelper(AttendanceActivity.this);
                dataHelper.open();
                Attendance currentAttend = dataHelper.getAttendanceRecord(currentAttendanceId);
                Member currentMem = new Member();
                if (currentAttend != null && currentAttend.getId() > -1) {
                    currentMem = dataHelper.getMember(currentAttend.getMemberId());
                    currentMemberId = currentAttend.getMemberId();
                    Log.i("AttendDets", "currentAttend Value: " + currentAttend.getId());
                    Log.i("AttendDets", "currentMember Value: " + currentMem.getId());
                } else {
                    Log.w("AttendDets", "currentAttend Value: " + currentAttend);
                }
                populateForm(currentAttend, currentMem);
                dataHelper.close();
                Log.w(MainActivity.class.getName(), "Database successfully opened ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(MainActivity.class.getName(), "Error opening database: " + e);
            }

        } else { // make sure we're in "new" mode
            editMode = Constants.EDIT_NEW;
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "setFocus set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mAttendanceDate.setFocusable(true);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void populateForm(Attendance attendance, Member mem) {
        mAttendanceId.setText(attendance.getId() + "");
        mFullName.setText(mem.getFirstName() + " " + mem.getLastName());
        mAttendanceDate.setText(attendance.getAttendDate());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        this.mainMenu = menu;
        // CHECK EDIT MODE
        if (editMode == Constants.VIEW_MODE) {
            mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
            mAttendanceDate.setFocusable(false);
            mAttendanceDate.setEnabled(false);
        } else { // IF IN EDIT MODE THEN DISPLAY THE SAVE BUTTON
            mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_save));
            mAttendanceDate.setFocusableInTouchMode(true);
            mAttendanceDate.setFocusable(true);
            mAttendanceDate.setEnabled(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_or_edit) {
            Log.i("onOption", "SAVE OR EDIT selected");
            //    MenuItem mi = (MenuItem) findViewById(R.id.action_save);
            // if in "View" mode, then the pencil is displayed, so switch
            // to checkbox and enter "Edit" mode
            if (editMode == Constants.VIEW_MODE) {

                editMode = Constants.EDIT_EXISTING;
                Log.i("AttendActivity", "1. editMode switched to " + editMode);
                mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_save));
                mAttendanceDate.setFocusableInTouchMode(true);
                mAttendanceDate.setFocusable(true);
                mAttendanceDate.setEnabled(true);
            } else if (editMode == Constants.EDIT_NEW || editMode == Constants.EDIT_EXISTING) {
                // Check form field values
                String msg = validateForm();
                if (msg == null || msg.length() == 0) { // this means that there are no error msgs, so we can proceed
                    // Save Member Data
                    editMode = Constants.VIEW_MODE;
                    Log.i("AttendActivity", "2. editMode switched to " + editMode);
                    mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
                    mAttendanceDate.setFocusable(false);
                    mAttendanceDate.setEnabled(false);
                    Attendance att = new Attendance(currentMemberId);
                    if (currentMemberId > -1)
                        att.setMemberId(Integer.parseInt(mAttendanceId.getText().toString()));
                    if (mAttendanceId != null && mAttendanceId.getText().toString().length() > 0)
                        att.setId(Integer.parseInt(mAttendanceId.getText().toString()));
                    if (mAttendanceDate != null && mAttendanceDate.getText().toString().length() > 0)
                        att.setAttendDate(mAttendanceDate.getText().toString());
                    try {
                        long memberId = -1;
                        DataHelper dataHelper = new DataHelper(AttendanceActivity.this);
                        dataHelper.open();
                        if (currentAttendanceId > -1) { // then this is an edit of an existing record
                            currentAttendanceId = dataHelper.updateAttend(att);
                        } else { // then this is a new entry
                            currentAttendanceId = dataHelper.createAttend(att);

                        }
                        dataHelper.close();

                        //memberId = mem.createMember(MemberActivity.this);
                        if (currentAttendanceId == -1)
                            throw new Exception("currentAttendanceId is -1, so something went wrong");
                        msgBox("Saved!", findViewById(android.R.id.content));
                    } catch (Exception e) {
                        msgBox("Error creating Member Record: " + e.toString(), findViewById(android.R.id.content));
                        e.printStackTrace();
                    }

                } else {
                    // use this syntax to access the current "View"
                    msgBox(msg, findViewById(android.R.id.content));
                }
            } else {
                Log.e("AttendActivity", "Warning unknown editMode: " + editMode);
            }


        } else if (id == R.id.action_delete) {
            Log.i("onOption", "DELETE selected");
        }
        return super.onOptionsItemSelected(item);
    }

    private String validateForm() {
        List<String> msgs = new ArrayList<String>();
        String readableMsg = "";
        if (mAttendanceDate.getText() == null || mAttendanceDate.getText().length() == 0) {
            msgs.add("Attendance Date");

        }
        for (String msg: msgs) {
            readableMsg = readableMsg + ", " + msg;
        }
        return (readableMsg.length() == 0)?readableMsg:readableMsg + " are invalid";
    }
    private void msgBox(String msg, View view) {
        Log.e("AttendActivity", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
