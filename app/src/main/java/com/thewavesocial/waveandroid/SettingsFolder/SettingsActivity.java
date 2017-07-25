package com.thewavesocial.waveandroid.SettingsFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.facebook.login.LoginManager;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.LoginFolder.LaunchActivity;
import com.thewavesocial.waveandroid.LoginFolder.LoginTutorialActivity;
import com.thewavesocial.waveandroid.R;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivity mainActivity;
    private View sos, privacy, service, share, logout, deleteaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mainActivity = this;

        setupActionbar();
        setupReference();
        setupOnClicks();
    }

    private void setupOnClicks() {
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, EditBestFriendActivity.class);
                startActivity(intent);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view_privacy = LayoutInflater.from(mainActivity).inflate(R.layout.document_privacy, null);
                final AlertDialog.Builder dialog_privacy = new AlertDialog.Builder(mainActivity);
                dialog_privacy.setView(view_privacy)
                        .setTitle("You have agreed to")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view_terms = LayoutInflater.from(mainActivity).inflate(R.layout.document_terms, null);
                final AlertDialog.Builder dialog_terms = new AlertDialog.Builder(mainActivity);
                dialog_terms.setView(view_terms)
                        .setTitle("You have agreed to")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
                dialog.setTitle("Logging out");
                dialog.setMessage("Are you sure you want to log out of current session?");
                dialog.setCancelable(false);
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        DatabaseAccess.saveTokentoLocal(mainActivity, "");//, "");

                        //Clear all activities and go to LoginTutorial Page
                        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        deleteaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupReference() {
        sos = findViewById(R.id.settings_sos);
        privacy = findViewById(R.id.settings_privacy);
        service = findViewById(R.id.settings_service);
        share = findViewById(R.id.settings_share);
        logout = findViewById(R.id.settings_logout);
        deleteaccount = findViewById(R.id.settings_delete_account);

        deleteaccount.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
    }

    private void setupActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_settings);

        ImageView back = (ImageView) findViewById(R.id.actionbar_settings_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
