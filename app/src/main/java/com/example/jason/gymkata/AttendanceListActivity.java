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

import java.util.List;

public class AttendanceListActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    List<Attendance> attendanceList;
    ArrayAdapter<Attendance> adapter;
    private long currentMemberId;
    private int editMode = Constants.VIEW_MODE;
    private Menu mainMenu;

    private Attendance currentAttendance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        // Tool Bar
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
        editMode = i.getIntExtra("EDIT_MODE", Constants.EDIT_NEW);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentMemberId = i.getLongExtra("MEMBER_ID", -1);
        Log.i("MemberActivity", "editMode=" + editMode);
        Log.i("MemberActivity", "currentMemberId=" + currentMemberId);

        // LIST VIEW
        // POPULATE THE LISTVIEW WIDGET
        // this.refreshListData();
        try {
            DataHelper dataHelper = new DataHelper(AttendanceListActivity.this);
            dataHelper.open();
            if (currentMemberId > -1) {
                attendanceList = dataHelper.getAllAttendance(currentMemberId, AttendanceListActivity.this);
            } else {
                attendanceList = dataHelper.getAllAttendance(AttendanceListActivity.this);
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
                currentAttendance = (Attendance) adapterView.getItemAtPosition(position);
                Log.e("Currentatt", "id: " + currentAttendance.getId() + "; MemberId: " + currentAttendance.getMemberId());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_admin_login) {
            Log.i("onOption", "Admin Login Selected");
            return true;
        } else if (id == R.id.action_save_or_edit) {
            //    MenuItem mi = (MenuItem) findViewById(R.id.action_save);

            //    mainMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
            Log.i("onOption", "SAVE OR EDIT selected");
            Log.i("CurrentAtt", "id: " + currentAttendance.getId() + "; Curr Mem: " + currentAttendance.getMemberId());
            Intent i = new Intent(getBaseContext(), AttendanceActivity.class);
            i.putExtra("EDIT_MODE", Constants.VIEW_MODE);
            i.putExtra("ATTENDANCE_ID", currentAttendance.getId());
            startActivity(i);


        }

        return super.onOptionsItemSelected(item);
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