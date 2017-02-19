package com.thewavesocial.waveandroid.BusinessObjects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Wei-Tung on 02/18/2017.
 * MapAddress holds a String value and a LatLng value
 */
public class MapAddress
{
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
}
