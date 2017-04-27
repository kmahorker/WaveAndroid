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
import com.thewavesocial.waveandroid.HomeFolder.MapsFragment;
import com.thewavesocial.waveandroid.HostFolder.HostControllerFragment;
import com.thewavesocial.waveandroid.SocialFolder.UserProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

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
        CurrentUser.setContext(this);
        mainActivity = this;
        setupMapActionbar();

        mPager = (ViewPager) findViewById(R.id.new_activity_home_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(new ScreenSlideChangeListener());

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
}
