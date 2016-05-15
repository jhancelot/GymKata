package com.example.jason.gymkata;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Constants {

    //private DataHelper dataHelper;
    ListView lvMembers;
    List<Member> memberList;
    // may not need this one anymore...
    List<String> namesList;
    ArrayAdapter<Member> adapter;
    //int currentPosition = 0;
    SearchView svFilter;
    Member currentMember;
    Menu mainMenu;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // LIST VIEW
        // OPEN THE DATABASE
        // POPULATE THE LISTVIEW WIDGET with the LastName and the FirstName out of the Members List
        this.refreshListData();

        // SEARCH VIEW FIELD
        svFilter = (SearchView) findViewById(R.id.svNameFilter);
        svFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("onQueryTextSubmit", "query=" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("onQueryTextChange", "newText=" + newText);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        // try closing the database (a website said this may help refresh the listview widget
      //  dataHelper.close();

        // Add the buttons
        Button buttList = (Button) findViewById(R.id.buttList);
        buttList.setOnClickListener(this);

        Button buttDelAll = (Button) findViewById(R.id.buttDelAll);
        buttDelAll.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_admin_logout) {
            Log.i("onOption", "Admin Logout Selected");
            SharedPreferences sharedPrefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(LOGIN_STATE, LOGGED_OUT);

            Intent i = new Intent(getBaseContext(), RunModeActivity.class);
            startActivity(i);
            finishAndRemoveTask();
            return true;
        } else if (id == R.id.action_save_or_edit) {
            //    MenuItem mi = (MenuItem) findViewById(R.id.action_save);

            //    mainMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
            Log.i("onOption", "SAVE OR EDIT selected");
            if (currentMember != null && currentMember.getId() > -1) {
                // then continue
                Log.i("CurrentMem", "id: " + currentMember.getId() + "; Last Name: " + currentMember.getLastName());
                Intent i = new Intent(getBaseContext(), MemberActivity.class);
                i.putExtra(EDIT_MODE, VIEW_MODE);
                i.putExtra(MEMBER_ID, currentMember.getId());
                startActivityForResult(i, 1);
            } else {
                snackMsg(getString(R.string.warning_no_member), findViewById(android.R.id.content));
            }


        } else if (id == R.id.action_delete) {
            Log.i("onOption", "DELETE selected");
            try {
                //adapter = new ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1, memberList);
                if (adapter.getCount() > 0 && adapter.getPosition(currentMember) > 0) {
                    try {
                        Log.i("CurrentMem", "id: " + currentMember.getId() + "; Last Name: " + currentMember.getLastName());
                        // DELETE CODE HERE
                        // SHOULD WE BE DELETING THE ASSOCIATED ATTENDANCE RECORDS AS WELL?
                        //currentMember.delete(MainActivity.this);
                        adapter.remove(currentMember);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("MainActivity.Option", "Error deleting member: " + e.toString());
                        e.printStackTrace();
                    }
                } else {
                    snackMsg(getString(R.string.warning_no_member), findViewById(android.R.id.content));

                }


            } catch (Exception e) {
                Log.e("onOptionSel", "Error deleting member: " + e.toString());
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                long mode = data.getIntExtra(EDIT_MODE, VIEW_MODE); // DEFAULT TO VIEW IF NOT EXIST
                long currentMemberId = -1;
                if (mode == DELETE_EXISTING) {
                    this.refreshListData();
                } else if (mode == EDIT_EXISTING || mode == EDIT_NEW) {
                    Log.i("onActResult", "currentMemberId: " + currentMemberId);
                    currentMemberId = data.getLongExtra(MEMBER_ID, -1);
                    this.refreshListData();
                } else { // ANY OTHER MODE GOES HERE
                    Log.w("onActResult", "hmmm... unexpected MODE value: " + mode);
                }

                //adapter.notifyDataSetChanged();

            }
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_generate_members) {
            // Handle Login
            Log.e("MainActivity.onClick", "Generating member rows...");
            DataHelper dataHelper = new DataHelper(this);
            try {
                dataHelper.open();
                dataHelper.generateSampleData(MainActivity.this);
                dataHelper.close();
                Log.w(MainActivity.class.getName(), "Database successfully opened ");
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(MainActivity.class.getName(), "Error opening database: " + e);
            }
            this.refreshListData();
        } else if (id == R.id.nav_runmode) {
            // Handle the RUN MODE action
            Intent i = new Intent(getBaseContext(), RunModeActivity.class);
            startActivity(i);
            Log.i("OnNav","RUN MODE");

        } else if (id == R.id.nav_members) {
            Log.i("OnNav","EDIT MEMBERS");
            Intent i = new Intent(getBaseContext(), MemberActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_attendance) {
            Log.i("OnNav","EDIT ATTENDANCE");
            Intent i = new Intent(getBaseContext(), AttendanceListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_import) {
            Intent i = new Intent(getBaseContext(), TestDatePickerActivity.class);
            startActivity(i);
            Log.i("OnNav","IMPORT");

        } else if (id == R.id.nav_export) {
            Log.i("OnNav", "EXPORT");
            // Make sure we have permission
            verifyStoragePermissions(MainActivity.this);
            if (filePermissionGranted) {
                Log.i("onNavItem", "File Permissions are granted. Export can begin...");
                ExportData exportData = new ExportData(MainActivity.this, MySqlHelper.TABLE_MEMBER);
                exportData.execute();
            } else {
                snackMsg("You need to grant file permissions to perform this function",findViewById(android.R.id.content) );
                Log.w("onNavItem", "Warning... no file permissions granted. No export possible.");
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        //ArrayAdapter<Member> adapter = (ArrayAdapter<Member>) lvMembers.getAdapter();
        // OPEN THE DATABASE

        DataHelper dataHelper = new DataHelper(this);
        try {
            dataHelper.open();
            Log.w(MainActivity.class.getName(), "Database successfully opened ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        }

        // check which widget was clicked
        switch (v.getId()) {
            case R.id.buttList:
                Log.e("MainActivity.onClick: ", "Listing all rows...");
                dataHelper.listMembersToLog(MainActivity.this);
                dataHelper.listAttendanceToLog();
            //    adapter.notifyDataSetChanged();
            //    lvMembers.invalidate();
             //   lvMembers.setAdapter(adapter);
                break;
            case R.id.buttDelAll:
                Log.e("MainActivity.onClick: ", "Deleting all rows...");
                try {
                    dataHelper.deleteAllMembers();
                    memberList = dataHelper.getAllMembers(MainActivity.this);
                //    this.refreshListData();

                    adapter.notifyDataSetChanged();
                    lvMembers.invalidate();
                    lvMembers.setAdapter(adapter);
                } catch (Exception e) {
                    Log.e("MainActivity.onClick: ", "Error calling deleteAllMembers: " + e.toString());
                    e.printStackTrace();
                }
                break;

/*            case R.id.buttDelOne:
                Log.e("MainActivity.onClick: ", "Deleting one row...");
                try {
                    if (adapter.getCount() > 0) {

                        if (currentMember != null && currentMember.getId() > 0) {
                          //  v.animate().setDuration(2000).alpha(0)
                          //          .withEndAction(new Runnable() {
                          //              @Override
                          //              public void run() {
                                            Log.e("MainActivity.onClick: ", "Deleting " + currentMember.toString());
                                            dataHelper.deleteMember(currentMember.getId());
                                            adapter.remove(currentMember);

                          //              }
                          //          });
                        } else {
                            Snackbar.make(v, "Nothing selected, so nothing to delete!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        //adapter.notifyDataSetChanged();
                    } else {
                        Log.e("MainActivity.onclick", "Adapater.getCount equals 0");
                    }
                } catch (Exception e) {
                    Log.e("MainActivity.onClick: ", "Error calling deleteOneMember: " + e.toString());
                    e.printStackTrace();
                }
                break;
*/
            case R.id.fab:
                Intent i = new Intent(getBaseContext(), MemberActivity.class);
                i.putExtra(EDIT_MODE, EDIT_NEW);
                startActivityForResult(i, 1);


                /*int total = dataHelper.countMembers();
                Log.e(MainActivity.class.getName(), "Total rows in database: " + total);
                //new MsgBox("Total members: " + total, v, MsgBox.YES_NO_BUTTON);
                msg("Total members: " + total, v, MsgBox.YES_NO_BUTTON);
                Snackbar.make(v, "Total rows in database: " + total, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                break;

        }

        //if (dataHelper != null) dataHelper.close();

    }

    private void refreshListData() {
        Log.e("refreshListData()", "refreshing list data...");
        DataHelper dataHelper = new DataHelper(this);
        try {
            dataHelper.openForRead();
            Log.i(MainActivity.class.getName(), "Database successfully opened ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        }

        // POPULATE THE LISTVIEW WIDGET with the LastName and the FirstName out of the Members List
        memberList = dataHelper.getAllMembers(this);
        dataHelper.close();
        lvMembers = (ListView) findViewById(R.id.lvMembers);
        adapter = new ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1, memberList);

        lvMembers.setAdapter(adapter);

        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position,
                                    long id) {

                //svFilter.setText(adapterView.getItemAtPosition(position) + "");
                currentMember = (Member) adapterView.getItemAtPosition(position);
                //currentPosition = position;
                Log.e(MainActivity.class.getName(),
                        "position=" + position
                                + " and id=" + id
                                + " and adapterViewItem=" + adapterView.getItemAtPosition(position));
                Log.e("CurrentMem", "id: " + currentMember.getId() + "; Last Name: " + currentMember.getLastName());
                if (currentMember != null && currentMember.getId() > -1) {
                    // then continue
                    Log.i("CurrentMem", "id: " + currentMember.getId() + "; Last Name: " + currentMember.getLastName());
                    Intent i = new Intent(getBaseContext(), MemberActivity.class);
                    i.putExtra(EDIT_MODE, VIEW_MODE);
                    i.putExtra(MEMBER_ID, currentMember.getId());
                    startActivityForResult(i, 1);
                } else {
                    snackMsg(getString(R.string.warning_no_member), findViewById(android.R.id.content));
                }


                // This code removes an entry from the list with a "fade" effect
                /*
                  v.animate().setDuration(2000).alpha(0)
                          .withEndAction(new Runnable() {
                              @Override
                              public void run() {
                dataHelper.deleteMember(currentMember);
                adapter.remove(currentMember);

                              }
                          });
                */

            }
        });
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
                    filePermissionGranted = true;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            filePermissionGranted = true;
        }
    }

    private void snackMsg(String msg, View view) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }


    private void msgBox(String msg, View view, String buttonType) {
        String OK_BUTTON = "OK";
        String YES_NO_BUTTON = "YES_NO";
        String DEFAULT_MSG = "NO MESSAGE SET";
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg).setCancelable(false);
        if (buttonType != null && buttonType.equals(OK_BUTTON)) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        } else if (buttonType.equals(YES_NO_BUTTON)) {
            Log.i("msgBox", "buttonType=" + buttonType);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //MainActivity.this.finish();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        } else {
            builder.setNeutralButton(OK_BUTTON, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();

    }
}
