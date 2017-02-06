package com.thewavesocial.waveandroid.dummy;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Wei-Tung on 02/06/2017.
 */

public class DummyUser
{
    public static User dummyUser = new User((long)0,
        "Dummy",
        "Mario",
        "dmario@ucsb.edu",
        "dmario123",
        "Cornell",
        "Male",
        "123 Mario Dr. Isla Vista, CA 12345",
        Calendar.getInstance(),
        new ArrayList<Long>(), //best friend list
        new ArrayList<Long>(), //friend list
        new ArrayList<Long>(), //party attended list
        new ArrayList<Long>(), //party hosted list
        new ArrayList<Long>(), //party bounced list
        new BitmapDrawable());

    private User friend1, friend2, friend3, friend4, friend5;
    private Party party1, party2, party3, party4, party5;

    public User getFriendObject(long id)
    {
        switch((int) id)
        {
            case 1:
                return friend1;
            case 2:
                return friend2;
            case 3:
                return friend3;
            case 4:
                return friend4;
            case 5:
                return friend5;
            default:
                return friend1;
        }
    }

    public Party getPartyObject(long id)
    {
        switch((int) id)
        {
            case 1:
                return party1;
            case 2:
                return party2;
            case 3:
                return party3;
            case 4:
                return party4;
            case 5:
                return party5;
            default:
                return party1;
        }
    }

    public static void setupDummy(Context context)
    {
        //setup best friend list
        dummyUser.getBirthday().set(1969, 4, 1);
        dummyUser.getBestFriends().add((long)1);
        dummyUser.getBestFriends().add((long)2);

        //setup friend list
        dummyUser.getFriends().add((long)1);
        dummyUser.getFriends().add((long)2);
        dummyUser.getFriends().add((long)3);
        dummyUser.getFriends().add((long)4);
        dummyUser.getFriends().add((long)5);

        //setup party attended list
        dummyUser.getAttended().add((long)1);
        dummyUser.getAttended().add((long)2);
        dummyUser.getAttended().add((long)3);
        dummyUser.getAttended().add((long)4);
        dummyUser.getAttended().add((long)5);

        //setup party hosted list
        dummyUser.getHosted().add((long)1);
        dummyUser.getHosted().add((long)2);
        dummyUser.getHosted().add((long)3);
        dummyUser.getHosted().add((long)4);
        dummyUser.getHosted().add((long)5);

        //setup party bounced list
        dummyUser.getBounced().add((long)1);
        dummyUser.getBounced().add((long)2);
        dummyUser.getBounced().add((long)3);
        dummyUser.getBounced().add((long)4);
        dummyUser.getBounced().add((long)5);

        dummyUser.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
    }
}
