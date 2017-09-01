package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/*
 * Things I did during file translation:
 * 1. Changed List<Int64> to ArrayList<Long> (see lines 43 - 46)
 * 2. Changed Int64 to long
 * 3. Changed DateTime to Date
 * - Wei Tung
 */
//public class User implements Parcelable
public class User extends CustomFirebaseObject implements Parcelable {
    private String facebookID;
    @Exclude //userID represents the Firebase key, so it should not be stored
    private String first_name; //
    private String last_name; //
    private String gender; //
    private int follower_count = 0;
    private int following_count = 0;
   // private Calendar birthday; //
    private List<BestFriend> bestFriends; //
   // private Bitmap profilePic; //

    public User() {
        this.setId("");
        first_name = ""; //
        last_name = ""; //
        gender = ""; //
        follower_count = 0;
        following_count = 0;
     //   birthday = Calendar.getInstance(); //
        bestFriends = new ArrayList<>();
     //   profilePic = null; //TODO Use different constructor
    }

    public User(String userID,
                String facebookID,
                String first_name,
                String last_name,
                String gender,
        //        Calendar birthday,
                List<BestFriend> bestFriends) {
                //Bitmap profilePic) {
        this.setId(userID);
        this.facebookID = facebookID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.follower_count = 0;
        this.following_count = 0;
      //  this.birthday = birthday;
        this.bestFriends = bestFriends;
      //  this.profilePic = profilePic;
    }

    //Setter Block

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFollower_count(int count) {this.follower_count = count; }

    public void setFollowing_count(int count) {this.following_count = count; }

  /*  public void setBirthday(Calendar birthday) {
        this.birthday = Calendar.getInstance();
    }*/

    public void setBestFriends(List<BestFriend> bestFriends) {
        this.bestFriends = bestFriends;
    }

/*    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }*/

    //Getter Block

    public int getFollower_count() {return follower_count; }

    public int getFollowing_count() {return following_count; }

    public String getFacebookID() {
        return facebookID;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFull_name() {
        return first_name + " " + last_name;
    }

    public String getGender() {
        return gender;
    }

  /*  public Calendar getBirthday() {
        return birthday;
    }*/

    public List<BestFriend> getBestFriends() {
        return bestFriends;
    }

   /* public Bitmap getProfilePic() {
        return profilePic;
    }*/

    @Override
    public String toString() {
        return first_name + " " + last_name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        try {
            User otherUser = (User) other;
            return this.getFull_name().equals(otherUser.getFull_name());
        } catch (ClassCastException e) {
            return this.getFull_name().equals(other);
        }
    }

    protected User(Parcel in) {
        this.setId(in.readString());
        first_name = in.readString();
        last_name = in.readString();
        gender = in.readString();
        //birthday = (Calendar) in.readValue(Calendar.class.getClassLoader());
        if (in.readByte() == 0x01) {
            bestFriends = new ArrayList<>();
            in.readList(bestFriends, BestFriend.class.getClassLoader());
        } else {
            bestFriends = null;
        }
        //profilePic = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(gender);
      //  dest.writeValue(birthday);
        if (bestFriends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bestFriends);
        }
       // dest.writeValue(profilePic);
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