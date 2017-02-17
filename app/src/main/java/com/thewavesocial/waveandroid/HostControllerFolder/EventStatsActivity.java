package com.thewavesocial.waveandroid.HostControllerFolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;

public class EventStatsActivity extends AppCompatActivity
{
    private Party party;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_stats_layout);

        Intent intent = getIntent();
        party = CurrentUser.getPartyObject(intent.getExtras().getLong("partyIDLong"));

        setupReferences();
        setupActionbar();
    }

    private void setupReferences()
    {
        TextView earnedText = (TextView) findViewById(R.id.eventStats_earning);
        TextView attendedText = (TextView) findViewById(R.id.eventStats_checkedIn);
        GridView hostlist = (GridView) findViewById(R.id.eventStats_hostlist);
        GridView bouncerlist = (GridView) findViewById(R.id.eventStats_bouncerlist);
        GridView peoplelist = (GridView) findViewById(R.id.eventStats_peoplelist);

        earnedText.setText("Amount earned     $" + party.getPrice()*party.getAttendingUsers().size());
        attendedText.setText("# of people checked in: " + party.getAttendingUsers().size());

    }

    private void setupActionbar()
    {

    }
}
