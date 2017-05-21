package com.thewavesocial.waveandroid.HostFolder;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.thewavesocial.waveandroid.AdaptersFolder.ManagePartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;


public class HostControllerFragment extends Fragment {
    private HomeSwipeActivity mainActivity;
    private HostControllerFragment thisFragment = this;
    private ListView manageList = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_host_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        ImageView createButton = (ImageView) mainActivity.findViewById(R.id.home_hostView_image_createEvent);
        manageList = (ListView) mainActivity.findViewById(R.id.home_hostView_list_manageEvents);
        populateListView();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateAnEventActivity.class);
                //FragmentManager manager = thisFragment.getFragmentManager();
                FragmentTransaction transaction = thisFragment.getFragmentManager().beginTransaction();
                transaction.detach(thisFragment);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    public void populateListView(){
        final List<String> sample = new ArrayList<>();
        sample.addAll(CurrentUser.theUser.getHosted());
        CurrentUser.server_getPartyListObjects(sample, new OnResultReadyListener<List<Party>>() {
            @Override
            public void onResultReady(List<Party> result) {
                if ( result != null ) {
                    manageList.setAdapter(new ManagePartyCustomAdapter(mainActivity, result));
                }
            }
        });
    }
}
