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

import com.thewavesocial.waveandroid.R;

import java.util.Calendar;

public class SignupActivity extends FragmentActivity
{
    private static final int NUM_PAGES = 6;
    private PagerAdapter mPagerAdapter;
    public ViewPager mPager;
    public String email = "", password = "", gender = "", friendname = "", friendphone = "";
    public Calendar birthday = Calendar.getInstance();
    public BitmapDrawable profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_viewpager_layout);

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

    public void saveUserDate()
    {

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
