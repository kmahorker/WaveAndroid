package com.thewavesocial.waveandroid.LoginFolder;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailField, newPass, confirmPass;
    private Button sendButton, createButton;
    private Activity mainActivity;
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_fragment1);
        viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        mainActivity = this;
        setupActionbar();
        setupReferences();
        setupOnClicks();
    }

    private void setupOnClicks() {
        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.setContentView(R.layout.forgot_password_fragment2);
                newPass = (EditText) findViewById(R.id.forgotPassword2_editttext_createPass);
                confirmPass = (EditText) findViewById(R.id.forgotPassword2_editttext_confirmPass);
                createButton = (Button) findViewById(R.id.forgotPassword2_button_create);

                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (newPass.getText().toString().equals(confirmPass.getText().toString())) {
                            onBackPressed();
                        } else {
                            AlertDialog.Builder fieldAlert = new AlertDialog.Builder(mainActivity);
                            fieldAlert.setMessage("Passwords not matched")
                                    .setCancelable(true)
                                    .show();
                        }
                    }
                });
            }
        });
    }

    private void setupActionbar() {
        getSupportActionBar().hide();
    }

    private void setupReferences() {
        emailField = (EditText) findViewById(R.id.forgotPassword1_editttext_email);
        sendButton = (Button) findViewById(R.id.forgotPassword1_button_send);
    }


}
