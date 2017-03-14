package com.thewavesocial.waveandroid.SocialFolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.UserNotificationCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.Calendar;

public class UserProfileFragment extends Fragment {

    public enum PopupPage {
        FOLLOWERS,
        FOLLOWING;
    }

    private User user;
    private TextView username_textview, followers_textview, following_textview;
    private ListView notification_listview;
    private ImageView profilepic_imageview;
    private UserProfileFragment userProfileFragment;
    private HomeSwipeActivity mainActivity;

    @Override
    //get fragment layout reference
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_user, container, false);
    }

    @Override
    //initialize everything
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfileFragment = this;
        mainActivity = (HomeSwipeActivity) getActivity();
        user = CurrentUser.theUser;

        setupProfileInfo();

        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });
    }

    @Override
    //Update result after new user data is saved
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            //display data on profile
            setupProfileInfo();
        }
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize user information
    private void setupProfileInfo() {
        followers_textview = (TextView) mainActivity.findViewById(R.id.user_followers_count);
        followers_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWERS);
            }
        });
        following_textview = (TextView) mainActivity.findViewById(R.id.user_following_count);
        following_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWING);
            }
        });

        username_textview = (TextView) mainActivity.findViewById(R.id.user_name);
        profilepic_imageview = (ImageView) mainActivity.findViewById(R.id.user_profile_pic);
        notification_listview = (ListView) mainActivity.findViewById(R.id.user_notification_list);

        followers_textview.setText(CurrentUser.theUser.getFollowers().size() + "");
        following_textview.setText(CurrentUser.theUser.getFollowing().size() + "");
        username_textview.setText(CurrentUser.theUser.getFullName());
        profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                CurrentUser.theUser.getProfilePic().getBitmap()));
        notification_listview.setAdapter(new UserNotificationCustomAdapter(getActivity(),
                CurrentUser.theUser.getNotifications()));
    }

    private void showPopup(PopupPage popup) {
        switch (popup) {
            case FOLLOWERS:
            case FOLLOWING:
                Intent intent = new Intent(mainActivity, FollowActivity.class);
                intent.putExtra(FollowActivity.FOLLOW_POPUP_TYPE_ARG, popup.name());
                mainActivity.startActivity(intent);
                break;
        }
    }

//----------------------------------------------------------------------------------Other Sub-tasks

    //compute age based on birthday: need fixed
    private int computeAge(Calendar birth) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR), month = now.get(Calendar.MONTH), day = now.get(Calendar.DATE);
        int byear = birth.get(Calendar.YEAR), bmonth = birth.get(Calendar.MONTH), bday = birth.get(Calendar.DATE);
        if (month == bmonth) {
            if (day < bday)
                return year - byear - 1;
            else
                return year - byear;
        } else if (month > bmonth)
            return year - byear;
        else
            return year - byear - 1;
    }
}
