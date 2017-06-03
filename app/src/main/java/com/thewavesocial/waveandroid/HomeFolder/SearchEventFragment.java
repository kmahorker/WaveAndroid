package com.thewavesocial.waveandroid.HomeFolder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.AdaptersFolder.SearchEventCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchEventFragment extends Fragment {

    private HomeSwipeActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        final SearchView searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        final ListView eventListView = (ListView) view.findViewById(R.id.searchEvent_list);

        //Auto-search database for existing query on start
        if (!searchbar.getQuery().toString().isEmpty()) {
            CurrentUser.server_getEventsByKeyword(searchbar.getQuery().toString(), new OnResultReadyListener<ArrayList<Party>>() {
                @Override
                public void onResultReady(ArrayList<Party> result) {
                    if (result != null)
                        eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity, result));
                }
            });
        }

        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ( query.isEmpty() )
                    return false;
                CurrentUser.server_getEventsByKeyword(query, new OnResultReadyListener<ArrayList<Party>>() {
                    @Override
                    public void onResultReady(ArrayList<Party> result) {
                        if (result != null)
                            eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity, result));
                    }
                });
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
//                eventListView.setAdapter(new SearchEventCustomAdapter(mainActivity,
//                        searchEvents(eventList, newText)));
                return false;
            }
        });
    }
}
