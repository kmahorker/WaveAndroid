package com.thewavesocial.waveandroid.HostFolder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

public class EventStatsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int activityHostFragment = 1, activitySocialFragment = 2, listInvited = 3, listGoing = 4, listBouncing = 5;
    private static final int CAMERA_PERMISSION = 3;
    private GoogleMap mMap;
    private LatLng latlng;
    private Party party;
    private List<User> goingList;
    private TextView attendingView, genderView, hostView, locView, dateView, timeView, editView, qrAction, bounceView, invitedView, goingView;

    private RecyclerView invitedFriends, bouncingFriends, goingFriends;
    private String loc, date, time;
    private int attending, male, female, callerType;
    private EventStatsActivity mainActivity;

    private final int EDIT_STATS_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats);
        mainActivity = this;

        Intent intent = getIntent();
        party = intent.getExtras().getParcelable("partyObject");
        callerType = intent.getExtras().getInt("callerActivity");
        setupReferences();

        loadActivity();
    }

    private void loadActivity() {
        setupActionbar();
        setupPartyInfos();
        setupFunctionalities();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupMapElements();
            }
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_STATS_REQUEST) {
            if (resultCode == RESULT_OK) {
                server_getPartyObject(party.getPartyID(), new OnResultReadyListener<Party>() {
                    @Override
                    public void onResultReady(Party result) {
                        if (result != null) {
                            party = result;
                            //mainActivity.recreate();
                            loadActivity();
                            //setupPartyInfos();
                            Log.d("editedParty", result + "");
                        }
                    }
                });
            } else {
                //Do Nothing
            }
        }
        ZxingOrientResult qrResult = ZxingOrient.parseActivityResult(requestCode, resultCode, data);
        if (qrResult != null) {
            if (qrResult.getContents() != null) {
                try {
                    int coef = Integer.parseInt(qrResult.getContents().substring(0, qrResult.getContents().indexOf('.')));
                    long rawID = Long.parseLong(qrResult.getContents().substring(qrResult.getContents().indexOf('.') + 1));
                    long userID = rawID/coef;
                    processUserID(userID+"");
                } catch (Exception e) {
                    Toast.makeText(mainActivity, "Error with QR code", Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(this, "No Content", Toast.LENGTH_LONG).show();
        }
    }

    private void processUserID(final String userID) {
        boolean found = false;
        for ( int i = 0; i < goingList.size() && !found; i++ ) {
            final User each = goingList.get(i);
            if ( each.getUserID().equals(userID) ) {
                found = true;
                displayUserProfile(each);
            }
        }
        if ( !found )
            Log.d("Error", "User Not Found");
    }

    private void displayUserProfile(final User each) {
        DatabaseAccess.server_getProfilePicture(each.getUserID(), new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap result) {
                View view = LayoutInflater.from(mainActivity).inflate(R.layout.qr_scan_view, null);
                ((ImageView) view.findViewById(R.id.qr_scan_image_view)).setImageDrawable(UtilityClass.toRoundImage(getResources(), result));
                ((TextView) view.findViewById(R.id.qr_scan_name)).setText("Name: " + each.getFull_name());
                ((TextView) view.findViewById(R.id.qr_scan_gender)).setText("Gender: " + each.getGender());

                AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
                dialog.setTitle("Scan")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                acceptUserToParty(each);
                            }
                        })
                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setView(view);
                dialog.show();
            }
        });
    }

    private void acceptUserToParty(final User each) {
        DatabaseAccess.server_manageUserForParty(each.getUserID(), party.getPartyID(), "attending", "POST", new OnResultReadyListener<String>() {
            @Override
            public void onResultReady(String result) {
                if ( result.equals("success") ) {
                    goingList.remove(each);
                    if ( each.getGender().toLowerCase().equals("male") )
                        male++;
                    else if ( each.getGender().toLowerCase().equals("female") )
                        female++;
                    attending++;
                    attendingView.setText(attending + "");
                    genderView.setText(female + "/" + male);
                    goingView.setText("FRIENDS GOING (" + goingList.size() + ")");
                    goingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, goingList));
                } else
                    Toast.makeText(mainActivity, "Failure.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSpecialFields(int callerType, String hostID) {
        if (callerType == activityHostFragment) {
            if (hostID.equals(DatabaseAccess.getTokenFromLocal(mainActivity).get("id")))
                editView.setVisibility(View.VISIBLE);
            qrAction.setText("Open QR Scanner");
            qrAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Camera", "Deny");
                        ActivityCompat.requestPermissions(mainActivity,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION);
                        return;
                    }
                    openScanner();
                }
            });
        } else if (callerType == activitySocialFragment) {
            editView.setVisibility(View.INVISIBLE);
            qrAction.setText("Open QR Code");
            qrAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = LayoutInflater.from(mainActivity).inflate(R.layout.qr_code_view, null);
                    int coef = (int) (Math.random()*9) + 1;
                    long id = Long.parseLong(CurrentUser.theUser.getUserID());
                    ((ImageView) view.findViewById(R.id.qr_code_image_view)).setImageBitmap(getQRCode(coef + "." + (id*coef)));

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
                    dialog.setTitle("QR Code")
                            .setPositiveButton("Close", null)
                            .setView(view)
                            .show();
                }
            });
        }
    }


    private void setupMapElements() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.hostEventStats_maps_fragment);
        mapFragment.getMapAsync(this);
    }


    private void setupPartyInfos() {
        loc = party.getMapAddress().getAddress_string();
        date = UtilityClass.dateToString(UtilityClass.epochToCalendar(party.getStartingDateTime())) + " - " +
                UtilityClass.dateToString(UtilityClass.epochToCalendar(party.getEndingDateTime()));
        time = UtilityClass.timeToString(UtilityClass.epochToCalendar(party.getStartingDateTime())) + " - " +
                UtilityClass.timeToString(UtilityClass.epochToCalendar(party.getEndingDateTime()));
    }


    private void setupReferences() {
        attendingView = (TextView) findViewById(R.id.hostEventStats_totalGoing_count);
        genderView = (TextView) findViewById(R.id.hostEventStats_femaleMale_count);
        hostView = (TextView) findViewById(R.id.hostEventStats_hostname);
        locView = (TextView) findViewById(R.id.hostEventStats_locname);
        dateView = (TextView) findViewById(R.id.hostEventStats_datename);
        timeView = (TextView) findViewById(R.id.hostEventStats_timename);
        qrAction = (TextView) findViewById(R.id.hostEventStats_qr_button);
        invitedView = (TextView) findViewById(R.id.hostEventStats_invitedtext);
        invitedFriends = (RecyclerView) findViewById(R.id.hostEventStats_invitedlist);
        goingView = (TextView) findViewById(R.id.hostEventStats_goingtext);
        goingFriends = (RecyclerView) findViewById(R.id.hostEventStats_goinglist);
        bounceView = (TextView) findViewById(R.id.hostEventStats_bouncertext);
        bouncingFriends = (RecyclerView) findViewById(R.id.hostEventStats_bouncerlist);
    }


    private void setupFunctionalities() {
        locView.setText(loc + "");
        dateView.setText(date + "");
        timeView.setText(time + "");

        server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(final HashMap<String, ArrayList<User>> result) {
                if (result != null) {
                    String hostname = "", hostID = "";
                    if (!result.get("hosting").isEmpty()) {
                        hostname = result.get("hosting").get(0).getFull_name();
                        hostID = result.get("hosting").get(0).getUserID();
                        hostView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                                intent.putExtra("userObject", result.get("hosting").get(0));
                                startActivity(intent);
                            }
                        });
                    }
                    hostView.setText(hostname);

                    //If coming from hostFragment and you are a host
                    if (callerType == activityHostFragment && hostID.equals(DatabaseAccess.getTokenFromLocal(mainActivity).get("id"))) {
                        invitedView.setText("INVITED (" + result.get("inviting").size() + ")");
                        populateHorizontalList(result.get("inviting"), listInvited);
                    }

                    goingList = result.get("going");
                    goingView.setText("FRIENDS GOING (" + goingList.size() + ")");
                    populateHorizontalList(goingList, listGoing);

                    bounceView.setText("BOUNCERS (" + result.get("bouncing").size() + ")");
                    populateHorizontalList(result.get("bouncing"), listBouncing);

                    attending = male = female = 0;
                    for ( User each : result.get("attending") ) {
                        attending++;
                        if(each.getGender().toLowerCase().equals("male")){
                            male++;
                        }
                        else if(each.getGender().toLowerCase().equals("female")){
                            female++;
                        }
                    }
                    attendingView.setText(attending + "");
                    genderView.setText(female + "/" + male);

                    setupSpecialFields(callerType, hostID);
                }
            }
        });
    }

    private void populateHorizontalList(List<User> list, int type) {
        LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);

        if (type == listInvited) {
            invitedFriends.setLayoutManager(layoutManagerAttendees);
            invitedFriends.setFocusable(false);
            invitedFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, list));
        } else if (type == listGoing) {
            goingFriends.setLayoutManager(layoutManagerAttendees);
            goingFriends.setFocusable(false);
            goingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, list));
        } else if (type == listBouncing) {
            bouncingFriends.setLayoutManager(layoutManagerAttendees);
            bouncingFriends.setFocusable(false);
            bouncingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, list));
        }
    }

    //http://stackoverflow.com/a/25283174
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
        } catch (WriterException e) {
            e.printStackTrace();
        }
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
                intent.putExtra("partyObject", party);
                startActivityForResult(intent, EDIT_STATS_REQUEST);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);

        EmojiconTextView emojiText = (EmojiconTextView) mainActivity.findViewById(R.id.hostEventStats_emoji);
        emojiText.setText(party.getPartyEmoji().substring(0, 1));
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

    /**
     * Check specified userID and partyID with server to see if they match.
     * @param user_id scanned userID
     * @param party_id scanned partyID
     */
    private void validateUserAndParty(long user_id, long party_id) {
        Toast.makeText(this, "UserID: " + user_id + ", PartyID: " + party_id, Toast.LENGTH_LONG).show();
        // TODO: 04/02/2017 Check with database
    }
}