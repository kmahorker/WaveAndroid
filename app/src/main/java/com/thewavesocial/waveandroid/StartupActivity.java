package com.thewavesocial.waveandroid;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.thewavesocial.waveandroid.LoginFolder.LoginActivity;

public class StartupActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);
        getSupportActionBar().hide();
        int DELAY = 5000;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Yes", "We're In!");
                Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, DELAY);
    }


}
