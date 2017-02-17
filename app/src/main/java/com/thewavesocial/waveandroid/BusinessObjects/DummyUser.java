package com.thewavesocial.waveandroid.BusinessObjects;

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

//dummy class to initialize CurrentUser
public class DummyUser extends User
{

    private User friend1, friend2, friend3, friend4, friend5;
    private Party party1, party2, party3, party4, party5;
    private Context context;

    public User getFriend1() {
        return friend1;
    }

    public void setFriend1(User friend1) {
        this.friend1 = friend1;
    }

    public User getFriend3() {
        return friend3;
    }

    public void setFriend3(User friend3) {
        this.friend3 = friend3;
    }

    public User getFriend2() {
        return friend2;
    }

    public void setFriend2(User friend2) {
        this.friend2 = friend2;
    }

    public User getFriend4() {
        return friend4;
    }

    public void setFriend4(User friend4) {
        this.friend4 = friend4;
    }

    public User getFriend5() {
        return friend5;
    }

    public void setFriend5(User friend5) {
        this.friend5 = friend5;
    }

    public Party getParty1() {
        return party1;
    }

    public void setParty1(Party party1) {
        this.party1 = party1;
    }

    public Party getParty2() {
        return party2;
    }

    public void setParty2(Party party2) {
        this.party2 = party2;
    }

    public Party getParty3() {
        return party3;
    }

    public void setParty3(Party party3) {
        this.party3 = party3;
    }

    public Party getParty4() {
        return party4;
    }

    public void setParty4(Party party4) {
        this.party4 = party4;
    }

    public Party getParty5() {
        return party5;
    }

    public void setParty5(Party party5) {
        this.party5 = party5;
    }

    public DummyUser(Context context){
        super((long) 0,
                "DummyLongNAME",
                "MarioLongNAME",
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
                new ArrayList<Long>(),
                new BitmapDrawable());



        setupDummy(context);
        setupUserObjects(context);
        setupPartyObjects();

    }

    public List<User> getFriendsListObjects(List<Long> userIdList){
        List<User> friendObjs = new ArrayList<User>();
        for(long id : userIdList){
            friendObjs.add(getFriendObject(id));
        }
        return friendObjs;
    }

