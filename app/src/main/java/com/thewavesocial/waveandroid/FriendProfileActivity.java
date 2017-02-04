package com.thewavesocial.waveandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FriendProfileActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.friend_profile);

        //TODO get intent, extract user object, and update the fields
        // access current user data
        User user = CurrentUser.getTheUser();
        updateUsername(user.getFullName());
        updateAge(user.getBirthday());
        updateCollege(user.getCollege());
        updatePartiesAttended(user.getAttending());
        updatePartiesHosted(user.getHosting());
    }

    private void updateUsername(String str)
    {
        TextView username = (TextView)findViewById (R.id.user_name);
        username.setText(str);
    }

    private void updatePicture(Object img)
    {
        //ImageView image = FindViewById<ImageView>(Resource.Id.profile_pic);
        //image.SetImageResource(img);
    }

    private void updateCollege(String str)
    {
        TextView username = (TextView)findViewById(R.id.user_college);
        username.setText("College: " + str);
    }

    private int updateAge(Date birth)
    {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR), month = now.get(Calendar.MONTH), day = now.get(Calendar.DATE);
        int byear = birth.getYear(), bmonth = birth.getMonth(), bday = birth.getDate();
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

    private void updatePartiesAttended(List<Long> list)
    {
        Long[] parties = list.toArray(new Long[list.size()]);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.each_party_item, parties);
        ListView listView = (ListView)findViewById(R.id.events_attended_list);
        listView.setAdapter(arrayAdapter);
    }

    private void updatePartiesHosted(List<Long> list)
    {
        Long[] parties = list.toArray(new Long[list.size()]);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.each_party_item, parties);
        ListView listView = (ListView)findViewById(R.id.events_hosted_list);
        listView.setAdapter(arrayAdapter);
    }
}
