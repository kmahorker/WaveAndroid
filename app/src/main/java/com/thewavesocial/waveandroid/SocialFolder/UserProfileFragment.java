package com.thewavesocial.waveandroid.SocialFolder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.UserActionAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class UserProfileFragment extends Fragment {

    private TextView activityButton, attendingButton;

    public enum PopupPage {
        FOLLOWERS,
        FOLLOWING;
    }

    private User user;
    private TextView followers_textview, following_textview;
    private ListView action_listview;
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
        profilepic_imageview = (ImageView) mainActivity.findViewById(R.id.user_profile_pic);
        action_listview = (ListView) mainActivity.findViewById(R.id.user_notification_list);
        activityButton = (TextView) mainActivity.findViewById(R.id.user_activity_button);
        attendingButton = (TextView) mainActivity.findViewById(R.id.user_attending_button);

        followers_textview.setText(CurrentUser.theUser.getFollowers().size() + "\nfollowers");
        following_textview.setText(CurrentUser.theUser.getFollowing().size() + "\nfollowing");
        following_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWING);
            }
        });

        Log.d("Bitmapppp", user.getProfilePic() + "");
        UtilityClass.getBitmapFromURL(mainActivity, user.getProfilePic(), new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap image) {
                if (image != null) {
                    try {
                        profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(getResources(), image));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButton(activityButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(attendingButton, R.color.appColor, R.drawable.round_corner_red_edge);
                UtilityClass.hideKeyboard(mainActivity);
                action_listview.setAdapter( new UserActionAdapter(getActivity(), CurrentUser.theUser.getNotifications1()));
            }
        });
        attendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButton(attendingButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(activityButton, R.color.appColor, R.drawable.round_corner_red_edge);
                UtilityClass.hideKeyboard(mainActivity);
                CurrentUser.server_getPartyListObjects(CurrentUser.theUser.getAttending(), new OnResultReadyListener<List<Party>>() {
                    @Override
                    public void onResultReady(List<Party> result) {
                        if ( result != null ) {
                            action_listview.setAdapter( new UserActionAdapter(getActivity(), result, 0));
                        }
                    }
                });
            }
        });

        activityButton.performClick();
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

    private void changeButton(TextView view, int textColor, int backgroundColor) {
        view.setTextColor(mainActivity.getResources().getColor(textColor));
        view.setBackgroundResource(backgroundColor);
    }
}
