package com.thewavesocial.waveandroid.HomeFolder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.Utility;
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
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_getEventsInDistance;

public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnTouchListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static HomeSwipeActivity mainActivity;
    private LocationManager locManager;
    private Marker cur_loc_marker;
    private List<String> partyList;
    private GoogleMap mMap;
    private LatLng loc;

    public static int mapHeight, separatorHeight, searchBarHeight;
    public static boolean searchOpened = false;
    private SearchView searchbar;
    private EditText editText;
    private int yDelta;


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

        getActivity().findViewById(R.id.home_mapsView_separator).setOnTouchListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UtilityClass.hideKeyboard(mainActivity);
                editText.setCursorVisible(false);
                return true;
            }
        });
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
                updateUserLoc(0);
            }
        });
    }


    private void setupMapElements() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        cur_loc_marker = null;
    }


    private void setupHeightVariables() {
        final View separator = getActivity().findViewById(R.id.home_mapsView_separator);
        separator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                separator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                separatorHeight = separator.getHeight();
            }
        });

        final View searchBar = getActivity().findViewById(R.id.home_mapsView_searchbar);
        searchBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                searchBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                searchBarHeight = searchBar.getHeight();
            }
        });

        final View myMapLayout = getActivity().findViewById(R.id.home_mapsView_relativeLayout);
        myMapLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        myMapLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mapHeight = myMapLayout.getHeight();
                        dragSeparator(mapHeight / 2 - (searchBarHeight + separatorHeight) - 20, 0);
                    }
                });
    }


    private void setupSearchbar() {
        searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dragSeparator(separatorHeight / 2 - mapHeight / 2, 0);
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
            }
        });
        searchbar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dragSeparator(separatorHeight / 2 - mapHeight / 2, 0);
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
            }
        });

        int id = searchbar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        editText = (EditText) searchbar.findViewById(id);
        editText.setCursorVisible(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dragSeparator(separatorHeight / 2 - mapHeight / 2, 0);
                if (!searchOpened)
                    openSearchView();
                editText.setCursorVisible(true);
                return false;
            }
        });
    }

//----------------------------------------------------------------------------------------------Map

    @Override
    public void onDetach() {
        try {
            UtilityClass.updateMapLocation(mMap.getCameraPosition().target);
        } catch (RuntimeException e) {
            Log.d("Runtime Exception", "updateMap");
        }
        super.onDetach();
    }

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
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        updateUserLoc(1);

        LatLng loc = UtilityClass.getUserLocation();
        if ( loc != null ) {
            server_getEventsInDistance(loc.latitude - 0.02 + "", loc.latitude + 0.02 + "",
                    loc.longitude - 0.02 + "", loc.longitude + 0.02 + "",
                    new OnResultReadyListener<ArrayList<Party>>() {
                        @Override
                        public void onResultReady(ArrayList<Party> result) {
                            if (result != null)
                                addParties(result);
                        }
                    });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        UtilityClass.hideKeyboard(mainActivity);
        if (marker.getTag() != null) {
            openPartyProfile( (Party)marker.getTag());
            editText.setCursorVisible(false);
            dragSeparator(80, 0);
            searchOpened = false;
        }
        moveMapCamera(marker.getPosition());
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        UtilityClass.hideKeyboard(mainActivity);
        editText.setCursorVisible(false);
        //searchbar.setIconified(true);
//        searchbar.clearFocus();
    }


    public void updateUserLoc(int key) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        , 10);
            }
        } else {
            Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                loc = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                loc = new LatLng(34.414899, -119.84312);
            }
            UtilityClass.updateUserLocation(loc);
            if (key == 1) {
                if (UtilityClass.getMapLocation() != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UtilityClass.getMapLocation(), (float) 15.0));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UtilityClass.getUserLocation(), (float) 15.0));
                }
//
//                cur_loc_marker = mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.plug_icon, 150, 150)))
//                        .position(UtilityClass.getUserLocation()));
            } else {
                moveMapCamera(loc);
            }
        }
    }


    public void addParty(Party party, LatLng loc) {
        EmojiconTextView emojiText = (EmojiconTextView) mainActivity.findViewById(R.id.home_mapsView_emoji);
        emojiText.setText(party.getPartyEmoji().substring(0,1));
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


    public Bitmap resizeMapIcons(int res, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), res);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

//-------------------------------------------------------------------------------------------Search

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //Credit: http://stackoverflow.com/questions/35032514/how-to-hold-and-drag-re-position-a-layout-along-with-its-associated-layouts-in
        int y = (int) event.getRawY();
        editText.setCursorVisible(false);
        if (y < 1157) {
            PartyProfileFragment.updateAttendeeImages();
        }
        if (y < UtilityClass.getScreenHeight(mainActivity) - mapHeight + separatorHeight) {
            y = UtilityClass.getScreenHeight(mainActivity) - mapHeight + separatorHeight;
        }
        Log.d("TRUE?", y + ", " + separatorHeight
                + ", " + (UtilityClass.getScreenHeight(mainActivity) - mapHeight + separatorHeight));
        if (y < UtilityClass.getScreenHeight(mainActivity) - (searchBarHeight + separatorHeight + 10)
                && y >= UtilityClass.getScreenHeight(mainActivity) - mapHeight + separatorHeight
                && !getActivity().findViewById(R.id.home_mapsView_searchbar).isFocused()) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = y - layoutParams1.bottomMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    dragSeparator(y - yDelta, 0);
                    break;
            }
        }
        UtilityClass.hideKeyboard(mainActivity);
        return true;
    }


    public static void dragSeparator(int distance, int duration) {
        Log.d("Distance", distance + "");
        View separator = mainActivity.findViewById(R.id.home_mapsView_separator);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) separator.getLayoutParams();
        layoutParams.bottomMargin = distance;
        layoutParams.topMargin = -distance;
        separator.setLayoutParams(layoutParams);

        separator.animate().translationY(distance).setDuration(duration);
        mainActivity.findViewById(R.id.home_mapsView_relativeLayout).invalidate();
    }


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
        if ( CurrentUser.theUser.getBestFriends().isEmpty() ) {
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
                            sendSOSMessage();
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


    private void sendSOSMessage() {
        String phone = "6692254467";
        String name = "Wei Tung Chen";
        String message = "";
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
        Toast.makeText(mainActivity, "Message Sent!!!!!!", Toast.LENGTH_LONG).show();
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
                updateUserLoc(0);
                break;
            case 20:
                askToSendSOSMessage();
            default:
                break;
        }
    }

    private String parseJSONFromServer(String server_url) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        InputStream stream = null;
        String error = "";
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(server_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if ( connection.getResponseCode() == 500 )
                stream = connection.getErrorStream();
            else
                stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            String line ="";

            while( (line = reader.readLine()) != null ) {
                buffer.append(line);
            }
            return buffer.toString();

        } catch (IOException e) {}
        finally {
            if ( connection != null ) {
                connection.disconnect();
            }
            try {
                if ( reader != null ){
                    reader.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return error + server_url;
    }


    @Override
    public void onResume() {
        super.onResume();
        UtilityClass.hideKeyboard(mainActivity);
    }

}
