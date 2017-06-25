package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kmaho on 3/27/2017.
 */

public class BestFriend implements Parcelable {
    String name;
    String phoneNumber;

    public BestFriend() {
        name = "";
        phoneNumber = "0";
    }

    public BestFriend(String name, String num) {
        this.name = name;
        phoneNumber = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    protected BestFriend(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BestFriend> CREATOR = new Parcelable.Creator<BestFriend>() {
        @Override
        public BestFriend createFromParcel(Parcel in) {
            return new BestFriend(in);
        }

        @Override
        public BestFriend[] newArray(int size) {
            return new BestFriend[size];
        }
    };
}
