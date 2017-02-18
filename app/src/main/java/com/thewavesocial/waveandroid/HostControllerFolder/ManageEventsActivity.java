package com.thewavesocial.waveandroid.HostControllerFolder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.AdaptersFolder.ManagePartyCustomAdapter;
import com.thewavesocial.waveandroid.AdaptersFolder.UserPartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.DummyUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;


public class ManageEventsActivity extends AppCompatActivity
{
    private User dummy;
    private View view;
    private ManageEventsActivity mainActivity;
    private ListView manageListView;
    private List<Party> partyList;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_events_layout);

        setupActionbar();
        setupReferences();
        setupListeners();
    }

    private void setupListeners()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                List<Party> newPartyList = search(partyList, query);
                manageListView.setAdapter(new ManagePartyCustomAdapter(mainActivity, newPartyList));
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                List<Party> newPartyList = search(partyList, query);
                manageListView.setAdapter(new ManagePartyCustomAdapter(mainActivity, newPartyList));
                return true;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                UtilityClass.hideKeyboard( mainActivity );
                return true;
            }
        });

        manageListView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                searchView.clearFocus();
                return false;
            }
        });
    }

    private void setupReferences()
    {
        view = this.findViewById(android.R.id.content).getRootView();
        mainActivity = this;
        dummy = CurrentUser.theUser;
        manageListView = (ListView) findViewById(R.id.manageEvents_listview);
        searchView = (SearchView) findViewById(R.id.manageEvents_searchbar);
        searchView.setIconifiedByDefault(false);
        partyList = ((DummyUser)dummy).getPartyListObjects(dummy.getHosted());
        CurrentUser.setContext(this);
        manageListView.setAdapter(new ManagePartyCustomAdapter(this, partyList));
    }

    private void setupActionbar()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Manage Events");
    }

    public List<Party> search(List<Party> parties, String query)
    {
        if(query == "")
            return parties;

        List<Party> users = new ArrayList<Party>();
        for(Party party : parties)
        {
            if( party.getName().matches("(?i:" + query + ".*)"))
            {
                users.add(party);
            }
        }
        return users;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home )
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
