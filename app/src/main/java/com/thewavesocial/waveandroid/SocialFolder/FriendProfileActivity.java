package com.thewavesocial.waveandroid.SocialFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.AdaptersFolder.FriendNotificationCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class FriendProfileActivity extends AppCompatActivity {
    private User friend; //TODO: Remove Empty User
    private String userID;
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
        friend = (User) intent.getExtras().get("userObject");
        userID = friend.getUserID();

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
        final TextView option = (TextView) findViewById(R.id.friend_options);
        ImageView back = (ImageView) findViewById(R.id.friend_back_button);

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
                PopupMenu popupMenu = new PopupMenu(mainActivity, option);
                popupMenu.getMenuInflater().inflate(R.menu.friend_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if ( menuItem.getItemId() == R.id.friend_options_block )
                            Toast.makeText(mainActivity, "You are blocking this user.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(mainActivity, "You are reporting this user.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                popupMenu.show();
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

        UtilityClass.getBitmapFromURL(mainActivity, friend.getProfilePic(), new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap image) {
                if (image != null)
                    profilepic_imageview.setImageDrawable( UtilityClass.toRoundImage(getResources(), image));
            }
        });

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
