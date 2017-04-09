package com.thewavesocial.waveandroid.HostFolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.StatsFriendCustomAdapter;
import com.thewavesocial.waveandroid.AdaptersFolder.StatsHostBouncerCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class EventStatsActivity extends AppCompatActivity {
    private Party party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats);

        Intent intent = getIntent();
        party = CurrentUser.getPartyObject(intent.getExtras().getLong("partyIDLong"));

        setupReferences();
        setupActionbar();
    }

    private void setupReferences() {
        TextView earnedText = (TextView) findViewById(R.id.eventStats_earning);
        TextView attendedText = (TextView) findViewById(R.id.eventStats_checkedIn);
        RecyclerView hostlist = (RecyclerView) findViewById(R.id.eventStats_hostlist);
        RecyclerView bouncerlist = (RecyclerView) findViewById(R.id.eventStats_bouncerlist);
        GridView peoplelist = (GridView) findViewById(R.id.eventStats_peoplelist);

        earnedText.setText("Amount earned     $" + party.getPrice() * party.getAttendingUsers().size());
        attendedText.setText("# of people checked in: " + party.getAttendingUsers().size());

        List<User> sample = new ArrayList(); //added friend list 3 times for testing purpose
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));
        sample.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));

        LinearLayoutManager layoutManagerHost = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerBounce = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hostlist.setLayoutManager(layoutManagerHost);
        bouncerlist.setLayoutManager(layoutManagerBounce);

        hostlist.setAdapter(new StatsHostBouncerCustomAdapter(this, sample));
        bouncerlist.setAdapter(new StatsHostBouncerCustomAdapter(this, sample));
        peoplelist.setAdapter(new StatsFriendCustomAdapter(this, sample));
    }

    private void setupActionbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
