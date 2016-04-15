package com.example.jason.gymkata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(2*1000);
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);

                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    //                Snackbar.make(MainActivity.this, "Error=" + e.toString(), Snackbar.LENGTH_INDEFINITE)
                    //                      .setAction("Action", null).show();
                }
            }
        };
        t.start();

    }
}
