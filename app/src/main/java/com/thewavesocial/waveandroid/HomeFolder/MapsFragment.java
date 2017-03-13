package com.thewavesocial.waveandroid.HomeFolder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnTouchListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{
    private static HomeSwipeActivity mainActivity;
    private LocationManager locManager;
    private Marker cur_loc_marker;
    private List<Long> partyList;
    private GoogleMap mMap;
    private LatLng loc;

    public static int mapHeight, separatorHeight, searchBarHeight;
    private SearchView searchbar;
    private EditText editText;
    private int yDelta;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.home_maps_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity)getActivity();
        User user = CurrentUser.theUser;
        partyList = user.getAttended();

        setupFloatingButtons();
        setupMapElements();
        setupHeightVariables();
        setupSearchbar();

        getActivity().findViewById(R.id.home_mapsView_separator).setOnTouchListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                //searchbar.setIconified(true);
                //searchbar.clearFocus();
                editText.setCursorVisible(false);
                return true;
            }
        });
    }

    private void setupFloatingButtons()
    {
        ImageView sos_button = (ImageView) getActivity().findViewById(R.id.sos_button);
        sos_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkSOSMessagePermission();
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

    private void setupMapElements()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        cur_loc_marker = null;
    }

    private void setupHeightVariables()
    {
        final View separator = getActivity().findViewById(R.id.home_mapsView_separator);
        separator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                separator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                separatorHeight = separator.getHeight();
            }
        });

        final View searchBar = getActivity().findViewById(R.id.home_mapsView_searchbar);
        searchBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                searchBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                searchBarHeight = searchBar.getHeight();
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
                        dragSeparator( mapHeight/2-(searchBarHeight+separatorHeight), 0 );
                    }
                });
    }

    private void setupSearchbar()
    {


        searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        searchbar.setFocusable(true);
        //searchbar.clearFocus();
        int id = searchbar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        editText = (EditText) searchbar.findViewById(id);
        editText.setCursorVisible(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragSeparator(30 - mapHeight/2, 0);
                openSearchView();
                editText.setCursorVisible(true);
            }
        });


        searchbar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //searchbar.setIconified(false);
                //searchbar.requestFocus();
                dragSeparator(30 - mapHeight/2, 0);
                openSearchView();
                editText.setCursorVisible(true);
            }
        });

        searchbar.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dragSeparator(30 - mapHeight/2, 0);
                openSearchView();
                editText.setCursorVisible(true);
            }
        });
    }

//----------------------------------------------------------------------------------------------Map

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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        updateUserLoc(1);

        addParties(googleMap, partyList);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        UtilityClass.hideKeyboard(mainActivity);
        if ( marker.getTag() != null )
        {
            openPartyProfile((long) marker.getTag());
            //searchbar.setIconified(true);
            //searchbar.clearFocus();
            editText.setCursorVisible(false);
            dragSeparator( 80, 0 );
        }
        moveMapCamera(marker.getPosition());
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        UtilityClass.hideKeyboard(mainActivity);
        editText.setCursorVisible(false);
        //searchbar.setIconified(true);
//        searchbar.clearFocus();
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
            Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ( location != null )
            {
                loc = new LatLng(location.getLatitude(), location.getLongitude());
            }
            else
            {
                loc = new LatLng(34.414899,-119.84312);
            }
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

    public void addParty(long partyID, LatLng loc)
    {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.happy_house ,150, 150))));
        marker.setTag(partyID);
    }

    public void addParties(GoogleMap googleMap, List<Long> partyIDs)
    {
        for (long party : partyIDs)
        {
            LatLng loc = CurrentUser.getPartyObject(party).getMapAddress().getAddress_latlng();
            if ( loc != null )
                addParty(party, loc );
        }
    }

    public void moveMapCamera(LatLng loc)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, (float) 15.0));
    }

    public Bitmap resizeMapIcons(int res, int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), res);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

//-------------------------------------------------------------------------------------------Search

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        //Credit: http://stackoverflow.com/questions/35032514/how-to-hold-and-drag-re-position-a-layout-along-with-its-associated-layouts-in
        int y = (int) event.getRawY();
        editText.setCursorVisible(false);
        //searchbar.setIconified(true);
//        searchbar.clearFocus();
        editText.setCursorVisible(false);
        if ( y < 1157 )
        {
            PartyProfileFragment.updateAttendeeImages();
        }
        if ( y < UtilityClass.getScreenHeight(mainActivity)-(searchBarHeight+separatorHeight)
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
        return true;
    }

    public static void dragSeparator(int distance, int duration)
    {
        // TODO: 03/09/2017 Think about adding other views inside drag bar
        Log.d("Distance", distance+"");
        View separator = mainActivity.findViewById(R.id.home_mapsView_separator);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) separator.getLayoutParams();
        layoutParams.bottomMargin = distance;
        layoutParams.topMargin = -distance;
        separator.setLayoutParams(layoutParams);

        separator.animate().translationY(distance).setDuration(duration);
        mainActivity.findViewById(R.id.home_mapsView_relativeLayout).invalidate();
    }

    public static void openSearchView()
    {
        Fragment fragment = new SearchFragment();

        FragmentManager fm = mainActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
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

//----------------------------------------------------------------------------------------------SOS

    private void checkSOSMessagePermission()
    {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.SEND_SMS))
            {
                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.SEND_SMS}, 20);
            }
            else
            {
                sendSOSMessage();
            }
        }
        else
        {
            sendSOSMessage();
        }
    }

    private void sendSOSMessage()
    {
        String phone = "6692254467";
        String name = "Wei Tung Chen";
        SmsManager sManager = SmsManager.getDefault();
        sManager.sendTextMessage(
                phone, null,
                "Help me, " + name.substring(0, name.lastIndexOf(' '))
                        + "!\n\nI'm drunk. LOL.. My last known location is ("
                        + loc.latitude + ", " + loc.longitude
                        + ")\n\n-Sent from ThePlugSocial Emergency Alert.",
                null, null);
        Toast.makeText(mainActivity, "Message Sent!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 10:
                updateUserLoc(0);
                break;
            case 20:
                sendSOSMessage();
            default:
                break;
        }
    }
}
