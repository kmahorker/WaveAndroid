package com.thewavesocial.waveandroid.SocialFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.FriendNotificationCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class FriendProfileActivity extends AppCompatActivity {
    private User friend; //TODO: Remove Empty User
    private long userID;
    private TextView followers_textview, following_textview, follow_button;
    private ListView notification_listview;
    private ImageView profilepic_imageview;
    private UserProfileFragment userProfileFragment;
    private FriendProfileActivity mainActivity;

    @Override
    //initialize everything
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.profile_friend);
        mainActivity = this;

        // access current friend data
        Intent intent = getIntent();
        userID = intent.getExtras().getLong("userIDLong");
        friend = CurrentUser.getUserObject(userID);
        //TODO: getUserObject(long id) from database class

        setupProfileInfo();
        setupActionbar();
    }

    @Override
    //onClick event for back button pressed
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize actionbar
    private void setupActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_friend);

        TextView title = (TextView) findViewById(R.id.friend_name);
        TextView back = (TextView) findViewById(R.id.friend_back_button);
        TextView option = (TextView) findViewById(R.id.friend_options);

        title.setText(friend.getFullName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
                dialog.setTitle("-Actions-")
                    .setCancelable(true)
                    .setSingleChoiceItems(new String[]{"Block", "Report"}, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    // TODO: 04/02/2017 Block user
                                    break;
                                case 1:
                                    // TODO: 04/02/2017 Report user
                                    break;
                                default:
                                    dialog.cancel();
                            }
                        }
                    })
                    .show();
            }
        });
    }

    //initialize user information
    private void setupProfileInfo() {
        followers_textview = (TextView) mainActivity.findViewById(R.id.friend_followers_count);
        following_textview = (TextView) mainActivity.findViewById(R.id.friend_following_count);
        profilepic_imageview = (ImageView) mainActivity.findViewById(R.id.friend_profile_pic);
        notification_listview = (ListView) mainActivity.findViewById(R.id.friend_notification_list);
        follow_button = (TextView) mainActivity.findViewById(R.id.friend_follow_button);

        followers_textview.setText(friend.getFollowers().size() + "\nfollowers");
        following_textview.setText(friend.getFollowing().size() + "\nfollowing");
        if (friend.getProfilePic() != null) {
            profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                    friend.getProfilePic().getBitmap()));
        }
        notification_listview.setAdapter(new FriendNotificationCustomAdapter(mainActivity,
                friend.getNotifications2()));

        if (!CurrentUser.theUser.getFollowing().contains(userID)) {
            changeButton("follow", R.color.appColor, R.drawable.round_corner_red_edge);
        } else {
            changeButton("following", R.color.white_solid, R.drawable.round_corner_red);
        }

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow_button.getText().equals("following")) {
                    CurrentUser.theUser.getFollowing().remove(userID);
                    changeButton("follow", R.color.appColor, R.drawable.round_corner_red_edge);
                } else {
                    CurrentUser.theUser.getFollowing().add(userID);
                    changeButton("following", R.color.white_solid, R.drawable.round_corner_red);
                }
            }
        });
    }


    private void changeButton(String text, int textColor, int backgroundColor) {
        follow_button.setText(text);
        follow_button.setTextColor(mainActivity.getResources().getColor(textColor));
        follow_button.setBackgroundResource(backgroundColor);
    }
}
