package com.quandary.quandary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by lim on 9/19/16.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
        finish();
    }
}
