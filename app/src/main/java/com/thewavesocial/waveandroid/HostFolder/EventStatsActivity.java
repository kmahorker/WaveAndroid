package com.thewavesocial.waveandroid.HostFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.Attendee;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventStatsActivity extends AppCompatActivity implements OnMapReadyCallback{
    public static final int activityHostFragment = 1, activitySocialFragment = 2;
    private GoogleMap mMap;
    private LatLng latlng;
    private Party party;
    private TextView goingView, genderView, hostView, locView, dateView, timeView, deleteView, editView;
    private ImageView qrCodeView;
    private RecyclerView attendingFriends;
    private String host, loc, date, time;
    private int going, male, female, callerType;
    private EventStatsActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats);
        mainActivity = this;
        Intent intent = getIntent();
        party = intent.getExtras().getParcelable("partyObject");

        setupActionbar();
        setupReferences();
        setupPartyInfos();
        setupFunctionalities();
        setupMapElements();
        setupDeleteEditButtons(callerType);
    }


    private void setupDeleteEditButtons(int callerType) {
        if ( callerType == activityHostFragment ) {
            editView.setVisibility(View.VISIBLE);
            deleteView.setVisibility(View.VISIBLE);
        } else if ( callerType == activitySocialFragment ){
            editView.setVisibility(View.INVISIBLE);
            deleteView.setVisibility(View.INVISIBLE);
        }
    }


    private void setupMapElements() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.hostEventStats_maps_fragment);
        mapFragment.getMapAsync(this);
    }


    private void setupPartyInfos() {
        loc = party.getMapAddress().getAddress_string();
        date = UtilityClass.dateToString(party.getStartingDateTime()) + " - " +
                UtilityClass.dateToString(party.getEndingDateTime());
        time = UtilityClass.timeToString(party.getStartingDateTime()) + " - " +
                UtilityClass.timeToString(party.getEndingDateTime());

        going = party.getAttendingUsers().size();
        male = party.getAttendingUsers().size()*3/4;
        female = party.getAttendingUsers().size()/4;
    }


    private void setupReferences() {
        goingView = (TextView) findViewById(R.id.hostEventStats_totalGoing_count);
        genderView = (TextView) findViewById(R.id.hostEventStats_femaleMale_count);
        hostView = (TextView) findViewById(R.id.hostEventStats_hostname);
        locView = (TextView) findViewById(R.id.hostEventStats_locname);
        dateView = (TextView) findViewById(R.id.hostEventStats_datename);
        timeView = (TextView) findViewById(R.id.hostEventStats_timename);
        qrCodeView = (ImageView) findViewById(R.id.hostEventStats_qrcode);
        attendingFriends = (RecyclerView) findViewById(R.id.hostEventStats_attendeelist);
        deleteView = (TextView) findViewById(R.id.hostEventStats_delete_button);
    }

    private void setupFunctionalities() {
        goingView.setText(going+"");
        genderView.setText(female+"/"+male);
        hostView.setText(host+"");
        locView.setText(loc+"");
        dateView.setText(date+"");
        timeView.setText(time+"");
        qrCodeView.setImageDrawable(getDrawable(R.drawable.sample_qrcode));

        final List<User> sample = new ArrayList();

        CurrentUser.server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                if ( result != null ) {
                    hostView.setText(result.get("hosting").get(0).getFullName());

                    sample.addAll(result.get("attending"));
                    LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
                    attendingFriends.setLayoutManager(layoutManagerAttendees);
                    attendingFriends.setFocusable(false);
                    attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, sample));
                }
            }
        });
        List<String> userIds = new ArrayList<>();
        for(Attendee a : party.getAttendingUsers()){
            userIds.add(a.getUserId());
        }

        CurrentUser.server_getUsersListObjects(userIds, new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(List<User> result) {
                if ( result != null ) {
                    LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
                    attendingFriends.setLayoutManager(layoutManagerAttendees);
                    attendingFriends.setFocusable(false);
                    attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, sample));
                }
            }
        });

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertMessage = new AlertDialog.Builder(mainActivity);
                alertMessage.setTitle("Warning")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mainActivity, "Todo: Delete this party from all attendees.", Toast.LENGTH_LONG).show();
                        }})
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .setCancelable(true)
                    .show();
            }
        });
    }


    private void setupActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_hoststats);

        TextView partynameView = (TextView) findViewById(R.id.actionbar_hoststats_partyname);
        editView = (TextView) findViewById(R.id.actionbar_hoststats_editparty);
        ImageView backButton = (ImageView) findViewById(R.id.actionbar_hoststats_backbutton);

        partynameView.setText(party.getName());
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, EditStatsActivity.class);
                intent.putExtra("partyObject", party);//TODO: XXX 4/25/17 Pass party object/id to next screen
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latlng = party.getMapAddress().getAddress_latlng();
        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng));
        marker.setTag(party.getPartyID());
        if ( party.getPartyEmoji() != "" )
            marker.setTitle(party.getPartyEmoji()); //TODO: 4/25/17 Make this the actual custom pin
                    //setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(CurrentUser.theUser.getProfilePic(), 150, 150)));
        //else
        //    marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(party.getPartyEmoji(), 150, 150)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, (float) 15.0));
    }


    public Bitmap resizeMapIcons(BitmapDrawable pic, int width, int height) {
        Bitmap imageBitmap = pic.getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}