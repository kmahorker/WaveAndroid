package com.thewavesocial.waveandroid.BusinessObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Things I did during file translation:
 * 1. Changed List<Int64> to ArrayList<Long> (see lines 43 - 46)
 * 2. Changed Int64 to long
 * 3. Changed DateTime to Date
 * - Wei Tung
 */
public class User
{
    private long userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String college;
    private String gender;
    private Date birthday;
    private List<Long> bestFriends;
    private List<Long> friends;
    //Below contain list of PartyIDs
    private List<Long> attending;
    private List<Long> hosting;
    private List<Long> bouncing;

    public User()
    {
        userID = 0;
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        college = "";
        gender = "";
        birthday = new Date();
        bestFriends = new ArrayList<Long>();
        attending = new ArrayList<Long>();
        hosting = new ArrayList<Long>();
        bouncing = new ArrayList<Long>();
    }

    public User(//Int64 userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String college,
                String gender,
                Date birthday,
                List<Long> friends,
                List<Long> bestFriends,
                List<Long> attending,
                List<Long> hosting,
                List<Long> bouncing)
    {
        //this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.college = college;
        this.gender = gender;
        this.birthday = birthday;
        this.friends = friends;
        this.bestFriends = bestFriends;
        this.attending = attending;
        this.hosting = hosting;
        this.bouncing = bouncing;
    }

    //Deleting Block
    public boolean removeFriend(long friendIDToRemove)
    {
        return friends.remove(friendIDToRemove);
    }

    public boolean removeBestFriend(long bestFriendIDToRemove)
    {

        return bestFriends.remove(bestFriendIDToRemove);
    }

    public boolean removeAttending(long attendingIDToRemove)
    {
        return attending.remove(attendingIDToRemove);
    }

    public boolean removeHosting(long hostingIDToRemove)
    {
        return hosting.remove(hostingIDToRemove);
    }

    public boolean removeBouncing(long bouncingIDToRemove)
    {
        return bouncing.remove(bouncingIDToRemove);
    }

    //Adding Block
    public void addFriend(long friendID)
    {
        this.friends.add(friendID);
    }
    public void addBestFriend(long bestFriendID)
    {
        this.bestFriends.add(bestFriendID);
    }

    public void addAttending(long attendingPartyID)
    {
        this.attending.add(attendingPartyID);
    }

    public void addHosting(long hostingPartyID)
    {
        this.hosting.add(hostingPartyID);
    }

    public void addBouncing(long bouncingPartyID)
    {
        this.bouncing.add(bouncingPartyID);
    }

    //Setter Block
    public void setUserID(long userID)
    {
        this.userID = userID;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setCollege(String college)
    {
        this.college = college;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public void setFriends(List<Long> friends)
    {
        this.friends = friends;
    }

    public void setBestFriends(List<Long> bestFriends)
    {
        this.bestFriends = bestFriends;
    }

    public void setAttending(List<Long> attending)
    {
        this.attending = attending;
    }

    public void setHosting(List<Long> hosting)
    {
        this.hosting = hosting;
    }

    public void setBouncing(List<Long> bouncing)
    {
        this.bouncing = bouncing;
    }

    //Getter Block
    public long getUserID()
    {
        return userID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getCollege()
    {
        return college;
    }

    public String getGender()
    {
        return gender;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public List<Long> getFriends()
    {
        return friends;
    }

    public List<Long> getBestFriends()
    {
        return bestFriends;
    }

    public List<Long> getAttending()
    {
        return attending;
    }

    public List<Long> getHosting()
    {
        return hosting;
    }

    public List<Long> getBouncing()
    {
        return bouncing;
    }
}
