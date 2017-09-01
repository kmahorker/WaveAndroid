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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginTutorialActivity extends AppCompatActivity {

    private static final String TAG = HomeSwipeActivity.TAG;
    private static final int NUM_PAGES = 3;
    private PagerAdapter mPagerAdapter;
    private ImageView dot1, dot2, dot3;
    private Button facebookLogin;
    public ViewPager mPager;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginTutorialActivity.onCreate");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            DatabaseAccess.server_getUserObjectWithFriends(user.getUid(), new OnResultReadyListener<User>() {
                @Override
                public void onResultReady(User result) {
                    if(result != null) {
                        Log.d(TAG, "Firebase is authenticated and user info exists; starting HomeSwipeActivity ...");
                        CurrentUser.setUser(result);
                        startHomeSwipeActivity();
                    } else {
                        Log.d(TAG, "Firebase is authenticated but user info does not exist");
                    }
                }
            });
        }

        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_tutorial);


        mPager = (ViewPager) findViewById(R.id.login_tutorial_viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ScreenSlideChangeListener());

        dot1 = (ImageView) findViewById(R.id.login_tutorial_dot1);
        dot2 = (ImageView) findViewById(R.id.login_tutorial_dot2);
        dot3 = (ImageView) findViewById(R.id.login_tutorial_dot3);

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginTutorialActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginTutorialActivity.this, "Error" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        facebookLogin = (Button) findViewById(R.id.facebook_login_button);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start Facebook login...");
                List<String> permissions = new ArrayList<String>();
                permissions.add("public_profile");
                LoginManager.getInstance().logInWithReadPermissions(LoginTutorialActivity.this, permissions);
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


    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("FacebookLogin", "");

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Log.i(TAG,
                                    "signInWithCredential:Success\n" +
                                    "facebook ID(AccessToken):" + token.getUserId() + "\n" +
                                    "provider0 ID(FirebaseUser):" + firebaseUser.getProviderData().get(1).getUid() + "\n" +
                                    "name:" + firebaseUser.getDisplayName() + "\n" +
                                    "email:" + firebaseUser.getEmail() + "\n" +
                                    "providers:" + firebaseUser.getProviders() + "\n" +
                                    "photo URL:" + firebaseUser.getPhotoUrl() + "\n"
                            );

                            DatabaseAccess.server_getUserObject(firebaseUser.getUid(), new OnResultReadyListener<User>() {
                                @Override
                                public void onResultReady(User userInfo) {
                                    if (userInfo != null) {
                                        CurrentUser.setUser(userInfo);
                                        startHomeSwipeActivity();
                                    } else {
                                        Log.i(TAG, "user info for " + firebaseUser.getDisplayName() + " does not exist, creating new database entry...");
                                        createNewUserFromFacebookToken(token, firebaseUser.getUid(), new OnResultReadyListener<Boolean>() {
                                            @Override
                                            public void onResultReady(Boolean result) {
                                                startHomeSwipeActivity();
                                            }
                                        });
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginTutorialActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startHomeSwipeActivity(){
        Intent intent = new Intent(this, HomeSwipeActivity.class);
        //do not add activity to back stack
        //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void createNewUserFromFacebookToken(final AccessToken token, final String uid, final OnResultReadyListener<Boolean> delegate){
        final GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                final User user = new User();
                try {
                    Log.i(TAG, "createNewUserFromFacebookToken: facebook info received:" + jsonObject.toString(2));
                    //https://developers.facebook.com/docs/android/graph/

                    if ( jsonObject.has("id")) {
                        user.setFacebookID(jsonObject.getString("id"));
                    }
                    if ( jsonObject.has("name") && !jsonObject.isNull("name") && !jsonObject.getString("name").isEmpty() ) {
                        user.setFirst_name(jsonObject.getString("name").substring(0, jsonObject.getString("name").indexOf(' ')));
                        user.setLast_name(jsonObject.getString("name").substring(jsonObject.getString("name").lastIndexOf(' ') + 1));
                    }
                    if ( jsonObject.has("gender") ) {
                        user.setGender(jsonObject.getString("gender"));
                    }


                }catch (JSONException e){
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                user.setId(uid); //will always get called

                DatabaseAccess.server_createNewUser(user, new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        Log.i(TAG, "createNewUserFromFacebookToken:Success - name:" + user.getFull_name() + ", key:" + result + ", facebookID:" + user.getFacebookID());
                        //sync instead of returning User directly to ensure data is consistent with subsequent login
                        CurrentUser.syncUser(new OnResultReadyListener<User>() {
                            @Override
                            public void onResultReady(User result) {
                                    if(delegate != null){
                                    delegate.onResultReady(true);
                                }
                            }
                        });
                    }
                });
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, age_range, birthday, location, education");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
