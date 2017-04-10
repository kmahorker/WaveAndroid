package com.thewavesocial.waveandroid.HostFolder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class EventStatsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private LatLng latlng;
    private Party party;
    private TextView goingView, genderView, hostView, locView, dateView, timeView;
    private ImageView qrCodeView;
    private RecyclerView attendingFriends;
    private String host, loc, date, time;
    private int going, male, female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats);
        Intent intent = getIntent();
        party = CurrentUser.getPartyObject(intent.getExtras().getLong("partyIDLong"));

        setupPartyInfos();
        setupReferences();
        setupFunctionalities();
        setupActionbar();
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
        attendingFriends = (RecyclerView) findViewById(R.id.hostEventStats_attendeelist);
    }

    private void setupFunctionalities() {
        goingView.setText(going+"");
        genderView.setText(female+"/"+male);
        hostView.setText(host+"");
        locView.setText(loc+"");
        dateView.setText(date+"");
        timeView.setText(time+"");
        qrCodeView.setImageDrawable(getDrawable(R.drawable.sample_qrcode));

        LinearLayoutManager layoutManagerAttendees =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attendingFriends.setLayoutManager(layoutManagerAttendees);
        attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(this,
                CurrentUser.getUsersListObjects(party.getAttendingUsers())));
    }

    private void setupActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_hoststats);

        TextView partynameView = (TextView) findViewById(R.id.actionbar_hoststats_partyname);
        TextView editpartyView = (TextView) findViewById(R.id.actionbar_hoststats_editparty);
        ImageView backButton = (ImageView) findViewById(R.id.actionbar_hoststats_backbutton);

        partynameView.setText(party.getName());
        editpartyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 04/09/2017 Intent Edit Party
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
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.happy_house, 150, 150))));
        marker.setTag(party.getPartyID());
    }

    public Bitmap resizeMapIcons(int res, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), res);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //36dp 2dp
}