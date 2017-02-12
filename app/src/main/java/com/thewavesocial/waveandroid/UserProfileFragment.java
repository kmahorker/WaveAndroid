package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfileFragment extends Fragment
{
    private User user;
    private TextView college;
    private TextView age;
    private final UserProfileFragment userProfileFragment = this;
    ImageView image;

    @Override
    //get fragment layout reference
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.user_profile, container, false);
    }

    @Override
    //initialize everything
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
//        CurrentUser.setContext(getContext());
//        user = CurrentUser.theUser;
        user = new User();
        CurrentUser.setContext(getContext());

        setupProfileInfo();
        setupActionbar();
        updateSample();
    }

    @Override
    //Update result after new user data is saved
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 1)
        {
            //store data to User
            User savedUser = data.getExtras().getParcelable("savedUserInfo");
            user.setEmail(savedUser.getEmail());
            user.setCollege(savedUser.getCollege());
            user.setBirthday(savedUser.getBirthday());
            user.setAddress(savedUser.getAddress());
            user.setProfilePic(savedUser.getProfilePic());

            //display data on profile
            setupActionbar();
            setupProfileInfo();
        }
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize actionbar
    private void setupActionbar()
    {
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_user);

        TextView username = (TextView) getActivity().findViewById(R.id.actionbar_username);
        username.setText(user.getFirstName() + " " + user.getLastName());

        ImageView actionbar_userprofile = (ImageView) getActivity().findViewById(R.id.actionbar_userprofile);
        actionbar_userprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), EditUserProfileActivity.class);
                intent.putExtra("myProfileObj", user);
                startActivityForResult(intent, 1);
            }
        });
    }

    //initialize user information
    private void setupProfileInfo()
    {
        college = (TextView)getActivity().findViewById(R.id.user_college);
        college.setText("College: " + user.getCollege());
        age = (TextView)getActivity().findViewById(R.id.user_age);
        age.setText("Age: " + computeAge(user.getBirthday()));
        image = (ImageView)getActivity().findViewById(R.id.profile_pic);
        image.setImageDrawable(user.getProfilePic());

        //updatePartiesAttended( user.getAttended() );
        //updatePartiesHosted( user.getHosted() );
    }

//----------------------------------------------------------------------------------Other Sub-tasks

    //compute age based on birthday: need fixed
    private int computeAge(Calendar birth)
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
    private void updatePartiesAttended(List<Party> list)
    {
        ListView partyListView = (ListView)getActivity().findViewById(R.id.events_attended_list);
        partyListView.setAdapter( new UserPartyCustomAdapter(getActivity(), userProfileFragment, list));
    }

    //update parties hosted
    private void updatePartiesHosted(List<Party> list)
    {
        ListView partyListView = (ListView)getActivity().findViewById(R.id.events_hosted_list);
        partyListView.setAdapter( new UserPartyCustomAdapter(getActivity(), userProfileFragment, list));
    }

    //Sample dummies for testing purpose
    private void updateSample()
    {
        List<Party> sampleList = new ArrayList<Party>();
        sampleList.add(new Party());
        sampleList.add(new Party());
        sampleList.add(new Party());
        sampleList.add(new Party());
        sampleList.add(new Party());

        sampleList.get(0).setName("SuperParty1");
        sampleList.get(1).setName("Super Super Party 2");
        sampleList.get(2).setName("Sad Party 3");
        sampleList.get(3).setName("Boring Party 4");
        sampleList.get(4).setName("Drunk Party 5");

        sampleList.get(0).getStartingDateTime().set(2017, 2, 15);
        sampleList.get(1).getStartingDateTime().set(2017, 3, 20);
        sampleList.get(2).getStartingDateTime().set(2017, 3, 31);
        sampleList.get(3).getStartingDateTime().set(2017, 5, 4);
        sampleList.get(4).getStartingDateTime().set(2017, 6, 1);

        updatePartiesAttended(sampleList);
        updatePartiesHosted( sampleList );

        age.setText("Age: " + 18);
        college.setText("College: " + "UCSB");
        image.setImageResource(R.drawable.profile_sample);
    }
}
