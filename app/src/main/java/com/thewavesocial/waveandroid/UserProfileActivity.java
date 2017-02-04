package com.thewavesocial.waveandroid;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity
{
    private User user;
    private TextView college;
    private TextView age;
    ImageView image;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.user_profile);

        user = CurrentUser.getTheUser();

        college = (TextView)findViewById(R.id.user_college);
        college.setText("College: " + user.getCollege());
        age = (TextView)findViewById(R.id.user_age);
        age.setText("Age: " + computeAge(user.getBirthday()));
        image = (ImageView)findViewById(R.id.profile_pic);
        //image = user.getImage();

        updatePartiesAttended( user.getAttending() );
        updatePartiesHosted( user.getHosting() );

        updateSample();
        updateActionBar();

//        college.addTextChangedListener(new TextWatcher()
//        {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
//            {
//                user.setCollege( college.getText().toString() );
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable){}
//        });
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

    private void updateActionBar()
    {
        //getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.profile_edit_button:
                //action here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSample()
    {
        List<Long> sampleList = new ArrayList<Long>();
        sampleList.add(new Long(1));
        sampleList.add(new Long(2));
        sampleList.add(new Long(3));
        sampleList.add(new Long(4));
        sampleList.add(new Long(5));
        updatePartiesAttended(sampleList);
        updatePartiesHosted( sampleList );

        age.setText("Age: " + 18);
        college.setText("College: " + "UCSB");
        image.setImageResource(R.drawable.profile_sample);
        getSupportActionBar().setTitle("            Melvin Zaid");
    }
}
