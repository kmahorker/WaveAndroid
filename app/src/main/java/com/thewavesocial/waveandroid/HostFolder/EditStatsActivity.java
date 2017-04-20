package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

public class EditStatsActivity extends AppCompatActivity {
    private Activity mainActivity;
    private TextView deleteButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats_edit);
        mainActivity = this;
        setupActionbar();
        setupReference();
        setupFunctionality();
    }

    private void setupFunctionality() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 04/20/2017 Remove party from server 
                // TODO: 04/20/2017 Notify all users 
                // TODO: 04/20/2017 Back to hostFragment
            }
        });
    }

    private void setupReference() {
        deleteButton = (TextView) findViewById(R.id.delete_button);
    }

    private void setupActionbar() {
        getSupportActionBar().setCustomView(R.layout.actionbar_hoststats_edit);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        TextView cancelButton = (TextView) findViewById(R.id.actionbar_hoststats_edit_cancel);
        TextView doneButton = (TextView) findViewById(R.id.actionbar_hoststats_edit_done);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmMessage = new AlertDialog.Builder(mainActivity);
                confirmMessage.setTitle("Unsaved Data")
                        .setMessage("Are you sure you want to discard the changes?")
                        .setCancelable(false)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 04/20/2017 Update server
                onBackPressed();
            }
        });
    }
}
