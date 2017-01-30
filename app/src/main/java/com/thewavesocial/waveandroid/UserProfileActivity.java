package com.thewavesocial.waveandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity
{
    private User user;
    private EditText username, college;
    private TextView age;
    ImageView image;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.user_profile);

        user = CurrentUser.getTheUser();
        username = (EditText)findViewById(R.id.user_name);
        college = (EditText)findViewById(R.id.edit_college);
        age = (TextView)findViewById(R.id.user_age);
        image = (ImageView)findViewById(R.id.profile_pic);

        username.setText(user.getFullName());
        age.setText("Age: " + computeAge(user.getBirthday()));
        college.setText(user.getCollege());
        //image = user.getImage();

        username.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                user.setFirstName(username.getText().toString()
                        .substring(0, username.getText().toString().lastIndexOf(' ')));
                user.setLastName(username.getText().toString()
                        .substring(username.getText().toString().lastIndexOf(' ') + 1));
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });

        college.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                user.setCollege( college.getText().toString() );
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });

        updatePartiesAttended( user.getAttending() );
        updatePartiesHosted( user.getHosting() );
    }

    private int computeAge(Date birth)
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
