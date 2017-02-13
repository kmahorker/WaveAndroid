package com.thewavesocial.waveandroid;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    private String loc, name;
    private Calendar startingCalendar, endingCalendar;
    private double price;

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

    public void savePartyInfo(Calendar sd, Calendar ed, String loc, double price)
    {
        this.startingCalendar = sd;
        this.endingCalendar = ed;
        this.loc = loc;
        this.price = price;
    }

    public Calendar getStartingCalendar()
    {
        return startingCalendar;
    }

    public Calendar getEndingCalendar()
    {
        return endingCalendar;
    }

    public String getLocation()
    {
        return loc;
    }

    public double getPrice()
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
        // TODO: 02/13/2017 Create New Party ID
        newParty.setName(name);
        newParty.setAddress(loc);
        newParty.setPrice(price);
        newParty.setStartingDateTime(startingCalendar);
        newParty.setEndingDateTime(endingCalendar);

        CurrentUser.theUser.getHosted().add(0, newParty.getPartyID());
    }
}
