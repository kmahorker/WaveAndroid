package com.thewavesocial.waveandroid;

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

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.SearchFolder.MapsFragment;
import com.thewavesocial.waveandroid.HostFolder.HostControllerFragment;
import com.thewavesocial.waveandroid.UserFolder.UserProfileFragment;

public class HomeActivity extends AppCompatActivity
{
    private PagerAdapter mPagerAdapter;
    public ViewPager mPager;
    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        CurrentUser.setContext(this);
        setupMapActionbar();

        mPager = (ViewPager) findViewById(R.id.new_activity_home_viewpager);
        mPagerAdapter = new HomeActivity.ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(new ScreenSlideChangeListener());
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
            switch( position )
            {
                case 0:
                    return new HostControllerFragment();
                case 1:
                    return new MapsFragment();
                default:
                    return new UserProfileFragment();
            }
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
            Log.d("Position", position+"");
            switch(position)
            {
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
        public void onPageScrollStateChanged(int state)
        {

        }
    }

    //actionbar settings
    public void setupMapActionbar()
    {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_activity_home);
        TextView hostText = (TextView) findViewById(R.id.actionbar_activity_home_text_host);
        TextView socialText = (TextView) findViewById(R.id.actionbar_activity_home_text_social);

        hostText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPager.setCurrentItem( mPager.getCurrentItem() - 1 );
            }
        });

        socialText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPager.setCurrentItem( mPager.getCurrentItem() + 1 );
            }
        });
    }

    public void setupHostActionbar()
    {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_hostcontroller);

        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_host_image_plug);
        plug_icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPager.setCurrentItem(mPager.getCurrentItem()+1);
            }
        });
    }

    public void setupUserActionbar()
    {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_user);

        ImageView plug_icon = (ImageView) findViewById(R.id.actionbar_user_image_plug);
        plug_icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPager.setCurrentItem(mPager.getCurrentItem()-1);
            }
        });
    }
}
