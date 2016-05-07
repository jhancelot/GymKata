package com.example.jason.gymkata;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageButton;
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

    ArrayAdapter<String> adapter;

    private long currentMemberId;
    private EditText mId;
    private EditText mLastName;
    private EditText mFirstName;
//    private long mDob;
    private EditText mEmail;
    private EditText mPhone;
   // private String mBeltLevel;
    private static EditText mMemberSince;
    private static ImageButton mMemberSinceCalendar;

    private static EditText mDob;
    private static ImageButton mDobCalendar;

    private static final int DIALOG_MEMBERSINCE = 0;
    private static final int DIALOG_DOB = 1;
    private static int dialogType = DIALOG_DOB;

    public final static int READ_ONLY = 0;
    public final static int READ_WRITE = 1;
    public final static int EDIT_NEW = 2;
    private int editMode = READ_ONLY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        mId = (EditText) findViewById(R.id.etId);
        mFirstName = (EditText) findViewById(R.id.etFirstName);
        mLastName = (EditText) findViewById(R.id.etLastName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPhone = (EditText) findViewById(R.id.etPhone);
        // Date Picker Fragment for Member Since
        mMemberSince = (EditText) findViewById(R.id.etMemberSince);
        mMemberSinceCalendar = (ImageButton) findViewById(R.id.buttMemberSinceCalendar);
        mMemberSinceCalendar.setOnClickListener(this);

        // Date Picker Fragment for DOB
        mDob = (EditText) findViewById(R.id.etDOB);
        mDobCalendar = (ImageButton) findViewById(R.id.buttDobCalendar);
        mDobCalendar.setOnClickListener(this);

        // TOOL BAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SPINNER
        belts = getResources().getStringArray(R.array.belt_level_list);
        beltImages = getResources().obtainTypedArray(R.array.belt_images_list);
        image = (ImageView) findViewById(R.id.imageBeltLevel);
        spin = (Spinner) findViewById(R.id.spBeltLevel);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, belts);
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

        // Populate fields
        Intent i = getIntent();
        // CHECK THE VALUE OF EDIT_MODE... IF ITS NULL THEN DEFAULT TO "NEW" MODE
        editMode = i.getIntExtra("EDIT_MODE", EDIT_NEW);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentMemberId = i.getLongExtra("MEMBER_ID", -1);
        Log.i("MemberActivity", "editMode=" + editMode);
        Log.i("MemberActivity", "currentMemberId=" + currentMemberId);
        if (currentMemberId > -1) {
            try {
                DataHelper dataHelper = new DataHelper(MemberActivity.this);
                dataHelper.open();
                Member currentMem = dataHelper.getMember(currentMemberId);
                populateForm(currentMem);
                Log.w(MainActivity.class.getName(), "Database successfully opened ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(MainActivity.class.getName(), "Error opening database: " + e);
            }

        } else { // make sure we're in "new" mode
            editMode = EDIT_NEW;
        }

        // FLOATING ACTION BAR
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void populateForm(Member member) {
        mId.setText(member.getId()+"");
        mFirstName.setText(member.getFirstName());
        mLastName.setText(member.getLastName());
        mDob.setText(member.getDob());
        mPhone.setText(member.getPhoneNumber());
        mEmail.setText(member.getEmail());
        spin.setSelection(adapter.getPosition(member.getBeltLevel()));
        mMemberSince.setText(member.getMemberSince());
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
            Log.i("onOption", "SAVE selected");
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
                    if (mDob != null && mDob.getText().toString().length() > 0) mem.setDob(mDob.getText().toString());
                    if (mMemberSince != null && mMemberSince.getText().toString().length() > 0) mem.setMemberSince(mMemberSince.getText().toString());
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
            case R.id.buttDobCalendar:
                Log.e("MainActivity.onClick: ", "DOB Calendar CLICKED");
                DialogFragment dobFragment = new DatePickerFragment();
                dialogType = DIALOG_DOB;
                dobFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.buttMemberSinceCalendar:
                Log.e("MainActivity.onClick: ", "MEMBER SINCE CLICKED");
                DialogFragment msFragment = new DatePickerFragment();
                dialogType = DIALOG_MEMBERSINCE;
                msFragment.show(getSupportFragmentManager(), "datePicker");
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
                // Update MemberSince OR DOB
                if (dialogType == DIALOG_MEMBERSINCE) {
                    mMemberSince.setText(MySqlHelper.getFormattedDate(datestring));
                } else {
                    mDob.setText(MySqlHelper.getFormattedDate(datestring));
                }

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
