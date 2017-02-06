package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileFragment extends Fragment
{
    private User user;
    private TextView college;
    private TextView age;
    ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        user = CurrentUser.getTheUser();

        college = (TextView)view.findViewById(R.id.user_college);
        college.setText("College: " + user.getCollege());
        age = (TextView)view.findViewById(R.id.user_age);
        age.setText("Age: " + computeAge(user.getBirthday()));
        image = (ImageView)view.findViewById(R.id.profile_pic);
        image.setImageDrawable(user.getProfilePic());

        updatePartiesAttended( user.getAttending() );
        updatePartiesHosted( user.getHosting() );

        updateSample();
        updateActionbar();

    }

    private void updateActionbar()
    {
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_user);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());

//        TextView actionbar_username = (TextView) getActivity().findViewById(R.id.actionbar_username);
//        actionbar_username.setText(user.getFirstName() + " " + user.getLastName());

        ImageView actionbar_userprofile = (ImageView) getActivity().findViewById(R.id.actionbar_userprofile);
        actionbar_userprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), EditUserProfileActivity.class);
                intent.putExtra("myProfileObj", user);
                startActivity(intent);
            }
        });
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.each_party_item, parties);
        ListView listView = (ListView)getActivity().findViewById(R.id.events_attended_list);
        listView.setAdapter(arrayAdapter);
    }

    private void updatePartiesHosted(List<Long> list)
    {
        Long[] parties = list.toArray(new Long[list.size()]);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.each_party_item, parties);
        ListView listView = (ListView)getActivity().findViewById(R.id.events_hosted_list);
        listView.setAdapter(arrayAdapter);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(user.getCollege());
        if(resultCode == Activity.RESULT_OK)
        {
            User savedUser = data.getExtras().getParcelable("savedUserInfo");
            user.setEmail(savedUser.getEmail());
            user.setCollege(savedUser.getCollege());
            user.setBirthday(savedUser.getBirthday());
            user.setAddress(savedUser.getAddress());

            college = (TextView)getActivity().findViewById(R.id.user_college);
            college.setText("College: " + user.getCollege());
            age = (TextView)getActivity().findViewById(R.id.user_age);
            age.setText("Age: " + computeAge(user.getBirthday()));
            image = (ImageView)getActivity().findViewById(R.id.profile_pic);
            image.setImageDrawable(user.getProfilePic());

            updatePartiesAttended( user.getAttending() );
            updatePartiesHosted( user.getHosting() );
            updateActionbar();
            System.out.println(user.getCollege());
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            ((HomeDrawerActivity)getActivity()).openUserProfile();
        }
    }
}
