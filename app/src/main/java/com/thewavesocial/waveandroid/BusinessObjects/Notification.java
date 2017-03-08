package com.thewavesocial.waveandroid.BusinessObjects;

/**
 * Created by Wei-Tung on 02/18/2017.
 * Stores all notifications:
 */
public class Notification
{
    public static final int type0GeneralRequests = 0;
    public static final int type1FollowingNotice = 1;
    public static final int type2HostingNotice = 2;
    public static final int type3AttendingNotice = 3;
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
