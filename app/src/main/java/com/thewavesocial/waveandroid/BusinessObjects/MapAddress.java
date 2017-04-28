package com.thewavesocial.waveandroid.BusinessObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Wei-Tung on 02/18/2017.
 * MapAddress holds a String value and a LatLng value
 */
public class MapAddress implements Parcelable {
    private String address_string;
    private LatLng address_latlng;

    public MapAddress()
    {
        this.address_string = "";
        this.address_latlng = null;
    }

    public MapAddress(String address_string, LatLng address_latlng )
    {
        this.address_string = address_string;
        this.address_latlng = address_latlng;
    }

    public String getAddress_string()
    {
        return address_string;
    }

    public LatLng getAddress_latlng()
    {
        return address_latlng;
    }

    public void setAddress_string(String address_string)
    {
        this.address_string = address_string;
    }

    public void setAddress_latlng(LatLng address_latlng)
    {
        this.address_latlng = address_latlng;
    }

    protected MapAddress(Parcel in) {
        address_string = in.readString();
        address_latlng = (LatLng) in.readValue(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address_string);
        dest.writeValue(address_latlng);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MapAddress> CREATOR = new Parcelable.Creator<MapAddress>() {
        @Override
        public MapAddress createFromParcel(Parcel in) {
            return new MapAddress(in);
        }

        @Override
        public MapAddress[] newArray(int size) {
            return new MapAddress[size];
        }
    };
}

