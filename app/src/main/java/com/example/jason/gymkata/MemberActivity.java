package com.example.jason.gymkata;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
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
import android.text.TextWatcher;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, Constants {
    private Spinner mSpinBeltLevel;
    private TypedArray beltImages;
    private String[] belts;
    private String belt;
    private ImageView mImageBeltLevel;
    private Calendar cal;
    private Menu mainMenu;

    ArrayAdapter<String> adapter;

    private long currentMemberId;
    private Member currentMember;
    private EditText mId;
    private TextInputEditText mLastName;
    private TextInputEditText mFirstName;
//    private long mDob;
    private TextInputEditText mEmail;
    private TextInputEditText mPhone;
   // private String mBeltLevel;
    private static TextInputEditText mMemberSince;
    private static ImageButton mMemberSinceCalendar;

    private static TextInputEditText mDob;
    private static ImageButton mDobCalendar;

    private static final int DIALOG_MEMBERSINCE = 0;
    private static final int DIALOG_DOB = 1;
    private static int dialogType = DIALOG_DOB;

    private int editMode = Constants.VIEW_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        // the ID and ID Label are set as Invisible in the content xml file
        mId = (EditText) findViewById(R.id.etId);
        mFirstName = (TextInputEditText) findViewById(R.id.etFirstName);
        mLastName = (TextInputEditText) findViewById(R.id.etLastName);
        mEmail = (TextInputEditText) findViewById(R.id.etEmail);
        mPhone = (TextInputEditText) findViewById(R.id.etPhone);
        mPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // Date Picker Fragment for Member Since
        mMemberSince = (TextInputEditText) findViewById(R.id.etMemberSince);
        //if (mMemberSince != null) mMemberSince.addTextChangedListener(mDateEntryWatcher);
        mMemberSince.setOnFocusChangeListener(this);
        mMemberSinceCalendar = (ImageButton) findViewById(R.id.buttMemberSinceCalendar);
        if (mMemberSinceCalendar != null) mMemberSinceCalendar.setOnClickListener(this);

        // Date Picker Fragment for DOB
        mDob = (TextInputEditText) findViewById(R.id.etDOB);
        //if (mDob != null) mDob.addTextChangedListener(mDateEntryWatcher);
        mDob.setOnFocusChangeListener(this);
        mDobCalendar = (ImageButton) findViewById(R.id.buttDobCalendar);
        if (mDobCalendar != null) mDobCalendar.setOnClickListener(this);

        // TOOL BAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // use these two lines to add the "back arrow" to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // SPINNER
        belts = getResources().getStringArray(R.array.belt_level_list);
        beltImages = getResources().obtainTypedArray(R.array.belt_images_list);
        mImageBeltLevel = (ImageView) findViewById(R.id.imageBeltLevel);
        mSpinBeltLevel = (Spinner) findViewById(R.id.spBeltLevel);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, belts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinBeltLevel.setAdapter(adapter);
        mSpinBeltLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mImageBeltLevel.setImageResource(beltImages.getResourceId(mSpinBeltLevel.getSelectedItemPosition(), -1));
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
        editMode = i.getIntExtra(EDIT_MODE, EDIT_NEW);
        // CHECK THE VALUE OF MEMBER_ID... IF ITS NULL THEN SET TO -1
        currentMemberId = i.getLongExtra(MEMBER_ID, -1);
        Log.i("MemberActivity", "editMode=" + editMode);
        Log.i("MemberActivity", "currentMemberId=" + currentMemberId);
        DataHelper dataHelper = null;
        try {
            dataHelper = new DataHelper(MemberActivity.this);
            dataHelper.openForRead();
            Log.w(MainActivity.class.getName(), "Database successfully opened ");
            if (currentMemberId > -1) {
                currentMember = dataHelper.getMember(currentMemberId);
                populateForm(currentMember);
            } else { // make sure we're in "new" mode
                editMode = EDIT_NEW;
                currentMember = new Member();
                // default the Member Since date to today's date
                mMemberSince.setText(DataHelper.getTodaysDate(MySqlHelper.DATE_DISPLAY_FORMAT));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.class.getName(), "Error opening database: " + e);
        } finally {
            if (dataHelper != null) dataHelper.close();
        }


        // FLOATING ACTION BAR
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This method is used in conjunction with the startActivityForResult method
        // I used it to call the AttendanceListActivity class
        // If the user exits that window, we need to go back to the current member
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                editMode = data.getIntExtra(EDIT_MODE, VIEW_MODE); // DEFAULT TO VIEW IF NOT EXIST
                currentMemberId = data.getLongExtra(MEMBER_ID, -1);
                if (editMode == VIEW_MODE) {
                    try {
                        DataHelper dataHelper = new DataHelper(MemberActivity.this);
                        dataHelper.openForRead();
                        currentMember = dataHelper.getMember(currentMemberId);
                        populateForm(currentMember);
                        dataHelper.close();
                        Log.w(MainActivity.class.getName(), "Database successfully opened ");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(MainActivity.class.getName(), "Error opening database: " + e);
                    }
                } else { // ANY OTHER MODE GOES HERE
                    Log.w("onActResult", "hmmm... unexpected MODE value: " + editMode);
                }

                //adapter.notifyDataSetChanged();

            }
        }
    }
    private void populateForm(Member member) {
        mId.setText(member.getId()+"");
        mFirstName.setText(member.getFirstName());
        mLastName.setText(member.getLastName());
        mDob.setText(member.getFormattedDob());
        mPhone.setText(member.getFormattedPhone());
        mEmail.setText(member.getEmail());
        mSpinBeltLevel.setSelection(adapter.getPosition(member.getBeltLevel()));
        mMemberSince.setText(member.getFormattedMemberSince());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member, menu);
        this.mainMenu = menu;
        // CHECK EDIT MODE
        if (editMode == VIEW_MODE) {
            disableForm();
        } else { // IF IN EDIT MODE THEN DISPLAY THE SAVE BUTTON
            enableForm();
        }
        return true;
    }
    private void enableForm() {
        mFirstName.setEnabled(true);
        mLastName.setEnabled(true);
        mDob.setEnabled(true);
        mDobCalendar.setEnabled(true);
        mEmail.setEnabled(true);
        mPhone.setEnabled(true);
        mSpinBeltLevel.setEnabled(true);
        mMemberSince.setEnabled(true);
        mMemberSinceCalendar.setEnabled(true);
        // set to Checkmark icon
        mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_save));

    }

    private void disableForm() {
        mFirstName.setEnabled(false);
        mLastName.setEnabled(false);
        mDob.setEnabled(false);
        mDobCalendar.setEnabled(false);
        mEmail.setEnabled(false);
        mPhone.setEnabled(false);
        mSpinBeltLevel.setEnabled(false);
        mMemberSince.setEnabled(false);
        mMemberSinceCalendar.setEnabled(false);
        // set to Pencil icon
        mainMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_edit));

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
        if (id == R.id.action_save_or_edit) {
            Log.i("onOption", "SAVE OR EDIT selected");
            //    MenuItem mi = (MenuItem) findViewById(R.id.action_save);
            // if in "View" mode, then the pencil is displayed, so switch
            // to checkbox and enter "Edit" mode
            if (editMode == VIEW_MODE) {
                editMode = EDIT_EXISTING;
                enableForm();
            } else if (editMode == EDIT_NEW || editMode == EDIT_EXISTING) {
                // Check form field values
                if (editMode == EDIT_NEW) {
                    // default to current date but allow user to change
                    mMemberSince.setText(DataHelper.getTodaysDate(MySqlHelper.DATE_DISPLAY_FORMAT));
                }
                String msg = validateForm();
                Log.i("MbrAct", "Message returned from validateForm: " + msg);
                if (msg == null || msg.length() == 0) { // this means that there are no error msgs, so we can proceed
                    // Save Member Data
                    currentMember.setFirstName(mFirstName.getText().toString());
                    currentMember.setLastName(mLastName.getText().toString());
                    currentMember.setBeltLevel(belt);
                    Member mem = new Member(mFirstName.getText().toString(), mLastName.getText().toString(), belt);
                    if (mEmail != null && mEmail.getText().toString().length() > 0)
                        currentMember.setEmail(mEmail.getText().toString());
                    if (mPhone != null && mPhone.getText().toString().length() > 0)
                        currentMember.setPhoneNumber(mPhone.getText().toString());
                    if (mDob != null && mDob.getText().toString().length() > 0)
                        currentMember.setDob(mDob.getText().toString());
                    if (mMemberSince != null && mMemberSince.getText().toString().length() > 0)
                        currentMember.setMemberSince(mMemberSince.getText().toString());
                    try {
                        DataHelper dataHelper = new DataHelper(MemberActivity.this);
                        dataHelper.open();
                        if (currentMemberId > -1) { // then we are updating an existing record
                            currentMember.setId(currentMemberId);
                            currentMemberId = dataHelper.updateMember(currentMember);
                        } else { // then we are creating a new member
                            currentMemberId = dataHelper.createMember(currentMember);
                        }
                        dataHelper.close();

                        //memberId = mem.createMember(MemberActivity.this);
                        if (currentMemberId == -1)
                            throw new Exception("curMemberId is -1, so something went wrong");
                        if (editMode == EDIT_EXISTING || editMode == EDIT_NEW) {
                            Intent i = new Intent();
                            i.putExtra(MEMBER_ID, currentMemberId);
                            i.putExtra(EDIT_MODE, EDIT_EXISTING);
                            setResult(RESULT_OK, i);
                            MemberActivity.this.finish();
              //          } else { // editMode must be EDIT_NEW, so close the window and back to list
                            // STAY IN THIS WINDOW AND DISABLE IT
              //              editMode = Constants.VIEW_MODE;
                 //           disableForm();

                        }
                        msgBox("Saved member " + currentMember.getFirstName() + " " + currentMember.getLastName(), findViewById(android.R.id.content));

                    } catch (Exception e) {
                        msgBox("Error creating Member Record: " + e.toString(), findViewById(android.R.id.content));
                        e.printStackTrace();
                    }

                } else {
                    // use this syntax to access the current "View"
                    msgBox(msg, findViewById(android.R.id.content));
                }
            } else {
                Log.e("MemberActivity", "Warning unknown editMode: " + editMode);
            }

        } else if (id == R.id.action_attendance) {
            Log.i("onOption", "ATTENDANCE selected");
            if (currentMember != null && currentMember.getId() > -1) {
                // then continue
                try {
                    Log.i("MemberActivity", "currentMember.getid: " + currentMember.getId() + "; Last Name: " + currentMember.getLastName());

                    Intent i = new Intent(getBaseContext(), AttendanceListActivity.class);
                    i.putExtra(EDIT_MODE, EDIT_EXISTING);
                    i.putExtra(MEMBER_ID, currentMember.getId());
                    startActivityForResult(i, 1);

                } catch (Exception e) {
                    Log.e("MainActivity.Option", "Error getting Attendance: " + e.toString());
                    e.printStackTrace();
                }
            } else {
                snackMsg(getString(R.string.warning_no_member), findViewById(android.R.id.content));
            }

        } else if (id == R.id.action_delete) {
            Log.i("onOption", "DELETE selected");
            /*
            MsgBox msg = new MsgBox("OK to delete the member and all associated attendance?", MemberActivity.this);
            if (msg.getResponse() != null && msg.getResponse().equals(MsgBox.RESPONSE_YES)) {
                Log.i("MemberAct", "yes! selected");
            } else {
                Log.i("MemberAct", "no! selected");
            }
            */

            this.deleteMember();

        }
        return super.onOptionsItemSelected(item);
    }

    private String validateForm() {
        List<String> msgs = new ArrayList<>();
        String readableMsg = "";
        View focusView = null;
        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mFirstName.getText().toString()) ) {
            mFirstName.setError(getString(R.string.error_invalid_fname));
            focusView = mFirstName;
            cancel = true;
            msgs.add("First Name");
        }
        if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mLastName.setError(getString(R.string.error_invalid_lname));
            msgs.add("Last Name");
            focusView = mLastName;
            cancel = true;
        }
        Log.i("valForm", "mDob.getText().toString:" + mDob.getText().toString() + " ; isDateValid(same): " + isDateValid(mDob.getText().toString()));
        if (mDob.getText().toString().length() > 0 && !isDateValid(mDob.getText().toString())) {
            mDob.setError(getString(R.string.error_invalid_dob));
            msgs.add("Dob");
            focusView = mDob;
            cancel = true;
        }
        if (mEmail.getText().toString().length() > 0 && !isEmailValid(mEmail.getText().toString())) {
            mEmail.setError(getString(R.string.error_invalid_email));
            msgs.add("Email");
            focusView = mEmail;
            cancel = true;
        }
        if (mPhone.getText().toString().length() > 0 && !isPhoneValid(mPhone.getText().toString())) {

            mPhone.setError(getString(R.string.error_invalid_phone));
            msgs.add("Phone number");
            focusView = mPhone;
            cancel = true;
        }
        if (!isDateValid(mMemberSince.getText().toString())) {
            mMemberSince.setError(getString(R.string.error_invalid_membersince));
            msgs.add("Member Since");
            focusView = mMemberSince;
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
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        } else {
            return email.contains("@") && email.contains(".");
        }
    }
    private boolean isDateValid(String date) {
        if (date == null) {
            return false;
        } else {
            String strDate = date.replaceAll("\\D+","");
            if (strDate.length() == 8) {
                try {
                    // try to parse the date based on the standard format. If there's an exception then
                    // can't continue
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat(MySqlHelper.DATE_SQL_FORMAT, Locale.getDefault());
                    Date d = inputDateFormat.parse(strDate);
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    Log.i("isDatVal", "Year=" + c.get(Calendar.YEAR) + "; Month=" + c.get(Calendar.MONTH) + "; Day=" + c.get(Calendar.MONTH));
                    //if (d == null) throw new ParseException("Invalid Date", 0);
                } catch (ParseException e) {
                    Log.w("isDateValid", "Date Parse Exception for " + strDate + ": " + e.toString());
                    return false;
                }
            } else {
                return false;
            }

        }
        return true;
    }

    private boolean isPhoneValid(String phone) {
        if (phone == null) {
            return false;
        } else {
            if (phone.length() < 10) return false;
            // after much consideration, I've decided that the user should be encouraged
            // to format the phone number correctly, but not insist on it.
            // there may be instances (such as phone Extensions) where they
            // want to add in their own special characters
            // so just let anything through.
            // the PhoneNumberFormattingTextWatcher should help with consistency

            // Native Android package doesn't have a PARSE feature
            //PhoneNumberUtils.formatNumber(phone, DEFAULT_COUNTRY_ISO);
            // Lets use this github package instead

            /*
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Log.w("isPhoneValid", "Parsing phone number " + phone + "...");
                phoneUtil.parse(phone, DEFAULT_COUNTRY_ISO);
            } catch (NumberParseException e) {
                Log.w("isPhoneValid", "Phone Parse Exception for " + phone + ": " + e.toString());
                return false;
            }
            */

        }
        return true;
    }
    private TextWatcher mDateEntryWatcher = new TextWatcher() {
        /**
         * the idea here is to capture the keystrokes of the user and auto-format the dates
         * as they type. You didn't quite get it working, so comment out for now
         * by commenting out the listener at the top of the class
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            // YEAR
            String enteredYear;
            Log.i("TextWatcher", "working: " + working);

            if (working.length() == 4 && before == 0) {

                enteredYear = working.substring(0,4);
                Log.i("TextWatcher", "enteredYear: " + enteredYear);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(enteredYear) > currentYear) {
                    isValid = false;
                } else if (isDateValid(enteredYear + "0101")) { // check if year is valid
                    isValid = false;
                } else {
                    working += "-";
                    mMemberSince.setText(working);
                    mMemberSince.setSelection(working.length());
                }
            } else if (working.length() == 7 && before == 0) { // MONTH
                String enteredMonth = working.substring(5);
                Log.i("TextWatcher", "enteredMonth: " + enteredMonth);
                //enteredMonth = working.substring(5);
                //Log.i("TextWatcher", "enteredMonth: " + enteredMonth);
                if ((Integer.parseInt(enteredMonth) < 1 || Integer.parseInt(enteredMonth) > 12)) {
                    isValid = false;
                } else {
                    working += "-";
                    mMemberSince.setText(working);
                    mMemberSince.setSelection(working.length());
                }
            } else if (working.length() == 10 && before == 0) {  // DAY
                String enteredDay = working.substring(8);
                Log.i("TextWatcher", "enteredMonth: " + enteredDay);

                if (Integer.parseInt(enteredDay) < 1 || Integer.parseInt(enteredDay) > 31) {
                    isValid = false;
                } else {
                    mMemberSince.setText(working);
                    mMemberSince.setSelection(working.length());
                }
            } else if (working.length() != 10) {
                isValid = false;
            }

            if (!isValid) {
                mMemberSince.setError("Enter a valid date: YYYY-MM-DD");
            } else {
                mMemberSince.setError(null);
            }


        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };

    @Override
    public void onClick(View v) {

        // check which widget was clicked
        DialogFragment dpFragment;
        Bundle args;
        switch (v.getId()) {
            case R.id.buttDobCalendar:
                Log.i("MemActivity.onClick: ", "DOB Calendar CLICKED");
                dpFragment = new DatePickerFragment();
                args = new Bundle();
                args.putString("current_date", mDob.getText().toString());
                Log.i("onClick", "mDob.getText().toString() = " + mDob.getText().toString());
                dpFragment.setArguments(args);
                dialogType = DIALOG_DOB;
                dpFragment.show(getSupportFragmentManager(), "dobPicker");
                break;
            case R.id.etDOB:
                Log.i("MemActivity.onClick: ", "DOB CLICKED");
                break;
            case R.id.buttMemberSinceCalendar:
                Log.i("MemActivity.onClick: ", "MEMBER SINCE CALENDAR CLICKED");
                dpFragment = new DatePickerFragment();
                args = new Bundle();
                args.putString("current_date", mMemberSince.getText().toString());
                Log.i("onClick", "mMemberSince.getText().toString() = " + mMemberSince.getText().toString());
                dpFragment.setArguments(args);
                dialogType = DIALOG_MEMBERSINCE;
                dpFragment.show(getSupportFragmentManager(), "msPicker");
                break;
            case R.id.etMemberSince:
                Log.i("MemActivity.onClick: ", "MEMBER SINCE CLICKED");
                //msFragment = new DatePickerFragment();
                //dialogType = DIALOG_MEMBERSINCE;
                //msFragment.show(getSupportFragmentManager(), "datePicker");
                break;

        }


    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        DialogFragment dpFragment;
        if (v.getId() == R.id.etMemberSince && hasFocus) {
            Log.i("MemActivity.onClick: ", "MEMBER SINCE FOCUS CHANGE");
            dpFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("current_date", mMemberSince.getText().toString());
            dpFragment.setArguments(args);
            dialogType = DIALOG_MEMBERSINCE;
            dpFragment.show(getSupportFragmentManager(), "msPicker");
        } else if (v.getId() == R.id.etDOB && hasFocus) {
            dpFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("current_date", mDob.getText().toString());
            dpFragment.setArguments(args);
            dialogType = DIALOG_DOB;
            dpFragment.show(getSupportFragmentManager(), "dobPicker");

        }
    }
    private void deleteMember() {
        // ASSUME THE USER CANCELLED the request
        AlertDialog.Builder alert = new AlertDialog.Builder(MemberActivity.this);
        alert.setTitle(this.getTitle() + " decision");
        alert.setMessage(getString(R.string.alert_member_delete));
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("MemAct.delMem", "YES clicked...");
                try {
                    DataHelper dataHelper = new DataHelper(MemberActivity.this);
                    dataHelper.open();
                    dataHelper.deleteMember(currentMemberId);
                    dataHelper.close();

                    // set up the calling Intent (MainActivity) so that it knows to recreate the adapter
                    // for the list box
                    Intent i = new Intent();
                    i.putExtra(MEMBER_ID, currentMemberId);
                    i.putExtra(EDIT_MODE, DELETE_EXISTING);
                    setResult(RESULT_OK, i);

                    MemberActivity.this.finish();
                } catch (Exception e) {
                    Log.e("MemAct.delMem", e.toString());
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
    private void msgBox(String msg, View view) {
        Log.e("MbrActivity", msg);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
            Log.i("DatePick", "AFTER CALENDAR year = " + year + "; month = " + month + "; day = " + day);

            // Create a new instance of DatePickerDialog and return it
            // for some reason the month is offset by 1... so subtract it out
            return new DatePickerDialog(getActivity(), this, year, month - 1, day);
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
                    Log.i("onDateSet", "Setting MEMBERSINCE to " + datestring);
                    mMemberSince.setText(MySqlHelper.getFormattedDate(datestring));
                } else if (dialogType == DIALOG_DOB) {
                    Log.i("onDateSet", "Setting DOB to " + datestring);
                    mDob.setText(MySqlHelper.getFormattedDate(datestring));
                } else {
                    Log.e("onDateSet", "Error dialogType unknown: " + dialogType);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void snackMsg(String msg, View view) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

}
