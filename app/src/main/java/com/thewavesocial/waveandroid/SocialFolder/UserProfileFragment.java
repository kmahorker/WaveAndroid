package com.thewavesocial.waveandroid.SocialFolder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.UserActionAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

public class UserProfileFragment extends Fragment {

    public static TextView activityButton, goingButton;
    public final static int ADD_PROFILEPIC_INTENT_ID = 5;

    public enum PopupPage {
        FOLLOWERS,
        FOLLOWING;
    }

    private User user;
    private TextView followers_textview, following_textview;
    private ListView action_listview;
    private ProgressBar progressBar;
    private static ImageView profilepic_imageview;
    private UserProfileFragment userProfileFragment;
    private static HomeSwipeActivity mainActivity;

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
    public void setupProfileInfo() {
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
        goingButton = (TextView) mainActivity.findViewById(R.id.user_going_button);
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.user_notification_progressbar);

        followers_textview.setText(CurrentUser.theUser.getFollowers().size() + "\nfollowers");
        following_textview.setText(CurrentUser.theUser.getFollowing().size() + "\nfollowing");
        following_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWING);
            }
        });

        DatabaseAccess.server_getProfilePicture(CurrentUser.theUser.getUserID(), new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap result) {
                if ( result != null ) {
                    try {
                        profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(getResources(), result));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                changeButton(activityButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(goingButton, R.color.appColor, R.drawable.round_corner_red_edge);
                UtilityClass.hideKeyboard(mainActivity);
                server_getNotificationsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<ArrayList<Notification>>() {
                    @Override
                    public void onResultReady(ArrayList<Notification> result) {
                        if ( result != null ) {
                            Log.d("READY", result.size() + "");
                            extractValues(result, new OnResultReadyListener<NotificationPair>() {
                                @Override
                                public void onResultReady(NotificationPair result) {
                                    ArrayList<Notification> notifications = result.getNotifications();
                                    ArrayList<Object> objects = result.getSenderObjects();
                                    action_listview.setAdapter( new UserActionAdapter(mainActivity, notifications, objects));
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
        goingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                changeButton(goingButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(activityButton, R.color.appColor, R.drawable.round_corner_red_edge);
                UtilityClass.hideKeyboard(mainActivity);
                server_getEventsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<HashMap<String, ArrayList<String>>>() {
                    @Override
                    public void onResultReady(HashMap<String, ArrayList<String>> result) {
                        if ( result != null ) {
                            server_getPartyListObjects(result.get("going"), new OnResultReadyListener<List<Party>>() {
                                @Override
                                public void onResultReady(List<Party> result) {
                                    if ( result != null ) {
                                        action_listview.setAdapter( new UserActionAdapter(getActivity(), result));
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
        profilepic_imageview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mainActivity.startActivityForResult(i, ADD_PROFILEPIC_INTENT_ID);
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

    private void extractValues(ArrayList<Notification> result, final OnResultReadyListener<NotificationPair> delegate) {
        final NotificationPair senderObjects = new NotificationPair(new TreeMap<Long, Notification>(Collections.reverseOrder()),
                new TreeMap<Long, Object>(Collections.reverseOrder()));

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
                            senderObjects.notifications.put(each.getCreate_time(), each);
                            senderObjects.objects.put(each.getCreate_time(), result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else if ( each.getRequestType() == Notification.TYPE_GOING || each.getRequestType() == Notification.TYPE_HOSTING || each.getRequestType() == Notification.TYPE_BOUNCING || each.getRequestType() == Notification.TYPE_INVITE_GOING || each.getRequestType() == Notification.TYPE_INVITE_BOUNCING) {
                DatabaseAccess.server_getPartyObject(each.getSenderID(), new OnResultReadyListener<Party>() {
                    @Override
                    public void onResultReady(Party result) {
                        if (result != null) {
                            senderObjects.notifications.put(each.getCreate_time(), each);
                            senderObjects.objects.put(each.getCreate_time(), result);
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
        private TreeMap<Long, Notification> notifications;
        private TreeMap<Long, Object> objects;
        public NotificationPair(TreeMap<Long, Notification> notifications, TreeMap<Long, Object> objects) {
            this.notifications = notifications;
            this.objects = objects;
        }

        public ArrayList<Notification> getNotifications() {
            ArrayList<Notification> list = new ArrayList<>();
            for ( Long key : notifications.keySet() ) {
                list.add(notifications.get(key));
            }
            return list;
        }

        public ArrayList<Object> getSenderObjects() {
            ArrayList<Object> list = new ArrayList<>();
            for ( Long key : notifications.keySet() ) {
                list.add(objects.get(key));
            }
            return list;
        }
    }

    public static void updateProfileImage(Bitmap bitmap) {
        profilepic_imageview.setImageDrawable( UtilityClass.toRoundImage( mainActivity.getResources(), bitmap) );
        server_upload_image(bitmap, null);
    }
}
