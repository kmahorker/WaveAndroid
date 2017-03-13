package com.thewavesocial.waveandroid.UserFolder;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.UserNotificationCustomAdapter;
import com.thewavesocial.waveandroid.AdaptersFolder.UserPartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfileFragment extends Fragment
{
    private User user;
    private TextView followers_textview, following_textview;
    private ListView notification_listview;
    private ImageView profilepic_imageview;
    private UserProfileFragment userProfileFragment;
    private HomeActivity mainActivity;

    @Override
    //get fragment layout reference
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.profile_user, container, false);
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
        followers_textview = (TextView) mainActivity.findViewById(R.id.user_followers_count);
        following_textview = (TextView) mainActivity.findViewById(R.id.user_following_count);
        profilepic_imageview = (ImageView) mainActivity.findViewById(R.id.user_profile_pic);
        notification_listview = (ListView) mainActivity.findViewById(R.id.user_notification_list);

        followers_textview.setText(CurrentUser.theUser.getFollowers().size() + "\nfollowers");
        following_textview.setText(CurrentUser.theUser.getFollowing().size() + "\nfollowing");

        if (CurrentUser.theUser.getProfilePic() != null)
        {
            profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                    CurrentUser.theUser.getProfilePic().getBitmap()));
        }
        notification_listview.setAdapter( new UserNotificationCustomAdapter(getActivity(),
                CurrentUser.theUser.getNotifications1()));
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
}
