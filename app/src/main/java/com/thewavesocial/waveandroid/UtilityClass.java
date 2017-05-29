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
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

/**
 * Created by Kaushik on 2/5/2017.
 */

public final class UtilityClass {
    private static LatLng loc = null, mapLoc = null;

    private UtilityClass() {
        //Add Needed
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

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

    public static RoundedBitmapDrawable toRoundImage(Resources res, Bitmap bitmap) {
        //http://stackoverflow.com/questions/2459916/how-to-make-an-imageview-with-rounded-corners
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCircular(true);
        return dr;
    }

    public static void printAlertMessage(Activity activity, String message, String header, boolean cancelable) {
        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(activity);
        fieldAlert.setMessage(message)
                .setCancelable(cancelable)
                .setTitle(header)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static Bitmap getBitmapFromURL(Activity mainActivity, String url, final OnResultReadyListener<Bitmap> delegate) {
        new ImageRequestTask(mainActivity, url, new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap result) {
                delegate.onResultReady(result);
            }
        }).execute();
        return null;
    }

    public static class ImageRequestTask extends AsyncTask<String, String, Bitmap> {
        private String url;
        private Activity mainActivity;
        private ProgressDialog progress;
        private Handler handler;
        private Runnable run;
        private OnResultReadyListener delegate;

        public ImageRequestTask(Activity mainActivity, String url, OnResultReadyListener<Bitmap> delegate) {
            this.url = url;
            this.mainActivity = mainActivity;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mainActivity, "Loading.....", Toast.LENGTH_SHORT).show();
//            progress = new ProgressDialog(mainActivity);
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setTitle("Please wait");
//            progress.setMessage("Connecting to Server...");
//            progress.setCancelable(false);

//            handler = new Handler();
//            run = new Runnable() {
//                @Override
//                public void run() {
//                    progress.show();
//                }
//            };
//            handler.postDelayed(run, 3000);
        }

        @Override
        protected Bitmap doInBackground(String... params){
            return getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            if ( progress.isShowing() )
//                progress.dismiss();
//            handler.removeCallbacks(run);
            delegate.onResultReady(bitmap);
        }

        //http://stackoverflow.com/questions/8992964/android-load-from-url-to-bitmap
        public static Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }
    }

//------------------------------------------------------------------------------------Map Functions

    public static LatLng getUserLocation() {
        return loc;
    }

    public static void updateUserLocation(LatLng loc1) {
        loc = loc1;
    }

    public static LatLng getMapLocation() {
        return mapLoc;
    }

    public static void updateMapLocation(LatLng loc1) {
        mapLoc = loc1;
    }

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

    public static String getAddressFromLocation(Activity activity, int lat, int lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {e.printStackTrace();}

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String postalCode = addresses.get(0).getPostalCode();
        return address + ", " + city + ", " + state + ", " + postalCode;
    }

    public static String priceToString(double price) {
        if ((int) price == price) {
            return "$" + (int) price;
        } else {
            return "$" + price;
        }
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static <T extends View> T getView(@NonNull View v, @IdRes int resId) {
        return (T) v.findViewById(resId);
    }

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

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public static Calendar unixToCalendar(long unixTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime);
        return calendar;
    }
}