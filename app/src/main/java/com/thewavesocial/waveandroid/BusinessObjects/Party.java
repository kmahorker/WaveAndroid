package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;


public class Party implements Parcelable {

    private String address;
    private long date;
    private long duration;
    private String emoji;
    private String host_id;
    private String host_name;
    private boolean is_public;
    private double lat;
    private double lng;
    private int max_age;
    private int min_age;
    private String name;
    private String partyID;
    private double price;

    public Party() {
        this.address = "";
        this.date = 0;
        this.duration = 0;
        this.emoji = "";
        this.host_id = "";
        this.host_name = "";
        this.is_public = false;
        this.lat = 0;
        this.lng = 0;
        this.max_age = 100;
        this.min_age = 18;
        this.name = "";
        this.partyID = "123";
        this.price = 0;
    }

    public Party(String address, long date, long duration, String emoji, String host_id, String host_name, boolean is_public, double lat, double lng, int max_age, int min_age, String name, String partyID, double price) {
        this.address = address;
        this.date = date;
        this.duration = duration;
        this.emoji = emoji;
        this.host_id = host_id;
        this.host_name = host_name;
        this.is_public = is_public;
        this.lat = lat;
        this.lng = lng;
        this.max_age = max_age;
        this.min_age = min_age;
        this.name = name;
        this.partyID = partyID;
        this.price = price;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public boolean is_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getMax_age() {
        return max_age;
    }

    public void setMax_age(int max_age) {
        this.max_age = max_age;
    }

    public int getMin_age() {
        return min_age;
    }

    public void setMin_age(int min_age) {
        this.min_age = min_age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartyID() {
        return partyID;
    }

    public void setPartyID(String partyID) {
        this.partyID = partyID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    protected Party(Parcel in) {
        address = in.readString();
        date = in.readLong();
        duration = in.readLong();
        emoji = in.readString();
        host_id = in.readString();
        host_name = in.readString();
        is_public = in.readByte() != 0x00;
        lat = in.readDouble();
        lng = in.readDouble();
        max_age = in.readInt();
        min_age = in.readInt();
        name = in.readString();
        partyID = in.readString();
        price = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeLong(date);
        dest.writeLong(duration);
        dest.writeString(emoji);
        dest.writeString(host_id);
        dest.writeString(host_name);
        dest.writeByte((byte) (is_public ? 0x01 : 0x00));
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(max_age);
        dest.writeInt(min_age);
        dest.writeString(name);
        dest.writeString(partyID);
        dest.writeDouble(price);
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