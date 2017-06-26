package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeFolder.MapsFragment;
import com.thewavesocial.waveandroid.HostFolder.HostControllerFragment;
import com.thewavesocial.waveandroid.SocialFolder.SettingsActivity;
import com.thewavesocial.waveandroid.SocialFolder.UserProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

/**
 * Central activity that controls the swiping mechanism of host, map, and social fragments.
 */
public class HomeSwipeActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 3;
    public ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private HomeSwipeActivity mainActivity;
    private HostControllerFragment hostControllerFragment;
    private UserProfileFragment userProfileFragment;
    private MapsFragment mapsFragment;

    /**
     * Initialize logged-in user information and all UIs.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        mainActivity = this;
        saveTokentoLocal(mainActivity, "10", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozMCwiaWF0IjoxNDk4NDQyMjE4LCJleHAiOjE1MDEwMzQyMTh9.p-pNgfKg6KL2kZeLrGJlw7k_7Yj1k8fLEYYjVgG-PQA");

        if (CurrentUser.mainActivity == null) {
            UtilityClass.startProgressbar(mainActivity);
            CurrentUser.setContext(this, new OnResultReadyListener<Boolean>() {
                @Override
                public void onResultReady(Boolean result) {
                    UtilityClass.endProgressbar(mainActivity, result);
                    if (result) {
//                        setupServerDummies();
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

    /**
     * Sliding mechanism for host, map, and social fragments.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        /**
         * Default FragmentStatePagerAdapter constructor.
         * @param fm fragment manager
         */
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returned fragment at the swiped position.
         * @param position swiped screen position
         * @return fragment at the swiped position
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    hostControllerFragment = new HostControllerFragment();
                    return hostControllerFragment;
                case 1:
                    mapsFragment = new MapsFragment();
                    return mapsFragment;
                default:
                    userProfileFragment = new UserProfileFragment();
                    return userProfileFragment;
            }
        }

        /**
         * Return number of fragments available.
         * @return number of fragments available
         */
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /**
     * Provide accurate callback of the swiped screen.
     */
    private class ScreenSlideChangeListener implements ViewPager.OnPageChangeListener {

        /**
         * Default ViewPager constructor. (Empty)
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * Method called when swipe to a fragment
         * @param position position of the swiped fragment
         */
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    hostControllerFragment.populateListView();
                    setupHostActionbar();
                    break;
                case 2:
                    userProfileFragment.setupProfileInfo();
                    setupUserActionbar();
                    break;
                case 1:
                    setupMapActionbar();
                    break;
            }
        }

        /**
         * @param state scroll state
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * Setup actionbar for map fragment.
     */
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

    /**
     * Setup actionbar for host fragment.
     */
    public void setupHostActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_hostcontroller);

        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_host_image_plug);
        plug_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });
    }

    /**
     * Setup actionbar for user fragment.
     */
    public void setupUserActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_user);

        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_user_image_plug);
        ImageView settings = (ImageView) findViewById(R.id.actionbar_user_setting);

        plug_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Dispatch incoming result to the correct fragment.
     * Activities include QR scanner and photo collection.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult qrResult = ZxingOrient.parseActivityResult(requestCode, resultCode, data);
        if (qrResult != null) {
            if (qrResult.getContents() != null) {
                try {
                    JSONObject json = new JSONObject(qrResult.getContents());
                    long party_id = json.getLong("party_id");
                    long user_id = json.getLong("user_id");
                    validateUserAndParty(user_id, party_id);
                } catch (JSONException e) {
                    Toast.makeText(this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(this, "No Content", Toast.LENGTH_LONG).show();
        } else if (requestCode == UserProfileFragment.ADD_IMAGE_INTENT_ID && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), selectedImage);
                userProfileFragment.updateProfileImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check specified userID and partyID with server to see if they match.
     * @param user_id scanned userID
     * @param party_id scanned partyID
     */
    private void validateUserAndParty(long user_id, long party_id) {
        Toast.makeText(this, "UserID: " + user_id + ", PartyID: " + party_id, Toast.LENGTH_LONG).show();
        // TODO: 04/02/2017 Check with database
    }

    /**
     * Setup server party, user dummies and their relationships.
     */
    private void setupServerDummies() {
        String uv = "1", pv = "1";

        //Main User ID=10
        HashMap<String, String> body = new HashMap<>();
        body.put("first_name", "Main");
        body.put("last_name", "User");
        body.put("email", "main@gmail.com");
        body.put("college", "UCSB");
        body.put("password", "main_user");
        body.put("gender", "Male");
        body.put("birthday", "1990-12-12");
        server_updateUser(uv + "0", body, null);

        //Friend 1-5
        HashMap<String, String> body1 = new HashMap<>();
        body1.put("first_name", "One");
        body1.put("last_name", "Friend1");
        body1.put("email", "one@gmail.com");
        body1.put("college", "UCSB");
        body1.put("password", "one");
        body1.put("gender", "Female");
        body1.put("birthday", "1990-01-01");
        server_updateUser(uv + "1", body1, null);

        HashMap<String, String> body2 = new HashMap<>();
        body2.put("first_name", "Two");
        body2.put("last_name", "Friend2");
        body2.put("email", "two@gmail.com");
        body2.put("college", "UCSB");
        body2.put("password", "two");
        body2.put("gender", "Male");
        body2.put("birthday", "1990-02-02");
        server_updateUser(uv + "2", body2, null);

        HashMap<String, String> body3 = new HashMap<>();
        body3.put("first_name", "Three");
        body3.put("last_name", "Friend3");
        body3.put("email", "three@gmail.com");
        body3.put("college", "UCSB");
        body3.put("password", "three");
        body3.put("gender", "Female");
        body3.put("birthday", "1990-03-03");
        server_updateUser(uv + "3", body3, null);

        HashMap<String, String> body4 = new HashMap<>();
        body4.put("first_name", "Four");
        body4.put("last_name", "Friend4");
        body4.put("email", "four@gmail.com");
        body4.put("college", "UCSB");
        body4.put("password", "four");
        body4.put("gender", "Male");
        body4.put("birthday", "1990-04-04");
        server_updateUser(uv + "4", body4, null);

        HashMap<String, String> body5 = new HashMap<>();
        body5.put("first_name", "Five");
        body5.put("last_name", "Friend5");
        body5.put("email", "five@gmail.com");
        body5.put("college", "UCSB");
        body5.put("password", "five");
        body5.put("gender", "Male");
        body5.put("birthday", "1990-05-05");
        server_updateUser(uv + "5", body5, null);

        //Follow Each Other
        server_followUser(uv + "0", uv + "1", null);
        server_followUser(uv + "0", uv + "2", null);
        server_followUser(uv + "0", uv + "3", null);
        server_followUser(uv + "0", uv + "4", null);
        server_followUser(uv + "0", uv + "5", null);

        server_followUser(uv + "1", uv + "0", null);
        server_followUser(uv + "1", uv + "2", null);
        server_followUser(uv + "1", uv + "3", null);
        server_followUser(uv + "1", uv + "4", null);
        server_followUser(uv + "1", uv + "5", null);

        server_followUser(uv + "2", uv + "0", null);
        server_followUser(uv + "2", uv + "1", null);
        server_followUser(uv + "2", uv + "3", null);
        server_followUser(uv + "2", uv + "4", null);
        server_followUser(uv + "2", uv + "5", null);

        server_followUser(uv + "3", uv + "0", null);
        server_followUser(uv + "3", uv + "1", null);
        server_followUser(uv + "3", uv + "2", null);
        server_followUser(uv + "3", uv + "4", null);
        server_followUser(uv + "3", uv + "5", null);

        server_followUser(uv + "4", uv + "0", null);
        server_followUser(uv + "4", uv + "1", null);
        server_followUser(uv + "4", uv + "2", null);
        server_followUser(uv + "4", uv + "3", null);
        server_followUser(uv + "4", uv + "5", null);

        server_followUser(uv + "5", uv + "0", null);
        server_followUser(uv + "5", uv + "1", null);
        server_followUser(uv + "5", uv + "2", null);
        server_followUser(uv + "5", uv + "3", null);
        server_followUser(uv + "5", uv + "4", null);

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
        body6.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body6.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body6.put("min_age", 18 + "");
        body6.put("max_age", 40 + "");
        server_updateParty(pv + "1", body6, null);

        HashMap<String, String> body7 = new HashMap<>();
        body7.put("name", "Party2");
        body7.put("emoji", "\ue32d");
        body7.put("price", "10");
        body7.put("address", "6555 Segovia Rd Goleta, CA 93117");
        body7.put("lat", 34.414241 + "");
        body7.put("lng", -119.856559 + "");
        body7.put("is_public", true ? "1" : "0");
        body7.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body7.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body7.put("min_age", 18 + "");
        body7.put("max_age", 40 + "");
        server_updateParty(pv + "2", body7, null);

        HashMap<String, String> body8 = new HashMap<>();
        body8.put("name", "Party3");
        body8.put("emoji", "\ue32d");
        body8.put("price", "20");
        body8.put("address", "6650 Picasso Rd, Goleta, CA 93117");
        body8.put("lat", 34.415500 + "");
        body8.put("lng", -119.860575 + "");
        body8.put("is_public", true ? "1" : "0");
        body8.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body8.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body8.put("min_age", 18 + "");
        body8.put("max_age", 40 + "");
        server_updateParty(pv + "3", body8, null);

        HashMap<String, String> body9 = new HashMap<>();
        body9.put("name", "Party4");
        body9.put("emoji", "\ue32d");
        body9.put("price", "50");
        body9.put("address", "895 Camino Del Sur Goleta, CA 93117");
        body9.put("lat", 34.412938 + "");
        body9.put("lng", -119.862853 + "");
        body9.put("is_public", true ? "1" : "0");
        body9.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body9.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body9.put("min_age", 18 + "");
        body9.put("max_age", 40 + "");
        server_updateParty(pv + "4", body9, null);

        HashMap<String, String> body10 = new HashMap<>();
        body10.put("name", "Party5");
        body10.put("emoji", "\ue32d");
        body10.put("price", "100");
        body10.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body10.put("lat", 34.411962 + "");
        body10.put("lng", -119.859848 + "");
        body10.put("is_public", true ? "1" : "0");
        body10.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body10.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body10.put("min_age", 18 + "");
        body10.put("max_age", 40 + "");
        server_updateParty(pv + "5", body10, null);

        HashMap<String, String> body11 = new HashMap<>();
        body11.put("name", "Party6");
        body11.put("emoji", "\ue32d");
        body11.put("price", "100");
        body11.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body11.put("lat", 34.411962 + "");
        body11.put("lng", -119.859848 + "");
        body11.put("is_public", true ? "1" : "0");
        body11.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body11.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body11.put("min_age", 18 + "");
        body11.put("max_age", 40 + "");
        server_updateParty(pv + "6", body11, null);

        HashMap<String, String> body12 = new HashMap<>();
        body12.put("name", "Party7");
        body12.put("emoji", "\ue32d");
        body12.put("price", "100");
        body12.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body12.put("lat", 34.411962 + "");
        body12.put("lng", -119.859848 + "");
        body12.put("is_public", true ? "1" : "0");
        body12.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body12.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body12.put("min_age", 18 + "");
        body12.put("max_age", 40 + "");
        server_updateParty(pv + "7", body12, null);

        HashMap<String, String> body13 = new HashMap<>();
        body13.put("name", "Party8");
        body13.put("emoji", "\ue32d");
        body13.put("price", "100");
        body13.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body13.put("lat", 34.411962 + "");
        body13.put("lng", -119.859848 + "");
        body13.put("is_public", true ? "1" : "0");
        body13.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body13.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body13.put("min_age", 18 + "");
        body13.put("max_age", 40 + "");
        server_updateParty(pv + "8", body13, null);

        HashMap<String, String> body14 = new HashMap<>();
        body14.put("name", "Party9");
        body14.put("emoji", "\ue32d");
        body14.put("price", "100");
        body14.put("address", "6628 Pasado Rd Goleta, CA 93117");
        body14.put("lat", 34.411962 + "");
        body14.put("lng", -119.859848 + "");
        body14.put("is_public", true ? "1" : "0");
        body14.put("start_timestamp", startDate.getTime().getTime() / 1000 + "");
        body14.put("end_timestamp", endDate.getTime().getTime() / 1000 + "");
        body14.put("min_age", 18 + "");
        body14.put("max_age", 40 + "");
        server_updateParty(pv + "9", body14, null);

        //Add user event relationship
        server_manageUserForParty(uv + "0", pv + "1", "hosting", "POST", null);
        server_manageUserForParty(uv + "0", pv + "2", "hosting", "POST", null);
        server_manageUserForParty(uv + "0", pv + "3", "hosting", "POST", null);
        server_manageUserForParty(uv + "0", pv + "4", "hosting", "POST", null);
        server_manageUserForParty(uv + "0", pv + "5", "hosting", "POST", null);

        server_manageUserForParty(uv + "1", pv + "1", "bouncing", "POST", null);
        server_manageUserForParty(uv + "1", pv + "2", "bouncing", "POST", null);
        server_manageUserForParty(uv + "1", pv + "3", "bouncing", "POST", null);
        server_manageUserForParty(uv + "1", pv + "4", "bouncing", "POST", null);
        server_manageUserForParty(uv + "1", pv + "5", "bouncing", "POST", null);

        server_manageUserForParty(uv + "2", pv + "1", "bouncing", "POST", null);
        server_manageUserForParty(uv + "2", pv + "2", "bouncing", "POST", null);
        server_manageUserForParty(uv + "2", pv + "3", "bouncing", "POST", null);
        server_manageUserForParty(uv + "2", pv + "4", "bouncing", "POST", null);
        server_manageUserForParty(uv + "2", pv + "5", "bouncing", "POST", null);

        server_manageUserForParty(uv + "3", pv + "1", "going", "POST", null);
        server_manageUserForParty(uv + "3", pv + "2", "going", "POST", null);
        server_manageUserForParty(uv + "3", pv + "3", "going", "POST", null);
        server_manageUserForParty(uv + "3", pv + "4", "going", "POST", null);
        server_manageUserForParty(uv + "3", pv + "5", "going", "POST", null);

        server_manageUserForParty(uv + "4", pv + "1", "going", "POST", null);
        server_manageUserForParty(uv + "4", pv + "2", "going", "POST", null);
        server_manageUserForParty(uv + "4", pv + "3", "going", "POST", null);
        server_manageUserForParty(uv + "4", pv + "4", "going", "POST", null);
        server_manageUserForParty(uv + "4", pv + "5", "going", "POST", null);

        server_manageUserForParty(uv + "5", pv + "1", "going", "POST", null);
        server_manageUserForParty(uv + "5", pv + "2", "going", "POST", null);
        server_manageUserForParty(uv + "5", pv + "3", "going", "POST", null);
        server_manageUserForParty(uv + "5", pv + "4", "going", "POST", null);
        server_manageUserForParty(uv + "5", pv + "5", "going", "POST", null);

        server_manageUserForParty(uv + "0", pv + "6", "going", "POST", null);
        server_manageUserForParty(uv + "0", pv + "7", "going", "POST", null);
        server_manageUserForParty(uv + "0", pv + "8", "going", "POST", null);
        server_manageUserForParty(uv + "0", pv + "9", "going", "POST", null);

        server_inviteUserToEvent(uv + "1", pv + "1", null);
        server_inviteUserToEvent(uv + "1", pv + "2", null);
        server_inviteUserToEvent(uv + "1", pv + "3", null);
        server_inviteUserToEvent(uv + "1", pv + "4", null);
        server_inviteUserToEvent(uv + "1", pv + "5", null);

        server_inviteUserToEvent(uv + "2", pv + "1", null);
        server_inviteUserToEvent(uv + "2", pv + "2", null);
        server_inviteUserToEvent(uv + "2", pv + "3", null);
        server_inviteUserToEvent(uv + "2", pv + "4", null);
        server_inviteUserToEvent(uv + "2", pv + "5", null);

        server_inviteUserToEvent(uv + "3", pv + "1", null);
        server_inviteUserToEvent(uv + "3", pv + "2", null);
        server_inviteUserToEvent(uv + "3", pv + "3", null);
        server_inviteUserToEvent(uv + "3", pv + "4", null);
        server_inviteUserToEvent(uv + "3", pv + "5", null);

        server_inviteUserToEvent(uv + "4", pv + "1", null);
        server_inviteUserToEvent(uv + "4", pv + "2", null);
        server_inviteUserToEvent(uv + "4", pv + "3", null);
        server_inviteUserToEvent(uv + "4", pv + "4", null);
        server_inviteUserToEvent(uv + "4", pv + "5", null);
    }
}
