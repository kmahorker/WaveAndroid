package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Wei-Tung on 02/18/2017.
 * Stores all notifications:
 */
public class Notification extends CustomFirebaseObject implements Parcelable {
    public static final int TYPE_FOLLOWING = 1;
    public static final int TYPE_FOLLOWED = 2;
    public static final int TYPE_HOSTING = 3;
    public static final int TYPE_GOING = 4;
    public static final int TYPE_BOUNCING = 5;
    public static final int TYPE_INVITE_GOING = 6;
    public static final int TYPE_INVITE_BOUNCING = 7;

    private String message, senderID, receiverID, id, eid;
    private long create_time;
    private int requestType;

    public Notification() {
        eid = "";
        message = "";
        requestType = 0;
        senderID = "";
        id = "";
        create_time = 0;
    }

    public Notification(String senderID, int requestType, String eid) {
        if (requestType == TYPE_FOLLOWING)
            message = "Started following";
        else if (requestType == TYPE_FOLLOWED)
            message = "Followed by";
        else if (requestType == TYPE_HOSTING)
            message = "Hosting";
        else if (requestType == TYPE_GOING)
            message = "Going to";
        else if (requestType == TYPE_BOUNCING)
            message = "Bouncing";
        else if (requestType == TYPE_INVITE_GOING)
            message = "You are invited to";
        else if (requestType == TYPE_INVITE_BOUNCING)
            message = "You are invited to bounce";
        else
            message = "No Message";

        this.requestType = requestType;
        this.senderID = senderID;
        this.create_time = Calendar.getInstance().getTimeInMillis();
        this.eid = eid;
    }

    public String getMessage() {
        return message;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
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

    public long getCreate_time() {
        return create_time;
    }
}
