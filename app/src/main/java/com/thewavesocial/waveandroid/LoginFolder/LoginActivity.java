package com.thewavesocial.waveandroid.LoginFolder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.HomeDrawerActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UserFolder.EditUserProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

public class LoginActivity extends AppCompatActivity
{
    private TextView loginText, forgotPassText, signupText;
    private ImageView loginButton;
    private EditText emailField, passField;

    private ViewGroup viewGroup;
    private Activity mainActivity;

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
                // TODO: 02/21/2017 Forgot password screen
            }
        });

        signupText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/21/2017 Sign up screen
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();
                if ( email.equals( "dmario@ucsb.edu" ) )
                {
                    if ( pass.equals( "dmario123" ) )
                    {
                        Intent intent = new Intent(mainActivity, HomeDrawerActivity.class);
                        startActivity(intent);
                        // TODO: 02/21/2017 Reset CurrentUser info
                    }
                    else
                    {
                        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(mainActivity);
                        fieldAlert.setMessage("Incorrect Password. Please try again.")
                                .setCancelable(true)
                                .show();
                    }
                }
                else
                {
                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(mainActivity);
                    fieldAlert.setMessage("Unrecognized Email. Please create an account.")
                            .setCancelable(true)
                            .show();
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
        loginText = (TextView) findViewById(R.id.login_text_login);
        forgotPassText = (TextView) findViewById(R.id.login_text_forgotpassword);
        signupText = (TextView) findViewById(R.id.login_text_signup);
        loginButton = (ImageView) findViewById(R.id.login_button);
        emailField = (EditText) findViewById(R.id.login_edittext_email);
        passField = (EditText) findViewById(R.id.login_edittext_password);
    }
}
