package com.thewavesocial.waveandroid.HostFolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class EventStatsActivity extends AppCompatActivity implements OnMapReadyCallback{
    public static final int activityHostFragment = 1, activitySocialFragment = 2;
    private GoogleMap mMap;
    private LatLng latlng;
    private Party party;
    private TextView goingView, genderView, hostView, locView, dateView, timeView, deleteView, editView, bounceView, attendingView;
    private ImageView qrCodeView;
    private RecyclerView attendingFriends, bouncingFriends;
    private String host, loc, date, time;
    private int going, male, female, callerType;
    private EventStatsActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats);
        mainActivity = this;
        Intent intent = getIntent();
        party = getIntent().getExtras().getParcelable("partyObject");
        callerType = intent.getExtras().getInt("callerActivity");

        setupMapElements();
        setupPartyInfos();
        setupReferences();
        setupFunctionalities();
        setupActionbar();
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
        host = party.getHostName();
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
        attendingView = (TextView) findViewById(R.id.hostEventStats_attendeetext);
        attendingFriends = (RecyclerView) findViewById(R.id.hostEventStats_attendeelist);
        deleteView = (TextView) findViewById(R.id.hostEventStats_delete_button);
        bounceView = (TextView) findViewById(R.id.hostEventStats_bouncertext);
        bouncingFriends = (RecyclerView) findViewById(R.id.hostEventStats_bouncerlist);
    }


    private void setupFunctionalities() {
        goingView.setText(going+"");
        genderView.setText(female+"/"+male);
        hostView.setText(host+"");
        locView.setText(loc+"");
        dateView.setText(date+"");
        timeView.setText(time+"");
        qrCodeView.setImageBitmap(getQRCode("WOWOW"));

        if ( callerType == activityHostFragment ) {
            attendingView.setText("Invited (" + party.getAttendingUsers().size() + ")");
            LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            attendingFriends.setLayoutManager(layoutManagerAttendees);
            attendingFriends.setFocusable(false);
            attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(this, CurrentUser.getUsersListObjects(party.getAttendingUsers())));
        } else {
            attendingView.setText("FRIENDS GOING (" + party.getAttendingUsers().size() + ")");
            LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            attendingFriends.setLayoutManager(layoutManagerAttendees);
            attendingFriends.setFocusable(false);
            attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(this, CurrentUser.getUsersListObjects(party.getAttendingUsers())));
        }

        if ( callerType == activityHostFragment ) {
            bounceView.setText("Bouncers (" + party.getHostingUsers().size() + ")");
            LinearLayoutManager layoutManagerBouncers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            bouncingFriends.setLayoutManager(layoutManagerBouncers);
            bouncingFriends.setFocusable(false);
            bouncingFriends.setAdapter(new PartyAttendeesCustomAdapter(this, CurrentUser.getUsersListObjects(party.getBouncingUsers())));
        }


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
                            // TODO: 04/20/2017 Remove party from server
                            // TODO: 04/20/2017 Notify all users
                            // TODO: 04/20/2017 Back to hostFragment
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

    //        http://stackoverflow.com/a/25283174
    private Bitmap getQRCode(String qrInput) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrInput, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {e.printStackTrace();}
        return null;
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