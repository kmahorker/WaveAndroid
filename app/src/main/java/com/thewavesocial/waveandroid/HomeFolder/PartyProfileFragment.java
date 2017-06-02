package com.thewavesocial.waveandroid.HomeFolder;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.UserProfileFragment;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

public class PartyProfileFragment extends Fragment {
    private static Party party;
    private Button goButton;
    private EditText editText;
    private SearchView searchbar;
    private ListView hostedEvents;
    private static List<User> sample;
    private static RecyclerView attendingFriends;
    private static HomeSwipeActivity mainActivity;
    private TextView partyname, hostname, datetime, location, price;
    private EmojiconTextView emoji;
    private TextView actionbar_social;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_party, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        party = getArguments().getParcelable("partyObject");

        actionbar_social = (TextView) mainActivity.getSupportActionBar().getCustomView().findViewById(R.id.actionbar_activity_home_text_social);

        setupReferences();
        setupOnClicks();
    }

    private void setupOnClicks() {
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( CurrentUser.theUser.getAttending().contains(party.getPartyID()) ) {
                    Toast.makeText(mainActivity, "Party Already Added.", Toast.LENGTH_LONG).show();
                } else {
                    CurrentUser.theUser.getAttending().add(0, party.getPartyID());
                    Toast.makeText(mainActivity, "Party Added!", Toast.LENGTH_LONG).show();
                    actionbar_social.setText("SOCIAL(1)");
                    UserProfileFragment.goingButton.setText("Attending(1)");
                }
                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                intent.putExtra("partyObject", party);
                intent.putExtra("callerActivity", EventStatsActivity.activitySocialFragment);
                mainActivity.startActivity(intent);
            }
        });
        hostname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", 0);
                mainActivity.startActivity(intent);
                // TODO: 02/24/2017 Change hostname to host user object
            }
        });
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsFragment.dragSeparator(30 - MapsFragment.mapHeight / 2, 0);
                if (!MapsFragment.searchOpened)
                    MapsFragment.openSearchView();
            }
        });

        int id = searchbar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        editText = (EditText) searchbar.findViewById(id);
        editText.setCursorVisible(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment.dragSeparator(30 - MapsFragment.mapHeight / 2, 0);
                if ( !MapsFragment.searchOpened )
                    MapsFragment.openSearchView();
                editText.setCursorVisible(true);
            }
        });
    }

    private void setupReferences() {
        searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        partyname = (TextView) mainActivity.findViewById(R.id.partyprofile_text_partyname);
        hostname = (TextView) mainActivity.findViewById(R.id.partyprofile_host);
        datetime = (TextView) mainActivity.findViewById(R.id.partyprofile_time);
        location = (TextView) mainActivity.findViewById(R.id.partyprofile_location);
        price = (TextView) mainActivity.findViewById(R.id.partyprofile_price);
        goButton = (Button) mainActivity.findViewById(R.id.partyprofile_go_button);
        attendingFriends = (RecyclerView) mainActivity.findViewById(R.id.partyprofile_attendee_list);
        hostedEvents = (ListView) mainActivity.findViewById(R.id.partyprofile_eventsHosted_list);
        emoji = (EmojiconTextView) mainActivity.findViewById(R.id.partyprofile_text_emoji);

        emoji.setText(party.getPartyEmoji().substring(0,1));
        partyname.setText(party.getName());
        hostname.setText(party.getHostName());
        datetime.setText(UtilityClass.timeToString(party.getStartingDateTime()) + " - " +
                UtilityClass.timeToString(party.getEndingDateTime()));
        location.setText(party.getMapAddress().getAddress_string());
        price.setText(UtilityClass.priceToString(party.getPrice()));

        sample = new ArrayList();

        CurrentUser.server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                if ( result != null ) {

                    if ( !result.get("hosting").isEmpty() )
                        hostname.setText(result.get("hosting").get(0).getFullName());

                    if ( !result.get("attending").isEmpty() )
                        sample.addAll(result.get("attending"));
                    LinearLayoutManager layoutManagerAttendees = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
                    attendingFriends.setLayoutManager(layoutManagerAttendees);
                    attendingFriends.setFocusable(false);
                    attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, sample));
                }
            }
        });

        final List<Party> events = new ArrayList<>();
        CurrentUser.server_getPartyListObjects(CurrentUser.theUser.getHosted(), new OnResultReadyListener<List<Party>>() {
            @Override
            public void onResultReady(List<Party> result) {
                if ( result != null ) {
                    events.addAll(result);  // TODO: 03/08/2017 Testing Purpose
                }
            }
        });
        hostedEvents.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, events));
        hostedEvents.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public static void updateAttendeeImages() {
        if (attendingFriends != null) {
            attendingFriends.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, sample));
        }
    }
}
