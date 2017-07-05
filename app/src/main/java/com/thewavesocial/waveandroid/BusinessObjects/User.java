package com.thewavesocial.waveandroid.BusinessObjects;

import android.graphics.Bitmap;
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
//public class User implements Parcelable
public class User implements Parcelable {
    private String userID; //
    private String firstName; //
    private String lastName; //
    private String email; //
    private String college; //
    private String gender; //
    private Calendar birthday; //
    private List<BestFriend> bestFriends; //
    private Bitmap profilePic; //

    public User() {
        userID = ""; //
        firstName = ""; //
        lastName = ""; //
        email = ""; //
        college = ""; //
        gender = ""; //
        birthday = Calendar.getInstance(); //
        bestFriends = new ArrayList<>();
        profilePic = null; //TODO Use different constructor
    }

    public User(String userID,
                String firstName,
                String lastName,
                String email,
                String college,
                String gender,
                Calendar birthday,
                List<BestFriend> bestFriends,
                Bitmap profilePic) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.college = college;
        this.gender = gender;
        this.birthday = birthday;
        this.bestFriends = bestFriends;
        this.profilePic = profilePic;
    }

    //Setter Block
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = Calendar.getInstance();
    }

    public void setBestFriends(List<BestFriend> bestFriends) {
        this.bestFriends = bestFriends;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    //Getter Block
    public String getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCollege() {
        return college;
    }

    public String getGender() {
        return gender;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public List<BestFriend> getBestFriends() {
        return bestFriends;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        try {
            User otherUser = (User) other;
            return this.getUserID().equals(otherUser.getUserID());
        } catch (ClassCastException e) {
            return this.getUserID().equals(other);
        }
    }

    protected User(Parcel in) {
        userID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        college = in.readString();
        gender = in.readString();
        birthday = (Calendar) in.readValue(Calendar.class.getClassLoader());
        if (in.readByte() == 0x01) {
            bestFriends = new ArrayList<>();
            in.readList(bestFriends, BestFriend.class.getClassLoader());
        } else {
            bestFriends = null;
        }
        profilePic = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(college);
        dest.writeString(gender);
        dest.writeValue(birthday);
        if (bestFriends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bestFriends);
        }
        dest.writeValue(profilePic);
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