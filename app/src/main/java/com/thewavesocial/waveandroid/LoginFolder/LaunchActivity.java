package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.DatabaseObjects.RequestComponents;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LaunchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);
        getSupportActionBar().hide();
        int DELAY = 2000;
        DatabaseAccess.saveTokentoLocal(this, "10", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxNywiaWF0IjoxNDkyODk5NDg0LCJleHAiOjE0OTU0OTE0ODR9.5lwF5yqZYummOw9qgHp0rq5SDe0eXNMpp1ebn4P9468");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = getString(R.string.server_url) + "users/"
                        + DatabaseAccess.getTokenFromLocal(LaunchActivity.this).get("id") + "?access_token="
                        + DatabaseAccess.getTokenFromLocal(LaunchActivity.this).get("jwt");
                RequestComponents comp = new RequestComponents(url, "GET", null );

                new DatabaseAccess.HttpRequestTask(LaunchActivity.this, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
                    @Override
                    public void onResultReady(ArrayList<String> result) {
                        Log.d("Launch!", result.get(0));
                        try {
                            JSONObject json = new JSONObject(result.get(0));
                            String message = json.getString("status");
                            if ( message.equals("success") ) {
                                CurrentUser.setContext(LaunchActivity.this, new OnResultReadyListener<Boolean>() {
                                    @Override
                                    public void onResultReady(Boolean result) {
                                        Intent intent = new Intent(LaunchActivity.this, HomeSwipeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else if ( message.equals("error") ) {
                                Intent intent = new Intent(LaunchActivity.this, LoginTutorialActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {e.printStackTrace();}
                    }
                }).execute();

            }
        }, DELAY);
    }
}
