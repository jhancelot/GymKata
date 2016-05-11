package com.example.jason.gymkata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class AttendanceActivityOld extends AppCompatActivity {
    private long currentAttendanceId;
    private TextView mFullName;
    private TextView mAttendanceId;
    private EditText mAttendanceDate;
    private Menu mainMenu;

    private int editMode = Constants.VIEW_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        mFullName = (TextView) findViewById(R.id.tvMemberFullName);
        mAttendanceId = (TextView) findViewById(R.id.tvAttendId);
        mAttendanceDate = (EditText) findViewById(R.id.etAttendanceDate);

        // Populate fields
        Intent i = getIntent();
        // CHECK THE VALUE OF EDIT_MODE... IF ITS NULL THEN DEFAULT TO "NEW" MODE
        editMode = i.getIntExtra("EDIT_MODE", Constants.EDIT_NEW);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentAttendanceId = i.getLongExtra("ATTENDANCE_ID", -1);
        Log.i("MemberActivity", "editMode=" + editMode);
        Log.i("MemberActivity", "currentAttendanceId=" + currentAttendanceId);
        if (currentAttendanceId > -1) {
            try {
                DataHelper dataHelper = new DataHelper(AttendanceActivityOld.this);
                dataHelper.open();
                Attendance currentAttend = dataHelper.getAttendanceRecord(currentAttendanceId);
                Member currentMem = new Member();
                if (currentAttend != null && currentAttend.getId() > -1) {
                    currentMem = dataHelper.getMember(currentAttend.getMemberId());
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
    }
    private void populateForm(Attendance attendance, Member mem) {
        mAttendanceId.setText(attendance.getId() + "");
        mFullName.setText(mem.getFirstName() + " " + mem.getLastName());
        mAttendanceDate.setText(attendance.getAttendDate());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance_list, menu);
        this.mainMenu = menu;
        // CHECK EDIT MODE
        /*
        if (editMode == VIEW_MODE) {
            mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
        } else { // IF IN EDIT MODE THEN DISPLAY THE SAVE BUTTON
            mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_save));
        }
        */
        return true;
    }
}
