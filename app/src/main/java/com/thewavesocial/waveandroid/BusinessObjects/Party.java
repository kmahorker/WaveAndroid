package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * Things I did during file translation:
 * 1. Changed List<Int64> to ArrayList<Long> (see line 37)
 * 2. Changed Int64 to long
 * 3. Changed DateTime to Date
 * - Wei Tung
 */
public class Party implements Parcelable {
    private long partyID;
    private String name;
    private double price; //decimal == double?
    private String hostName;
    private Calendar startingDateTime;
    private Calendar endingDateTime;
    private String address;
    private List<Long> attendingUsers;
    private boolean isPublic;

    public Party()
    {
        partyID = 0;
        name = "";
        price = 0;
        hostName = "";
        startingDateTime = Calendar.getInstance();
        endingDateTime = Calendar.getInstance();
        address = "";
        attendingUsers = new ArrayList<Long>();
        isPublic = false;
    }

    public Party(
            long partyID,
            String name,
            double price,
            String hostName,
            Calendar startingDateTime,
            Calendar endingDateTime,
            String address,
            List<Long> attendingUsers,
            boolean isPublic)
    {
        this.partyID = partyID;
        this.name = name;
        this.price = price;
        this.hostName = hostName;
        this.startingDateTime = startingDateTime;
        this.endingDateTime = endingDateTime;
        this.address = address;
        this.attendingUsers = attendingUsers;
        this.isPublic = isPublic;
    }

    //Delete
    public boolean removeAttending(long userIDToRemove)
    {
        return attendingUsers.remove(userIDToRemove);
    }

    //Add
    public void addAttending(long userIDToAdd)
    {
        attendingUsers.add(userIDToAdd);
    }

    //Setters
    public void setPartyID(long partyID)
    {
        this.partyID = partyID;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    public void setStartingDateTime(Calendar dateTimeObj)
    {
        this.startingDateTime = dateTimeObj;
    }

    public void setEndingDateTime(Calendar dateTimeObj)
    {
        this.endingDateTime = dateTimeObj;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setAttendingUsers(List<Long> attendingUsers)
    {
        this.attendingUsers = attendingUsers;
    }

    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    //Getters
    public long getPartyID()
    {
        return partyID;
    }

    public String getName()
    {
        return name;
    }

    public double getPrice()
    {
        return price;
    }

    public String getHostName()
    {
        return hostName;
    }

    public Calendar getStartingDateTime()
    {
        return startingDateTime;
    }

    public Calendar getEndingDateTime()
    {
        return endingDateTime;
    }

    public String getAddress()
    {
        return address;
    }

    public List<Long> getAttendingUsers()
    {
        return attendingUsers;
    }

    public boolean getIsPublic()
    {
        return isPublic;
    }

    @Override
    public String toString()
    {
        return name;
    }


    protected Party(Parcel in) {
        partyID = in.readLong();
        name = in.readString();
        price = in.readDouble();
        hostName = in.readString();
        startingDateTime = (Calendar) in.readValue(Calendar.class.getClassLoader());
        endingDateTime = (Calendar) in.readValue(Calendar.class.getClassLoader());
        address = in.readString();
        if (in.readByte() == 0x01) {
            attendingUsers = new ArrayList<Long>();
            in.readList(attendingUsers, Long.class.getClassLoader());
        } else {
            attendingUsers = null;
        }
        isPublic = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(partyID);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(hostName);
        dest.writeValue(startingDateTime);
        dest.writeValue(endingDateTime);
        dest.writeString(address);
        if (attendingUsers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attendingUsers);
        }
        dest.writeByte((byte) (isPublic ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Party> CREATOR = new Parcelable.Creator<Party>() {
        @Override
        public Party createFromParcel(Parcel in) {
            return new Party(in);
        }

        @Override
        public Party[] newArray(int size) {
            return new Party[size];
        }
    };
}