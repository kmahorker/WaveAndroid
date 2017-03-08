package com.thewavesocial.waveandroid.EventsFolder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.thewavesocial.waveandroid.HomeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener, View.OnTouchListener
{
    private List<Long> partyList;
    private Party curParty;
    private HomeActivity mainActivity;
    private int yDelta;

    private GoogleMap mMap;
    private LocationManager locManager;
    private Marker cur_loc_marker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.home_maps_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeActivity)getActivity();
        User user = CurrentUser.theUser;
        partyList = user.getAttended();

        setupFloatingButtons();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        cur_loc_marker = null;

        getActivity().findViewById(R.id.home_mapsView_separator).setOnTouchListener(this);

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });
    }

    @Override
    //triggered when map is ready
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        updateUserLoc(1);

        addParties(googleMap, partyList);
    }

    @Override
    public void onDetach()
    {
        try
        {
            UtilityClass.updateMapLocation(mMap.getCameraPosition().target);
        }
        catch (RuntimeException e)
        {
            Log.d("Runtime Exception", "updateMap");
        }
        super.onDetach();
    }

    //----------------------------------------------------------------------------------Party Functions

    // Add a marker to UCSB and move the camera
    public void addParty(long partyID, LatLng loc)
    {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.happy_house ,150, 150))));
        marker.setTag(partyID);
    }

    //add partylist
    public void addParties(GoogleMap googleMap, List<Long> partyIDs)
    {
        for (long party : partyIDs)
        {
            LatLng loc = CurrentUser.getPartyObject(party).getMapAddress().getAddress_latlng();
            if ( loc != null )
                addParty(party, loc );
        }
    }

//------------------------------------------------------------------------------------Map Functions

    //move camera with specified loc
    public void moveMapCamera(LatLng loc)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, (float) 15.0));
    }

    //resize image icons
    public Bitmap resizeMapIcons(int res, int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), res);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent intent = new Intent(mainActivity, PartyProfileActivity.class);
        intent.putExtra("partyIDLong", (long) marker.getTag());
        mainActivity.startActivity(intent);
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        if ( marker.getTag() == null )
        {
            int size = (int)((Math.random()+2)) * 250;
            Log.d("Wow", size + "");
            cur_loc_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.profile_sample, size, size)));
            return null;
        }
        View view = View.inflate(getContext(), R.layout.home_maps_marker, null);
        curParty = CurrentUser.getPartyObject((long) marker.getTag());

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        TextView host = (TextView) view.findViewById(R.id.marker_host);
        TextView loc = (TextView) view.findViewById(R.id.marker_loc);
        TextView time = (TextView) view.findViewById(R.id.marker_time);
        TextView spot = (TextView) view.findViewById(R.id.marker_spot);
        TextView price = (TextView) view.findViewById(R.id.marker_price);

        title.setText(curParty.getName());
        host.setText("Host: " + curParty.getHostName());
        loc.setText("Location: " + curParty.getMapAddress().getAddress_string());
        time.setText("Time: " + UtilityClass.timeToString(curParty.getStartingDateTime()) + " - " +
                UtilityClass.timeToString(curParty.getEndingDateTime()));
        spot.setText("Spot: " + "Not implemented");
        price.setText("   Price: $" + curParty.getPrice());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        if ( marker.getTag() == null )
        {
            int size = (int)((Math.random()*2+1)) * 250;
            Log.d("Wow", size + "");
            cur_loc_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.profile_sample, size, size)));
            return null;
        }
        View view = View.inflate(getContext(), R.layout.home_maps_marker, null);
        curParty = CurrentUser.getPartyObject((long) marker.getTag());

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        TextView host = (TextView) view.findViewById(R.id.marker_host);
        TextView loc = (TextView) view.findViewById(R.id.marker_loc);
        TextView time = (TextView) view.findViewById(R.id.marker_time);
        TextView spot = (TextView) view.findViewById(R.id.marker_spot);
        TextView price = (TextView) view.findViewById(R.id.marker_price);

        title.setText(curParty.getName());
        host.setText("Host: " + curParty.getHostName());
        loc.setText("Location: " + curParty.getMapAddress().getAddress_string());
        time.setText("Time: " + UtilityClass.timeToString(curParty.getStartingDateTime()) + " - " +
                UtilityClass.timeToString(curParty.getEndingDateTime()));
        spot.setText("Spot: " + "Not implemented");
        price.setText("   Price: $" + curParty.getPrice());
        return view;
    }

//--------------------------------------------------------------------------------------GPS Methods

    public void updateUserLoc(int key)
    {
        if ( ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        ,10);
            }
        }
        else
        {
            Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if ( location != null )
            {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                UtilityClass.updateUserLocation(loc);
                if (key == 1)
                {
                    if (UtilityClass.getMapLocation() != null)
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UtilityClass.getMapLocation(), (float) 15.0));
                    } else
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UtilityClass.getUserLocation(), (float) 15.0));
                    }

                    cur_loc_marker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.plug_icon, 150, 150)))
                            .position(UtilityClass.getUserLocation()));
                } else
                {
                    moveMapCamera(loc);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 10:
                updateUserLoc(0);
                break;
            default:
                break;
        }
    }

//-----------------------------------------------------------------------------------Setup Methods

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
                updateUserLoc(0);
            }
        });
    }

    @Override
    //Credit: http://stackoverflow.com/questions/35032514/how-to-hold-and-drag-re-position-a-layout-along-with-its-associated-layouts-in
    public boolean onTouch(View view, MotionEvent event)
    {
        int y = (int) event.getRawY();
        if ( y < 1730 && y > 320 && !getActivity().findViewById(R.id.home_mapsView_searchbar).isFocused())
        {
            switch (event.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = y - layoutParams1.bottomMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    Log.d("1", layoutParams2.topMargin + ", " + layoutParams2.bottomMargin + ", " + y + ", " + yDelta);

                    layoutParams2.bottomMargin = (y - yDelta);
                    Log.d("2", layoutParams2.topMargin + ", " + layoutParams2.bottomMargin + ", " + y + ", " + yDelta);

                    layoutParams2.topMargin = -layoutParams2.bottomMargin;
                    Log.d("3", layoutParams2.topMargin + ", " + layoutParams2.bottomMargin + ", " + y + ", " + yDelta);

                    view.setLayoutParams(layoutParams2);
                    Log.d("4", layoutParams2.topMargin + ", " + layoutParams2.bottomMargin + ", " + y + ", " + yDelta);

                    view.animate().translationY(y - yDelta).setDuration(0);
                    break;
            }
            getActivity().findViewById(R.id.home_mapsView_relativeLayout).invalidate();
        }
        return true;
    }
}
