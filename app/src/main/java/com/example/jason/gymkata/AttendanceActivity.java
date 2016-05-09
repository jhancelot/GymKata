package com.example.jason.gymkata;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.sql.SQLException;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    List<Attendance> attendanceList;
    ArrayAdapter<Attendance> adapter;
    private long currentMemberId;
    public final static int VIEW_MODE = 0;
    public final static int EDIT_EXISTING = 1;
    public final static int EDIT_NEW = 2;
    private int editMode = VIEW_MODE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SEARCH VIEW FIELD
        searchView = (SearchView) findViewById(R.id.svAttendByName);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        // Populate fields
        Intent i = getIntent();
        // CHECK THE VALUE OF EDIT_MODE... IF ITS NULL THEN DEFAULT TO "NEW" MODE
        editMode = i.getIntExtra("EDIT_MODE", EDIT_NEW);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentMemberId = i.getLongExtra("MEMBER_ID", -1);
        Log.i("MemberActivity", "editMode=" + editMode);
        Log.i("MemberActivity", "currentMemberId=" + currentMemberId);

        // LIST VIEW
        // POPULATE THE LISTVIEW WIDGET
        // this.refreshListData();
        try {
            DataHelper dataHelper = new DataHelper(AttendanceActivity.this);
            dataHelper.open();
            if (currentMemberId > -1) {
                attendanceList = dataHelper.getAllAttendance(currentMemberId, AttendanceActivity.this);
            } else {
                attendanceList = dataHelper.getAllAttendance(AttendanceActivity.this);
            }
            dataHelper.close();
        } catch (Exception e) {
            Log.e("AttActivity", "Error getting attendance: " + e.toString());
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.lvAttendance);
        adapter = new ArrayAdapter<Attendance>(this, android.R.layout.simple_list_item_1, attendanceList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position,
                                    long id) {

                Log.e(MainActivity.class.getName(),
                        "position=" + position
                                + " and id=" + id
                                + " and adapterViewItem=" + adapterView.getItemAtPosition(position));

                // FLOATING ACTION BUTTON
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        });
    }
}