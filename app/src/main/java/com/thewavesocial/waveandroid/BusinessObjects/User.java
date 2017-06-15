package com.thewavesocial.waveandroid.BusinessObjects;

//import android.media.Image;

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
    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String college;
    private String gender;
    private Calendar birthday;
    private List<BestFriend> bestFriends;
    private List<String> followers;
    private List<String> following;

    //Below contain list of PartyIDs
    private List<String> hosted;
    private List<String> hosting;
    private List<String> attended;
    private List<String> attending;
    private List<String> bouncing;
    private List<String> going;
    private List<Notification> notifications;
    private Bitmap profilePic;

    public User() {
        userID = "0";
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        college = "";
        gender = "";
        birthday = Calendar.getInstance();
        bestFriends = new ArrayList<>();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        hosting = new ArrayList<>();
        attended = new ArrayList<>();
        hosted = new ArrayList<>();
        bouncing = new ArrayList<>();
        attending = new ArrayList<>();
        going = new ArrayList<>();
        notifications = new ArrayList<>();
        profilePic = null; //TODO Use different constructor
    }

    public User(String userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String college,
                String gender,
                Calendar birthday,
                List<String> followers,
                List<String> following,
                List<BestFriend> bestFriends,
                List<String> hosting,
                List<String> attended,
                List<String> hosted,
                List<String> bouncing,
                List<String> attending,
                List<String> going,
                List<Notification> notifications,
                Bitmap profilePic) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.college = college;
        this.gender = gender;
        this.birthday = birthday;
        this.followers = followers;
        this.following = following;
        this.bestFriends = bestFriends;
        this.hosting = hosting;
        this.hosted = hosted;
        this.attending = attending;
        this.attended = attended;
        this.bouncing = bouncing;
        this.going = going;
        this.notifications = notifications;
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

    public void setPassword(String password) {
        this.password = password;
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

    public void setAttended(List<String> attended) {
        this.attended = attended;
    }

    public void setHosted(List<String> hosted) {
        this.hosted = hosted;
    }

    public void setBouncing(List<String> bouncing) {
        this.bouncing = bouncing;
    }

    public void setAttending(List<String> attending) {
        this.attending = attending;
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

    public List<String> getAttended() {
        return attended;
    }

    public List<String> getHosted() {
        return hosted;
    }

    public List<String> getBouncing() {
        return bouncing;
    }


    public List<String> getAttending() {
        return attending;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public List<String> getHosting() {
        return hosting;
    }

    public void setHosting(List<String> hosting) {
        this.hosting = hosting;
    }

    public List<String> getGoing() {
        return going;
    }

    public void setGoing(List<String> going) {
        this.going = going;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User otherUser = (User) other;
        return this.getUserID().equals(otherUser.getUserID());
    }

    protected User(Parcel in) {
        userID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        password = in.readString();
        college = in.readString();
        gender = in.readString();
        birthday = (Calendar) in.readValue(Calendar.class.getClassLoader());
        if (in.readByte() == 0x01) {
            bestFriends = new ArrayList<BestFriend>();
            in.readList(bestFriends, BestFriend.class.getClassLoader());
        } else {
            bestFriends = null;
        }
        if (in.readByte() == 0x01) {
            followers = new ArrayList<String>();
            in.readList(followers, String.class.getClassLoader());
        } else {
            followers = null;
        }
        if (in.readByte() == 0x01) {
            following = new ArrayList<String>();
            in.readList(following, String.class.getClassLoader());
        } else {
            following = null;
        }
        if (in.readByte() == 0x01) {
            hosting = new ArrayList<String>();
            in.readList(hosting, String.class.getClassLoader());
        } else {
            hosting = null;
        }
        if (in.readByte() == 0x01) {
            attended = new ArrayList<String>();
            in.readList(attended, String.class.getClassLoader());
        } else {
            attended = null;
        }
        if (in.readByte() == 0x01) {
            hosted = new ArrayList<String>();
            in.readList(hosted, String.class.getClassLoader());
        } else {
            hosted = null;
        }
        if (in.readByte() == 0x01) {
            bouncing = new ArrayList<String>();
            in.readList(bouncing, String.class.getClassLoader());
        } else {
            bouncing = null;
        }
        if (in.readByte() == 0x01) {
            attending = new ArrayList<String>();
            in.readList(attending, String.class.getClassLoader());
        } else {
            attending = null;
        }
        if (in.readByte() == 0x01) {
            going = new ArrayList<String>();
            in.readList(going, String.class.getClassLoader());
        } else {
            going = null;
        }
        if (in.readByte() == 0x01) {
            notifications = new ArrayList<Notification>();
            in.readList(notifications, Notification.class.getClassLoader());
        } else {
            notifications = null;
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
        dest.writeString(password);
        dest.writeString(college);
        dest.writeString(gender);
        dest.writeValue(birthday);
        if (bestFriends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bestFriends);
        }
        if (followers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(followers);
        }
        if (following == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(following);
        }
        if (hosting == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hosting);
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
        if (bouncing == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bouncing);
        }
        if (attending == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attending);
        }
        if (going == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(going);
        }
        if (notifications == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(notifications);
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