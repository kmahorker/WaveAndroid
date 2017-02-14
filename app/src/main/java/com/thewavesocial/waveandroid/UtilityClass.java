package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

/**
 * Created by Kaushik on 2/5/2017.
 */

public final class UtilityClass {

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
        return ( prefixM + m + "/" + prefixD + d + "/" + (c.get(Calendar.YEAR))%100);
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
}
