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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RunModeActivity extends AppCompatActivity {
    private DataHelper dataHelper;
    ListView lvMembers;
    List<Member> memberList;
    // may not need this one anymore...
    List<String> namesList;
    ArrayAdapter<Member> adapter;
    SearchView svFilter;
    Member currentMember;
    Menu runmodeMenu;
    //Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // LIST VIEW
        // OPEN THE DATABASE
        dataHelper = new DataHelper(this);
        try {
            dataHelper.openForRead();
            Log.w(RunModeActivity.class.getName(), "Database successfully opened ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(RunModeActivity.class.getName(), "Error opening database: " + e);
        }

        // POPULATE THE LISTVIEW WIDGET with the LastName and the FirstName out of the Members List
        // this.refreshListData();
        memberList = dataHelper.getAllMembers(RunModeActivity.this);
        dataHelper.close();
        lvMembers = (ListView) findViewById(R.id.lvMembers);
        adapter = new ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1, memberList);
        lvMembers.setAdapter(adapter);

        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {

                //svFilter.setText(adapterView.getItemAtPosition(position) + "");
                currentMember = (Member) adapterView.getItemAtPosition(position);
                Log.e(MainActivity.class.getName(),
                        "position=" + position
                                + " and id=" + id
                                + " and adapterViewItem=" + adapterView.getItemAtPosition(position));

            }
        });

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

        // FLOATING ACTION BAR
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the "auto increment" nature of the sql data means there's the
                // potential that the first value in the list could be a legitimate "0"
                if (currentMember != null && currentMember.getId() >= 0) {
                   // Attendance att = new Attendance(currentMember.getId(), System.currentTimeMillis());
                    Attendance att = new Attendance();
                    Log.w("TESTING", "att.getmemid: " + att.getMemberId());
                    Log.w("TESTING", "att.getDAte: " + att.getAttendDate());
                    att.setMemberId(currentMember.getId());
                    //att.setAttendDate(System.currentTimeMillis());
                    DateFormat dateFormat = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT);
                    att.setAttendDate(dateFormat.format(new Date()));
                    Log.w("TESTING2", "att.getmemid: " + att.getMemberId());
                    Log.w("TESTING2", "att.getDate: " + att.getAttendDate());
                    try {
                        long attendId = -1;
                        DataHelper dataHelper = new DataHelper(RunModeActivity.this);
                        dataHelper.open();
                        // first check to see if they have already signed in today
                        attendId = dataHelper.checkForExistingAttend(att);
                        if (attendId == -1) {
                            attendId = dataHelper.createAttend(att);
                            if (attendId == -1) throw new Exception("attendId is -1, so something went wrong");
                            snackMsg(getString(R.string.welcome_message) + " " + currentMember.getFirstName()
                                    + " " + currentMember.getLastName() + "!", view);
                        } else { // then the person is trying to sign-in twice on the same day
                            snackMsg(currentMember.getFirstName() + " " + currentMember.getLastName()
                                    + getString(R.string.warn_duplicate_signin) + " !", view);
                        }
                    } catch (Exception e) {
                        snackMsg(getString(R.string.error_signin_error), view);
                        e.printStackTrace();
                    } finally {
                        if (dataHelper != null) dataHelper.close();
                    }
                } else {
                    snackMsg(getString(R.string.warn_no_member_selected), view);
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_runmode, menu);
        this.runmodeMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_admin_login) {
            Log.i("onOption", "Admin Login Selected");
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void snackMsg(String msg, View view) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

}
