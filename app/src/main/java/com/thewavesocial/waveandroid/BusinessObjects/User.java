package com.thewavesocial.waveandroid.BusinessObjects;

//import android.media.Image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
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
    private MapAddress mapAddress;
    private Calendar birthday;
    private List<Long> bestFriends;
    private List<Long> friends;
    //Below contain list of PartyIDs
    private List<Long> attended;
    private List<Long> hosted;
    private List<Long> bounced;
    private List<Long> signedUp;
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
        mapAddress = new MapAddress();
        birthday = Calendar.getInstance();
        bestFriends = new ArrayList<Long>();
        attended = new ArrayList<Long>();
        hosted = new ArrayList<Long>();
        bounced = new ArrayList<Long>();
        signedUp = new ArrayList<Long>();
        profilePic = new BitmapDrawable(); //TODO Use different constructor
    }

    public User(Long userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String college,
                String gender,
                MapAddress mapAddress,
                Calendar birthday,
                List<Long> friends,
                List<Long> bestFriends,
                List<Long> attended,
                List<Long> hosted,
                List<Long> bounced,
                List<Long> signedUp,
                BitmapDrawable profilePic)
    {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.college = college;
        this.gender = gender;
        this.mapAddress = mapAddress;
        this.birthday = Calendar.getInstance();
        this.friends = friends;
        this.bestFriends = bestFriends;
        this.attended = attended;
        this.hosted = hosted;
        this.bounced = bounced;
        this.signedUp = signedUp;
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

    public boolean removeAttended(long attendedIDToRemove)
    {
        return attended.remove(attendedIDToRemove);
    }

    public boolean removeHosted(long hostedIDToRemove)
    {
        return hosted.remove(hostedIDToRemove);
    }

    public boolean removeBounced(long bouncedIDToRemove)
    {
        return bounced.remove(bouncedIDToRemove);
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

    public void addAttended(long attendedPartyID)
    {
        this.attended.add(attendedPartyID);
    }

    public void addHosted(long hostedPartyID)
    {
        this.hosted.add(hostedPartyID);
    }

    public void addBounced(long bouncedPartyID)
    {
        this.bounced.add(bouncedPartyID);
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

    public void setMapAddress(MapAddress mapAddress)
    {
        this.mapAddress = mapAddress;
    }

    public void setBirthday(Calendar birthday)
    {
        this.birthday = Calendar.getInstance();
    }

    public void setFriends(List<Long> friends)
    {
        this.friends = friends;
    }

    public void setBestFriends(List<Long> bestFriends)
    {
        this.bestFriends = bestFriends;
    }

    public void setAttended(List<Long> attended)
    {
        this.attended = attended;
    }

    public void setHosted(List<Long> hosted)
    {
        this.hosted = hosted;
    }

    public void setBounced(List<Long> bounced)
    {
        this.bounced = bounced;
    }

    public void setSignedUp(List<Long> signedUp) {
        this.signedUp = signedUp;
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

    public MapAddress getMapAddress()
    {
        return mapAddress;
    }

    public Calendar getBirthday()
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

    public List<Long> getAttended()
    {
        return attended;
    }

    public List<Long> getHosted()
    {
        return hosted;
    }

    public List<Long> getBounced()
    {
        return bounced;
    }


    public List<Long> getSignedUp() {
        return signedUp;
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
        //mapAddress = in.readString();
        birthday = (Calendar) in.readValue(Calendar.class.getClassLoader());
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
            attended = new ArrayList<Long>();
            in.readList(attended, Long.class.getClassLoader());
        } else {
            attended = null;
        }
        if (in.readByte() == 0x01) {
            hosted = new ArrayList<Long>();
            in.readList(hosted, Long.class.getClassLoader());
        } else {
            hosted = null;
        }
        if (in.readByte() == 0x01) {
            bounced = new ArrayList<Long>();
            in.readList(bounced, Long.class.getClassLoader());
        } else {
            bounced = null;
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
        //dest.writeString(mapAddress);
        dest.writeValue(birthday);
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
        if (attended == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attended);
        }
        if (hosted == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hosted);
        }
        if (bounced == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bounced);
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