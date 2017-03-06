package com.thewavesocial.waveandroid.UserFolder;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.UserPartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeActivity;
import com.thewavesocial.waveandroid.HomeDrawerActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfileFragment extends Fragment
{
    private User user;
    private TextView college;
    private TextView age;
    private ImageView image;
    private UserProfileFragment userProfileFragment;
    private HomeActivity mainActivity;

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
        userProfileFragment = this;
        mainActivity = (HomeActivity)getActivity();
        user = CurrentUser.theUser;

        setupProfileInfo();

        getView().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });
    }

    @Override
    //Update result after new user data is saved
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 1)
        {
            //display data on profile
            setupProfileInfo();
        }
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize user information
    private void setupProfileInfo()
    {
        college = (TextView)getActivity().findViewById(R.id.user_college);
        college.setText("College: " + user.getCollege());
        age = (TextView)getActivity().findViewById(R.id.user_age);
        age.setText("Age: " + computeAge(user.getBirthday()));
        image = (ImageView)getActivity().findViewById(R.id.profile_pic);
        image.setImageDrawable( UtilityClass.convertRoundImage(getResources(),
                user.getProfilePic().getBitmap()));

        updatePartiesAttended( CurrentUser.getPartyListObjects(user.getAttended()) );
        updatePartiesHosted( CurrentUser.getPartyListObjects(user.getHosted()) );
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
}
