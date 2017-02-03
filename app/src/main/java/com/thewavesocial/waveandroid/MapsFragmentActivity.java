package com.thewavesocial.waveandroid;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thewavesocial.waveandroid.BusinessObjects.*;

import java.util.List;

public class MapsFragmentActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_maps_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        refreshParty("UCSB", 34, 120);
    }

    // Add a marker to UCSB and move the camera
    public void refreshParty(String name, double lat, double lng)
    {
        LatLng loc = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("Marker at " + name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.happy_house)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

//    public void refreshParties(GoogleMap googleMap, List<String> parties_addresses)
//    {
//        for ( String each_address : parties_addresses )
//        {
//            refreshParty(googleMap, each_address);
//        }
//    }
//
//    private void refreshParty(GoogleMap googleMap, String each_address)
//    {
//    }
}
