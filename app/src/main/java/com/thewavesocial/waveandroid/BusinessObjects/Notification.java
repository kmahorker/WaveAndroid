package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wei-Tung on 02/18/2017.
 * Stores all notifications:
 */
public class Notification implements Parcelable {
    public static final int type1FollowingNotice = 1;
    public static final int type2HostingNotice = 2;
    public static final int type3AttendingNotice = 3;
    public static final int type4FriendFollowingNotice = 4;
    public static final int type5FriendHostingNotice = 5;
    public static final int type6FriendHostedNotice = 6;
    public static final int type7FriendAttendingNotice = 7;
    public static final int type8FriendAttendedNotice = 8;
    private String message;
    private int requestType;
    private String senderID;

    public Notification()
    {
        message = "";
        requestType = 0;
    }

    public Notification( String senderID, int requestType )
    {
        if ( requestType == type1FollowingNotice )
        {
            message = "started following you.";
        }
        else if ( requestType == type2HostingNotice )
        {
            message = "is hosting a party.";
        }
        else if ( requestType == type3AttendingNotice )
        {
            message = "is going to a party near you.";
        }
        else if ( requestType == type4FriendFollowingNotice )
        {
            message = "Started following";
        }
        else if ( requestType == type5FriendHostingNotice)
        {
            message = "Hosting";
        }
        else if ( requestType == type6FriendHostedNotice )
        {
            message = "Hosted";
        }
        else if ( requestType == type7FriendAttendingNotice )
        {
            message = "Going to";
        }
        else if ( requestType == type8FriendAttendedNotice )
        {
            message = "Went to";
        }
        else
        {
            message = "No Content";
        }
        this.requestType = requestType;
        this.senderID = senderID;
    }

    public String getMessage()
    {
        return message;
    }

    public int getRequestType()
    {
        return requestType;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setRequestType(int requestType)
    {
        this.requestType = requestType;
    }

    public String getSenderID()
    {
        return senderID;
    }

    public void setSenderID(String senderID)
    {
        this.senderID = senderID;
    }

    protected Notification(Parcel in) {
        message = in.readString();
        requestType = in.readInt();
        senderID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeInt(requestType);
        dest.writeString(senderID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
