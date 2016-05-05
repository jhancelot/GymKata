package com.example.jason.gymkata;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner spin;
    private TypedArray beltImages;
    private String[] belts;
    private String belt;
    private ImageView image;
    private Calendar cal;
    private Menu mainMenu;

    private EditText mLastName;
    private EditText mFirstName;
//    private long mDob;
    private EditText mEmail;
    private EditText mPhone;
   // private String mBeltLevel;
    private static EditText mMemberSince;

    private final static int READ_ONLY = 0;
    private final static int READ_WRITE = 1;
    private int editMode = READ_ONLY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        mFirstName = (EditText) findViewById(R.id.etFirstName);
        mLastName = (EditText) findViewById(R.id.etLastName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPhone = (EditText) findViewById(R.id.etPhone);
        // Date Picker Fragment
        mMemberSince = (EditText) findViewById(R.id.etMemberSince);
        mMemberSince.setOnClickListener(this);

        // TOOL BAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SPINNER
        belts = getResources().getStringArray(R.array.belt_level_list);
        beltImages = getResources().obtainTypedArray(R.array.belt_images_list);
        image = (ImageView) findViewById(R.id.imageBeltLevel);
        spin = (Spinner) findViewById(R.id.spBeltLevel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, belts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                image.setImageResource(beltImages.getResourceId(spin.getSelectedItemPosition(),-1));
                belt = parent.getItemAtPosition(position).toString();
                Log.i("ItemSelected", "belt: " + belt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // FLOATING ACTION BAR
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (id == R.id.action_admin_login) {
            Log.i("onOption", "Admin Login Selected");
            return true;
        }
        if (id == R.id.action_save) {
            //    MenuItem mi = (MenuItem) findViewById(R.id.action_save);
            if (editMode == READ_ONLY) {
                editMode = READ_WRITE;
                mainMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_save));
            } else {
                // Check form field values
                String msg = validateForm();
                if (msg == null || msg.length() == 0) { // this means that there are no error msgs, so we can proceed
                    // Save Member Data
                    editMode = READ_ONLY;
                    mainMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
                    Member mem = new Member(mFirstName.getText().toString(), mLastName.getText().toString(), belt);
                    if (mEmail != null && mEmail.getText().toString().length() > 0) mem.setEmail(mEmail.getText().toString());
                    if (mPhone != null && mPhone.getText().toString().length() > 0) mem.setPhoneNumber(mPhone.getText().toString());
                    try {
                        long memberId = -1;
                        memberId = mem.createMember(MemberActivity.this);
                        if (memberId == -1) throw new Exception("memberId is -1, so something went wrong");
                        msgBox("Saved!", findViewById(android.R.id.content));
                    } catch (Exception e) {
                        msgBox("Error creating Member Record: " + e.toString(),findViewById(android.R.id.content) );
                        e.printStackTrace();
                    }

                } else {
                    // use this syntax to access the current "View"
                    msgBox(msg, findViewById(android.R.id.content));
                }
            }


            Log.i("onOption", "SAVE selected");
        } else if (id == R.id.action_delete) {
            Log.i("onOption", "DELETE selected");
        }
        return super.onOptionsItemSelected(item);
    }

    private String validateForm() {
        List<String> msgs = new ArrayList<String>();
        String readableMsg = "";
        if (mFirstName.getText() == null || mFirstName.getText().length() == 0) {
            msgs.add("First Name");
        }
        if (mLastName.getText() == null || mLastName.getText().length() == 0) {
            msgs.add("Last Name");
        }
        if (mEmail.getText() == null || mEmail.getText().length() == 0) {
            msgs.add("Email");

        }
        if (mPhone.getText() == null || mPhone.getText().length() == 0) {
            msgs.add("Phone number");

        }
        for (String msg: msgs) {
            readableMsg = readableMsg + ", " + msg;
        }
        return (readableMsg.length() == 0)?readableMsg:readableMsg + " are invalid";
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
                Log.e("MainActivity.onClick", "SAVE MEMBER CLICKED");

                break;
            case R.id.buttDelOne:
                Log.e("MainActivity.onClick: ", "DELETE MEMBER CLICKED");

                break;
            case R.id.etMemberSince:
                Log.e("MainActivity.onClick: ", "MEMBER SINCE CLICKED");
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.fab:
                Log.e("MainActivity.onClick: ", "FLOATING ACTION BAR CLICKED");
                Snackbar.make(v, "FLOATING ACTION BAR CLICKED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                /*
                int total = dataHelper.countMembers();
                Log.e(MainActivity.class.getName(), "Total rows in database: " + total);
                //new MsgBox("Total members: " + total, v, MsgBox.YES_NO_BUTTON);
                msg("Total members: " + total, v, MsgBox.YES_NO_BUTTON);
                Snackbar.make(v, "Total rows in database: " + total, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
               */
                break;

        }

        //  dataHelper.close();

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
                String datestring = year + "-" + monthOfYear + "-" + dayOfMonth;
                SimpleDateFormat sd = new SimpleDateFormat(MySqlHelper.DATE_FORMAT);
                //adding one to the month
                datestring = year + "-" + String.format("%02d",(++monthOfYear)) + "-" + String.format("%02d",dayOfMonth);
                Log.i("datestring", "datestring: " + datestring);
                Date dd = sd.parse(datestring);
                Log.i("dd", "dd: " + dd);
                // Update MemberSince
                mMemberSince.setText(datestring);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void msgBox(String msg, View view) {
        Log.e("MemberActivity", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
