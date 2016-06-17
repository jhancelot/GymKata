package com.example.jason.gymkata;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportActivity extends AppCompatActivity implements View.OnClickListener,Constants {
    private static TextInputEditText mStartDate;
    private static TextInputEditText mEndDate;
    private static ImageButton mStartDateCalendar;
    private static ImageButton mEndDateCalendar;
    private static CheckBox mExportMembers;
    private static CheckBox mIncludeAttTotals;
    private static CheckBox mExportAttendance;
    private ProgressBar mProgBar = null;

    private static final int DIALOG_START_DATE = 0;
    private static final int DIALOG_END_DATE = 1;
    private static int dialogType = DIALOG_START_DATE;
    // Storage Permissions
    boolean filePermissionGranted = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStartDate = (TextInputEditText) findViewById(R.id.etStartDate);
        mEndDate = (TextInputEditText) findViewById(R.id.etEndDate);
        // Date Picker Fragment for Start and End Dates
        mStartDateCalendar = (ImageButton) findViewById(R.id.buttStartDateCalendar);
        mEndDateCalendar = (ImageButton) findViewById(R.id.buttEndDateCalendar);
        mStartDateCalendar.setOnClickListener(this);
        mEndDateCalendar.setOnClickListener(this);

        // Checkboxes
        mExportMembers = (CheckBox) findViewById(R.id.cbExportMembers);
        mExportMembers.setOnClickListener(this);
        mIncludeAttTotals = (CheckBox) findViewById(R.id.cbIncludeAttTotals);
        mIncludeAttTotals.setOnClickListener(this);
        mIncludeAttTotals.setEnabled(false);
        mExportAttendance = (CheckBox) findViewById(R.id.cbExportAttendance);

        mProgBar = (ProgressBar) findViewById(R.id.progressBar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // this command adds the Back arrow to the Action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttStartDateCalendar:
                Log.i("Export.onClick: ", "START CALEANDAR CLICKED");
                DialogFragment sDateFragment = new DatePickerFragment();
                dialogType = DIALOG_START_DATE;
                sDateFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.buttEndDateCalendar:
                Log.i("Export.onClick: ", "END CALEANDAR CLICKED");
                DialogFragment eDateFragment = new DatePickerFragment();
                dialogType = DIALOG_END_DATE;
                eDateFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.cbExportMembers:
                if (mExportMembers.isChecked()) {
                    mIncludeAttTotals.setEnabled(true);
                } else {
                    mIncludeAttTotals.setChecked(false);
                    mIncludeAttTotals.setEnabled(false);
                }
                break;
            case R.id.fab:
                Log.e("Export.onClick: ", "FLOATING ACTION BAR CLICKED");
                // Make sure we have permission
                verifyStoragePermissions(ExportActivity.this);
                if (filePermissionGranted) {
                    Log.i("onNavItem", "File Permissions are granted. Export can begin...");
                    String msg = validateForm();
                    Log.i("MbrAct", "Message returned from validateForm: " + msg);

                    if (msg == null || msg.length() == 0) { // this means that there are no error msgs, so we can proceed
                        snackMsg(getString(R.string.info_export_data), findViewById(android.R.id.content));
                        Log.i("ExportAct", "simulating exporting...");
                        // start the progress bar
                        showProgress(true);

                        // CALL DO WORK HERE
                        DoExport exp = new DoExport(mExportMembers.isChecked(),mIncludeAttTotals.isChecked(), mExportAttendance.isChecked() );
                        exp.setStartDate(mStartDate.getText().toString());
                        exp.setEndDate(mEndDate.getText().toString());
                        exp.execute();



                    } else {
                        // use this syntax to access the current "View"
                        snackMsg(msg, findViewById(android.R.id.content));
                    }
                } else {
                    snackMsg(getString(R.string.error_file_permissions), findViewById(android.R.id.content));

                }


                break;

        }
    }
    private void snackMsg(String msg, View view) {
        Log.e("ExportAct", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    private String validateForm() {
        List<String> msgs = new ArrayList<String>();
        String readableMsg = "";
        View focusView = null;
        boolean cancel = false;
        if (mStartDate.getText().toString().length() > 0 && !isDateValid(mStartDate.getText().toString())) {
            mStartDate.setError(getString(R.string.error_invalid_startdate));
            msgs.add("Start Date");
            focusView = mStartDate;
            cancel = true;
        }

        if (mEndDate.getText().toString().length() > 0 && !isDateValid(mEndDate.getText().toString())) {
            mEndDate.setError(getString(R.string.error_invalid_enddate));
            msgs.add("End Date");
            focusView = mEndDate;
            cancel = true;
        }
        for (String msg: msgs) {
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
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        Log.i("verifyPerm", "Checking to see if we already have permissions...");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i("verifyPerm", "Check done, result permission = " + permission);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            Log.i("verifyPerm", "We are past the requestPermissions call...");
        } else {
            filePermissionGranted = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // file-related task you need to do.
                    filePermissionGranted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    filePermissionGranted = false;
                    snackMsg(getString(R.string.error_file_permissions),
                            findViewById(android.R.id.content));
                }
                return;

            }
            default: {
                Log.w("onReqPerm", "Warning, other requestCode not caught: " + requestCode);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                if (dialogType == DIALOG_START_DATE) {
                    mStartDate.setText(MySqlHelper.getFormattedDate(datestring));
                } else { // ANYTHING ELSE MUST BE END DATE
                    mEndDate.setText(MySqlHelper.getFormattedDate(datestring));
                }

            } catch (ParseException e) {
                Log.e("Export.DateSet", "Error setting date: " + e.toString());
                e.printStackTrace();
            }
        }
    }
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    /**
     * Represents an asynchronous task used to show the progress bar
     */
    public class DoExport extends AsyncTask<Void, Void, Boolean> {
        String startDate;
        String endDate;
        boolean exportMembers, exportTotals, exportAttendance;


        DoExport() {
        }

        DoExport(boolean exportMem, boolean exportTot, boolean exportAttend) {
            this.exportMembers = exportMem;
            this.exportTotals = exportTot;
            this.exportAttendance = exportAttend;
        }
        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
                // Simulate doing work
                Log.i("doInBackground", "simulating doing work...");
                // Kick off the export process
                try {
                    Thread.sleep(SLEEP_VALUE);

                    if (exportMembers) {
                        ExportData exportMembers = new ExportData(ExportActivity.this, MEMBER_REPORT);
                        exportMembers.execute();
                    }
                    if (exportAttendance) {
                        ExportData exportAttendance = new ExportData(ExportActivity.this, ATTENDANCE_REPORT);
                        exportAttendance.setStartDate(startDate);
                        exportAttendance.setEndDate(endDate);
                        exportAttendance.execute();
                    }
                    if (exportTotals) {
                        ExportData exportAttendance = new ExportData(ExportActivity.this, MEMBERS_PLUS_TOTALS);
                        exportAttendance.setStartDate(startDate);
                        exportAttendance.setEndDate(endDate);
                        exportAttendance.execute();
                    }
                } catch (InterruptedException e) {
                    Log.e("ExportAct", "error executing sleep cycle: " + e.toString());
                    e.printStackTrace();
                    return false;
                }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                snackMsg(getString(R.string.info_export_complete) + " "
                                + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        findViewById(android.R.id.content));
            } else {
                snackMsg(getString(R.string.error_unknown_export), findViewById(android.R.id.content));
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
