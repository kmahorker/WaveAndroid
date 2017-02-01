package com.thewavesocial.waveandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.*;

public class PartyProfileActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Party party = new Party();
        updatePartyName(party.getName());
        updateHostName(party.getHostName());
        updateAddress(party.getAddress());
        updatePrice(party.getPrice());
        //updateAddressImg( Image img );
        updateDate(party.getStartingDateTime().getDay(),
                party.getStartingDateTime().getMonth(), party.getStartingDateTime().getDate());
        updateTime(party.getStartingDateTime().getHours(), party.getStartingDateTime().getMinutes(),
                party.getEndingDateTime().getHours(), party.getEndingDateTime().getMinutes());
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

    private void updateDate(int day, int month, int date)
    {
        TextView text = (TextView)findViewById (R.id.date_text);
        text.setText(getWeekText(day) + ", " + getMonthText(month) + " " + date);
    }

    private void updateTime(int sHour, int sMin, int eHour, int eMin)
    {
        TextView text = (TextView)findViewById (R.id.time_text);
        text.setText(getTimeView(sHour, sMin) + " - " + getTimeView(eHour, eMin));
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
}
