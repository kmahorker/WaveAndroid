package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.Calendar;


public class EditUserProfileActivity extends AppCompatActivity
{
    EditText edit_email, edit_school, edit_bday,edit_address;
    ImageView profile_pic;
    User user;
    Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        mainActivity = this;

        Intent intent = getIntent();
        user = intent.getExtras().getParcelable("myProfileObj");
        updateFieldText();
        updateActionbar();
        updateOnClicks();
    }

    @Override
    //onClick event for back button pressed
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            askToSave();
        }
        return super.onOptionsItemSelected(item);
    }

    //ask user to save changes or not
    private void askToSave()
    {
        AlertDialog.Builder confirmMessage = new AlertDialog.Builder(this);
        confirmMessage.setTitle("Unsaved Data")
                .setMessage("Are you sure you want to discard the changes?")
                .setCancelable(false)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing
                    }
                })
                .show();
    }

    //send new data back to user info
    private void saveData()
    {
        user.setEmail(edit_email.getText().toString());
        user.setCollege(edit_school.getText().toString());
        setBirthday(user);
        user.setAddress(edit_address.getText().toString());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("savedUserInfo", user);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

//------------------------------------------------------------------------------ OnCreate Sub-tasks

    //update text fields with user info
    private void updateFieldText()
    {
        //get references
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_school = (EditText) findViewById(R.id.edit_school);
        edit_bday = (EditText) findViewById(R.id.edit_bday);
        edit_address = (EditText) findViewById(R.id.edit_address);
        profile_pic = (ImageView) findViewById(R.id.edit_profile_pic);

        //update text with old user info
        edit_email.setText(user.getEmail());
        edit_school.setText(user.getCollege());
        edit_bday.setText(user.getBirthday().get(Calendar.MONTH)
                + "/" + user.getBirthday().get(Calendar.DATE)
                + "/" + user.getBirthday().get(Calendar.YEAR));
        edit_address.setText(user.getAddress());
    }

    //setup the actionbar + onClick for saveData
    private void updateActionbar()
    {
        Log.d("query", "came to Actionbar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_edit_user);

        TextView save_text = (TextView) findViewById(R.id.actionbar_editsave);
        save_text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveData();
                onBackPressed();
            }
        });
    }

    //set unfocused listeners
    private void updateOnClicks()
    {
        edit_email.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    UtilityClass.hideKeyboard( mainActivity );
                }
            }
        });

        edit_school.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    UtilityClass.hideKeyboard( mainActivity );
                }
            }
        });

        edit_bday.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    UtilityClass.hideKeyboard( mainActivity );
                }
            }
        });

        edit_address.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    UtilityClass.hideKeyboard( mainActivity );
                }
            }
        });
    }

    public void setBirthday(User user)
    {
        //user.getBirthday().set(edit_bday.getText().toString());

    }
}
