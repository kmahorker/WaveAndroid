package com.thewavesocial.waveandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static android.R.attr.name;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter
{
    private GoogleMap mMap;
    private String partyname;

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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);

        setupActionbar();
        setupFloatingButtons();
    }

    @Override
    //triggered when map is ready
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        addParty("Super Party1", 34.4133, -119.8610);
        addParty("Boring Party2", 34.4140, -119.8630);
        addParty("Sad Party3", 34.4110, -119.8620);
    }

//--------------------------------------------------------------------------OnViewCreated Sub-tasks

    //actionbar settings
    private void setupActionbar()
    {
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_home);

        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ImageView notification = (ImageView) getActivity().findViewById(R.id.notif_button);
        notification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                drawer.openDrawer(Gravity.RIGHT);
            }
        });
    }

    //setup sos and curloc buttons
    private void setupFloatingButtons()
    {
        ImageView sos_button = (ImageView) getActivity().findViewById(R.id.sos_button);
        sos_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "SOSSSSSSSSSSSSS!!!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView cur_loc_button = (ImageView) getActivity().findViewById(R.id.cur_loc_button);
        cur_loc_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                moveMapCamera(new LatLng(34.4133, -119.8610));
            }
        });
    }

//----------------------------------------------------------------------------------Other Sub-tasks

    // Add a marker to UCSB and move the camera
    public void addParty(String name, double lat, double lng)
    {
        partyname = name;
        LatLng loc = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(150,150))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, (float)15.0));
    }

    //refresh partylist
    public void addParties(GoogleMap googleMap, List<String> parties_addresses)
    {
        for ( String each_address : parties_addresses )
        {
            //refreshParty(googleMap, each_address);
        }
    }

    //move camera with specified loc
    public void moveMapCamera(LatLng loc)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, (float)15.0));
    }

    //resize image icons
    public Bitmap resizeMapIcons(int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.happy_house);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        View view = View.inflate(getContext(),R.layout.map_marker_layout, null );

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        title.setText(partyname);

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View view = View.inflate(getContext(),R.layout.map_marker_layout, null );

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        title.setText(partyname);
        return view;
    }
}
