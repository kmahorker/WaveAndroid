package com.thewavesocial.waveandroid.FindEventsFolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class PartyProfileActivity extends AppCompatActivity
{
    Party party;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        party = CurrentUser.getPartyObject(intent.getExtras().getLong("partyIDLong"));
        updatePartyName(party.getName());
        updateHostName(party.getHostName());
        updateAddress(party.getMapAddress().getAddress_string());
        updatePrice(party.getPrice());
        //updateAddressImg( Image img );
        updateDate( UtilityClass.dateToString(party.getStartingDateTime()) );
        updateTime( UtilityClass.timeToString(party.getStartingDateTime()) );
    }

    private void updatePartyName(String str)
    {
        TextView text = (TextView)findViewById (R.id.party_name);
        text.setText(str);
    }

    private void updateHostName(String str)
    {
        TextView text = (TextView)findViewById (R.id.host_name);
        text.setText(str);
    }

    private void updateAddress(String str)
    {
        TextView text = (TextView)findViewById (R.id.location_text);
        text.setText("Location: " + str);
    }

    private void updateAddressImg(Object img)
    {
        //ImageView image = FindViewById<ImageView>(Resource.Id.location_image);
        //image = img;
    }

    private void updateDate(String str)
    {
        TextView text = (TextView)findViewById (R.id.date_text);
        text.setText(str);
    }

    private void updateTime(String str)
    {
        TextView text = (TextView)findViewById (R.id.time_text);
        text.setText(str);
    }

    private void updatePrice(double price)
    {
        TextView text = (TextView)findViewById (R.id.price_text);
        if (price == 0.0)
            text.setText("Price: FREE");
        else
            text.setText("Price: " + price);
    }

    private String getMonthText(int i)
    {
        String month = "";
        switch (i)
        {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
            default:
                month = "Undefined";
                break;
        }
        return month;
    }

    private String getWeekText(int i)
    {
        String week = "";
        switch (i)
        {
            case 1:
                week = "Monday";
                break;
            case 2:
                week = "Tuesday";
                break;
            case 3:
                week = "Wednesday";
                break;
            case 4:
                week = "Thursday";
                break;
            case 5:
                week = "Friday";
                break;
            case 6:
                week = "Saturday";
                break;
            case 7:
                week = "Sunday";
                break;
            default:
                week = "Undefined";
                break;
        }
        return week;
    }

    private String getTimeView(int h, int m)
    {
        String ampm = "AM";
        int hour = h;
        if (h >= 12)
        {
            ampm = "PM";
            if (h > 12)
                hour = h - 12;
        }
        return hour + ":" + m + ampm;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
