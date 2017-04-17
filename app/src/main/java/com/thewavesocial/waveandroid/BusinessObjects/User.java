package com.thewavesocial.waveandroid.BusinessObjects;

//import android.media.Image;

import android.graphics.drawable.BitmapDrawable;

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
public class User
{
    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String college;
    private String gender;
    private String phone;
    private MapAddress mapAddress;
    private Calendar birthday;
    private List<BestFriend> bestFriends;
    private List<String> followers;
    private List<String> following;
    //Below contain list of PartyIDs
    private List<String> hosting;
    private List<String> attended;
    private List<String> hosted;
    private List<String> bounced;
    private List<String> attending;
    private List<Notification> notifications1;
    private List<Notification> notifications2;
    private BitmapDrawable profilePic;

    public User()
    {
        userID = "";
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        college = "";
        gender = "";
        phone = "";
        mapAddress = new MapAddress();
        birthday = Calendar.getInstance();
        bestFriends = new ArrayList<>();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        hosting = new ArrayList<>();
        attended = new ArrayList<>();
        hosted = new ArrayList<>();
        bounced = new ArrayList<>();
        attending = new ArrayList<>();
        notifications1 = new ArrayList<>();
        notifications2 = new ArrayList<>();
        profilePic = new BitmapDrawable(); //TODO Use different constructor
    }

    public User(String userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String college,
                String gender,
                String phone,
                MapAddress mapAddress,
                Calendar birthday,
                List<String> followers,
                List<String> following,
                List<BestFriend> bestFriends,
                List<String> hosting, List<String> attended,
                List<String> hosted,
                List<String> bounced,
                List<String> attending,
                List<Notification> notifications1,
                List<Notification> notifications2,
                BitmapDrawable profilePic)
    {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.college = college;
        this.gender = gender;
        this.phone = phone;
        this.mapAddress = mapAddress;
        this.birthday = birthday;
        this.followers = followers;
        this.following = following;
        this.bestFriends = bestFriends;
        this.hosting = hosting;
        this.attended = attended;
        this.hosted = hosted;
        this.bounced = bounced;
        this.attending = attending;
        this.notifications1 = notifications1;
        this.notifications2 = notifications2;
        this.profilePic = profilePic;
    }

    //Setter Block
    public void setUserID(String userID)
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

    public void setBestFriends(List<BestFriend> bestFriends)
    {
        this.bestFriends = bestFriends;
    }

    public void setAttended(List<String> attended)
    {
        this.attended = attended;
    }

    public void setHosted(List<String> hosted)
    {
        this.hosted = hosted;
    }

    public void setBounced(List<String> bounced)
    {
        this.bounced = bounced;
    }

    public void setAttending(List<String> attending) {
        this.attending = attending;
    }

    public void setProfilePic(BitmapDrawable profilePic) { this.profilePic = profilePic; }

    //Getter Block
    public String getUserID()
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

    public List<BestFriend> getBestFriends()
    {
        return bestFriends;
    }

    public List<String> getAttended()
    {
        return attended;
    }

    public List<String> getHosted()
    {
        return hosted;
    }

    public List<String> getBounced()
    {
        return bounced;
    }


    public List<String> getAttending()
    {
        return attending;
    }

    public BitmapDrawable getProfilePic()
    {
        return profilePic;
    }

    public List<Notification> getNotifications1()
    {
        return notifications1;
    }

    public void setNotifications1(List<Notification> notifications1)
    {
        this.notifications1 = notifications1;
    }

    public List<Notification> getNotifications2()
    {
        return notifications2;
    }

    public void setNotifications2(List<Notification> notifications2)
    {
        this.notifications2 = notifications2;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public List<String> getFollowers()
    {
        return followers;
    }

    public void setFollowers(List<String> followers)
    {
        this.followers = followers;
    }

    public List<String> getFollowing()
    {
        return following;
    }

    public void setFollowing(List<String> following)
    {
        this.following = following;
    }

    //    //http://www.parcelabler.com/
//    //Allows putExtra(String, Parcelable Obj)
//    protected User(Parcel in) {
//        userID = in.readLong();
//        firstName = in.readString();
//        lastName = in.readString();
//        email = in.readString();
//        password = in.readString();
//        college = in.readString();
//        gender = in.readString();
//        //mapAddress = in.readString();
//        birthday = (Calendar) in.readValue(Calendar.class.getClassLoader());
//        if (in.readByte() == 0x01) {
//            bestFriends = new ArrayList<Long>();
//            in.readList(bestFriends, Long.class.getClassLoader());
//        } else {
//            bestFriends = null;
//        }
//        if (in.readByte() == 0x01) {
//            followers = new ArrayList<Long>();
//            in.readList(followers, Long.class.getClassLoader());
//        } else {
//            followers = null;
//        }
//        if (in.readByte() == 0x01) {
//            attended = new ArrayList<Long>();
//            in.readList(attended, Long.class.getClassLoader());
//        } else {
//            attended = null;
//        }
//        if (in.readByte() == 0x01) {
//            hosted = new ArrayList<Long>();
//            in.readList(hosted, Long.class.getClassLoader());
//        } else {
//            hosted = null;
//        }
//        if (in.readByte() == 0x01) {
//            bounced = new ArrayList<Long>();
//            in.readList(bounced, Long.class.getClassLoader());
//        } else {
//            bounced = null;
//        }
//        Bitmap bitmap = (Bitmap)in.readParcelable(getClass().getClassLoader());
//        profilePic = new BitmapDrawable(bitmap);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(userID);
//        dest.writeString(firstName);
//        dest.writeString(lastName);
//        dest.writeString(email);
//        dest.writeString(password);
//        dest.writeString(college);
//        dest.writeString(gender);
//        //dest.writeString(mapAddress);
//        dest.writeValue(birthday);
//        if (bestFriends == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(bestFriends);
//        }
//        if (followers == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(followers);
//        }
//        if (attended == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(attended);
//        }
//        if (hosted == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(hosted);
//        }
//        if (bounced == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(bounced);
//        }
//        Bitmap bitmap = profilePic.getBitmap();
//        dest.writeParcelable(bitmap, flags);
//    }
//
//    @SuppressWarnings("unused")
//    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
//        @Override
//        public User createFromParcel(Parcel in) {
//            return new User(in);
//        }
//
//        @Override
//        public User[] newArray(int size) {
//            return new User[size];
//        }
//    };
}