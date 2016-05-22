package com.example.jason.gymkata;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.DatePickerDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener, Constants {
    private long currentAttendanceId;
    private long currentMemberId;

    private TextView mFullName;
    private TextView mAttendanceId;
    private static TextInputEditText mAttendanceDate;
    private static ImageButton mAttDateCalendar;
    private Menu mainMenu;

    private int editMode = VIEW_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        // Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFullName = (TextView) findViewById(R.id.tvMemberFullName);
        mAttendanceId = (TextView) findViewById(R.id.tvAttendId);
        mAttendanceDate = (TextInputEditText) findViewById(R.id.etAttendanceDate);
        // Date Picker Fragment for DOB
        mAttDateCalendar = (ImageButton) findViewById(R.id.buttAttDateCalendar);
        mAttDateCalendar.setOnClickListener(this);
                /*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity.onClick: ", "MEMBER SINCE CLICKED");
                DialogFragment msFragment = new DatePickerFragment();
                msFragment.show(getSupportFragmentManager(), "datePicker");

            } */
       // });

        // Populate fields
        Intent i = getIntent();
        // CHECK THE VALUE OF EDIT_MODE... IF ITS NULL THEN DEFAULT TO "NEW" MODE
        editMode = i.getIntExtra(EDIT_MODE, EDIT_NEW);
        currentMemberId = i.getLongExtra(MEMBER_ID, -1);

        Log.i("AttendActivity", "editMode=" + editMode);
        // CHECK THE VALUE OF ATTENDANCE_ID... IF ITS NULL THEN SET TO -1
        currentAttendanceId = i.getLongExtra(ATTENDANCE_ID, -1);
        Log.i("AttendActivity", "currentAttendanceId=" + currentAttendanceId);
        this.populateForm();
        // this gives us the BACK ARROW on the menu bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void populateForm() {
        DataHelper dataHelper = null;
        try {
            dataHelper = new DataHelper(AttendanceActivity.this);
            dataHelper.openForRead();
            Attendance currentAttend = null;
            Member currentMem = null;
            if (currentAttendanceId > -1) { // then we're in EDIT MODE
                currentAttend = dataHelper.getAttendanceRecord(currentAttendanceId);
                Log.i("Att.popForm", "memId:" + currentMemberId);
                Log.i("Att.popForm", "AttId:" + currentAttendanceId);
                currentMem = dataHelper.getMember(currentAttend.getMemberId());
                currentMemberId = currentAttend.getMemberId();
                Log.i("AttendDets", "currentAttend Value: " + currentAttend.getId());
                mAttendanceId.setText(currentAttend.getId() + "");
                mAttendanceDate.setText(currentAttend.getAttendDate());
            } else { // we're in NEW ATTENDANCE MODE
                currentMem = dataHelper.getMember(currentMemberId);
                // default the attendance date to today's date
                mAttendanceDate.setText(DataHelper.getTodaysDate(MySqlHelper.DATE_DISPLAY_FORMAT));
            }
            // whether we're in NEW OR EDIT, we still need to populate the member data
            Log.i("AttendDets", "currentMember Value: " + currentMem.getId());
            mFullName.setText(currentMem.getFirstName() + " " + currentMem.getLastName());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        } finally {
            if (dataHelper != null) dataHelper.close();

        }

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Log.i("DatePickFrag", "onDateSet Fired");
        try {
            //Member mem = new Member();
            //adding one to the month
            // mem.setMemberSince(year + String.format("%02d",(++monthOfYear)) + String.format("%02d",dayOfMonth));
            Log.i("onDateSet", "year=" + year + "; month=" + monthOfYear + "; day=" + dayOfMonth);
            String datestring = year + String.format("%02d",(++monthOfYear)) + String.format("%02d",dayOfMonth);
            SimpleDateFormat sd = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT);
            //datestring = year + "-" + String.format("%02d",(++monthOfYear)) + "-" + String.format("%02d",dayOfMonth);
            Log.i("datestring", "datestring: " + datestring);
            Date dd = sd.parse(datestring);
            Log.i("dd", "dd: " + dd);
            // Update AttendDate
            mAttendanceDate.setText(MySqlHelper.getFormattedDate(datestring));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        this.mainMenu = menu;
        // CHECK EDIT MODE
        if (editMode == Constants.VIEW_MODE) {
            disableForm();
        } else { // IF IN EDIT MODE THEN DISPLAY THE SAVE BUTTON
            enableForm();
        }
        return true;
    }

    private void enableForm() {
        mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_save));
        //mAttendanceDate.setFocusableInTouchMode(true);
        //mAttendanceDate.setFocusable(true);
        mAttendanceDate.setEnabled(true);

    }

    private void disableForm() {
        mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
        // setFocusable seems to need both setFocusable and setFocusableInTouchMode to
        // set it back to true
        // setEnabled works good but the letters are greyed out.
        //mAttendanceDate.setFocusable(false);
        mAttendanceDate.setEnabled(false);

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
            if (editMode == VIEW_MODE) {

                editMode = EDIT_EXISTING;
                Log.i("AttendActivity", "1. editMode switched to " + editMode);
                enableForm();
            } else if (editMode == EDIT_NEW || editMode == EDIT_EXISTING) {
                // Check form field values
                String msg = validateForm();
                if (msg == null || msg.length() == 0) { // this means that there are no error msgs, so we can proceed
                    // Save Attendance Data
                    Attendance att = new Attendance(currentMemberId);
                    att.setAttendDate(mAttendanceDate.getText().toString());
                    att.setMemberId(currentMemberId);
                    DataHelper dataHelper = null;
                    try {
                        dataHelper = new DataHelper(AttendanceActivity.this);
                        dataHelper.open();
                        if (currentAttendanceId > -1) { // then this is an edit of an existing record
                            att.setId(currentAttendanceId);
                            currentAttendanceId = dataHelper.updateAttend(att);
                        } else { // then this is a new entry
                            currentAttendanceId = dataHelper.createAttend(att);
                        }

                        if (currentAttendanceId == -1)
                            throw new Exception("currentAttendanceId is -1, so something went wrong");
                        msgBox("Saved attendance for member " + mFullName.getText(), findViewById(android.R.id.content));
                        // set back to READ-ONLY MODE:
                        editMode = VIEW_MODE;
                        disableForm();
                        Log.i("AttendActivity", "2. editMode switched to " + editMode);

                        // close out the window and go back to AttendanceListActivity
                        Intent i = new Intent();
                        i.putExtra(MEMBER_ID, currentMemberId);
                        setResult(RESULT_OK, i);
                        finish();
                    } catch (Exception e) {
                        msgBox("Error creating Attendance Record: " + e.toString(), findViewById(android.R.id.content));
                        e.printStackTrace();
                    } finally {
                        if (dataHelper != null) dataHelper.close();

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
        } else if (id == R.id.home || id == 16908332) {
            Log.i("R.id.home", "Back Arrow on Menu fired, R.id.home value: " + R.id.home);
            // close out the window and go back to AttendanceListActivity
            Intent i = new Intent();
            i.putExtra(MEMBER_ID, currentMemberId);
            setResult(RESULT_OK, i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String validateForm() {
        List<String> msgs = new ArrayList<String>();
        String readableMsg = "";
        View focusView = null;
        boolean cancel = false;
        if (!isDateValid(mAttendanceDate.getText().toString())) {
            mAttendanceDate.setError(getString(R.string.error_invalid_membersince));
            msgs.add("Attendance Date");
            focusView = mAttendanceDate;
            cancel = true;
        }        for (String msg: msgs) {
            readableMsg = readableMsg + ", " + msg;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return (readableMsg.length() == 0)?readableMsg:readableMsg + " are invalid";
    }
    private boolean isDateValid(String date) {
        if (date == null) {
            return false;
        } else {
            String d = date.replaceAll("\\D+","");
            if (d.length() == 8) {
                try {
                    // try to parse the date based on the standard format. If there's an exception then
                    // can't continue
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT, Locale.getDefault());
                    inputDateFormat.parse(d);
                } catch (ParseException e) {
                    Log.w("isDateValid", "Date Parse Exception for " + d + ": " + e.toString());
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Log.i("MainActivity.onClick: ", "MEMBER SINCE CLICKED");
        DialogFragment msFragment = new DatePickerFragment();
        msFragment.show(getSupportFragmentManager(), "datePicker");

    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Log.i("DatePickFrag", "onDateSet Fired");
            try {
                Log.i("onDateSet", "year=" + year + "; month=" + monthOfYear + "; day=" + dayOfMonth);
                String datestring = year + String.format("%02d",(++monthOfYear)) + String.format("%02d",dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT);
                //datestring = year + "-" + String.format("%02d",(++monthOfYear)) + "-" + String.format("%02d",dayOfMonth);
                Log.i("datestring", "datestring: " + datestring);
                Date dd = sd.parse(datestring);
                Log.i("dd", "dd: " + dd);
                // update AttendanceDate
                mAttendanceDate.setText(MySqlHelper.getFormattedDate(datestring));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void msgBox(String msg, View view) {
        Log.e("AttendActivity", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
