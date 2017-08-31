package com.thewavesocial.waveandroid.BusinessObjects;

import android.util.Log;

import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;

import java.util.ArrayList;
import java.util.List;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

//Nothing changed
public final class CurrentUser {
    private static User user;
    public static final String TAG = HomeSwipeActivity.TAG;

    /**
     * Set main user object
     */
    public static void syncUser(final OnResultReadyListener<User> delegate) {
        Log.d(HomeSwipeActivity.TAG, "CurrentUser.loadBestFriends");
        server_getUserObjectWithFriends(getCurrentUserId(), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                CurrentUser.user = result;
                delegate.onResultReady(result);
            }
        });
    }

    /**
     * updates current user object with Firebase, using the Firebase id form DatabaseAccess.getCurrentUserId
     */
    public static void syncUser() {
        syncUser(null);
    }

    public static void setUser(User user) {
        CurrentUser.user = user;
    }

    public static User getUser() {
        return user;
    }
}
