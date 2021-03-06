package com.example.jason.gymkata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
        // the ID and ID Label are set as Invisible in the content xml file
        mAttendanceId = (TextView) findViewById(R.id.tvAttendId);
        mAttendanceDate = (TextInputEditText) findViewById(R.id.etAttendanceDate);
        if (mAttendanceDate != null) mAttendanceDate.setOnClickListener(this);
        mAttendanceDate.setOnClickListener(this);
        // Date Picker Fragment for DOB
        mAttDateCalendar = (ImageButton) findViewById(R.id.buttAttDateCalendar);
        if (mAttDateCalendar != null) mAttDateCalendar.setOnClickListener(this);
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
            Attendance currentAttend;
            Member currentMem;
            if (currentAttendanceId > -1) { // then we're in EDIT MODE
                currentAttend = dataHelper.getAttendanceRecord(currentAttendanceId);
                Log.i("Att.popForm", "memId:" + currentMemberId);
                Log.i("Att.popForm", "AttId:" + currentAttendanceId);
                currentMem = dataHelper.getMember(currentAttend.getMemberId());
                currentMemberId = currentAttend.getMemberId();
                Log.i("AttendDets", "currentAttend Value: " + currentAttend.getId());
                mAttendanceId.setText(currentAttend.getId() + "");
                mAttendanceDate.setText(currentAttend.getFormattedAttendDate());
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
        mAttDateCalendar.setEnabled(true);

    }

    private void disableForm() {
        mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
        // setFocusable seems to need both setFocusable and setFocusableInTouchMode to
        // set it back to true
        // setEnabled works good but the letters are greyed out.
        //mAttendanceDate.setFocusable(false);
        mAttendanceDate.setEnabled(false);
        mAttDateCalendar.setEnabled(false);

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
                    Log.i("AttAct", "Value of att.getAttDate: " + att.getAttendDate()
                            + " and att.getFormDate: " + att.getFormattedAttendDate());
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
                        snackMsg(getString(R.string.info_attendance_saved) + " "
                                + mFullName.getText(), findViewById(android.R.id.content));
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
                        snackMsg(getString(R.string.error_attendance_create) + " " + e.toString(),
                                findViewById(android.R.id.content));
                        e.printStackTrace();
                    } finally {
                        if (dataHelper != null) dataHelper.close();

                    }

                } else {
                    // use this syntax to access the current "View"
                    snackMsg(msg, findViewById(android.R.id.content));
                }
            } else {
                Log.e("AttendActivity", "Warning unknown editMode: " + editMode);
            }


        } else if (id == R.id.action_delete) {
            Log.i("onOption", "DELETE selected");
            this.deleteAttendance();
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
        List<String> msgs = new ArrayList<>();
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
        DialogFragment msFragment;
        if (v.getId() == R.id.buttAttDateCalendar || v.getId() == R.id.etAttendanceDate) {
            Log.i("AttAct.onClick: ", "ATTENDANCE DATE PICKER CLICKED");
            msFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("current_date", mAttendanceDate.getText().toString());
            Log.i("onClick", "mDob.getText().toString() = " + mAttendanceDate.getText().toString());
            msFragment.setArguments(args);
            msFragment.show(getSupportFragmentManager(), "datePicker");

        }
    }

    private void snackMsg(String msg, View view) {
        Log.e("AttendActivity", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    private void deleteAttendance() {
        // ASSUME THE USER CANCELLED the request
        AlertDialog.Builder alert = new AlertDialog.Builder(AttendanceActivity.this);
        alert.setTitle(this.getTitle() + " decision");
        alert.setMessage(getString(R.string.alert_attendance_delete));
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("AttAct.delAtt", "YES clicked...");
                try {
                    DataHelper dataHelper = new DataHelper(AttendanceActivity.this);
                    dataHelper.open();
                    dataHelper.deleteAttendance(currentAttendanceId);
                    dataHelper.close();

                    // set up the calling Intent (AttendanceListActivity) so that it knows to recreate the adapter
                    // for the list box
                    Intent i = new Intent();
                    i.putExtra(MEMBER_ID, currentMemberId);
                    i.putExtra(EDIT_MODE, DELETE_EXISTING);
                    setResult(RESULT_OK, i);

                    AttendanceActivity.this.finish();
                } catch (Exception e) {
                    Log.e("AttAct.delAtt", e.toString());
                    e.printStackTrace();

                }
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("MemAct.delMem", "NO clicked...");
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = 0;
            int month = 0;
            int day = 0;
            // Here's the bundle that contains the arguments
            // if the date is in the bundle, then use it
            Bundle args = getArguments();
            String currDate = null;
            if (args != null) currDate = args.getString("current_date");
            Log.i("DatePick", "Current Date = " + currDate);
            String currYear;
            String currMonth;
            String currDay;
            // the date has to resemble a proper date (8 digits with no mask or 10 with mask)
            if (currDate != null && currDate.length() >= 8) {
                // strip out the "-" signs and then figure out the year, month, day
                currDate = currDate.replaceAll("\\D+", "");
                currYear = currDate.substring(0, 4);
                currMonth = currDate.substring(4, 6);
                currDay = currDate.substring(6);
                //19710508
                //01234567
                Log.i("DatePick", "currMonth=" + currMonth);
                Log.i("DatePick", "currDay=" + currDay);
                if (currYear.length() == 4) year = Integer.parseInt(currYear);
                if (currMonth.length() == 2) month = Integer.parseInt(currMonth);
                if (currDay.length() == 2) day = Integer.parseInt(currDay);
                Log.i("DatePick", "BEFORE CALENDAR year = " + year + "; month = " + month + "; day = " + day);
            }

            // otherwise, Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            if (year == 0) year = c.get(Calendar.YEAR);
            if (month == 0) month = c.get(Calendar.MONTH);
            if (day == 0) day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month -1, day);
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

}
