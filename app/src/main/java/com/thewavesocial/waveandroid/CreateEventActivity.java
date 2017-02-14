package com.thewavesocial.waveandroid;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    public String location, name, privatePublic, paidFree;
    public Calendar startCalendar, endCalendar;
    public int maleCount, femaleCount;
    public double price;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        setContentView(R.layout.create_event_layout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.createEvent_fragment_container, new CreateEvent1Fragment())
                .addToBackStack(null).commit();

        location = "";
        name = "";
        privatePublic = "Private";
        paidFree = "Paid";
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        maleCount = 0;
        femaleCount = 0;
        price = 0.0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                getSupportFragmentManager().popBackStack();
            else
                askToSave();
        }
        return super.onOptionsItemSelected(item);
    }

    private void askToSave()
    {
        AlertDialog.Builder confirmMessage = new AlertDialog.Builder(this);
        confirmMessage.setTitle("Unsaved Data")
                .setMessage("Are you sure you want to discard the changes?")
                .setCancelable(false)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                        {
                            getSupportFragmentManager().popBackStack();
                        }
                        onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing
                    }
                })
                .show();
    }
}
