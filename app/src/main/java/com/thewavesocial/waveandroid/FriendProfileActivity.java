package com.thewavesocial.waveandroid;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FriendProfileActivity extends AppCompatActivity
{
    private User friend;

    @Override
    //initialize everything
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.friend_profile);

        // access current friend data
        Intent intent = getIntent();
        friend = intent.getExtras().getParcelable("userObj");

        setupFriendInfo();
        setupActionbar();
    }

    @Override
    //onClick event for back button pressed
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize actionbar
    private void setupActionbar()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(friend.getFirstName() + " " + friend.getLastName());
    }

    //initialize friend data
    private void setupFriendInfo()
    {
        TextView username = (TextView)findViewById(R.id.user_college);
        ImageView image = (ImageView)findViewById(R.id.profile_pic);

        username.setText("College: " + friend.getCollege());
        image.setImageDrawable(friend.getProfilePic());

        updateAge(friend.getBirthday());
        updatePartiesAttended(friend.getAttended());
        updatePartiesHosted(friend.getHosted());
    }

//----------------------------------------------------------------------------------Other Sub-tasks

    //update friend age
    private int updateAge(Calendar birth)
    {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR), month = now.get(Calendar.MONTH), day = now.get(Calendar.DATE);
        int byear = birth.get(Calendar.YEAR), bmonth = birth.get(Calendar.MONTH), bday = birth.get(Calendar.DATE);
        if (month == bmonth)
        {
            if (day < bday)
                return year - byear - 1;
            else
                return year - byear;
        }
        else if (month > bmonth)
            return year - byear;
        else
            return year - byear - 1;
    }

    //update parties attended
    private void updatePartiesAttended(List<Long> list)
    {
        Long[] parties = list.toArray(new Long[list.size()]);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.each_party_item, parties);
        ListView listView = (ListView)findViewById(R.id.events_attended_list);
        listView.setAdapter(arrayAdapter);
    }

    //update parties hosted
    private void updatePartiesHosted(List<Long> list)
    {
        Long[] parties = list.toArray(new Long[list.size()]);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.each_party_item, parties);
        ListView listView = (ListView)findViewById(R.id.events_hosted_list);
        listView.setAdapter(arrayAdapter);
    }
}
