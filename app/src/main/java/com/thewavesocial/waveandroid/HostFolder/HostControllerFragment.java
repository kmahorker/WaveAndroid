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
import android.widget.ProgressBar;

import com.thewavesocial.waveandroid.AdaptersFolder.ManagePartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


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
        Log.d("Resume", "TRUE");
    }

    public void populateListView() {
        Log.d(HomeSwipeActivity.TAG, "HostControllerFragment.populateListView");
        final ProgressBar progressBar = (ProgressBar) mainActivity.findViewById(R.id.home_hostView_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        server_getEventsOfUser(CurrentUser.theUser.getUserID(), new OnResultReadyListener<HashMap<String, ArrayList<Party>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<Party>> result) {
                if (result != null) {
                    List<Party> list = new ArrayList<>();
                    list.addAll(result.get("hosting"));
                    list.addAll(result.get("bouncing"));

                    TreeMap<Long, Party> partyTreeMap = new TreeMap<>();
                    for ( Party party : list ) {
                        long time = party.getDate();
                        if ( time - Calendar.getInstance().getTimeInMillis()/1000 > -86400 ) { //1 day
                            while (partyTreeMap.containsKey(time)) {
                                time++;
                            }
                            partyTreeMap.put(time, party);
                        }
                    }

                    List<Party> new_list = new ArrayList<>();
                    for ( Long key : partyTreeMap.keySet() ) {
                        new_list.add(partyTreeMap.get(key));
                    }
                    manageList.setAdapter(new ManagePartyCustomAdapter(mainActivity, new_list));

                    if (mainActivity.findViewById(R.id.home_hostView_text_noEvent) == null)
                        return;
                    if (!result.isEmpty())
                        mainActivity.findViewById(R.id.home_hostView_text_noEvent).setVisibility(View.INVISIBLE);
                    else
                        mainActivity.findViewById(R.id.home_hostView_text_noEvent).setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
