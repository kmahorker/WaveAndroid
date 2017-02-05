package com.thewavesocial.waveandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thewavesocial.waveandroid.BusinessObjects.*;

import java.util.List;
import java.util.zip.Inflater;

public class MapsFragmentActivity extends Fragment implements OnMapReadyCallback
{

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.home_maps_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        refreshParty("UCSB", 34.4133, -119.8610);
    }

    // Add a marker to UCSB and move the camera
    public void refreshParty(String name, double lat, double lng)
    {
        LatLng loc = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("Marker at " + name)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(150,150))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, (float)15.0));
    }

    public Bitmap resizeMapIcons(int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.happy_house);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
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
