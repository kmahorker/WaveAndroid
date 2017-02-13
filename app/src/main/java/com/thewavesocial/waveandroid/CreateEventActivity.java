package com.thewavesocial.waveandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
    private String loc, name, privatePublic, paidFree;
    private Calendar startingCalendar, endingCalendar;
    private int manCount, femaleCount;
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

    public void savePartyInfo(Calendar sd, Calendar ed, String loc, boolean privat, boolean paid,
                              double price, int manCount, int femaleCount)
    {
        this.startingCalendar = sd;
        this.endingCalendar = ed;
        this.loc = loc;
        this.price = price;
        this.manCount = manCount;
        this.femaleCount = femaleCount;
        if ( privat )
            this.privatePublic = "Private";
        else
            this.privatePublic = "Public";
        if ( paid )
            this.paidFree = "Paid";
        else
            this.paidFree = "Free";
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

    public String getPrivatePublic()
    {
        return privatePublic;
    }

    public String getPaidFree()
    {
        return paidFree;
    }

    public int getFemaleCount()
    {
        return femaleCount;
    }

    public int getManCount()
    {
        return manCount;
    }
}
