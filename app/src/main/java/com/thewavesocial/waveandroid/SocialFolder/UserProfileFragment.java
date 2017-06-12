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
import android.widget.AbsListView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

public class UserProfileFragment extends Fragment {
    public final static int ADD_IMAGE_INTENT_ID = 5, LIST_ACTIVITY = 2, LIST_GOING = 1, LOAD_SIZE = 8;
    private boolean flag_loading;
    private int flag_list_type;
    private int notifications_offset;
    private HomeSwipeActivity mainActivity;

    public TextView activityButton, goingButton;
    private ListView action_listView;
    private ImageView profile_picture;

    enum PopupPage {
        FOLLOWERS,
        FOLLOWING;
    }

    private UserActionAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<Notification> notifications;
    private ArrayList<Object> senderObjects;

    @Override
    //get fragment layout reference
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_user, container, false);
    }

    @Override
    //initialize everything
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();
        flag_loading = false;

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
        TextView followers_text = (TextView) mainActivity.findViewById(R.id.user_followers_count);
        followers_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(PopupPage.FOLLOWERS);
            }
        });
        TextView following_text = (TextView) mainActivity.findViewById(R.id.user_following_count);
        profile_picture = (ImageView) mainActivity.findViewById(R.id.user_profile_pic);
        action_listView = (ListView) mainActivity.findViewById(R.id.user_notification_list);
        activityButton = (TextView) mainActivity.findViewById(R.id.user_activity_button);
        goingButton = (TextView) mainActivity.findViewById(R.id.user_going_button);
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.user_notification_progressbar);

        followers_text.setText(CurrentUser.theUser.getFollowers().size() + "\nfollowers");
        following_text.setText(CurrentUser.theUser.getFollowing().size() + "\nfollowing");
        following_text.setOnClickListener(new View.OnClickListener() {
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
                        profile_picture.setImageDrawable(UtilityClass.toRoundImage(getResources(), result));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mainActivity.startActivityForResult(i, ADD_IMAGE_INTENT_ID);
            }
        });
        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_list_type = LIST_ACTIVITY;
                UtilityClass.hideKeyboard(mainActivity);
                progressBar.setVisibility(View.VISIBLE);

                changeButton(activityButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(goingButton, R.color.appColor, R.drawable.round_corner_red_edge);
                server_getNotificationsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<ArrayList<Notification>>() {
                    @Override
                    public void onResultReady(ArrayList<Notification> result) {
                        if ( result != null ) {
                            notifications_offset = 0; //eliminate off-by-one error
                            notifications = sortNotifications(result);
                            getSenderObjects(notifications_offset, LOAD_SIZE, new OnResultReadyListener<ArrayList<Object>>() {
                                @Override
                                public void onResultReady(ArrayList<Object> result) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    flag_loading = false;
                                    senderObjects = result;
                                    adapter = new UserActionAdapter(mainActivity, notifications, senderObjects);
                                    action_listView.setAdapter(adapter);
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
                progressBar.setVisibility(View.VISIBLE);
                flag_list_type = LIST_GOING;
                UtilityClass.hideKeyboard(mainActivity);

                changeButton(goingButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(activityButton, R.color.appColor, R.drawable.round_corner_red_edge);
                server_getEventsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<HashMap<String, ArrayList<String>>>() {
                    @Override
                    public void onResultReady(HashMap<String, ArrayList<String>> result) {
                        if ( result != null ) {
                            server_getPartyListObjects(result.get("going"), new OnResultReadyListener<List<Party>>() {
                                @Override
                                public void onResultReady(List<Party> result) {
                                    if ( result != null ) {
                                        action_listView.setAdapter( new UserActionAdapter(getActivity(), result));
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
        action_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               if(flag_list_type == LIST_ACTIVITY && !flag_loading && totalItemCount != 0 &&
                        firstVisibleItem + visibleItemCount == notifications_offset + LOAD_SIZE) {
                    flag_loading = true;
                    notifications_offset += LOAD_SIZE;
                    loadNotifications();
                }
            }
        });

        activityButton.performClick();
    }

    /**Display follower/following list.*/
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

    /**Update profile image with the given bitmap*/
    public void updateProfileImage(Bitmap bitmap) {
        profile_picture.setImageDrawable( UtilityClass.toRoundImage( mainActivity.getResources(), bitmap) );
        server_upload_image(bitmap, null);
    }

    /**Change action buttons' color.*/
    private void changeButton(TextView view, int textColor, int backgroundColor) {
        view.setTextColor(mainActivity.getResources().getColor(textColor));
        view.setBackgroundResource(backgroundColor);
    }

    /**Sort notifications by creation time, using TreeMap.*/
    private ArrayList<Notification> sortNotifications(ArrayList<Notification> notifications) {
        TreeMap<Long, Notification> temp = new TreeMap<>(Collections.reverseOrder());
        ArrayList<Notification> new_list = new ArrayList<>();
        for ( Notification each : notifications ) {
            temp.put(each.getCreate_time(), each);
        }
        for ( Long key : temp.keySet() ) {
            new_list.add(temp.get(key));
        }
        return new_list;
    }

    /**Load the next LOAD_SIZE notifications.*/
    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        getSenderObjects(notifications_offset, LOAD_SIZE, new OnResultReadyListener<ArrayList<Object>>() {
            @Override
            public void onResultReady(ArrayList<Object> result) {
                progressBar.setVisibility(View.INVISIBLE);
                flag_loading = false;
                senderObjects.addAll(result);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**Get senders object of notification in sorted list. Either User or Party*/
    private void getSenderObjects(int startingIndex, int items, final OnResultReadyListener<ArrayList<Object>> delegate) {
        final TreeMap<Long, Object> loaded_objects = new TreeMap<>(Collections.reverseOrder());
        if ( notifications.size() < startingIndex + items ) {
            items = notifications.size() - startingIndex;
        }

        //Light-weight threads management
        class ThreadManager{
            private int max, completes;
            private ThreadManager(int max){
                this.max = max;
            }
            void completeThreads(){
                completes++;
                if ( completes >= max && delegate != null ) {
                    ArrayList<Object> objects = new ArrayList<>();
                    for ( long key : loaded_objects.keySet() ) {
                        objects.add(loaded_objects.get(key));
                    }
                    delegate.onResultReady(objects);
                }
            }
        }
        final ThreadManager threadManager = new ThreadManager(items);

        for ( int i = startingIndex; i < (items + startingIndex); i++ ) {
            final Notification each = notifications.get(i);
            if ( each.getRequestType() == Notification.TYPE_FOLLOWING || each.getRequestType() == Notification.TYPE_FOLLOWED ) {
                DatabaseAccess.server_getUserObject(each.getSenderID(), new OnResultReadyListener<User>() {
                    @Override
                    public void onResultReady(User result) {
                        if (result != null) {
                            loaded_objects.put(each.getCreate_time(), result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else if ( each.getRequestType() == Notification.TYPE_GOING || each.getRequestType() == Notification.TYPE_HOSTING || each.getRequestType() == Notification.TYPE_BOUNCING || each.getRequestType() == Notification.TYPE_INVITE_GOING || each.getRequestType() == Notification.TYPE_INVITE_BOUNCING) {
                DatabaseAccess.server_getPartyObject(each.getSenderID(), new OnResultReadyListener<Party>() {
                    @Override
                    public void onResultReady(Party result) {
                        if (result != null) {
                            loaded_objects.put(each.getCreate_time(), result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else {
                threadManager.completeThreads();
            }
        }
    }

}
