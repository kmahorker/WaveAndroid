package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SignupActivity extends FragmentActivity
{
    private static final int NUM_PAGES = 6;
    private PagerAdapter mPagerAdapter;
    private ImageView dot1, dot2, dot3, dot4, dot5, dot6;
    public ViewPager mPager;
    public String name = "", email = "", password = "", gender = "", friendname = "";
    public String friendphone = "", userID;
    public Calendar birthday;
    public BitmapDrawable profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_viewpager_layout);

        Intent intent = getIntent();
        if(intent.getExtras() != null) { //TODO 3/8/2017 Only Get Extras if Login with Facebook
            userID = intent.getExtras().getString("userIDLong");
            name = intent.getExtras().getString("userName");
            email = intent.getExtras().getString("userEmail");
            gender = intent.getExtras().getString("userGender");
        }

        try
        {
            birthday = Calendar.getInstance();
            birthday.set(Integer.parseInt(intent.getExtras().getString("userBirthday").substring(8)),
                    Integer.parseInt(intent.getExtras().getString("userBirthday").substring(0, 2)),
                    Integer.parseInt(intent.getExtras().getString("userBirthday").substring(3, 5)));
        }
        catch(Exception e)
        {
            birthday = Calendar.getInstance();
            e.printStackTrace();
        }

        mPager = (ViewPager) findViewById(R.id.signup_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ScreenSlideChangeListener());

        dot1 = (ImageView) findViewById(R.id.signup_dot1);
        dot2 = (ImageView) findViewById(R.id.signup_dot2);
        dot3 = (ImageView) findViewById(R.id.signup_dot3);
        dot4 = (ImageView) findViewById(R.id.signup_dot4);
        dot5 = (ImageView) findViewById(R.id.signup_dot5);
        dot6 = (ImageView) findViewById(R.id.signup_dot6);
    }

    public void saveUserData()
    {
        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(this);
        if ( email.equals("") )
        {
            mPager.setCurrentItem(0);
            fieldAlert.setMessage("Please specify your email address.")
                    .setCancelable(true)
                    .show();
        }
        else if ( password.equals("") )
        {
            mPager.setCurrentItem(1);
            fieldAlert.setMessage("Please specify your password.")
                    .setCancelable(true)
                    .show();
        }
        else if ( gender.equals("") )
        {
            mPager.setCurrentItem(3);
            fieldAlert.setMessage("Please specify your gender.")
                    .setCancelable(true)
                    .show();
        }
        else if ( friendname.equals("") )
        {
            fieldAlert.setMessage("Please specify your emergency friend's name.")
                    .setCancelable(true)
                    .show();
        }
        else if ( friendphone.equals("0") )
        {
            fieldAlert.setMessage("Please specify your emergency friend's phone number.")
                    .setCancelable(true)
                    .show();
        }
        else if ( birthday == Calendar.getInstance() )
        {
            mPager.setCurrentItem(2);
            fieldAlert.setMessage("Please specify your birthday.")
                    .setCancelable(true)
                    .show();
        }
        else
        {
            User bestFriend = new User();
            friendname = friendname.trim();
            if ( friendname.contains(" ") )
            {
                bestFriend.setFirstName(friendname.substring(0, friendname.lastIndexOf(' ')).trim());
                bestFriend.setLastName(friendname.substring(friendname.lastIndexOf(' ') + 1).trim());
            }
            else if ( friendname.contains(",") )
            {
                bestFriend.setFirstName(friendname.substring(0, friendname.lastIndexOf(',')).trim());
                bestFriend.setLastName(friendname.substring(friendname.lastIndexOf(',') + 1).trim());
            }
            else
            {
                bestFriend.setFirstName(friendname);
                bestFriend.setLastName("");
            }
            bestFriend.setPhone(friendphone);
            bestFriend.setUserID("1000");

            User user = new User("0",
                    "Noname", "Duh",
                    email, password,
                    "", gender,
                    "0", new MapAddress(),
                    birthday,
                    new ArrayList<String>(), //best friend list
                    new ArrayList<String>(), //followers list
                    new ArrayList<com.thewavesocial.waveandroid.BusinessObjects.BestFriend>(), //following list
                    new ArrayList<String>(), //attending list
                    new ArrayList<String>(), //party attended list
                    new ArrayList<String>(), //party hosted list
                    new ArrayList<String>(), //party bounced list
                    new ArrayList<String>(),
                    new ArrayList<Notification>(),
                    new ArrayList<Notification>(),
                    new BitmapDrawable());
            //user.getBestFriends().add(bestFriend.getUserID());
            user.setProfilePic(profilePic);

            CurrentUser.setTheUser(user);

            Intent intent = new Intent(this, HomeSwipeActivity.class);
            startActivity(intent);

            finish();
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

    private class ScreenSlideChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            resetDots(position + 1);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    }

    private void resetDots(int position)
    {
        switch(position)
        {
            case 1:
                dot1.setImageResource(R.drawable.hollow_dot);
                dot2.setImageResource(R.drawable.solid_dot);
                dot6.setImageResource(R.drawable.solid_dot);
                break;
            case 2:
                dot2.setImageResource(R.drawable.hollow_dot);
                dot1.setImageResource(R.drawable.solid_dot);
                dot3.setImageResource(R.drawable.solid_dot);
                dot6.setImageResource(R.drawable.solid_dot);
                break;
            case 3:
                dot3.setImageResource(R.drawable.hollow_dot);
                dot2.setImageResource(R.drawable.solid_dot);
                dot4.setImageResource(R.drawable.solid_dot);
                dot6.setImageResource(R.drawable.solid_dot);
                break;
            case 4:
                dot4.setImageResource(R.drawable.hollow_dot);
                dot3.setImageResource(R.drawable.solid_dot);
                dot5.setImageResource(R.drawable.solid_dot);
                dot6.setImageResource(R.drawable.solid_dot);
                break;
            case 5:
                dot5.setImageResource(R.drawable.hollow_dot);
                dot4.setImageResource(R.drawable.solid_dot);
                dot6.setImageResource(R.drawable.solid_dot);
                break;
            case 6:
                dot6.setImageResource(R.drawable.hollow_dot);
                dot5.setImageResource(R.drawable.solid_dot);
                break;
        }
    }
}
