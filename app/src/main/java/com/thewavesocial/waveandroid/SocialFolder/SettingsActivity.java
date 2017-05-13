package com.thewavesocial.waveandroid.SocialFolder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
