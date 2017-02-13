package com.thewavesocial.waveandroid;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEvent2Fragment extends Fragment
{
    EditText partyname;
    TextView startingDate, startingTime, endingDate, endingTime,
            location, paidfree, privatePublic, maleCount, femaleCount, finish;
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

        setupActionbar();
        setupReferences();
        setupOnClicks();
    }

    private void setupReferences()
    {
        partyname = (EditText) getActivity().findViewById(R.id.createEvent2_partyname);
        startingDate = (TextView) getActivity().findViewById(R.id.createEvent2_startingDate);
        startingTime = (TextView) getActivity().findViewById(R.id.createEvent2_startingTime);
        endingDate = (TextView) getActivity().findViewById(R.id.createEvent2_endingDate);
        endingTime = (TextView) getActivity().findViewById(R.id.createEvent2_endingTime);
        location = (TextView) getActivity().findViewById(R.id.createEvent2_location);
        privatePublic = (TextView) getActivity().findViewById(R.id.createEvent2_private_public);
        paidfree = (TextView) getActivity().findViewById(R.id.createEvent2_paid_free);
        maleCount = (TextView) getActivity().findViewById(R.id.createEvent2_male_count);
        femaleCount = (TextView) getActivity().findViewById(R.id.createEvent2_female_count);
        finish = (TextView) getActivity().findViewById(R.id.actionbar_createEvent2_finish);

        startingDate.setText( UtilityClass.dateToString( ((CreateEventActivity)getActivity()).getStartingCalendar()) );
        endingDate.setText( UtilityClass.dateToString( ((CreateEventActivity)getActivity()).getEndingCalendar()) );
        startingTime.setText( UtilityClass.timeToString( ((CreateEventActivity)getActivity()).getStartingCalendar()) );
        endingTime.setText( UtilityClass.timeToString( ((CreateEventActivity)getActivity()).getStartingCalendar()) );
        location.setText( ((CreateEventActivity)getActivity()).getLocation() );
        privatePublic.setText( ((CreateEventActivity)getActivity()).getPrivatePublic() );
        paidfree.setText( ((CreateEventActivity)getActivity()).getPaidFree()
                + " - $" + ((CreateEventActivity)getActivity()).getPrice());
        maleCount.setText( ((CreateEventActivity)getActivity()).getManCount() + " Males" );
        femaleCount.setText( ((CreateEventActivity)getActivity()).getFemaleCount() + " Females");
    }

    private void setupOnClicks()
    {
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

    private void setupActionbar()
    {
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event2);
    }


}
