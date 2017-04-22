package com.thewavesocial.waveandroid.BusinessObjects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.R;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

//dummy class to initialize CurrentUser
public class DummyUser extends User
{

    private User friend1, friend2, friend3, friend4, friend5;
    private Party party1, party2, party3, party4, party5;
    private Context context;

    public DummyUser(Context context)
    {
        super("",
                "DummyLongNAME",
                "MarioLongNAME",
                "dmario@ucsb.edu",
                "dmario123",
                "Cornell",
                "Male",
                "0000000000",
                new MapAddress("Pardall Gardens, Isla Vista, CA 93117", new LatLng(34.413331,-119.854490)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");

        setupDummy(context);
        setupUserObjects(context);
        setupPartyObjects(context);
        setupNotifications1();
    }

    private void setupNotifications1()
    {
        this.getNotifications1().add( new Notification( this.getFriend1().getUserID(), Notification.type1FollowingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend2().getUserID(), Notification.type1FollowingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend3().getUserID(), Notification.type1FollowingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend4().getUserID(), Notification.type1FollowingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend5().getUserID(), Notification.type1FollowingNotice ) );

        this.getNotifications1().add( new Notification( this.getFriend1().getUserID(), Notification.type2HostingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend2().getUserID(), Notification.type2HostingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend3().getUserID(), Notification.type2HostingNotice ) );

        this.getNotifications1().add( new Notification( this.getFriend1().getUserID(), Notification.type3AttendingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend2().getUserID(), Notification.type3AttendingNotice ) );
        this.getNotifications1().add( new Notification( this.getFriend3().getUserID(), Notification.type3AttendingNotice ) );
    }

    private void setupFriendNotifications2(User friend)
    {
        friend.getNotifications2().add( new Notification( this.getFriend1().getUserID(), Notification.type4FriendFollowingNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend2().getUserID(), Notification.type4FriendFollowingNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend3().getUserID(), Notification.type5FriendHostingNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend4().getUserID(), Notification.type5FriendHostingNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend5().getUserID(), Notification.type6FriendHostedNotice ) );

        friend.getNotifications2().add( new Notification( this.getFriend1().getUserID(), Notification.type6FriendHostedNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend2().getUserID(), Notification.type6FriendHostedNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend3().getUserID(), Notification.type7FriendAttendingNotice ) );

        friend.getNotifications2().add( new Notification( this.getFriend1().getUserID(), Notification.type7FriendAttendingNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend2().getUserID(), Notification.type8FriendAttendedNotice ) );
        friend.getNotifications2().add( new Notification( this.getFriend3().getUserID(), Notification.type8FriendAttendedNotice ) );
    }

    public List<User> getFriendsListObjects(List<String> userIdList)
    {
        List<User> friendObjs = new ArrayList<User>();
        for(String id : userIdList){
            friendObjs.add(getFriendObject(id));
        }
        return friendObjs;
    }

    public User getFriendObject(String id)
    {
        if ( id.equals("1") ) {
            return friend1;
        } else if ( id.equals("2") ) {
            return friend2;
        } else if ( id.equals("3") ) {
            return friend3;
        } else if ( id.equals("4") ) {
            return friend4;
        } else if ( id.equals("5") ) {
            return friend5;
        } else {
            return friend1;
        }
    }

    public List<Party> getPartyListObjects(List<String> partyIdList)
    {
        List<Party> partyObjs = new ArrayList<>();
        for(String id: partyIdList){
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }

    public Party getPartyObject(String id)
    {
        if ( id.equals("1") ) {
            return party1;
        } else if ( id.equals("2") ) {
            return party2;
        } else if ( id.equals("3") ) {
            return party3;
        } else if ( id.equals("4") ) {
            return party4;
        } else if ( id.equals("5") ) {
            return party5;
        } else {
            return party1;
        }
    }

    private void setupDummy(Context context)
    {
        //setup best friend list
        this.getBirthday().set(1997, 4, 1);
        this.getBestFriends().add(new BestFriend("BFF", "1231231234"));
        this.getBestFriends().add(new BestFriend("BFF2", "1231231235"));

        //setup followers list
        this.getFollowers().add("1");
        this.getFollowers().add("2");
        this.getFollowers().add("3");
        this.getFollowers().add("4");
        this.getFollowers().add("5");
        this.getFollowers().add("1");
        this.getFollowers().add("2");
        this.getFollowers().add("3");

        //setup following list
        this.getFollowing().add("1");
        this.getFollowing().add("2");
        this.getFollowing().add("3");
        this.getFollowing().add("4");
        this.getFollowing().add("5");

        //setup party attended list
        this.getAttended().add("1");
        this.getAttended().add("2");
        this.getAttended().add("3");
        this.getAttended().add("4");
        this.getAttended().add("5");

        //setup party hosted list
        this.getHosted().add("1");
        this.getHosted().add("2");
        this.getHosted().add("3");
        this.getHosted().add("4");
        this.getHosted().add("5");

        //setup party bounced list
        this.getBounced().add("1");
        this.getBounced().add("2");
        this.getBounced().add("3");
        this.getBounced().add("4");
        this.getBounced().add("5");

        this.getAttending().add("1");
        this.getAttending().add("2");
        this.getAttending().add("3");
        this.getAttending().add("4");
        this.getAttending().add("5");

        // TODO: 04/21/2017 Add image by url
//        this.setProfilePic(new BitmapDrawable(context.getResources(),BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
    }

    private void setupPartyObjects(Context context)
    {
        party1 = new Party(
                "1",
                "Super Party 1",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                new MapAddress("6612 Sueno Rd Goleta, CA 93117", new LatLng(34.412923, -119.859315)),
                this.getFollowing(), this.getFollowing(), this.getFollowing(),
                true, "");
        party2 = new Party(
                "2",
                "Super Party 2",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                new MapAddress("6555 Segovia Rd Goleta, CA 93117", new LatLng(34.414241, -119.856559)),
                this.getFollowing(), this.getFollowing(), this.getFollowing(),
                true, "");
        party3 = new Party(
                "3",
                "Super Party 3",
                0,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                new MapAddress("6650 Picasso Rd, Goleta, CA 93117", new LatLng( 34.415500, -119.860575)),
                this.getFollowing(), this.getFollowing(), this.getFollowing(),
                true, null );
        party4 = new Party(
                "4",
                "Super Party 4 LongName for layout TESTING Text wrapping Multiple Lines Longer Event Here Making It Longer!",
                50,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                new MapAddress("895 Camino Del Sur Goleta, CA 93117", new LatLng(34.412938, -119.862853)),
                this.getFollowing(), this.getFollowing(), this.getFollowing(),
                true, "");
        party5 = new Party(
                "5",
                "Super Party 5",
                100,
                "Super Mario",
                Calendar.getInstance(),
                Calendar.getInstance(),
                new MapAddress("6628 Pasado Rd Goleta, CA 93117", new LatLng(34.411962, -119.859848)),
                this.getFollowing(), this.getFollowing(), this.getFollowing(),
                true, "");

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
        friend1 = new User("1",
                "Happy",
                "Friend1",
                "h1@ucsb.edu",
                "happy1",
                "MIT",
                "Female",
                "0000000",
                new MapAddress("6628 Pasado Rd Goleta, CA 93117", new LatLng(34.411962, -119.859848)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");
        friend2 = new User("2",
                "Sad",
                "Friend2",
                "s2@ucsb.edu",
                "sad2",
                "Cornell",
                "Female",
                "0000000000",
                new MapAddress("895 Camino Del Sur Goleta, CA 93117", new LatLng(34.412938, -119.862853)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");
        friend3 = new User("3",
                "Boring",
                "Friend3",
                "b3@ucsb.edu",
                "boring3",
                "UCB",
                "Male",
                "0000000000",
                new MapAddress("6650 Picasso Rd, Goleta, CA 93117", new LatLng( 34.415500, -119.860575)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");
        friend4 = new User("4",
                "Angry",
                "Friend4",
                "a4@ucsb.edu",
                "angry4",
                "UCSD",
                "Male",
                "0000000000",
                new MapAddress("6555 Segovia Rd Goleta, CA 93117", new LatLng(34.414241, -119.856559)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");
        friend5 = new User("5",
                "Lit",
                "Friend5",
                "l5@ucsb.edu",
                "lit5",
                "UCSB",
                "Other",
                "0000000000",
                new MapAddress("6612 Sueno Rd Goleta, CA 93117", new LatLng(34.412923, -119.859315)),
                Calendar.getInstance(),
                new ArrayList<String>(), //best friend list
                new ArrayList<String>(), //follower list
                new ArrayList<BestFriend>(), //following list
                new ArrayList<String>(), //attending list
                new ArrayList<String>(), //party attended list
                new ArrayList<String>(), //party hosted list
                new ArrayList<String>(), //party bounced list
                new ArrayList<String>(),
                new ArrayList<Notification>(), //notifications1
                new ArrayList<Notification>(), //notifications2
                "");
        friend1.getBirthday().set(1997, 1, 2);
        friend2.getBirthday().set(1998, 2, 3);
        friend3.getBirthday().set(1999, 3, 4);
        friend4.getBirthday().set(1990, 4, 5);
        friend5.getBirthday().set(1991, 5, 6);

        // TODO: 04/21/2017 Add image by url
//        friend1.setProfilePic(new BitmapDrawable(context.getResources(),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
//        friend2.setProfilePic(new BitmapDrawable(context.getResources(),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.happy_house)));
//        friend3.setProfilePic(new BitmapDrawable(context.getResources(),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));
//        friend4.setProfilePic(new BitmapDrawable(context.getResources(),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.happy_house)));
//        friend5.setProfilePic(new BitmapDrawable(context.getResources(),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_sample)));

        setupFriendNotifications2(friend1);
        setupFriendNotifications2(friend2);
        setupFriendNotifications2(friend3);
        setupFriendNotifications2(friend4);
        setupFriendNotifications2(friend5);
    }

    public User getDummyUser()
    {
        return this;
    }

    public void setContext(Context cont){
        context = cont;
    }

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
}
