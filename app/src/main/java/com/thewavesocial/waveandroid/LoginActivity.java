package com.thewavesocial.waveandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Button loginButton = (Button) findViewById(R.id.button1);
        Button forgotPasswordButton = (Button) findViewById(R.id.button2);
        Button signUpButton = (Button) findViewById(R.id.button5);
        Button facebookLoginButton = (Button) findViewById(R.id.button3);
        AutoCompleteTextView emailTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        AutoCompleteTextView passwordTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        // Create your application here
    }
}
