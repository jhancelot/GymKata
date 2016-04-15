package com.example.jason.gymkata;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DataHelper dataHelper;
    ListView lvMembers;
    List<Member> memberList;
    // may not need this one anymore...
    List<String> namesList;
    ArrayAdapter<Member> adapter;
    SearchView svFilter;
    Member currentMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // LIST VIEW
        // OPEN THE DATABASE
        dataHelper = new DataHelper(this);
        try {
            dataHelper.open();
            Log.w(MainActivity.class.getName(), "Database successfully opened ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        }

        // POPULATE THE LISTVIEW WIDGET with the LastName and the FirstName out of the Members List
       // this.refreshListData();
        memberList = dataHelper.getAllMembers();
        lvMembers = (ListView) findViewById(R.id.lvMembers);
        adapter = new ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1, memberList);
        lvMembers.setAdapter(adapter);

        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position,
                                    long id) {

                //svFilter.setText(adapterView.getItemAtPosition(position) + "");
                currentMember = (Member) adapterView.getItemAtPosition(position);
                Log.e(MainActivity.class.getName(),
                        "position=" + position
                                + " and id=" + id
                                + " and adapterViewItem=" + adapterView.getItemAtPosition(position));
                Log.e("setOnItem...", "lvMembers.getSelectedItem(): " + lvMembers.getSelectedItem());
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
        Button buttGen = (Button) findViewById(R.id.buttGenerate);
        buttGen.setOnClickListener(this);

        Button buttList = (Button) findViewById(R.id.buttList);
        buttList.setOnClickListener(this);

        Button buttDelAll = (Button) findViewById(R.id.buttDelAll);
        buttDelAll.setOnClickListener(this);

        Button buttDelOne = (Button) findViewById(R.id.buttDelOne);
        buttDelOne.setOnClickListener(this);

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

    private void refreshListData() {
        Log.e("refreshListData()", "refreshing list data...");
        memberList = dataHelper.getAllMembers();
        namesList = new ArrayList<String>();
        for (Member m : memberList) {
            namesList.add(m.getLastName() + ", " + m.getFirstName());
        }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        //ArrayAdapter<Member> adapter = (ArrayAdapter<Member>) lvMembers.getAdapter();
        // OPEN THE DATABASE
        /*
        dataHelper = new DataHelper(this);
        try {
            dataHelper.open();
            Log.w(MainActivity.class.getName(), "Database successfully opened ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        }
*/
        // check which widget was clicked
        switch (v.getId()) {
            case R.id.buttGenerate:
                Log.e("MainActivity.onClick", "Generating member rows...");

                dataHelper.generateSampleData();
               // this.refreshListData();
                memberList = dataHelper.getAllMembers();
                adapter.notifyDataSetChanged();
                lvMembers.invalidate();
                lvMembers.setAdapter(adapter);
                break;
            case R.id.buttList:
                Log.e("MainActivity.onClick: ", "Listing all rows...");
                dataHelper.listAllToLog();
            //    adapter.notifyDataSetChanged();
            //    lvMembers.invalidate();
             //   lvMembers.setAdapter(adapter);
                break;
            case R.id.buttDelAll:
                Log.e("MainActivity.onClick: ", "Deleting all rows...");
                try {
                    dataHelper.deleteAllMembers();
                    memberList = dataHelper.getAllMembers();
                //    this.refreshListData();

                    adapter.notifyDataSetChanged();
                    lvMembers.invalidate();
                    lvMembers.setAdapter(adapter);
                } catch (Exception e) {
                    Log.e("MainActivity.onClick: ", "Error calling deleteAllMembers: " + e.toString());
                    e.printStackTrace();
                }
                break;
            case R.id.buttDelOne:
                Log.e("MainActivity.onClick: ", "Deleting one row...");
                try {
                    if (adapter.getCount() > 0) {

                        if (currentMember != null && currentMember.getId() > 0) {
                          //  v.animate().setDuration(2000).alpha(0)
                          //          .withEndAction(new Runnable() {
                          //              @Override
                          //              public void run() {
                                            Log.e("MainActivity.onClick: ", "Deleting " + currentMember.toString());
                                            dataHelper.deleteMember(currentMember);
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
            case R.id.fab:
                int total = dataHelper.countMembers();
                Log.e(MainActivity.class.getName(), "Total rows in database: " + total);
                Snackbar.make(v, "Total rows in database: " + total, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }

      //  dataHelper.close();

    }
}
