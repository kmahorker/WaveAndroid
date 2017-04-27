package com.thewavesocial.waveandroid.HostFolder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    public String name, privatePublic, paidFree;
    public MapAddress location;
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

        location = null;
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

    public void saveToUser()
    {
        Party newParty = new Party();
        // TODO: 02/13/2017 Create New Party ID
        newParty.setPartyID("12345");
        newParty.setName(name);
        newParty.setMapAddress( location );
        newParty.setPrice(price);
        newParty.setStartingDateTime(startCalendar);
        newParty.setEndingDateTime(endCalendar);
        CurrentUser.theUser.getHosted().add(0, newParty.getPartyID());
    }
}
