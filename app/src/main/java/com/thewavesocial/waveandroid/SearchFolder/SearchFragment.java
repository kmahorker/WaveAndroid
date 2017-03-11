package com.thewavesocial.waveandroid.SearchFolder;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.search_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.d("In", "Search Fragment");
        //setupReferences();
    }

    private void setupReferences()
    {
        final TextView searchEventButton = (TextView) getActivity().findViewById(R.id.searchView_events_button);
        final TextView searchPeopleButton = (TextView) getActivity().findViewById(R.id.searchView_people_button);

        searchEventButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchEventButton.setBackgroundResource(R.drawable.round_corner_red);
                searchPeopleButton.setBackgroundResource(R.drawable.round_corner_red_edge);
                searchEventButton.setTextColor(getResources().getColor(R.color.white));
                searchPeopleButton.setTextColor(getResources().getColor(R.color.appColor));
                openSearchEvent();
            }
        });

        searchPeopleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchPeopleButton.setBackgroundResource(R.drawable.round_corner_red);
                searchEventButton.setBackgroundResource(R.drawable.round_corner_red_edge);
                searchPeopleButton.setTextColor(getResources().getColor(R.color.white));
                searchEventButton.setTextColor(getResources().getColor(R.color.appColor));
                openSearchPeople();
            }
        });
    }

    private void openSearchPeople()
    {
        Fragment fragment = new SearchPeopleFragment();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
    }

    private void openSearchEvent()
    {
        Fragment fragment = new SearchEventFragment();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.home_mapsView_infoFrame, fragment);
        transaction.commit();
    }



    public List<Party> searchEvent(List<Party> parties, String query)
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
