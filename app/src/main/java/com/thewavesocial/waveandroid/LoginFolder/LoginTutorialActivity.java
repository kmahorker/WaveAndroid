package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
        Intent intentLogin = new Intent(mainActivity, HomeSwipeActivity.class);
        //new LoginTask("Specific URL with Facebook ID").execute();
        try
        {
            if ( json.getString("id") == "100000000000" ) // TODO: 03/01/2017 Check with database
            {
                //login
                intentLogin.putExtra("userIDLong", json.getString("id"));
                startActivity(intentLogin);
                finish();
            }
            if ( Integer.parseInt(json.getString("age_range").substring(
                    json.getString("age_range").lastIndexOf(':')+1, json.getString("age_range").length()-1)) < 17 )
            {
                UtilityClass.printAlertMessage(this, "Sorry. This app is limited to 17+ (College Students) only.", true);
                return;
            }
            else
            {
                /*
                //signup
                intentSignup.putExtra("userIDLong", json.getString("id"));
                intentSignup.putExtra("userName", json.getString("name"));
                intentSignup.putExtra("userEmail", json.getString("email"));
                intentSignup.putExtra("userGender", json.getString("gender"));
                intentSignup.putExtra("userBirthday", json.getString("birthday"));*/

                //Parse Birthdays
                String string = json.getString("birthday");
                String pattern1 = "MM/dd/yyyy";
                String pattern2 = "MM/dd";
                String pattern3 = "yyyy";
                Date date = null;
                Calendar calendar = null;
                try {
                    if(string.length() == pattern1.length()) {
                        date = new SimpleDateFormat(pattern1).parse(string);
                    }
                    else if(string.length() == pattern2.length()){
                        date = new SimpleDateFormat(pattern2).parse(string);
                    }
                    else{
                        date = new SimpleDateFormat(pattern3).parse(string);
                    }
                    if(date != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Get profile pic and convert to bitmapdrawable
                String id = json.getString("id");
                URL img_value = null;
                try {
                    img_value = new URL("http://graph.facebook.com/"+id+"/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                    BitmapDrawable profilePic = new BitmapDrawable(bitmap);
                    User newUser = new User(json.getString("id"),
                            json.getString("first_name"),
                            json.getString("last_name"),
                            json.getString("email"),
                            "password" /*TODO: delete password field*/,
                            "UCSB" /*TODO: delete college field*/,
                            json.getString("gender"),
                            "1231231234" /*TODO: delte ph#*/,
                            new MapAddress(),
                            calendar,
                            new ArrayList<String>(), //followers
                            new ArrayList<String>(), //following
                            new ArrayList<com.thewavesocial.waveandroid.BusinessObjects.BestFriend>(), //bestFriends
                            new ArrayList<String>(), //hosting
                            new ArrayList<String>(), //attended
                            new ArrayList<String>(), //hosted
                            new ArrayList<String>(), //bounced
                            new ArrayList<String>(), //attending
                            new ArrayList<Notification>(),
                            new ArrayList<Notification>(),
                            profilePic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO: Add user object to Database
            }
        }
        catch (JSONException e) {
            System.out.println("Error with JSON LOL" + e.getLocalizedMessage());
        }
        startActivity(intentLogin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    //Update user id and login token
    public void server_loginByEmail(String email, String password) {
        String url = getString(R.string.server_url)+"auth";
        HashMap<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        new DatabaseAccess.HttpRequestTask(mainActivity, url, "POST", body, new DatabaseAccess.OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Login_byEmail", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String user_id = jsonObject.getJSONObject("data").getString("id");
                    String access_token = jsonObject.getJSONObject("data").getString("jwt");
                    DatabaseAccess.saveTokentoLocal(mainActivity, user_id, access_token);
                    server_getUserInfo();
                } catch (JSONException e) {
                    UtilityClass.printAlertMessage(mainActivity, "Incorrect email or password", true);
                }
            }
        }).execute();
    }

    //Get user information
    private void server_getUserInfo() {
        String url = getString(R.string.server_url) + "users/" + DatabaseAccess.getTokenFromLocal(mainActivity)[0] + "?access_token=" + DatabaseAccess.getTokenFromLocal(mainActivity)[1];
        new DatabaseAccess.HttpRequestTask(mainActivity, url, "GET", null, new DatabaseAccess.OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Login_GetUserInfo", result);
                // TODO: 04/17/2017 Parse JSON
                createCurrentUser(new HashMap<String, Object>());
            }
        }).execute();
    }

    //Create current user
    private void createCurrentUser(HashMap<String, Object> info) {
        String userID = (String) info.get("id");
        String firstName = (String) info.get("first_name");
        String lastName = (String) info.get("last_name");
        String email = (String) info.get("email");
        String password = (String) info.get("password");
        String college = (String) info.get("college");
        String gender = (String) info.get("gender");
        String phone = (String) info.get("phone");
        MapAddress mapAddress = new MapAddress(); // TODO: 04/17/2017 what to store as address
        Calendar birthday = Calendar.getInstance();
        String strB = (String) info.get("birthday");
        birthday.set(Calendar.YEAR, Integer.parseInt(strB.substring(0, 4)));
        birthday.set(Calendar.YEAR, Integer.parseInt(strB.substring(5, 7)));
        birthday.set(Calendar.YEAR, Integer.parseInt(strB.substring(8)));

        List bestFriends = (ArrayList) info.get("best_friends");
        List followers = (ArrayList) info.get("followers");
        List following = (ArrayList) info.get("following");
        List hosting = (ArrayList) info.get("hosting");
        List attended = (ArrayList) info.get("attended");
        List hosted = (ArrayList) info.get("hosted");
        List bounced = (ArrayList) info.get("bounced");
        List attending = (ArrayList) info.get("attending");
        List notifications1 = (ArrayList) info.get("notifications1");
        List notifications2 = (ArrayList) info.get("notifications2");
        BitmapDrawable profilePic = new BitmapDrawable(); // TODO: 04/17/2017 Extract image

        //Compose user
        User user = new User(userID, firstName, lastName, email, password, college, gender, phone, mapAddress, birthday,
                bestFriends, followers, following, hosting, attended, hosted, bounced, attending, notifications1,
                notifications2, profilePic);
//        CurrentUser.theUser = user;
    }
}
