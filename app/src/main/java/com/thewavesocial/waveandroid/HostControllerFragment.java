package com.thewavesocial.waveandroid;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HostControllerFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.host_controller_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_hostcontroller);

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
