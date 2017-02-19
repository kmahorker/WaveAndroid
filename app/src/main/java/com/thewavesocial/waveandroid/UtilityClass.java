package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Kaushik on 2/5/2017.
 */

public final class UtilityClass
{
    private static LatLng loc = null, mapLoc = null;
    private UtilityClass(){
        //Add Needed
    }

    public static void hideKeyboard(Activity activity)
    {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static String dateToString(Calendar c)
    {
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DATE);
        String prefixM = "";
        String prefixD = "";
        if ( m < 10 )
            prefixM = "0";
        if ( d < 10 )
            prefixD = "0";
        return ( prefixM + m + "/" + prefixD + d + "/" + (c.get(Calendar.YEAR)+"").substring(2));
    }

    public static String timeToString(Calendar c)
    {
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        String prefixH = "";
        String prefixM = "";
        String ampm = "am";
        if ( hour > 12 )
        {
            hour -= 12;
            ampm = "pm";
        }
        if ( hour == 0 )
        {
            hour = 12;
            ampm = "am";
        }
        if ( hour < 10 )
            prefixH = "0";
        if ( min < 10 )
            prefixM = "0";
        return prefixH + hour + ":" + prefixM + min + " " + ampm;
    }

//------------------------------------------------------------------------------------Map Functions

    public static LatLng getUserLocation()
    {
        return loc;
    }

    public static void updateUserLocation( LatLng loc1 )
    {
        loc = loc1;
    }

    public static LatLng getMapLocation()
    {
        return mapLoc;
    }

    public static void updateMapLocation( LatLng loc1 )
    {
        mapLoc = loc1;
    }

    public static LatLng getLocationFromAddress( Activity activity, String strAddress)
    {
        Geocoder coder = new Geocoder(activity);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress,1);
            for ( int i = 0; i < 3 && address.size()==0; i++ )
            {
                address = coder.getFromLocationName("strAddress", 1);
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(),location.getLongitude());
        }
        catch (Exception e)
        {
            Log.d("Sorry", e.getMessage());
        }
        return p1;
    }
}