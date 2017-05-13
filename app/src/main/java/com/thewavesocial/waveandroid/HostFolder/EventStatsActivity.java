package com.thewavesocial.waveandroid.HostFolder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import me.sudar.zxingorient.ZxingOrient;

public class EventStatsActivity extends AppCompatActivity implements OnMapReadyCallback{
    public static final int activityHostFragment = 1, activitySocialFragment = 2;
    private static final int CAMERA_PERMISSION = 3;
    private GoogleMap mMap;
    private LatLng latlng;
    private Party party;
    private TextView goingView, genderView, hostView, locView, dateView, timeView, qrAction, editView, bounceView, attendingView;
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
        setupPartyInfos();
        setupReferences();
        setupFunctionalities();
        setupActionbar();
        setupQRAndEditButtons(callerType);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupMapElements();
            }
        }, 500);
    }


    private void setupQRAndEditButtons(int callerType) {
        if ( callerType == activityHostFragment ) {
            editView.setVisibility(View.VISIBLE);
            qrAction.setVisibility(View.VISIBLE);
        } else if ( callerType == activitySocialFragment ){
            editView.setVisibility(View.INVISIBLE);
            qrAction.setVisibility(View.INVISIBLE);
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
        qrAction = (TextView) findViewById(R.id.hostEventStats_qr_button);
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
            attendingView.setText("INVITED (" + party.getAttendingUsers().size() + ")");
            attendingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Intent Start Activty Put Extra party
                }
            });
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
            bounceView.setText("BOUNCERS (" + party.getBouncingUsers().size() + ")");
            bounceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: intent start edit bouncers put extra party
                }
            });
            LinearLayoutManager layoutManagerBouncers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            bouncingFriends.setLayoutManager(layoutManagerBouncers);
            bouncingFriends.setFocusable(false);
            bouncingFriends.setAdapter(new PartyAttendeesCustomAdapter(this, CurrentUser.getUsersListObjects(party.getBouncingUsers())));
        }


        qrAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)) {
                    Log.d("Camera", "Deny");
                    ActivityCompat.requestPermissions(mainActivity,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION);
                    return;
                }
                openScanner();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, (float) 15.0));

        EmojiconTextView emojiText = (EmojiconTextView) mainActivity.findViewById(R.id.hostEventStats_emoji);
        emojiText.setText(party.getPartyEmoji().substring(0,1));
        emojiText.buildDrawingCache();

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromBitmap(emojiText.getDrawingCache())));
        marker.setTag(party.getPartyID());
    }


    private void openScanner() {
        ZxingOrient zxingOrient = new ZxingOrient(this);
        zxingOrient.setInfo("Scan QR Code");
        zxingOrient.setToolbarColor("black");//getString(R.string.appColorHexString));
        zxingOrient.setInfoBoxColor("black");//getString(R.string.appColorHexString));
        zxingOrient.setIcon(R.drawable.plug_icon);
        zxingOrient.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openScanner();
        }
    }
}