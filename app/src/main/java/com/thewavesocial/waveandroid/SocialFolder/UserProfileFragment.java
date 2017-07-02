package com.thewavesocial.waveandroid.SocialFolder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

public class UserProfileFragment extends Fragment {
    public final static int ADD_IMAGE_INTENT_ID = 5, LIST_ACTIVITY = 2, LIST_GOING = 1, LOAD_SIZE = 8;
    private boolean flag_loading_notif;
    private int flag_list_type;
    private int notifications_offset;
    private HomeSwipeActivity mainActivity;

    public TextView activityButton, goingButton, following_text, followers_text;
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
    /**get fragment layout reference*/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_user, container, false);
    }

    @Override
    /**initialize everything*/
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();
        flag_loading_notif = false;

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
    /**Update result after new user data is saved*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            //display data on profile
            setupProfileInfo();
        }
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    /**
     * initialize user information
     */
    public void setupProfileInfo() {
        followers_text = (TextView) mainActivity.findViewById(R.id.user_followers_count);
        following_text = (TextView) mainActivity.findViewById(R.id.user_following_count);
        profile_picture = (ImageView) mainActivity.findViewById(R.id.user_profile_pic);
        action_listView = (ListView) mainActivity.findViewById(R.id.user_notification_list);
        activityButton = (TextView) mainActivity.findViewById(R.id.user_activity_button);
        goingButton = (TextView) mainActivity.findViewById(R.id.user_going_button);
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.user_notification_progressbar);

        updateFollowingFollowers();

        DatabaseAccess.server_getProfilePicture(CurrentUser.theUser.getUserID(), new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap result) {
                if (result != null) {
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
            public void onClick(View view) {
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

                changeButton(activityButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(goingButton, R.color.appColor, R.drawable.round_corner_red_edge);
                server_getNotificationsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<ArrayList<Notification>>() {
                    @Override
                    public void onResultReady(ArrayList<Notification> result) {
                        if (result != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            notifications_offset = 0;
                            notifications = sortNotifications(result);
                            getSenderObjects(notifications_offset, LOAD_SIZE, new OnResultReadyListener<ArrayList<Object>>() {
                                @Override
                                public void onResultReady(ArrayList<Object> result) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    flag_loading_notif = false;
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
                flag_list_type = LIST_GOING;
                UtilityClass.hideKeyboard(mainActivity);

                changeButton(goingButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(activityButton, R.color.appColor, R.drawable.round_corner_red_edge);
                goingButton.setText("Going");
                server_getEventsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<HashMap<String, ArrayList<Party>>>() {
                    @Override
                    public void onResultReady(HashMap<String, ArrayList<Party>> result) {
                        if (result != null) {
                            TreeMap<Long, Party> partyTreeMap = new TreeMap<>();
                            for ( Party party : result.get("going") ) {
                                long time = party.getStartingDateTime().getTimeInMillis();
                                if ( time - Calendar.getInstance().getTimeInMillis() > -86400000 ) { //1 day
                                    while (partyTreeMap.containsKey(time)) {
                                        time++;
                                    }
                                    partyTreeMap.put(time, party);
                                }
                            }

                            List<Party> new_list = new ArrayList<>();
                            for ( Long key : partyTreeMap.keySet() ) {
                                new_list.add(partyTreeMap.get(key));
                            }
                            adapter = new UserActionAdapter(mainActivity, new_list);
                            action_listView.setAdapter(adapter);
                        }
                    }
                });
            }
        });
        action_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (flag_list_type == LIST_ACTIVITY && !flag_loading_notif && totalItemCount != 0 &&
                        firstVisibleItem + visibleItemCount == notifications_offset + LOAD_SIZE &&
                        notifications.size() > notifications_offset) {
                    flag_loading_notif = true;
                    notifications_offset += LOAD_SIZE;
                    loadNotifications();
                }
            }
        });
        activityButton.performClick();
    }

    /**Get following and followers list from server and update UI*/
    private void updateFollowingFollowers() {
        DatabaseAccess.server_getUserFollowing(CurrentUser.theUser.getUserID(), new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(final List<User> following) {
                DatabaseAccess.server_getUserFollowers(CurrentUser.theUser.getUserID(), new OnResultReadyListener<List<User>>() {
                    @Override
                    public void onResultReady(final List<User> followers) {
                        String text = followers.size() + "\nfollowers";
                        followers_text.setText(text);
                        followers_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPopup(PopupPage.FOLLOWERS, new ArrayList<>(followers), new ArrayList<>(following));
                            }
                        });

                        text = following.size() + "\nfollowing";
                        following_text.setText(text);
                        following_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPopup(PopupPage.FOLLOWING, new ArrayList<>(following), new ArrayList<>(following));
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Display follower/following list.
     */
    private void showPopup(PopupPage popup, ArrayList<User> users, ArrayList<User> following) {
        switch (popup) {
            case FOLLOWERS:
            case FOLLOWING:
                Intent intent = new Intent(mainActivity, FollowActivity.class);
                intent.putExtra(FollowActivity.FOLLOW_POPUP_TYPE_ARG, popup.name());
                intent.putParcelableArrayListExtra(FollowActivity.USER_LIST_OBJECTS, users);
                intent.putParcelableArrayListExtra(FollowActivity.USER_FOLLOWING, following);
                mainActivity.startActivity(intent);
                break;
        }
    }

    /**
     * Update profile image with the given bitmap
     */
    public void updateProfileImage(Bitmap bitmap) {
        profile_picture.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), bitmap));
        server_upload_image(bitmap, null);
    }

    /**
     * Change action buttons' color.
     */
    private void changeButton(TextView view, int textColor, int backgroundColor) {
        view.setTextColor(mainActivity.getResources().getColor(textColor));
        view.setBackgroundResource(backgroundColor);
    }

    /**
     * Sort notifications by creation time, using TreeMap.
     */
    private ArrayList<Notification> sortNotifications(ArrayList<Notification> notifications) {
        TreeMap<Long, Notification> temp = new TreeMap<>(Collections.reverseOrder());
        ArrayList<Notification> new_list = new ArrayList<>();
        for (Notification each : notifications) {
            temp.put(each.getCreate_time(), each);
        }
        for (Long key : temp.keySet()) {
            new_list.add(temp.get(key));
        }
        return new_list;
    }

    /**
     * Load the next LOAD_SIZE notifications.
     */
    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        getSenderObjects(notifications_offset, LOAD_SIZE, new OnResultReadyListener<ArrayList<Object>>() {
            @Override
            public void onResultReady(ArrayList<Object> result) {
                progressBar.setVisibility(View.INVISIBLE);
                flag_loading_notif = false;
                senderObjects.addAll(result);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Get senders object of notification in sorted list. Either User or Party
     */
    private void getSenderObjects(final int startingIndex, int items, final OnResultReadyListener<ArrayList<Object>> delegate) {
        final HashMap<Long, Object> loaded_objects = new HashMap<>();

        if (notifications.size() < startingIndex + items)
            items = notifications.size() - startingIndex;

        //Light-weight threads management
        final int finalItems = items;
        class ThreadManager {
            private int max, completes;

            private ThreadManager(int max) {
                this.max = max;
            }

            void completeThreads() {
                completes++;
                if (completes >= max && delegate != null) {
                    ArrayList<Object> objects = new ArrayList<>();
                    for (int i = startingIndex; i < startingIndex + finalItems; i++)
                        objects.add(loaded_objects.get(notifications.get(i).getCreate_time()));
                    delegate.onResultReady(objects);
                }
            }
        }
        final ThreadManager threadManager = new ThreadManager(items);

        for (int i = startingIndex; i < (items + startingIndex); i++) {
            final Notification each = notifications.get(i);
            if (each.getRequestType() == Notification.TYPE_FOLLOWING || each.getRequestType() == Notification.TYPE_FOLLOWED) {
                DatabaseAccess.server_getUserObject(each.getSenderID(), new OnResultReadyListener<User>() {
                    @Override
                    public void onResultReady(User result) {
                        if (result != null) {
                            loaded_objects.put(each.getCreate_time(), result);
                        }
                        threadManager.completeThreads();
                    }
                });
            } else if (each.getRequestType() == Notification.TYPE_GOING || each.getRequestType() == Notification.TYPE_HOSTING || each.getRequestType() == Notification.TYPE_BOUNCING || each.getRequestType() == Notification.TYPE_INVITE_GOING || each.getRequestType() == Notification.TYPE_INVITE_BOUNCING) {
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

    @Override
    public void onResume() {
        super.onResume();
        updateFollowingFollowers();
    }
}
