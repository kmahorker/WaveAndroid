package com.thewavesocial.waveandroid.EventsFolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.R;
import java.util.ArrayList;
import java.util.List;

public class PartyProfileActivity extends AppCompatActivity
{
    private Party party;
    private TextView partyname, hostname, datetime, location;
    private Button getinButton;
    private RecyclerView attendingFriends;
    private ImageView streetview;
    private PartyProfileActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_party);
        mainActivity = this;

        Intent intent = getIntent();
        party = CurrentUser.getPartyObject(intent.getExtras().getLong("partyIDLong"));

        setupActionbar();
        setupReferences();
        setupOnClicks();
    }

    private void setupOnClicks()
    {
        getinButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/24/2017 Need to implement
            }
        });

        hostname.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
//                intent.putExtra("userIDLong", party.getHost().getUserID());
//                mainActivity.startActivity(intent);
                // TODO: 02/24/2017 Change hostname to host user object
            }
        });
    }

    private void setupReferences()
    {
        partyname = (TextView) findViewById(R.id.partyprofile_text_partyname);
        hostname = (TextView) findViewById(R.id.partyprofile_text_hostname);
        datetime = (TextView) findViewById(R.id.partyprofile_text_datetime);
        location = (TextView) findViewById(R.id.partyprofile_text_location);
        getinButton = (Button) findViewById(R.id.partyprofile_button_getin);
        attendingFriends = (RecyclerView) findViewById(R.id.partyprofile_listview_attendees);
        streetview = (ImageView) findViewById(R.id.partyprofile_image_streetview);

        List<User> sample = new ArrayList(); //added friend list 3 times for testing purpose
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));

        LinearLayoutManager layoutManagerAttendees=
                new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        attendingFriends.setLayoutManager( layoutManagerAttendees );
        attendingFriends.setAdapter( new PartyAttendeesCustomAdapter(this, sample));

//        try
//        {
//            //http://stackoverflow.com/questions/27024965/how-to-display-a-streetview-preview
//            String imageUrl = "https://maps.googleapis.com/maps/api/streetview?"
//                    + "size=370x70" + "&" + "location="
//                    + party.getMapAddress().getAddress_latlng().latitude + ","
//                    + party.getMapAddress().getAddress_latlng().longitude + "&"
//                    + "key=" + getString(R.string.google_maps_key);
//
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
//            streetview.setImageBitmap(bitmap);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    private void setupActionbar()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
