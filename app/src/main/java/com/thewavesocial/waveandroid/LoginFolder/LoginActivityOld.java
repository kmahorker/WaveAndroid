package com.thewavesocial.waveandroid.LoginFolder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

public class LoginActivityOld extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout_old);

        Button loginButton = (Button) findViewById(R.id.button1);
        TextView forgotPasswordButton = (TextView) findViewById(R.id.button2);
        Button signUpButton = (Button) findViewById(R.id.button5);
        Button facebookLoginButton = (Button) findViewById(R.id.button3);
        AutoCompleteTextView emailTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        AutoCompleteTextView passwordTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        // Create your application here
    }
}
