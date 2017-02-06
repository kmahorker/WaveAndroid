package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.User;


public class EditUserProfileActivity extends AppCompatActivity
{
    EditText edit_email, edit_school, edit_bday,edit_address;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_school = (EditText) findViewById(R.id.edit_school);
        edit_bday = (EditText) findViewById(R.id.edit_bday);
        edit_address = (EditText) findViewById(R.id.edit_address);

        Intent intent = getIntent();
        user = intent.getExtras().getParcelable("myProfileObj");

        edit_email.setText(user.getEmail());
        edit_school.setText(user.getCollege());
        edit_bday.setText(user.getBirthday().toString());
        edit_address.setText(user.getAddress());

        setActionbar();
    }

    private void setActionbar()
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
                updateUserData();
                onBackPressed();
            }
        });
    }

    private void updateUserData()
    {
        user.setEmail(edit_email.getText().toString());
        user.setCollege(edit_school.getText().toString());
        //user.setBirthday(edit_bday.getText().toString());
        user.setAddress(edit_address.getText().toString());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("savedUserInfo", user);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
