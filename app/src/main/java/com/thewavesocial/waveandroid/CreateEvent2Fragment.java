package com.thewavesocial.waveandroid;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEvent2Fragment extends Fragment
{
    EditText partyname;
    Activity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.create_event_fragment2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = this.getActivity();
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event2);

        partyname = (EditText) getActivity().findViewById(R.id.createEvent2_partyname);
        partyname.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    UtilityClass.hideKeyboard( mainActivity );
                }
            }
        });

        TextView finish = (TextView) getActivity().findViewById(R.id.actionbar_createEvent2_finish);
        finish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (partyname.getText().toString().isEmpty())
                {
                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(getActivity());
                    fieldAlert.setMessage("Please specify your party name.")
                            .setCancelable(true)
                            .show();
                }
                else
                {
                    ((CreateEventActivity)getActivity()).setPartyName(partyname.getText().toString());
                    ((CreateEventActivity)getActivity()).saveToUser();
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().onBackPressed();
                }
            }
        });
    }
}
