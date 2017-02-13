package com.thewavesocial.waveandroid;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;

public class CreateEventActivity extends AppCompatActivity
{
    private String sd, st, ed, et, loc, price, name;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("I'm here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_layout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.createEvent_fragment_container, new CreateEvent1Fragment())
                .addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        System.out.println(getSupportFragmentManager().getBackStackEntryCount() + "");
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

    public void saveData(String sd, String st, String ed, String et, String loc, String price)
    {
        this.sd = sd;
        this.st = st;
        this.ed = ed;
        this.et = et;
        this.loc = loc;
        this.price = price;
    }

    public String getStartingDate()
    {
        return sd;
    }

    public String getStartingTime()
    {
        return st;
    }

    public String getEndingDate()
    {
        return ed;
    }

    public String getEndingTime()
    {
        return et;
    }

    public String getLocation()
    {
        return loc;
    }

    public String getPrice()
    {
        return price;
    }

    public String getPartyName()
    {
        return name;
    }

    public void setPartyName(String name)
    {
        this.name = name;
    }

    public void saveToUser()
    {
        Party newParty = new Party();
        newParty.setName(name);
        newParty.setAddress(loc);
        newParty.setPrice(Double.parseDouble(price.substring(1)));
        // TODO: 02/12/2017 Convert StartingDateTime and EndingDateTime String to Date object
        CurrentUser.theUser.getHosted().add(0, newParty.getPartyID());
    }
}
