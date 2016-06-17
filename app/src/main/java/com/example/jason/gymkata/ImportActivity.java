package com.example.jason.gymkata;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class ImportActivity extends AppCompatActivity
        implements View.OnClickListener, Constants, MsgBox.NoticeDialogListener {

    private String path = null;
    List<File> fileList;
    ListView lvFiles;
    ArrayAdapter<File> fileAdapter;
    ArrayAdapter<String> typesAdapter;
    File currentFile;
    private String response = null;
    private Spinner mImportType = null;
    private String[] importTypes = null;

    private ProgressBar mProgBar = null;
    private int mProgress = 0;
    private Handler mHandler = new Handler();

    private boolean filePermissionGranted = false;
    private int reportType = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // MsgBox unique tags
    private String msgBoxTag = null;
    private static final String REPORT_TYPE_MISMATCH = "ReportTypeMismatch";
    private static final String READY_TO_IMPORT = "ReadyToImport";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        File importFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        path = importFolder.getPath();

        // Load the screen data
        this.refreshListData();

        // Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                snackMsg(getString(R.string.starting_import), v);
                reportType = mImportType.getSelectedItemPosition();
                if (reportType  == MEMBER_REPORT || reportType == MEMBERS_PLUS_TOTALS
                        || reportType == ATTENDANCE_REPORT) {
                    if (currentFile != null) {
                        Log.i("onclick", "currentFile? " + currentFile.getName());
                        Log.i("onclick", "currentFile contained members? " + currentFile.getName().toUpperCase().contains("MEMBERS"));
                        if ((reportType == MEMBER_REPORT && !currentFile.getName().toUpperCase().contains("MEMBERS")) ||
                                (reportType == ATTENDANCE_REPORT && !currentFile.getName().toUpperCase().contains("ATTENDANCE"))) {
                            //alertBox("The file you selected doesn't match the report type selected. Are you sure?", v, "YES_NO");
                            DialogFragment dialog = new MsgBox();
                            Bundle args = new Bundle();
                            args.putString(MsgBox.TITLE, getString(R.string.title_activity_import));
                            args.putString(MsgBox.MESSAGE, getString(R.string.report_type_mismatch));
                            dialog.setArguments(args);
                            // the second argument is a unique tag name used to restore the fragment
                            // state when necessary. You can also get a handle to the fragment by
                            // calling findFragmentByTag()
                            dialog.show(getFragmentManager(), REPORT_TYPE_MISMATCH);
                            msgBoxTag = REPORT_TYPE_MISMATCH;
                        } else {
//                            Log.i("ImportAct", "showing progress bar..." + System.currentTimeMillis());
//                            mProgBar.setVisibility(View.VISIBLE);


                            // report type more or less matches the file selected, so confirm
                            // If 'yes', then the actual call to the ImportData will occur in the
                            // onDialogPositiveClick method below
                            DialogFragment dialog = new MsgBox();
                            Bundle args = new Bundle();
                            args.putString(MsgBox.TITLE, getString(R.string.title_activity_import));
                            args.putString(MsgBox.MESSAGE, getString(R.string.ready_to_import) + " " + currentFile.getName() + "?");
                            dialog.setArguments(args);
                            // the second argument is a unique tag name used to restore the fragment
                            // state when necessary. You can also get a handle to the fragment by
                            // calling findFragmentByTag()
                            dialog.show(getFragmentManager(), READY_TO_IMPORT);
                            msgBoxTag = READY_TO_IMPORT;
/*
                            // Pretend like the process takes a while
                            try {
                                Thread.sleep(SLEEP_VALUE);
                            } catch (InterruptedException e) {
                                Log.i("ImportAct", "Error running thread.sleep: " + e.toString());
                                e.printStackTrace();
                            }
                            Log.i("ImportAct", "mProgress = " + mProgress);
                            Log.i("ImportAct", "hiding progress bar..."+ System.currentTimeMillis());
                            mProgBar.setVisibility(View.GONE);
*/

                        }
                    } else {
                        snackMsg(getString(R.string.no_file_selected), v);
                    }
                } else {
                    snackMsg(getString(R.string.no_report_type_selected), v);
                }
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActResult", "requestCode = " + requestCode + " and resultCode = " + resultCode);

    }
    private void refreshListData() {
        Log.e("refreshListData()", "refreshing list data...");
        // This is to load the List Box with Files from the Downloads folder
        DataHelper dataHelper = null;
        try {
            dataHelper = new DataHelper(this);
            Log.i(MainActivity.class.getName(), "Database successfully opened ");
            // POPULATE THE LISTVIEW WIDGET with the LastName and the FirstName out of the Members List
            verifyStoragePermissions(ImportActivity.this);
            if (filePermissionGranted) {
                fileList = dataHelper.getEligibleFiles();
            } else {
                fileList = new ArrayList<File>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening file system: " + e);
        } finally {
            if (dataHelper != null) dataHelper = null;
        }

        lvFiles = (ListView) findViewById(R.id.lvFiles);
        fileAdapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, fileList);
        lvFiles.setAdapter(fileAdapter);
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position,
                                    long id) {

                //svFilter.setText(adapterView.getItemAtPosition(position) + "");
                currentFile = (File) adapterView.getItemAtPosition(position);
                //currentPosition = position;
                Log.e(MainActivity.class.getName(),
                        "position=" + position
                                + " and id=" + id
                                + " and adapterViewItem=" + adapterView.getItemAtPosition(position));

                if (currentFile == null) {
                    snackMsg(getString(R.string.warning_no_member), findViewById(android.R.id.content));
                }


            }
        });

        // This is to load the Spinner (drop-down) with report types
        // SPINNER
        importTypes = getResources().getStringArray(R.array.report_types);
        mImportType = (Spinner) findViewById(R.id.spImportType);
        typesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, importTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mImportType.setAdapter(typesAdapter);
        mProgBar = (ProgressBar) findViewById(R.id.progressBar);


    }
    private void snackMsg(String msg, View view) {
        Log.e("ExportAct", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        Log.i("verifyPerm", "Checking to see if we already have permissions...");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
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
                    snackMsg(getString(R.string.permissions_error), findViewById(android.R.id.content));
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

    // Both of these methods are needed to implement the MsgBox Dialog Box (Alert Dialog) class
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // user clicked the Positive button
        Log.i("onDiaPos", "msgBoxTag value is " + msgBoxTag);
        if (msgBoxTag != null) {
            if (msgBoxTag.equals(READY_TO_IMPORT) || msgBoxTag.equals(REPORT_TYPE_MISMATCH)) {
                // start the progress bar
                showProgress(true);
                // do the work
                new DoImport().execute();


            }
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // user clicked the Negative button
        Log.i("onDia", "onDialogNegativeClick");
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
    public class DoImport extends AsyncTask<Void, Void, Boolean> {


        DoImport() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Log.i("doInBackground", "simulating doing work...");
                Thread.sleep(SLEEP_VALUE);
                // Kick off the import process
                ImportData importData = new ImportData(ImportActivity.this, reportType);
                importData.setFileName(currentFile.getName());
                importData.setPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                importData.execute();



            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                snackMsg(getString(R.string.info_import_complete), findViewById(android.R.id.content));
            } else {
                snackMsg(getString(R.string.error_unknown_import), findViewById(android.R.id.content));
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
