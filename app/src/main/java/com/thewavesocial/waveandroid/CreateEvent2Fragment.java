package com.thewavesocial.waveandroid;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreateEvent2Fragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.create_event_fragment2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
