package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginTutorialActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 3;
    private PagerAdapter mPagerAdapter;
    private ImageView dot1, dot2, dot3;
    private Button facebookLogin;
    public ViewPager mPager;
    private CallbackManager callbackManager;
    public final LoginTutorialActivity mainActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_tutorial);
        setupReferences();
    }


    private void setupReferences(){
        mPager = (ViewPager) findViewById(R.id.login_tutorial_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ScreenSlideChangeListener());

        dot1 = (ImageView) findViewById(R.id.login_tutorial_dot1);
        dot2 = (ImageView) findViewById(R.id.login_tutorial_dot2);
        dot3 = (ImageView) findViewById(R.id.login_tutorial_dot3);

        //setup facebook
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        processJSONObject(graphResponse.getJSONObject());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, age_range, birthday, location, education");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                Toast.makeText(mainActivity, "Cancel", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mainActivity, "Error" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        facebookLogin = (Button) findViewById(R.id.facebook_login_button);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = new ArrayList<String>();
                permissions.add("public_profile");
                LoginManager.getInstance().logInWithReadPermissions(mainActivity, permissions);
            }
        });
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            LoginTutorialFragment fragment = new LoginTutorialFragment(position + 1);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


    private class ScreenSlideChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            resetDots(position + 1);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }


    private void resetDots(int position) {
        switch(position) {
            case 1:
                dot1.setImageResource(R.drawable.pink_full_dot);
                dot2.setImageResource(R.drawable.pink_hollow_dot);
                dot3.setImageResource(R.drawable.pink_hollow_dot);
                break;
            case 2:
                dot1.setImageResource(R.drawable.pink_hollow_dot);
                dot2.setImageResource(R.drawable.pink_full_dot);
                dot3.setImageResource(R.drawable.pink_hollow_dot);

                break;
            case 3:
                dot1.setImageResource(R.drawable.pink_hollow_dot);
                dot2.setImageResource(R.drawable.pink_hollow_dot);
                dot3.setImageResource(R.drawable.pink_full_dot);
                break;
        }
    }


    private void processJSONObject(JSONObject json) {
        Toast.makeText(this, "Home", Toast.LENGTH_LONG).show();
        System.out.println(json);
        Intent intentLogin = new Intent(mainActivity, HomeSwipeActivity.class);
        startActivity(intentLogin);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
