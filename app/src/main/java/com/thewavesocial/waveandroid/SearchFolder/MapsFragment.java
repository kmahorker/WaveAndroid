package com.thewavesocial.waveandroid.SearchFolder;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnTouchListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{
    private List<Long> partyList;
    private Party curParty;
    private HomeActivity mainActivity;
    private int yDelta;
    private int mapHeight;

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
        Log.d("Size", UtilityClass.getScreenHeight(mainActivity)+"");

        setupFloatingButtons();
        setupMapElements();

        getActivity().findViewById(R.id.home_mapsView_separator).setOnTouchListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                dragSeparator( mapHeight-180, 0 );
                return true;
            }
        });
        final View myMapLayout = getActivity().findViewById(R.id.home_mapsView_relativeLayout);
        myMapLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                myMapLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mapHeight = myMapLayout.getHeight();
                dragSeparator( mapHeight-180, 0 );
            }
        });
    }

    private void setupMapElements()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        cur_loc_marker = null;
    }

    @Override
    //triggered when map is ready
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
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
    public boolean onMarkerClick(Marker marker)
    {
        UtilityClass.hideKeyboard(mainActivity);
        if ( marker.getTag() != null )
        {
            openPartyProfile((long) marker.getTag());
            dragSeparator( 185, 0 );
        }
        moveMapCamera(marker.getPosition());
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        UtilityClass.hideKeyboard(mainActivity);
        dragSeparator( mapHeight-180, 0 );
    }

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
        if ( y < 1157 )
        {
            PartyProfileFragment.updateAttendeeImages();
        }
        if ( y < UtilityClass.getScreenHeight(mainActivity)-180
                && y > UtilityClass.getScreenHeight(mainActivity) - mapHeight + 30
                && !getActivity().findViewById(R.id.home_mapsView_searchbar).isFocused())
        {
            switch (event.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = y - layoutParams1.bottomMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    dragSeparator(y-yDelta, 0);
                    break;
            }
        }
        UtilityClass.hideKeyboard(mainActivity);
        dragSeparator( mapHeight-180, 0 );
        return true;
    }

    private void dragSeparator(int distance, int duration)
    {
        // TODO: 03/09/2017 Think about adding other views inside drag bar
        Log.d("Distance", distance+"");
        View separator = getActivity().findViewById(R.id.home_mapsView_separator);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) separator.getLayoutParams();
        layoutParams.bottomMargin = distance;
        layoutParams.topMargin = -distance;
        separator.setLayoutParams(layoutParams);

        separator.animate().translationY(distance).setDuration(duration);
        getActivity().findViewById(R.id.home_mapsView_relativeLayout).invalidate();
    }

    private void openPartyProfile(long partyID )
    {
        Fragment fragment = new PartyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("partyIDLong", partyID);
        fragment.setArguments(bundle);

        FragmentManager fm = mainActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
    }
}
