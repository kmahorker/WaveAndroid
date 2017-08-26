package com.thewavesocial.waveandroid.BusinessObjects;

import android.util.Log;

import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;

import java.util.ArrayList;
import java.util.List;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

//Nothing changed
public final class CurrentUser {
    private static User user = new User();
    public static final String TAG = HomeSwipeActivity.TAG;

    /**
     * Initialize user object
     */
    public static void loadBestFriends(final OnResultReadyListener<Boolean> delegate) {
        Log.d(HomeSwipeActivity.TAG, "CurrentUser.loadBestFriends");
        server_getUserObject(getTokenFromLocal().get("id"), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                CurrentUser.user = result;
                Log.i(TAG, "Current user set. name:" + user.getFull_name());
                server_getBestFriends(getTokenFromLocal().get("id"), new OnResultReadyListener<List<BestFriend>>() {
                    @Override
                    public void onResultReady(List<BestFriend> result) {
                        CurrentUser.getUser().setBestFriends(new ArrayList<BestFriend>());
                        if (result != null)
                            CurrentUser.getUser().getBestFriends().addAll(result);

                        if (delegate == null)
                            return;
                        if (CurrentUser.getUser().getUserID() != null)
                            delegate.onResultReady(true);
                        else
                            delegate.onResultReady(false);
                    }
                });
            }
        });
    }

    /**
     * Set main user object
     */
    public static void syncUser(final OnResultReadyListener<Boolean> delegate) {
        loadBestFriends(delegate);
    }

    /**
     * updates current user object with Firebase, using the Firebase id form DatabaseAccess.getTokenFromLocal
     */
    public static void syncUser() {
        syncUser(null);
    }

    public static User getUser() {
        return user;
    }
}
