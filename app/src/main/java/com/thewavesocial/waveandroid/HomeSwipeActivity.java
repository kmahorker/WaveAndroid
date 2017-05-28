package com.thewavesocial.waveandroid;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeFolder.MapsFragment;
import com.thewavesocial.waveandroid.HostFolder.HostControllerFragment;
import com.thewavesocial.waveandroid.SocialFolder.UserProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

public class HomeSwipeActivity extends AppCompatActivity {
    private PagerAdapter mPagerAdapter;
    public ViewPager mPager;
    private static final int NUM_PAGES = 3;
    private HomeSwipeActivity mainActivity;
    //private IntentIntegrator qrScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        mainActivity = this;
        DatabaseAccess.saveTokentoLocal(mainActivity, "10", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozMSwiaWF0IjoxNDk1ODM2MDQyLCJleHAiOjE0OTg0MjgwNDJ9.5zJdgo72EWqeRioT5X-Bea2TPkQqgsKxGzCHE2WfOj4");

        setupServerDummies();
        if ( CurrentUser.context == null ) {
            CurrentUser.setContext(this, new OnResultReadyListener<Boolean>() {
                @Override
                public void onResultReady(Boolean result) {
                    if (result) {
                        setupMapActionbar();
                        mPager = (ViewPager) findViewById(R.id.new_activity_home_viewpager);
                        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                        mPager.setAdapter(mPagerAdapter);
                        mPager.setCurrentItem(1);
                        mPager.setOnPageChangeListener(new ScreenSlideChangeListener());
                    } else {
                        Log.d("HomeSwipeActivity", "Set User Context Failed...");
                    }
                }
            });
        } else {
            setupMapActionbar();
            mPager = (ViewPager) findViewById(R.id.new_activity_home_viewpager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(1);
            mPager.setOnPageChangeListener(new ScreenSlideChangeListener());
        }

    }

    private void setupServerDummies() {

        //Main User ID=10
        HashMap<String, String> body = new HashMap<>();
        body.put("first_name", "Main");
        body.put("last_name", "User");
        body.put("email", "main@gmail.com");
        body.put("college", "UCSB");
        body.put("password", "main_user");
        body.put("gender", "Male");
        body.put("birthday", "1990-12-12");
        CurrentUser.server_updateUser("10", body, null);

        //Friend 1-5
        HashMap<String, String> body1 = new HashMap<>();
        body1.put("first_name", "One");
        body1.put("last_name", "Friend1");
        body1.put("email", "one@gmail.com");
        body1.put("college", "UCSB");
        body1.put("password", "one");
        body1.put("gender", "Female");
        body1.put("birthday", "1990-01-01");
        CurrentUser.server_updateUser("11", body1, null);

        HashMap<String, String> body2 = new HashMap<>();
        body2.put("first_name", "Two");
        body2.put("last_name", "Friend2");
        body2.put("email", "two@gmail.com");
        body2.put("college", "UCSB");
        body2.put("password", "two");
        body2.put("gender", "Male");
        body2.put("birthday", "1990-02-02");
        CurrentUser.server_updateUser("12", body2, null);

        HashMap<String, String> body3 = new HashMap<>();
        body3.put("first_name", "Three");
        body3.put("last_name", "Friend3");
        body3.put("email", "three@gmail.com");
        body3.put("college", "UCSB");
        body3.put("password", "three");
        body3.put("gender", "Female");
        body3.put("birthday", "1990-03-03");
        CurrentUser.server_updateUser("13", body3, null);

        HashMap<String, String> body4 = new HashMap<>();
        body4.put("first_name", "Four");
        body4.put("last_name", "Friend4");
        body4.put("email", "four@gmail.com");
        body4.put("college", "UCSB");
        body4.put("password", "four");
        body4.put("gender", "Male");
        body4.put("birthday", "1990-04-04");
        CurrentUser.server_updateUser("14", body4, null);

        HashMap<String, String> body5 = new HashMap<>();
        body5.put("first_name", "Five");
        body5.put("last_name", "Friend5");
        body5.put("email", "five@gmail.com");
        body5.put("college", "UCSB");
        body5.put("password", "five");
        body5.put("gender", "Male");
        body5.put("birthday", "1990-05-05");
        CurrentUser.server_updateUser("15", body5, null);

        //Follow Each Other
        CurrentUser.server_followUser("10", "11", null);
        CurrentUser.server_followUser("10", "12", null);
        CurrentUser.server_followUser("10", "13", null);
        CurrentUser.server_followUser("10", "14", null);
        CurrentUser.server_followUser("10", "15", null);

        CurrentUser.server_followUser("11", "10", null);
        CurrentUser.server_followUser("11", "12", null);
        CurrentUser.server_followUser("11", "13", null);
        CurrentUser.server_followUser("11", "14", null);
        CurrentUser.server_followUser("11", "15", null);

        CurrentUser.server_followUser("12", "10", null);
        CurrentUser.server_followUser("12", "11", null);
        CurrentUser.server_followUser("12", "13", null);
        CurrentUser.server_followUser("12", "14", null);
        CurrentUser.server_followUser("12", "15", null);

        CurrentUser.server_followUser("13", "10", null);
        CurrentUser.server_followUser("13", "11", null);
        CurrentUser.server_followUser("13", "12", null);
        CurrentUser.server_followUser("13", "14", null);
        CurrentUser.server_followUser("13", "15", null);

        CurrentUser.server_followUser("14", "10", null);
        CurrentUser.server_followUser("14", "11", null);
        CurrentUser.server_followUser("14", "12", null);
        CurrentUser.server_followUser("14", "13", null);
        CurrentUser.server_followUser("14", "15", null);

        CurrentUser.server_followUser("15", "10", null);
        CurrentUser.server_followUser("15", "11", null);
        CurrentUser.server_followUser("15", "12", null);
        CurrentUser.server_followUser("15", "13", null);
        CurrentUser.server_followUser("15", "14", null);

        //Create Parties
        Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
        startDate.set(2017, 7, 1, 11, 0);
        endDate.set(2017, 7, 1, 15, 0);

        HashMap<String, String> body6 = new HashMap<>();
        body6.put("name", "Party1");
        body6.put("emoji", "\ue32d");
        body6.put("price", "0");
        body6.put("address", "6612 Sueno Rd Goleta, CA 93117");
        body6.put("lat", 34.412923 + "");
        body6.put("lng", -119.859315 + "");
        body6.put("is_public", true ? "1" : "0");
        body6.put("start_timestamp", startDate.getTime().getTime()/1000 + "");
        body6.put("end_timestamp", endDate.getTime().getTime()/1000 + "");
        body6.put("min_age", 18 + "");
        body6.put("max_age", 40 + "");
        CurrentUser.server_updateParty("11", body6, null );

        HashMap<String, String> body7 = new HashMap<>();
        body7.put("name", "Party2");
        body7.put("emoji", "\ue32d");
        body7.put("price", "10");
        body7.put("address", "6555 Segovia Rd Goleta, CA 93117");
        body7.put("lat", 34.414241 + "");
        body7.put("lng", -119.856559 + "");
        body7.put("is_public", true ? "1" : "0");
        body7.put("start_timestamp", startDate.getTime().getTime()/1000 + "");
        body7.put("end_timestamp", endDate.getTime().getTime()/1000 + "");
        body7.put("min_age", 18 + "");
        body7.put("max_age", 40 + "");
        CurrentUser.server_updateParty("12", body7, null );

        HashMap<String, String> body8 = new HashMap<>();
        body8.put("name", "Party3");
        body8.put("emoji", "\ue32d");
        body8.put("price", "20");
        body8.put("address", "6650 Picasso Rd, Goleta, CA 93117");
        body8.put("lat", 34.415500 + "");
        body8.put("lng", -119.860575 + "");
        body8.put("is_public", true ? "1" : "0");
        body8.put("start_timestamp", startDate.getTime().getTime()/1000 + "");
        body8.put("end_timestamp", endDate.getTime().getTime()/1000 + "");
        body8.put("min_age", 18 + "");
        body8.put("max_age", 40 + "");
        CurrentUser.server_updateParty("13", body8, null );

        HashMap<String, String> body9 = new HashMap<>();
        body9.put("name", "Party4");
        body9.put("emoji", "\ue32d");
        body9.put("price", "50");
        body9.put("address", "895 Camino Del Sur Goleta, CA 93117");
        body9.put("lat", 34.412938 + "");
        body9.put("lng", -119.862853 + "");
        body9.put("is_public", true ? "1" : "0");
        body9.put("start_timestamp", startDate.getTime().getTime()/1000 + "");
        body9.put("end_timestamp", endDate.getTime().getTime()/1000 + "");
        body9.put("min_age", 18 + "");
        body9.put("max_age", 40 + "");
        CurrentUser.server_updateParty("14", body9, null );

        HashMap<String, String> body10 = new HashMap<>();
        body10.put("name", "Party5");
        body10.put("emoji", "\ue32d");
        body10.put("price", "100");
        body10.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body10.put("lat", 34.411962 + "");
        body10.put("lng", -119.859848 + "");
        body10.put("is_public", true ? "1" : "0");
        body10.put("start_timestamp", startDate.getTime().getTime()/1000 + "");
        body10.put("end_timestamp", endDate.getTime().getTime()/1000 + "");
        body10.put("min_age", 18 + "");
        body10.put("max_age", 40 + "");
        CurrentUser.server_updateParty("15", body10, null );

        //Add user event relationship
        CurrentUser.server_manageUserForParty("10", "11", "hosting", "POST", null);
        CurrentUser.server_manageUserForParty("10", "12", "hosting", "POST", null);
        CurrentUser.server_manageUserForParty("10", "13", "hosting", "POST", null);
        CurrentUser.server_manageUserForParty("10", "14", "hosting", "POST", null);
        CurrentUser.server_manageUserForParty("10", "15", "hosting", "POST", null);

        CurrentUser.server_manageUserForParty("11", "11", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("11", "12", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("11", "13", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("11", "14", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("11", "15", "bouncing", "POST", null);

        CurrentUser.server_manageUserForParty("12", "11", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("12", "12", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("12", "13", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("12", "14", "bouncing", "POST", null);
        CurrentUser.server_manageUserForParty("12", "15", "bouncing", "POST", null);

        CurrentUser.server_manageUserForParty("13", "11", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("13", "12", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("13", "13", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("13", "14", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("13", "15", "attending", "POST", null);

        CurrentUser.server_manageUserForParty("14", "11", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("14", "12", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("14", "13", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("14", "14", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("14", "15", "attending", "POST", null);

        CurrentUser.server_manageUserForParty("15", "11", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("15", "12", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("15", "13", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("15", "14", "attending", "POST", null);
        CurrentUser.server_manageUserForParty("15", "15", "attending", "POST", null);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HostControllerFragment();
                case 1:
                    return new MapsFragment();
                default:
                    return new UserProfileFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private class ScreenSlideChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d("Position", position + "");
            switch (position) {
                case 0:
                    setupHostActionbar();
                    break;
                case 2:
                    setupUserActionbar();
                    break;
                case 1:
                    setupMapActionbar();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //actionbar settings
    public void setupMapActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_activity_home);
        TextView hostText = (TextView) findViewById(R.id.actionbar_activity_home_text_host);
        TextView socialText = (TextView) findViewById(R.id.actionbar_activity_home_text_social);

        hostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                UtilityClass.hideKeyboard(mainActivity);
            }
        });

        socialText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                UtilityClass.hideKeyboard(mainActivity);
            }
        });
    }

    public void setupHostActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_hostcontroller);

        //qrScanner = new IntentIntegrator(this);
        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_host_image_plug);
        plug_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });
        ImageView qrButton = (ImageView) findViewById(R.id.actionbar_host_qrsymbol);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZxingOrient zxingOrient = new ZxingOrient(HomeSwipeActivity.this);
                zxingOrient.setInfo("Scan QR Code");
                zxingOrient.setToolbarColor("black");//getString(R.string.appColorHexString));
                zxingOrient.setInfoBoxColor("black");//getString(R.string.appColorHexString));
                zxingOrient.setIcon(R.drawable.plug_icon);
                zxingOrient.initiateScan();

            }
        });
    }

    public void setupUserActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_user);

        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_user_image_plug);
        plug_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult qrResult = ZxingOrient.parseActivityResult(requestCode, resultCode, data);
        if ( qrResult != null ){
            if ( qrResult.getContents() != null ){
                try {
                    JSONObject json = new JSONObject(qrResult.getContents());
                    long party_id = json.getLong("party_id");
                    long user_id = json.getLong("user_id");
                    validateUserAndParty(user_id, party_id);
                } catch (JSONException e) {
                    Toast.makeText(this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                }
            } 
            else
                Toast.makeText(this, "No Content", Toast.LENGTH_LONG).show();
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void validateUserAndParty(long user_id, long party_id) {
        Toast.makeText(this, "UserID: " + user_id + ", PartyID: " + party_id, Toast.LENGTH_LONG).show();
        // TODO: 04/02/2017 Check with database
    }

    @Override
    public void onResume() {
        super.onResume();
        //Overriden in fragments
    }
}
