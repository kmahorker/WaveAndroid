package com.thewavesocial.waveandroid.SearchFolder;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.AdaptersFolder.SearchEventCustomAdapter;
import com.thewavesocial.waveandroid.AdaptersFolder.SearchPeopleCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchEventFragment extends Fragment
{

    private HomeSwipeActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.search_view_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity)getActivity();
        Log.d("In", "Search People");

        final ListView eventListView = (ListView) view.findViewById(R.id.searchEvent_list);
        final List<Party> eventList = CurrentUser.getPartyListObjects(CurrentUser.theUser.getAttended());
        eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity, eventList));

        final SearchView searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity,
                        searchEvents(eventList, query)));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity,
                        searchEvents(eventList, newText)));
                return false;
            }
        });
    }

    public List<Party> searchEvents(List<Party> parties, String query)
    {
        if(query == "")
        {
            return parties;
        }
        List<Party> newParties = new ArrayList<>();
        for(Party party : parties)
        {
            if((party.getName().matches("(?i:" + query + ".*)")))
            {
                newParties.add(party);
            }
        }
        return newParties;
    }
}
