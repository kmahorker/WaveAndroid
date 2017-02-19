package com.thewavesocial.waveandroid.UserFolder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.Calendar;


public class EditUserProfileActivity extends AppCompatActivity
{
    EditText edit_email, edit_school, edit_bday, edit_address;
    TextView username;
    ImageView profile_pic;
    Calendar birthday;
    User user;
    Activity mainActivity;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        mainActivity = this;

        Intent intent = getIntent();
        user = CurrentUser.theUser;

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
        LatLng latlng = UtilityClass.getLocationFromAddress( this, edit_address.getText().toString());
        if ( latlng == null )
        {
            AlertDialog.Builder fieldAlert = new AlertDialog.Builder(this);
            fieldAlert.setMessage("Please enter a valid address.")
                    .setCancelable(true)
                    .show();
        }
        else
        {
            user.setEmail(edit_email.getText().toString());
            user.setCollege(edit_school.getText().toString());
            user.getMapAddress().setAddress_string(edit_address.getText().toString());
            user.getMapAddress().setAddress_latlng(latlng);
            user.getBirthday().set(birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DATE));
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            onBackPressed();
        }
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
        username = (TextView) findViewById(R.id.edit_username);

        //update text with old user info
        edit_email.setText(user.getEmail());
        edit_school.setText(user.getCollege());
        birthday = user.getBirthday();
        edit_bday.setText( UtilityClass.dateToString(birthday) );
        edit_bday.setFocusable(false);
        edit_address.setText(user.getMapAddress().getAddress_string());
        profile_pic.setImageDrawable(user.getProfilePic());
        username.setText(user.getFullName());
    }

    //setup the actionbar + onClick for saveData
    private void updateActionbar()
    {
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
            }
        });
    }

    //set unfocused listeners
    private void updateOnClicks()
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

        edit_bday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(mainActivity, dateListener,
                        user.getBirthday().get(Calendar.YEAR),
                        user.getBirthday().get(Calendar.MONTH),
                        user.getBirthday().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d)
        {
            birthday.set(Calendar.YEAR, y);
            birthday.set(Calendar.MONTH, m);
            birthday.set(Calendar.DAY_OF_MONTH, d);
            edit_bday.setText( UtilityClass.dateToString(birthday) );
        }
    };
}
