package com.thewavesocial.waveandroid.BusinessObjects;

import android.app.Activity;
import android.util.Log;

import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

//Nothing changed
public final class CurrentUser {
    public static Activity mainActivity;
    public static User theUser = new User();

    private CurrentUser() {
        mainActivity = null;
        server_getUserObject("10", new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                CurrentUser.theUser = result;
            }
        });
    }

    /**
     * Initialize user object
     */
    public static void setContext(Activity activity, final OnResultReadyListener<Boolean> delegate) {
        Log.d(HomeSwipeActivity.TAG, "CurrentUser.setContext");
        mainActivity = activity;
        DatabaseAccess.setContext(mainActivity);
        server_getUserObject(getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                CurrentUser.theUser = result;
                server_getBestFriends(getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<List<BestFriend>>() {
                    @Override
                    public void onResultReady(List<BestFriend> result) {
                        CurrentUser.theUser.setBestFriends(new ArrayList<BestFriend>());
                        if (result != null)
                            CurrentUser.theUser.getBestFriends().addAll(result);

                        if (delegate == null)
                            return;
                        if (CurrentUser.theUser.getUserID() != null)
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
    public static void setTheUser(User theUser) {
        CurrentUser.theUser = theUser;
    }

}
