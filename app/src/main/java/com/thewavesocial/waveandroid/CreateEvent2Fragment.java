package com.thewavesocial.waveandroid;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEvent2Fragment extends Fragment
{
    private EditText partyname;
    private TextView startingDate, startingTime, endingDate, endingTime,
            location, paidfree, privatePublic, maleCount, femaleCount, finish;
    private CreateEventActivity mainActivity;
    private View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.create_event_fragment2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (CreateEventActivity)getActivity();
        mainView = view;

        setupActionbar();
        setupReferences();
        setupInitializeValues();
        setupOnClicks();
    }

    private void setupInitializeValues()
    {
        startingDate.setText( UtilityClass.dateToString( mainActivity.startCalendar ));
        startingTime.setText( UtilityClass.timeToString( mainActivity.startCalendar ));
        endingDate.setText( UtilityClass.dateToString( mainActivity.endCalendar ));
        endingTime.setText( UtilityClass.timeToString( mainActivity.endCalendar ));
        location.setText( mainActivity.location );
        privatePublic.setText( mainActivity.privatePublic );
        paidfree.setText( mainActivity.paidFree
                + " - $" + mainActivity.price);
        maleCount.setText( mainActivity.maleCount + " Males" );
        femaleCount.setText( mainActivity.femaleCount + " Females");
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
                    mainActivity.name = partyname.getText().toString();
                    mainActivity.saveToUser();
                    mainActivity.getSupportFragmentManager().popBackStack();
                    mainActivity.getSupportFragmentManager().popBackStack();
                    mainActivity.onBackPressed();
                }
            }
        });

        mainView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
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
