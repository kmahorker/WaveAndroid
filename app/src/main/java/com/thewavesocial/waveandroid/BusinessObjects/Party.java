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
    private String partyID;
    private String name;
    private double price;
    private String hostName; //to hostID
    private Calendar startingDateTime;
    private Calendar endingDateTime;
    private MapAddress mapAddress;
    private boolean isPublic;
    private String partyEmoji;
    private int minAge;
    private int maxAge;

    public Party() {
        partyID = "0";
        name = "";
        price = 0;
        hostName = "";
        startingDateTime = Calendar.getInstance();
        endingDateTime = Calendar.getInstance();
        mapAddress = new MapAddress();
        isPublic = false;
        partyEmoji = "";
        minAge = 0;
        maxAge = 0;
    }

    public Party(
            String partyID,
            String name,
            double price,
            String hostName,
            Calendar startingDateTime,
            Calendar endingDateTime,
            MapAddress mapAddress,
            boolean isPublic,
            String partyEmoji,
            int minAge,
            int maxAge) {
        this.partyID = partyID;
        this.name = name;
        this.price = price;
        this.hostName = hostName;
        this.startingDateTime = startingDateTime;
        this.endingDateTime = endingDateTime;
        this.mapAddress = mapAddress;
        this.isPublic = isPublic;
        this.partyEmoji = partyEmoji;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    //Setters
    public void setPartyID(String partyID) {
        this.partyID = partyID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setStartingDateTime(Calendar dateTimeObj) {
        this.startingDateTime = dateTimeObj;
    }

    public void setEndingDateTime(Calendar dateTimeObj) {
        this.endingDateTime = dateTimeObj;
    }

    public void setMapAddress(MapAddress mapAddress) {
        this.mapAddress = mapAddress;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    //Getters
    public String getPartyID() {
        return partyID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getHostName() {
        return hostName;
    }

    public Calendar getStartingDateTime() {
        return startingDateTime;
    }

    public Calendar getEndingDateTime() {
        return endingDateTime;
    }

    public MapAddress getMapAddress() {
        return mapAddress;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getPartyEmoji() {
        return partyEmoji;
    }

    public void setPartyEmoji(String partyEmoji) {
        this.partyEmoji = partyEmoji;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }


    protected Party(Parcel in) {
        partyID = in.readString();
        name = in.readString();
        price = in.readDouble();
        hostName = in.readString();
        startingDateTime = (Calendar) in.readValue(Calendar.class.getClassLoader());
        endingDateTime = (Calendar) in.readValue(Calendar.class.getClassLoader());
        mapAddress = (MapAddress) in.readValue(MapAddress.class.getClassLoader());
        isPublic = in.readByte() != 0x00;
        partyEmoji = in.readString();
        minAge = in.readInt();
        maxAge = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partyID);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(hostName);
        dest.writeValue(startingDateTime);
        dest.writeValue(endingDateTime);
        dest.writeValue(mapAddress);
        dest.writeByte((byte) (isPublic ? 0x01 : 0x00));
        dest.writeString(partyEmoji);
        dest.writeInt(minAge);
        dest.writeInt(maxAge);
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

