package com.thewavesocial.waveandroid.HomeFolder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

public class SearchFragment extends Fragment {
    private HomeSwipeActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();
        setupReferences();
    }


    private void setupReferences() {
        final TextView searchEventButton = (TextView) getActivity().findViewById(R.id.searchView_events_button);
        final TextView searchPeopleButton = (TextView) getActivity().findViewById(R.id.searchView_people_button);
        if ( !MapsFragment.searchOpened )
            openSearchEvent();
        searchEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButton(searchEventButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(searchPeopleButton, R.color.appColor, R.drawable.round_corner_red_edge);
                openSearchEvent();
                UtilityClass.hideKeyboard(mainActivity);
                MapsFragment.searchOpened = true;
            }
        });
        searchPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButton(searchPeopleButton, R.color.white_solid, R.drawable.round_corner_red);
                changeButton(searchEventButton, R.color.appColor, R.drawable.round_corner_red_edge);
                openSearchPeople();
                UtilityClass.hideKeyboard(mainActivity);
                MapsFragment.searchOpened = true;
            }
        });
    }


    private void changeButton(TextView view, int textColor, int backgroundColor) {
        view.setTextColor(mainActivity.getResources().getColor(textColor));
        view.setBackgroundResource(backgroundColor);
    }


    private void openSearchPeople() {
        Fragment fragment = new SearchPeopleFragment();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.searchView_list_container, fragment);
        transaction.commit();
    }


    private void openSearchEvent() {
        Fragment fragment = new SearchEventFragment();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.searchView_list_container, fragment);
        transaction.commit();
    }
}
