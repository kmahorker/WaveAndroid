package com.thewavesocial.waveandroid.LoginFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.R;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "Launch test";
    private int DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: startup success");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);
        getSupportActionBar().hide();
        DatabaseAccess.saveCurrentUserId("123");//, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxNywiaWF0IjoxNDkyODk5NDg0LCJleHAiOjE0OTU0OTE0ODR9.5lwF5yqZYummOw9qgHp0rq5SDe0eXNMpp1ebn4P9468");

        enterApp();
    }

    private void showDocuments() {
        View view_privacy = LayoutInflater.from(this).inflate(R.layout.document_privacy, null);
        View view_terms = LayoutInflater.from(this).inflate(R.layout.document_terms, null);

        final AlertDialog.Builder dialog_privacy = new AlertDialog.Builder(this);
        final AlertDialog.Builder dialog_terms = new AlertDialog.Builder(this);
        dialog_privacy.setView(view_privacy).setCancelable(false)
                .setPositiveButton("I have read and accept.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialog_terms.show();
                    }
                }).show();
        dialog_terms.setView(view_terms).setCancelable(false)
                .setPositiveButton("I have read and accept.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(LaunchActivity.this, LoginTutorialActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void enterApp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.i("Logging in", "run: attempting to login ");
//                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
//                db.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.hasChild(DatabaseAccess.getCurrentUserId())){
//                            CurrentUser.loadBestFriends(new OnResultReadyListener<Boolean>() {
//                                @Override
//                                public void onResultReady(Boolean result) {
//                                    Intent intent = new Intent(LaunchActivity.this, HomeSwipeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
//                        }
//                        else
//                            showDocuments();
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
/*                String url = getString(R.string.server_url) + "users/"
                        + DatabaseAccess.getCurrentUserId(LaunchActivity.this).get("id") + "?access_token="
                        + DatabaseAccess.getCurrentUserId(LaunchActivity.this).get("jwt");
                RequestComponents comp = new RequestComponents(url, "GET", null);

                new DatabaseAccess.HttpRequestTask(LaunchActivity.this, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
                    @Override
                    public void onResultReady(ArrayList<String> result) {
                        Log.d("Launch!", result.get(0));
                        try {
                            JSONObject json = new JSONObject(result.get(0));
                            String message = json.getString("status");
                            if (message.equals("success")) {
                                CurrentUser.loadBestFriends(LaunchActivity.this, new OnResultReadyListener<Boolean>() {
                                    @Override
                                    public void onResultReady(Boolean result) {
                                        Intent intent = new Intent(LaunchActivity.this, HomeSwipeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else if (message.equals("error")) {
                                showDocuments();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute();*/

            }
        }, DELAY);
    }
}
