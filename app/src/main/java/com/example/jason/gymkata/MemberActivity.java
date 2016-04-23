package com.example.jason.gymkata;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;

public class MemberActivity extends AppCompatActivity {
    private Spinner spin;
    private TypedArray beltImages;
    private String[] belts;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // FLOATING ACTION BAR
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
