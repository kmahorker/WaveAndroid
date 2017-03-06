package com.thewavesocial.waveandroid.LoginFolder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.thewavesocial.waveandroid.HomeDrawerActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
{
    private TextView forgotPassText, signupText;
    private Button loginButton;
    private LoginButton fbloginButton;
    private EditText emailField, passField;

    private ViewGroup viewGroup;
    private Activity mainActivity;
    public static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        mainActivity = this;

        setupActionbar();
        setupReferences();
        setupOnClicks();
        setupFacebook();
    }



    private void setupFacebook()
    {
        callbackManager = CallbackManager.Factory.create();
        fbloginButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "public_profile"));
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse)
                            {
                                processJSONObject(graphResponse.getJSONObject());
                            }
                        });

                //gets called when login is successful + request the following parameters
                //https://developers.facebook.com/docs/facebook-login/permissions
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, age_range, birthday, location, education");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException exception)
            {
                Toast.makeText(mainActivity, "Error Accessing Data", Toast.LENGTH_LONG);
            }
        });
    }

    private void processJSONObject(JSONObject json)
    {
        System.out.println(json);
        Intent intentSignup = new Intent(mainActivity, SignupActivity.class);
        try
        {
            if ( json.getString("id") == "100000000000" ) // TODO: 03/01/2017 Check with database
            {
                //login
                Intent intentLogin = new Intent(mainActivity, HomeDrawerActivity.class);
                intentLogin.putExtra("userIDLong", json.getString("id"));
                startActivity(intentLogin);
                finish();
            }
            if ( Integer.parseInt(json.getString("age_range").substring(
                    json.getString("age_range").lastIndexOf(':')+1, json.getString("age_range").length()-1)) < 18 )
            {
                UtilityClass.displayAlertMessage(this, "Sorry. This app is limited to 18+ (College Students) only.", true);
                return;
            }
            else
            {
                //signup
                intentSignup.putExtra("userIDLong", json.getString("id"));
                intentSignup.putExtra("userName", json.getString("name"));
                intentSignup.putExtra("userEmail", json.getString("email"));
                intentSignup.putExtra("userGender", json.getString("gender"));
                intentSignup.putExtra("userBirthday", json.getString("birthday"));
            }
        }
        catch (JSONException e)
        {
            System.out.println("Error with JSON LOL" + e.getLocalizedMessage());

        }
        startActivity(intentSignup);
    }

    private void setupOnClicks()
    {
        viewGroup.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });

        forgotPassText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(mainActivity, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signupText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(mainActivity, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();
                if (email.equals("dmario@ucsb.edu"))
                {
                    if (pass.equals("dmario123"))
                    {
                        Intent intent = new Intent(mainActivity, HomeDrawerActivity.class);
                        startActivity(intent);
                        finish();
                        // TODO: 02/21/2017 Reset CurrentUser info
                    }
                    else
                    {
                        UtilityClass.displayAlertMessage(mainActivity, "Incorrect Password. Please try again.", true);
                    }
                }
                else
                {
                    UtilityClass.displayAlertMessage(mainActivity, "Unrecognized Email. Please create an account.", true);
                }
            }
        });
    }

    private void setupActionbar()
    {
        getSupportActionBar().hide();
    }

    private void setupReferences()
    {
        forgotPassText = (TextView) findViewById(R.id.login_text_forgotpassword);
        signupText = (TextView) findViewById(R.id.login_text_signup);
        loginButton = (Button) findViewById(R.id.login_button);
        fbloginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        emailField = (EditText) findViewById(R.id.login_edittext_email);
        passField = (EditText) findViewById(R.id.login_edittext_password);

        emailField.setText("dmario@ucsb.edu");
        passField.setText("dmario123");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
