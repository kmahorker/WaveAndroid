package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kmaho on 3/27/2017.
 */

public class BestFriend implements Parcelable {
    String name;
    int phoneNumber;

    public BestFriend(){
        name = "";
        phoneNumber = 0;
    }

    public BestFriend(String name, int num){
        this.name = name;
        phoneNumber = num;
    }

    public BestFriend(String name, String numString){
        this.name = name;
        String str = numString.replaceAll("[^\\d]", "");
        phoneNumber = Integer.parseInt(str);
    }

    public String getName() {
        return name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    protected BestFriend(Parcel in) {
        name = in.readString();
        phoneNumber = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(phoneNumber);
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
