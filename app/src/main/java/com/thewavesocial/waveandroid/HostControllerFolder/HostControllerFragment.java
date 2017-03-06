package com.thewavesocial.waveandroid.HostControllerFolder;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.HomeActivity;
import com.thewavesocial.waveandroid.HomeDrawerActivity;
import com.thewavesocial.waveandroid.R;


public class HostControllerFragment extends Fragment
{
    private HomeActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.host_controller_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (HomeActivity) getActivity();

        TextView createEventText = (TextView) getActivity().findViewById(R.id.hostControl_createEvent_text);
        TextView manageEventText = (TextView) getActivity().findViewById(R.id.hostControl_manageEvent_text);

        createEventText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivity(intent);
            }
        });

        manageEventText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), ManageEventsActivity.class);
                startActivity(intent);
            }
        });
    }


}
