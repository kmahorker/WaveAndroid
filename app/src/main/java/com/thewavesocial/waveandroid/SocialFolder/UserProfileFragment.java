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
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

public class UserProfileFragment extends Fragment {

    public static TextView activityButton, goingButton;

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

        followers_textview.setText(CurrentUser.theUser.getFollowers().size() + "\nfollowers");
        following_textview.setText(CurrentUser.theUser.getFollowing().size() + "\nfollowing");
        following_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWING);
            }
        });

        Log.d("Bitmapppp", user.getProfilePic() + "");
//        UtilityClass.getBitmapFromURL(mainActivity, user.getProfilePic(), new OnResultReadyListener<Bitmap>() {
//            @Override
//            public void onResultReady(Bitmap image) {
//                if (image != null) {
//                    try {
//                        profilepic_imageview.setImageDrawable(UtilityClass.toRoundImage(getResources(), image));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButton(activityButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(goingButton, R.color.appColor, R.drawable.round_corner_red_edge);
                UtilityClass.hideKeyboard(mainActivity);
                server_getNotificationsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<ArrayList<Notification>>() {
                    @Override
                    public void onResultReady(ArrayList<Notification> result) {
                        if ( result != null ) {
                            extractValues(result, new OnResultReadyListener<NotificationPair>() {
                                @Override
                                public void onResultReady(NotificationPair result) {
                                    ArrayList<Notification> notifications = result.notifications;
                                    ArrayList<Object> objects = result.objects;
                                    action_listview.setAdapter( new UserActionAdapter(mainActivity, notifications, objects));
                                }
                            });
                        }
                    }
                });
            }
        });
        goingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                }
                            });
                        }
                    }
                });
                server_getPartyListObjects(CurrentUser.theUser.getGoing(), new OnResultReadyListener<List<Party>>() {
                    @Override
                    public void onResultReady(List<Party> result) {
                        if ( result != null ) {
                            action_listview.setAdapter( new UserActionAdapter(getActivity(), result));
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
            } else if ( each.getRequestType() == Notification.TYPE_GOING || each.getRequestType() == Notification.TYPE_HOSTING || each.getRequestType() == Notification.TYPE_INVITE_GOING || each.getRequestType() == Notification.TYPE_INVITE_BOUNCING) {
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
