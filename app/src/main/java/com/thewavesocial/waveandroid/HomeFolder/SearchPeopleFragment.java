package com.thewavesocial.waveandroid.HomeFolder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thewavesocial.waveandroid.AdaptersFolder.SearchPeopleCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

import java.util.ArrayList;
import java.util.List;

public class SearchPeopleFragment extends Fragment {
    HomeSwipeActivity mainActivity;
    private SearchView searchbar;
    private ListView peopleListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view_people, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        peopleListView = (ListView) view.findViewById(R.id.searchPeople_list);

        //Auto-search database for existing query on start
        if (!searchbar.getQuery().toString().isEmpty())
            searchQueryOnServer(searchbar.getQuery().toString());
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ( !query.isEmpty() )
                    searchQueryOnServer(query);
                return !query.isEmpty();
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchQueryOnServer(String query) {
        server_getUsersByKeyword(query, new OnResultReadyListener<ArrayList<User>>() {
            @Override
            public void onResultReady(ArrayList<User> result) {
                getFollowingAndUpdateList(result);
            }
        });
    }

    private void getFollowingAndUpdateList(final ArrayList<User> result) {
        server_getUserFollowing(CurrentUser.theUser.getUserID(), new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(List<User> following) {
                peopleListView.setAdapter(new SearchPeopleCustomAdapter(mainActivity, result, following));
            }
        });
    }
}
