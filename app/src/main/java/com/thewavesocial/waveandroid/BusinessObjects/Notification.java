package com.thewavesocial.waveandroid.BusinessObjects;

/**
 * Created by Wei-Tung on 02/18/2017.
 * Stores all notifications:
 */
public class Notification
{
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
    private long senderID;

    public Notification()
    {
        message = "";
        requestType = 0;
    }

    public Notification( long senderID, int requestType )
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

    public long getSenderID()
    {
        return senderID;
    }

    public void setSenderID(long senderID)
    {
        this.senderID = senderID;
    }
}
