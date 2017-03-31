package com.thewavesocial.waveandroid.HostFolder;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.thewavesocial.waveandroid.AdaptersFolder.ManagePartyCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;


public class HostControllerFragment extends Fragment
{
    private HomeSwipeActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.home_host_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeSwipeActivity) getActivity();

        ImageView createButton = (ImageView) mainActivity.findViewById(R.id.home_hostView_image_createEvent);
        ListView manageList = (ListView) mainActivity.findViewById(R.id.home_hostView_list_manageEvents);

        List<Long> sample = new ArrayList<>();
        sample.addAll(CurrentUser.theUser.getHosted());
        sample.addAll(CurrentUser.theUser.getHosted());
        manageList.setAdapter(new ManagePartyCustomAdapter(mainActivity,
                CurrentUser.getPartyListObjects(sample)));

        createButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), CreateAnEventActivity.class);
                startActivity(intent);
            }
        });
    }
}
