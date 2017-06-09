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
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_getNotificationsOfUser;

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
        setupProfileInfo();
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

        UtilityClass.startProgressbar(mainActivity);
        server_getNotificationsOfUser(userID, new OnResultReadyListener<ArrayList<Notification>>() {
            @Override
            public void onResultReady(ArrayList<Notification> result) {
                if ( result != null ) {
                    extractValues(result, new OnResultReadyListener<NotificationPair>() {
                        @Override
                        public void onResultReady(NotificationPair result) {
                            UtilityClass.endProgressbar(mainActivity, true);
                            ArrayList<Notification> notifications = result.notifications;
                            ArrayList<Object> objects = result.objects;
                            notification_listview.setAdapter(new FriendNotificationCustomAdapter(mainActivity, notifications, objects));
                        }
                    });
                }
            }
        });

        if (userID.equals(CurrentUser.theUser.getUserID())) {
            follow_button.setVisibility(View.INVISIBLE);
        } else if (!CurrentUser.theUser.getFollowing().contains(userID)) {
            changeButton("Follow", R.color.appColor, R.drawable.round_corner_red_edge);
        } else {
            changeButton("Following", R.color.white_solid, R.drawable.round_corner_red);
        }

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow_button.getText().equals("Following")) {
                    //If delete from server is successful, then delete locally and change button.
                    DatabaseAccess.server_unfollow(userID, new OnResultReadyListener<String>() {
                        @Override
                        public void onResultReady(String result) {
                            if ( result.equals("success") ) {
                                CurrentUser.theUser.getFollowing().remove(userID);
                                changeButton("Follow", R.color.appColor, R.drawable.round_corner_red_edge);
                            }
                        }
                    });
                } else {
                    //If follow from server is successful, then follow locally and change button.
                    DatabaseAccess.server_followUser(CurrentUser.theUser.getUserID(), userID, new OnResultReadyListener<String>() {
                        @Override
                        public void onResultReady(String result) {
                            if ( result.equals("success") ) {
                                CurrentUser.theUser.getFollowing().add(userID);
                                changeButton("Following", R.color.white_solid, R.drawable.round_corner_red);
                            }
                        }
                    });
                }
            }
        });
    }


    private void changeButton(String text, int textColor, int backgroundColor) {
        follow_button.setText(text);
        follow_button.setTextColor(mainActivity.getResources().getColor(textColor));
        follow_button.setBackgroundResource(backgroundColor);
    }

    private void extractValues(ArrayList<Notification> result, final OnResultReadyListener<NotificationPair> delegate) {
        final NotificationPair senderObjects = new NotificationPair(new ArrayList<Notification>(), new ArrayList<>());

        //Light-weight threads management
        class ThreadManager{
            private int max, completes;
            public ThreadManager(int max){
                this.max = max;
            }
            void completeThreads(){
                completes++;
                if ( completes >= max && delegate != null )
                    delegate.onResultReady(senderObjects);
            }
        }
        final ThreadManager threadManager = new ThreadManager(result.size());

        for ( final Notification each : result ) {
            if ( each.getRequestType() == Notification.TYPE_FOLLOWING || each.getRequestType() == Notification.TYPE_FOLLOWED ) {
                DatabaseAccess.server_getUserObject(each.getSenderID(), new OnResultReadyListener<User>() {
                    @Override
                    public void onResultReady(User result) {
                        if (result != null) {
                            senderObjects.notifications.add(each);
                            senderObjects.objects.add(result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else if ( each.getRequestType() == Notification.TYPE_GOING || each.getRequestType() == Notification.TYPE_HOSTING ) {
                DatabaseAccess.server_getPartyObject(each.getSenderID(), new OnResultReadyListener<Party>() {
                    @Override
                    public void onResultReady(Party result) {
                        if (result != null) {
                            senderObjects.notifications.add(each);
                            senderObjects.objects.add(result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else {
                threadManager.completeThreads();
            }
        }
    }

    class NotificationPair {
        public ArrayList<Notification> notifications;
        public ArrayList<Object> objects;
        public NotificationPair(ArrayList<Notification> notifications, ArrayList<Object> objects) {
            this.notifications = notifications;
            this.objects = objects;
        }
    }
}
