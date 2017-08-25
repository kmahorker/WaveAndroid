package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import org.json.JSONException;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseAccess.setContext(mainActivity);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_tutorial);
        setupReferences();
    }


    private void setupReferences() {
        mPager = (ViewPager) findViewById(R.id.login_tutorial_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ScreenSlideChangeListener());

        dot1 = (ImageView) findViewById(R.id.login_tutorial_dot1);
        dot2 = (ImageView) findViewById(R.id.login_tutorial_dot2);
        dot3 = (ImageView) findViewById(R.id.login_tutorial_dot3);

        mAuth = FirebaseAuth.getInstance();

        //setup facebook
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                processJSONObject(loginResult.getAccessToken().getToken(), graphResponse.getJSONObject());
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            resetDots(position + 1);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    private void resetDots(int position) {
        switch (position) {
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



    private void processJSONObject(final String token, final JSONObject json) {
        Log.d("FacebookLogin", json.toString());

        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("Firebase_Auth_Result", "SignInWithCredential:Success - " + user.getUid() + ", " + user.getDisplayName() + ", " + user.getEmail());

                            // TODO: 08/24/2017 This method below is not working. Please fix.
                            
//                            DatabaseAccess.server_login_facebook(token, new OnResultReadyListener<String>() {
//                                //If login not successful, create new user and login again
//                                @Override
//                                public void onResultReady(String result) {
//                                    if (result.equals("success")) {
//                                        Intent intent = new Intent(mainActivity, HomeSwipeActivity.class);
//                                        startActivity(intent);
//                                    } else {
//                                        String fname, lname, email, college, password, fb_id, fb_token, gender, birthday;
//                                        try {
//                                            fname = (!json.has("name")) ? "N/A" : json.getString("name").substring(0, json.getString("name").lastIndexOf(' '));
//                                            lname = (!json.has("name")) ? "N/A" : json.getString("name").substring(json.getString("name").lastIndexOf(' ') + 1);
//                                            email = "N/A";
//                                            college = "N/A";
//                                            password = "N/A";
//                                            fb_id = (!json.has("id")) ? "N/A" : json.getString("id");
//                                            fb_token = token;
//                                            gender = (!json.has("gender")) ? "N/A" : json.getString("gender");
//                                            birthday = (!json.has("birthday")) ? "" : json.getString("birthday");
//
//                                            DatabaseAccess.server_createNewUser(fname, lname, password, fb_id, fb_token, gender, birthday, new OnResultReadyListener<String>() {
//                                                @Override
//                                                public void onResultReady(String result) {
//                                                    if (result != null) {
//                                                        DatabaseAccess.server_login_facebook(token, new OnResultReadyListener<String>() {
//                                                            @Override
//                                                            public void onResultReady(String result) {
//                                                                if (result.equals("success")) {
//                                                                    Intent intent = new Intent(mainActivity, HomeSwipeActivity.class);
//                                                                    startActivity(intent);
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            });
//                                        } catch (JSONException | NullPointerException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase Signin", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginTutorialActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        try
//        {
//            if ( json.getString("id") == "100000000000" ) // TODO: 03/01/2017 Check with database
//            {
//                //login
//                intentLogin.putExtra("userIDLong", json.getString("id"));
//                startActivity(intentLogin);
//                finish();
//            }
//            if ( Integer.parseInt(json.getString("age_range").substring(
//                    json.getString("age_range").lastIndexOf(':')+1, json.getString("age_range").length()-1)) < 17 ) {
//                UtilityClass.printAlertMessage(this, "Sorry. This app is limited to 17+ (College Students) only.", "Underage!", true);
//                return;
//            }
//            else
//            {
                /*
                //signup
                intentSignup.putExtra("userIDLong", json.getString("id"));
                intentSignup.putExtra("userName", json.getString("name"));
                intentSignup.putExtra("userEmail", json.getString("email"));
                intentSignup.putExtra("userGender", json.getString("gender"));
                intentSignup.putExtra("userBirthday", json.getString("birthday"));*/

        //Parse Birthdays
//                String string = json.getString("birthday");
//                String pattern1 = "MM/dd/yyyy";
//                String pattern2 = "MM/dd";
//                String pattern3 = "yyyy";
//                Date date = null;
//                Calendar calendar = null;
//                try {
//                    if(string.length() == pattern1.length()) {
//                        date = new SimpleDateFormat(pattern1).parse(string);
//                    }
//                    else if(string.length() == pattern2.length()){
//                        date = new SimpleDateFormat(pattern2).parse(string);
//                    }
//                    else{
//                        date = new SimpleDateFormat(pattern3).parse(string);
//                    }
//                    if(date != null) {
//                        calendar = Calendar.getInstance();
//                        calendar.setTime(date);
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                //Get profile pic and convert to bitmapdrawable
//                String id = json.getString("id");
//                URL img_value = null;
//                try {
//                    img_value = new URL("http://graph.facebook.com/"+id+"/picture?type=large");
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//
//                // TODO: 04/21/2017 Add image by url
//                String profilePic = "";
////                    Bitmap bitmap = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
////                    BitmapDrawable profilePic = new BitmapDrawable(bitmap);
//                User newUser = new User(json.getString("id"),
//                        json.getString("first_name"),
//                        json.getString("last_name"),
//                        json.getString("email"),
//                        "password" /*TODO: delete password field*/,
//                        "UCSB" /*TODO: delete college field*/,
//                        json.getString("gender"),
//                        calendar,
//                        new ArrayList<String>(), //followers
//                        new ArrayList<String>(), //following
//                        new ArrayList<com.thewavesocial.waveandroid.BusinessObjects.BestFriend>(), //bestFriends
//                        new ArrayList<String>(), //hosting
//                        new ArrayList<String>(), //attended
//                        new ArrayList<String>(), //hosted
//                        new ArrayList<String>(), //bounced
//                        new ArrayList<String>(), //attending
//                        new ArrayList<String>(), //going
//                        new ArrayList<Notification>(),
//                        profilePic);
//                //TODO: Add user object to Database
//            }
//        }
//        catch (JSONException e) {
//            System.out.println("Error with JSON LOL" + e.getLocalizedMessage());
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
