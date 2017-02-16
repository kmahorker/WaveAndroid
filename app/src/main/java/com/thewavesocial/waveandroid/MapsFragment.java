package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.List;

import static android.R.attr.name;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener
{
    private GoogleMap mMap;
    private List<Long> partyList;
    private Party curParty;
    private Activity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.home_maps_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = getActivity();
        User user = CurrentUser.theUser;
        partyList = user.getAttended();

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
        mMap.setOnInfoWindowClickListener(this);
        addParties(googleMap, partyList);
    }

//----------------------------------------------------------------------------------Other Sub-tasks

    // Add a marker to UCSB and move the camera
    public void addParty(long partyID, double lat, double lng)
    {
        LatLng loc = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(150,150))));
        marker.setTag(partyID);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, (float)15.0));
    }

    //add partylist
    public void addParties(GoogleMap googleMap, List<Long> partyIDs)
    {
        double lat = 34.4133;
        double lng = -119.8610;
        for ( long party : partyIDs )
        {
            addParty( party, lat, lng );
            lat += 0.001;
            lng += 0.001;
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
        curParty = CurrentUser.getPartyObject((long)marker.getTag());

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        TextView host = (TextView) view.findViewById(R.id.marker_host);
        TextView loc = (TextView) view.findViewById(R.id.marker_loc);
        TextView time = (TextView) view.findViewById(R.id.marker_time);
        TextView spot = (TextView) view.findViewById(R.id.marker_spot);
        TextView price = (TextView) view.findViewById(R.id.marker_price);

        title.setText( curParty.getName());
        host.setText("Host: " + curParty.getHostName());
        loc.setText("Location: " + curParty.getAddress());
        time.setText("Time: " + UtilityClass.timeToString(curParty.getStartingDateTime()) + " - " +
                            UtilityClass.timeToString(curParty.getEndingDateTime()));
        spot.setText("Spot: " + "Not implemented");
        price.setText("   Price: $" + curParty.getPrice());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View view = View.inflate(getContext(),R.layout.map_marker_layout, null );
        curParty = CurrentUser.getPartyObject((long)marker.getTag());

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        TextView host = (TextView) view.findViewById(R.id.marker_host);
        TextView loc = (TextView) view.findViewById(R.id.marker_loc);
        TextView time = (TextView) view.findViewById(R.id.marker_time);
        TextView spot = (TextView) view.findViewById(R.id.marker_spot);
        TextView price = (TextView) view.findViewById(R.id.marker_price);

        title.setText( curParty.getName());
        host.setText("Host: " + curParty.getHostName());
        loc.setText("Location: " + curParty.getAddress());
        time.setText("Time: " + UtilityClass.timeToString(curParty.getStartingDateTime()) + " - " +
                UtilityClass.timeToString(curParty.getEndingDateTime()));
        spot.setText("Spot: " + "Not implemented");
        price.setText("   Price: $" + curParty.getPrice());
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent intent = new Intent(mainActivity, PartyProfileActivity.class);
        intent.putExtra("partyIDLong", (long) marker.getTag());
        mainActivity.startActivity(intent);
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
}