    public User getFriendObject(long id)
    {
        switch ((int) id)
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

    public List<Party> getPartyListObjects(List<Long> partyIdList)
    {
        List<Party> partyObjs = new ArrayList<>();
        for(long id: partyIdList){
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }

    public Party getPartyObject(long id)
    {
        switch ((int) id)
        {
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

    private void setupDummy(Context context)
    {
        //setup best friend list
        this.getBirthday().set(1997, 4, 1);
        this.getBestFriends().add((long) 1);
        this.getBestFriends().add((long) 2);

        //setup friend list
        this.getFriends().add((long) 1);
        this.getFriends().add((long) 2);
        this.getFriends().add((long) 3);
        this.getFriends().add((long) 4);
        this.getFriends().add((long) 5);

        //setup party attended list
        this.getAttended().add((long) 1);
        this.getAttended().add((long) 2);
        this.getAttended().add((long) 3);
        this.getAttended().add((long) 4);
        this.getAttended().add((long) 5);

        //setup party hosted list
        this.getHosted().add((long) 1);
        this.getHosted().add((long) 2);
        this.getHosted().add((long) 3);
        this.getHosted().add((long) 4);
        this.getHosted().add((long) 5);

        //setup party bounced list
        this.getBounced().add((long) 1);
        this.getBounced().add((long) 2);
        this.getBounced().add((long) 3);
        this.getBounced().add((long) 4);
        this.getBounced().add((long) 5);

        this.getSignedUp().add((long) 1);
        this.getSignedUp().add((long) 2);
        this.getSignedUp().add((long) 3);
        this.getSignedUp().add((long) 4);
        this.getSignedUp().add((long) 5);

        this.setProfilePic(new BitmapDrawable(context.getResources(),BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
    }

    private void setupPartyObjects()
    {
        party1 = new Party(
                1,
                "Super Party 1",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                "1th Dummy Ave. San Diego, CA 54321",
                this.getFriends(),
                true);
        party2 = new Party(
                2,
                "Super Party 2",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                "2th Dummy Ave. San Diego, CA 54321",
                this.getFriends(),
                true);
        party3 = new Party(
                3,
                "Super Party 3",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                "3th Dummy Ave. San Diego, CA 54321",
                this.getFriends(),
                true);
        party4 = new Party(
                4,
                "Super Party 4",
                50,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                "4th Dummy Ave. San Diego, CA 54321",
                this.getFriends(),
                true);
        party5 = new Party(
                5,
                "Super Party 5",
                100,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                "5th Dummy Ave. San Diego, CA 54321",
                this.getFriends(),
                true);

        party1.getStartingDateTime().set(2017, 2, 6);
        party2.getStartingDateTime().set(2017, 3, 7);
        party3.getStartingDateTime().set(2017, 9, 8);
        party4.getStartingDateTime().set(2017, 2, 10);
        party5.getStartingDateTime().set(2018, 2, 4);

        party1.getEndingDateTime().set(2017, 2, 7);
        party2.getEndingDateTime().set(2017, 3, 7);
        party3.getEndingDateTime().set(2017, 9, 9);
        party4.getEndingDateTime().set(2017, 2, 10);
        party5.getEndingDateTime().set(2018, 2, 5);
    }

    private void setupUserObjects(Context context)
    {
        friend1 = new User((long) 1,
                "Happy",
                "Friend1",
                "h1@ucsb.edu",
                "happy1",
                "MIT",
                "Female",
                "1 Mario Dr. Isla Vista, CA 12345",
                Calendar.getInstance(),
                new ArrayList<Long>(), //best friend list
                new ArrayList<Long>(), //friend list
                new ArrayList<Long>(), //party attended list
                new ArrayList<Long>(), //party hosted list
                new ArrayList<Long>(), //party bounced list
                new ArrayList<Long>(),
                new BitmapDrawable());
        friend2 = new User((long) 2,
                "Sad",
                "Friend2",
                "s2@ucsb.edu",
                "sad2",
                "Cornell",
                "Female",
                "2 Mario Dr. Isla Vista, CA 12345",
                Calendar.getInstance(),
                new ArrayList<Long>(), //best friend list
                new ArrayList<Long>(), //friend list
                new ArrayList<Long>(), //party attended list
                new ArrayList<Long>(), //party hosted list
                new ArrayList<Long>(), //party bounced list
                new ArrayList<Long>(),
                new BitmapDrawable());
        friend3 = new User((long) 3,
                "Boring",
                "Friend3",
                "b3@ucsb.edu",
                "boring3",
                "UCB",
                "Male",
                "3 Mario Dr. Isla Vista, CA 12345",
                Calendar.getInstance(),
                new ArrayList<Long>(), //best friend list
                new ArrayList<Long>(), //friend list
                new ArrayList<Long>(), //party attended list
                new ArrayList<Long>(), //party hosted list
                new ArrayList<Long>(), //party bounced list
                new ArrayList<Long>(),
                new BitmapDrawable());
        friend4 = new User((long) 4,
                "Angry",
                "Friend4",
                "a4@ucsb.edu",
                "angry4",
                "UCSD",
                "Male",
                "4 Mario Dr. Isla Vista, CA 12345",
                Calendar.getInstance(),
                new ArrayList<Long>(), //best friend list
                new ArrayList<Long>(), //friend list
                new ArrayList<Long>(), //party attended list
                new ArrayList<Long>(), //party hosted list
                new ArrayList<Long>(), //party bounced list
                new ArrayList<Long>(),
                new BitmapDrawable());
        friend5 = new User((long) 5,
                "Lit",
                "Friend5",
                "l5@ucsb.edu",
                "lit5",
                "UCSB",
                "Other",
                "5 Mario Dr. Isla Vista, CA 12345",
                Calendar.getInstance(),
                new ArrayList<Long>(), //best friend list
                new ArrayList<Long>(), //friend list
                new ArrayList<Long>(), //party attended list
                new ArrayList<Long>(), //party hosted list
                new ArrayList<Long>(), //party bounced list,
                new ArrayList<Long>(),
                new BitmapDrawable());
        friend1.getBirthday().set(1997, 1, 2);
        friend2.getBirthday().set(1998, 2, 3);
        friend3.getBirthday().set(1999, 3, 4);
        friend4.getBirthday().set(1990, 4, 5);
        friend5.getBirthday().set(1991, 5, 6);

        friend1.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
        friend2.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.happy_house)));
        friend3.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
        friend4.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.happy_house)));
        friend5.setProfilePic(new BitmapDrawable(context.getResources(),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
    }

    public User getDummyUser() {
        return this;
    }

    public void setContext(Context cont){
        context = cont;
    }
}
