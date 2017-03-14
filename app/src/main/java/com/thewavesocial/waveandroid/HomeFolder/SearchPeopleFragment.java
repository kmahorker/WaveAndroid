package com.thewavesocial.waveandroid.HomeFolder;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thewavesocial.waveandroid.AdaptersFolder.SearchEventCustomAdapter;
import com.thewavesocial.waveandroid.AdaptersFolder.SearchPeopleCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchPeopleFragment extends Fragment {
    HomeSwipeActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view_people, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        final SearchView searchbar = (SearchView) mainActivity.findViewById(R.id.home_mapsView_searchbar);
        final ListView peopleListView = (ListView) view.findViewById(R.id.searchPeople_list);
        final List<User> userList = CurrentUser.getUsersListObjects(CurrentUser.theUser.getFollowing());

        peopleListView.setAdapter(new SearchPeopleCustomAdapter(mainActivity,
                searchPeople(userList, searchbar.getQuery().toString())));
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                peopleListView.setAdapter(new SearchPeopleCustomAdapter(mainActivity,
                        searchPeople(userList, query)));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                peopleListView.setAdapter(new SearchPeopleCustomAdapter(mainActivity,
                        searchPeople(userList, newText)));
                return false;
            }
        });
    }


    private List<User> searchPeople(List<User> users, String query) {
        if (query == "") {
            return users;
        }
        List<User> newUsers = new ArrayList<>();
        for (User user : users) {
            if ( user.getFullName().toLowerCase().contains( query.toLowerCase() ) ) {
                newUsers.add(user);
            }
        }
        return newUsers;
    }
}
