package com.thewavesocial.waveandroid.HomeFolder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_getEventsInDistance;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static HomeSwipeActivity mainActivity;
    private LocationManager locManager;
    private GoogleMap mMap;
    private LatLng loc;

    public static int mapHeight, separatorHeight, searchBarHeight;
    public static boolean searchOpened = false;
    private SearchView searchbar;
    private EditText editText;
    private SlidingUpPanelLayout sliding_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_maps_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();
        User user = CurrentUser.theUser;

        setupFloatingButtons();
        setupMapElements();
        setupHeightVariables();
        setupSearchbar();
        sliding_layout = (SlidingUpPanelLayout) mainActivity.findViewById(R.id.main_sliding_layout);
    }


    private void setupFloatingButtons() {
        final ImageView sos_button = (ImageView) getActivity().findViewById(R.id.sos_button);

        final Handler handle = new Handler();
        sos_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sos_button.setAlpha(155);
                        handle.postDelayed(run, 0000);
                        break;
                    case MotionEvent.ACTION_UP:
                        sos_button.setAlpha(255);
                        handle.removeCallbacks(run);
                        break;
                }
                return true;
            }

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    checkSOSMessagePermission();
                }
            };
        });

        ImageButton cur_loc_button = (ImageButton) getActivity().findViewById(R.id.cur_loc_button);
        cur_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                loc = new LatLng(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                        locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
                moveMapCamera(loc);
            }
        });
    }


    private void setupMapElements() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }


    private void setupHeightVariables() {
        final View separator = getActivity().findViewById(R.id.home_mapsView_separator);
        separator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                separator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                separatorHeight = separator.getHeight();

                final View searchBar = getActivity().findViewById(R.id.home_mapsView_searchbar);
                searchBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        searchBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        searchBarHeight = searchBar.getHeight();
                        sliding_layout.setPanelHeight(separatorHeight + searchBarHeight + 50);
                        sliding_layout.setAnchorPoint((float)0.5);
                        sliding_layout.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                UtilityClass.hideKeyboard(mainActivity);
                                return false;
                            }
                        });
                    }
                });
            }
        });

        final View myMapLayout = getActivity().findViewById(R.id.home_mapsView_relativeLayout);
        myMapLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        myMapLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mapHeight = myMapLayout.getHeight();
                    }
                });
    }


    private void setupSearchbar() {
        searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        searchbar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        int id = searchbar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        editText = (EditText) searchbar.findViewById(id);
        editText.setCursorVisible(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                return false;
            }
        });

        //enable user to return to map using back button
        final Activity main_activity = getActivity();
        if(main_activity instanceof HomeSwipeActivity){
            ((HomeSwipeActivity) main_activity).setOnBackPressedListener(new Runnable() {
                @Override
                public void run() {
                    if(sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                            sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else if(sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        ((HomeSwipeActivity) main_activity).setOnBackPressedListener(null);
                    }
                }
            });
        }
    }

//----------------------------------------------------------------------------------------------Map

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        Location lastLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if ( lastLoc != null ) {
            loc = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, (float) 15.0));
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                final Button search_button = (Button) mainActivity.findViewById(R.id.redo_search_button);
                search_button.setVisibility(View.VISIBLE);
                search_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_button.setVisibility(View.INVISIBLE);
                        mMap.clear();
                        displayEventsInRange();
                    }
                });
            }
        });
    }

    private void displayEventsInRange() {
        double nBound, sBound, eBound, wBound;
        nBound = mMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude;
        eBound = mMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude;
        sBound = mMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude;
        wBound = mMap.getProjection().getVisibleRegion().latLngBounds.southwest.longitude;
        server_getEventsInDistance(sBound + "", nBound + "", wBound + "", eBound + "",
                new OnResultReadyListener<ArrayList<Party>>() {
                    @Override
                    public void onResultReady(ArrayList<Party> result) {
                        if (result != null)
                            addParties(result);
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        UtilityClass.hideKeyboard(mainActivity);
        if (marker.getTag() != null) {
            openPartyProfile((Party) marker.getTag());
            editText.setCursorVisible(false);
            searchOpened = false;
        }
        moveMapCamera(new LatLng(marker.getPosition().latitude - 0.003, marker.getPosition().longitude));
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        UtilityClass.hideKeyboard(mainActivity);
        editText.setCursorVisible(false);
    }


    public void addParty(Party party, LatLng loc) {
        EmojiconTextView emojiText = (EmojiconTextView) mainActivity.findViewById(R.id.home_mapsView_emoji);
        emojiText.setText(party.getPartyEmoji().substring(0, 1));
        emojiText.buildDrawingCache();

        Marker marker = mMap.addMarker(new MarkerOptions().position(loc));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(emojiText.getDrawingCache()));
        marker.setTag(party);
    }


    public void addParties(List<Party> parties) {
        for (Party party : parties) {
            LatLng loc = party.getMapAddress().getAddress_latlng();
            if (loc != null)
                addParty(party, loc);
        }
    }


    public void moveMapCamera(LatLng loc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, (float) 15.0));
    }

//-------------------------------------------------------------------------------------------Search

    public static void openSearchView() {
        Fragment fragment = new SearchFragment();

        FragmentManager fm = mainActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
    }


    private void openPartyProfile(Party party) {
        Fragment fragment = new PartyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("partyObject", party);
        fragment.setArguments(bundle);

        FragmentManager fm = mainActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
    }

//----------------------------------------------------------------------------------------------SOS

    private void checkSOSMessagePermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.SEND_SMS}, 20);
            }
            return;
        }
        askToSendSOSMessage();
    }


    private void askToSendSOSMessage() {
        if (CurrentUser.theUser.getBestFriends().isEmpty()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
            dialog.setTitle("Error")
                    .setMessage("No best friend contact specified")
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainActivity.mPager.setCurrentItem(2);
                            mainActivity.findViewById(R.id.actionbar_user_setting).performClick();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            AlertDialog.Builder fieldAlert = new AlertDialog.Builder(mainActivity);
            fieldAlert.setTitle("Send an alert to " +
                    (CurrentUser.theUser.getBestFriends().get(0)).getName())
                    .setMessage("A text will be sent to your friend notifying your current location.")
                    .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendSOSMessage(CurrentUser.theUser.getBestFriends().get(0).getName(),
                                    CurrentUser.theUser.getBestFriends().get(0).getPhoneNumber());
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }


    private void sendSOSMessage(String name, String phone) {
        String message;
        SmsManager sManager = SmsManager.getDefault();
        try {
            message = "Help me, " + name.substring(0, name.lastIndexOf(' '))
                    + "!\n\nI'm drunk. LOL.. My last known location is ("
                    + loc.latitude + ", " + loc.longitude
                    + ")\n\n-Sent from ThePlugSocial Emergency Alert.";
        } catch (Exception e) {
            message = "Help me, " + name.substring(0, name.lastIndexOf(' '))
                    + "!\n\nI'm drunk. LOL.. My last known location is ("
                    + "location Unknown"
                    + ")\n\n-Sent from ThePlugSocial Emergency Alert.";
        }
        sManager.sendTextMessage(
                phone, null,
                message,
                null, null);
        Toast.makeText(mainActivity, "Message Sent.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                Location lastLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if ( lastLoc != null ) {
                    loc = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
                    moveMapCamera(loc);
                }
                break;
            case 20:
                askToSendSOSMessage();
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilityClass.hideKeyboard(mainActivity);
    }

}
