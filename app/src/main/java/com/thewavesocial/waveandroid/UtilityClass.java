package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

import java.util.TimeZone;

/**
 * This class provides static, useful functions to other activities.
 */
public final class UtilityClass {
    private static LatLng loc = null, mapLoc = null;
    private static boolean dialogShowing = false;
    private static ProgressDialog progressDialog;

    /**
     * Hide input keyboard from the screen.
     * @param activity keyboard's activity
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * Display date from a calender object.
     * @param c calendar object
     * @return string display of date
     */
    public static String dateToString(Calendar c) {
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DATE);
        String prefixM = "";
        String prefixD = "";
        if (m < 10)
            prefixM = "0";
        if (d < 10)
            prefixD = "0";
        return (prefixM + m + "/" + prefixD + d + "/" + (c.get(Calendar.YEAR) + "").substring(2));
    }

    /**
     * Display time from a calendar object.
     * @param c calendar object
     * @return string display of time
     */
    public static String timeToString(Calendar c) {
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String prefixH = "";
        String prefixM = "";
        String ampm = "AM";
        if (hour > 12) {
            hour -= 12;
            ampm = "PM";
        }
        if (hour == 0) {
            hour = 12;
            ampm = "AM";
        }
        if (hour < 10)
            prefixH = "0";
        if (min < 10)
            prefixM = "0";
        return prefixH + hour + ":" + prefixM + min + " " + ampm;
    }

    /**
     * Display the difference in current time and notification-created time.
     * @param create_time epoch time when notification is created
     * @return string display of the time difference between now and created time
     */
    public static String getNotificationTime(long create_time) {
        String suffix;
        int sec = 60, min = 60, hr = 24, mth = 30, yr = 12;
        long time_period = (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - create_time) / 1000;

        long num = time_period / (sec * min * hr * mth * yr);
        if (num != 0) {
            suffix = (num == 1) ? "" : "s";
            return num + " year" + suffix + " ago.";
        }
        num = time_period / (sec * min * hr * mth);
        if (num != 0) {
            suffix = (num == 1) ? "" : "s";
            return num + " month" + suffix + " ago.";
        }
        num = time_period / (sec * min * hr);
        if (num != 0) {
            suffix = (num == 1) ? "" : "s";
            return num + " day" + suffix + " ago.";
        }
        num = time_period / (sec * min);
        if (num != 0) {
            suffix = (num == 1) ? "" : "s";
            return num + " hour" + suffix + " ago.";
        }
        num = time_period / (sec);
        if (num != 0) {
            suffix = (num == 1) ? "" : "s";
            return num + " min" + suffix + " ago.";
        }
        suffix = (time_period == 1) ? "" : "s";
        return time_period + " sec" + suffix + " ago.";
    }

    /**
     * Convert Bitmap to RoundedBitmapDrawable
     * @param res activity resources
     * @param bitmap specified bitmap
     * @return RoundedBitmapDrawable object
     * @see <a href="http://stackoverflow.com/questions/2459916/how-to-make-an-imageview-with-rounded-corners">http://stackoverflow.com/questions/2459916/how-to-make-an-imageview-with-rounded-corners</a>
     */
    public static RoundedBitmapDrawable toRoundImage(Resources res, Bitmap bitmap) {
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCircular(true);
        return dr;
    }

    /**
     * Display alert dialog for error-handling.
     * @param activity activity
     * @param message dialog's message
     * @param header dialog's header
     * @param cancelable dialog's ability to be canceled
     */
    public static void printAlertMessage(Activity activity, String message, String header, boolean cancelable) {
        if (dialogShowing)
            return;
        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(activity);
        fieldAlert.setMessage(message)
                .setCancelable(cancelable)
                .setTitle(header)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogShowing = false;
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        dialogShowing = false;
                    }
                }).show();
        dialogShowing = true;
    }

    /**
     * Return current user GPS location
     * @return LatLng position
     */
    public static LatLng getUserLocation() {
        return loc;
    }

    /**
     * Update current user GPS location
     * @param loc1 new LatLng position
     */
    public static void updateUserLocation(LatLng loc1) {
        loc = loc1;
    }

    /**
     * Return current user Maps location
     * @return LatLng position
     */
    public static LatLng getMapLocation() {
        return mapLoc;
    }

    /**
     * Update current user Maps location
     * @param loc1 new LatLng position
     */
    public static void updateMapLocation(LatLng loc1) {
        mapLoc = loc1;
    }

    /**
     * Convert string address to LatLng position.
     * @param activity activity
     * @param strAddress string address
     * @return LatLng position
     */
    public static LatLng getLocationFromAddress(Activity activity, String strAddress) {
        Geocoder coder = new Geocoder(activity);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            for (int i = 0; i < 3 && address.size() == 0; i++) {
                address = coder.getFromLocationName("strAddress", 1);
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            Log.d("Sorry", e.getMessage());
        }
        return p1;
    }

    /**
     * Convert LatLng position to string address.
     * @param activity activity
     * @param lat latitude
     * @param lng longitude
     * @return string address
     */
    public static String getAddressFromLocation(Activity activity, int lat, int lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String postalCode = addresses.get(0).getPostalCode();
        return address + ", " + city + ", " + state + ", " + postalCode;
    }

    /**
     * Display price in string.
     * @param price price
     * @return string display
     */
    public static String priceToString(double price) {
        if ((int) price == price) {
            return "$" + (int) price;
        } else {
            return "$" + price;
        }
    }

    /**
     * Get the width of screen
     * @param activity activity
     * @return screen width
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Get the height of screen
     * @param activity activity
     * @return screen height
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Get xml view object with specified ID.
     * @param v parent view
     * @param resId resource id
     * @param <T> view type
     * @return view
     */
    public static <T extends View> T getView(@NonNull View v, @IdRes int resId) {
        return (T) v.findViewById(resId);
    }

    /**
     * Search query from list.
     * @param oldList old list
     * @param query query
     * @param <T> object type
     * @return new list
     */
    public static <T extends Object> List<T> search(List<T> oldList, String query) {
        if (query == "")
            return oldList;
        List<T> newList = new ArrayList<>();
        Log.d("outside loop", "Query: " + query);
        for (T obj : oldList) {

            if (obj.toString().toLowerCase().trim().contains(query.toLowerCase())) {
                newList.add(obj);
                Log.d("inside Loop", "Query: " + query);
            }
        }

        return newList;
    }

    /**
     * Start loading progressbar.
     * @param activity activity
     */
    public static void startProgressbar(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Connecting to Server...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * End loading progressbar with loading result.
     * @param activity activity
     * @param success loading result
     */
    public static void endProgressbar(Activity activity, boolean success) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (!success)
            printAlertMessage(activity, "Sorry. Internet Connection Error.", "Network Error", true);
    }

    /**
     * Get list of string IDs from list of users.
     * @param userList list of users
     * @return list of IDs
     */
    public static List<String> userObjectToStringId(List<User> userList) {
        List<String> result = new ArrayList<>();
        for (User user : userList) {
            result.add((user.getUserID()));
        }
        return result;
    }

    /**
     * Get list of int IDs from list of users.
     * @param userList list of users
     * @return list of IDs
     */
    public static List<Integer> userObjectToIntegerId(List<User> userList) {
        List<Integer> result = new ArrayList<>();
        for (User user : userList) {
            result.add(Integer.parseInt(user.getUserID()));
        }
        return result;
    }

    /**
     * Get duplicated items in list a and b.
     * @param a first list
     * @param b second list
     * @param <T> item type
     * @return list of duplicated items
     */
    public static <T extends Object> List<T> findDuplicates(List<T> a, List<T> b) {
        List<T> duplicates = new ArrayList<>();
        for (T item : a) {
            if (b.contains(item)) {
                duplicates.add(item);
            }
        }
        return duplicates;
    }
}