package com.thewavesocial.waveandroid.BusinessObjects;

//import android.media.Image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

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
public class User implements Parcelable {
    private long userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String college;
    private String gender;
    private String address;
    private Date birthday;
    private List<Long> bestFriends;
    private List<Long> friends;
    //Below contain list of PartyIDs
    private List<Long> attending;
    private List<Long> hosting;
    private List<Long> bouncing;
    private BitmapDrawable profilePic;



    public User()
    {

        userID = 0;
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        college = "";
        gender = "";
        address = "";
        birthday = new Date();
        bestFriends = new ArrayList<Long>();
        attending = new ArrayList<Long>();
        hosting = new ArrayList<Long>();
        bouncing = new ArrayList<Long>();
        profilePic = new BitmapDrawable(); //TODO Use different constructor
    }

    public User(//Int64 userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String college,
                String gender,
                String address,
                Date birthday,
                List<Long> friends,
                List<Long> bestFriends,
                List<Long> attending,
                List<Long> hosting,
                List<Long> bouncing,
                BitmapDrawable profilePic)
    {
        //this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.college = college;
        this.gender = gender;
        this.address = address;
        this.birthday = birthday;
        this.friends = friends;
        this.bestFriends = bestFriends;
        this.attending = attending;
        this.hosting = hosting;
        this.bouncing = bouncing;
        this.profilePic = profilePic;
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

    public void setAddress(String address)
    {
        this.address = address;
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

    public void setProfilePic(BitmapDrawable profilePic) { this.profilePic = profilePic; }

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

    public String getAddress()
    {
        return address;
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

    public BitmapDrawable getProfilePic() { return profilePic; }

    //http://www.parcelabler.com/
    //Allows putExtra(String, Parcelable Obj)
    protected User(Parcel in) {
        userID = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        password = in.readString();
        college = in.readString();
        gender = in.readString();
        address = in.readString();
        long tmpBirthday = in.readLong();
        birthday = tmpBirthday != -1 ? new Date(tmpBirthday) : null;
        if (in.readByte() == 0x01) {
            bestFriends = new ArrayList<Long>();
            in.readList(bestFriends, Long.class.getClassLoader());
        } else {
            bestFriends = null;
        }
        if (in.readByte() == 0x01) {
            friends = new ArrayList<Long>();
            in.readList(friends, Long.class.getClassLoader());
        } else {
            friends = null;
        }
        if (in.readByte() == 0x01) {
            attending = new ArrayList<Long>();
            in.readList(attending, Long.class.getClassLoader());
        } else {
            attending = null;
        }
        if (in.readByte() == 0x01) {
            hosting = new ArrayList<Long>();
            in.readList(hosting, Long.class.getClassLoader());
        } else {
            hosting = null;
        }
        if (in.readByte() == 0x01) {
            bouncing = new ArrayList<Long>();
            in.readList(bouncing, Long.class.getClassLoader());
        } else {
            bouncing = null;
        }
        Bitmap bitmap = (Bitmap)in.readParcelable(getClass().getClassLoader());
        profilePic = new BitmapDrawable(bitmap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(college);
        dest.writeString(gender);
        dest.writeString(address);
        dest.writeLong(birthday != null ? birthday.getTime() : -1L);
        if (bestFriends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bestFriends);
        }
        if (friends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(friends);
        }
        if (attending == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attending);
        }
        if (hosting == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hosting);
        }
        if (bouncing == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bouncing);
        }
        Bitmap bitmap = profilePic.getBitmap();
        dest.writeParcelable(bitmap, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}