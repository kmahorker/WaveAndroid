package com.thewavesocial.waveandroid.LoginFolder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class SignupActivity extends FragmentActivity
{
    private static final int NUM_PAGES = 6;
    private PagerAdapter mPagerAdapter;
    public ViewPager mPager;
    public String email = "", password = "", gender = "", friendname = "";
    public long friendphone = 0;
    public Calendar birthday;
    public BitmapDrawable profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_viewpager_layout);

        birthday = Calendar.getInstance();

        mPager = (ViewPager) findViewById(R.id.signup_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed()
    {
        if (mPager.getCurrentItem() == 0)
        {
            super.onBackPressed();
        }
        else
        {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void saveUserData()
    {
        if ( email == "" )
        {

        }
        else if ( password == "" )
        {

        }
        else if ( gender == "" )
        {

        }
        else if ( friendname == "" )
        {

        }
        else if ( friendphone == 0 )
        {

        }
        else if ( birthday.get(Calendar.MONTH) == 0 )
        {

        }
        else if ( birthday.get(Calendar.DATE) == 0 )
        {

        }
        else if ( birthday.get(Calendar.YEAR) == 0 )
        {

        }
        else if ( profilePic == null )
        {

        }
        else
        {
            User bestFriend = new User();
            bestFriend.setFirstName(friendname.substring(0, friendname.lastIndexOf(' ')));
            bestFriend.setLastName(friendname.substring(friendname.lastIndexOf(' ') + 1));
            bestFriend.setPhone(friendphone);
            bestFriend.setUserID(1000);

            User user = new User((long) 0,
                    "", "",
                    email, password,
                    "", gender,
                    0, new MapAddress(),
                    birthday,
                    new ArrayList<Long>(), //best friend list
                    new ArrayList<Long>(), //friend list
                    new ArrayList<Long>(), //party attended list
                    new ArrayList<Long>(), //party hosted list
                    new ArrayList<Long>(), //party bounced list
                    new ArrayList<Long>(),
                    new LinkedList(), //notifications
                    new BitmapDrawable());
            user.getBestFriends().add(bestFriend.getUserID());
            user.setProfilePic(profilePic);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return new SignupFragment( position );
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }
    }
}
