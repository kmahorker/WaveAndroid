package com.thewavesocial.waveandroid;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateEvent1Fragment extends Fragment
{
    private int maleNum, femaleNum;
    private EditText startingD, startingT, endingD, endingT, location, price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.create_event_fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setupActionBar();
        setupTextFields();
        setupOnClicks();
    }

    private void setupTextFields()
    {
         startingD = (EditText)getActivity().findViewById(R.id.createEvent1_startingDate_editText);
         startingT = (EditText)getActivity().findViewById(R.id.createEvent1_startingTime_editText);
         endingD = (EditText)getActivity().findViewById(R.id.createEvent1_endingDate_editText);
         endingT = (EditText)getActivity().findViewById(R.id.createEvent1_endingTime_editText);
         location = (EditText)getActivity().findViewById(R.id.createEvent1_location_editText);
         price = (EditText)getActivity().findViewById(R.id.createEvent1_price_editText);
    }

    private void setupOnClicks()
    {
        final TextView privateText = (TextView) getActivity().findViewById(R.id.createEvent1_private_text);
        final TextView publicText = (TextView) getActivity().findViewById(R.id.createEvent1_public_text);
        final TextView paidText = (TextView) getActivity().findViewById(R.id.createEvent1_paid_text);
        final TextView freeText = (TextView) getActivity().findViewById(R.id.createEvent1_free_text);
        final ImageView maleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_maleMinus_image);
        final ImageView malePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_malePlus_image);
        final ImageView femaleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_femaleMinus_image);
        final ImageView femalePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_femalePlus_image);
        final TextView maleCount = (TextView)getActivity().findViewById(R.id.createEvent1_maleCount_text);
        final TextView femaleCount = (TextView)getActivity().findViewById(R.id.createEvent1_femaleCount_text);

        maleNum = Integer.parseInt(maleCount.getText().toString());
        femaleNum = Integer.parseInt(femaleCount.getText().toString());

        privateText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                privateText.setTextColor(Color.WHITE);
                privateText.setBackgroundResource(R.color.appColor);
                publicText.setTextColor(getResources().getColor(R.color.appColor));
                publicText.setBackgroundColor(Color.WHITE);
            }
        });

        publicText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                publicText.setTextColor(Color.WHITE);
                publicText.setBackgroundResource(R.color.appColor);
                privateText.setTextColor(getResources().getColor(R.color.appColor));
                privateText.setBackgroundColor(Color.WHITE);
            }
        });

        paidText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                paidText.setTextColor(Color.WHITE);
                paidText.setBackgroundResource(R.color.appColor);
                freeText.setTextColor(getResources().getColor(R.color.appColor));
                freeText.setBackgroundColor(Color.WHITE);
            }
        });

        freeText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                freeText.setTextColor(Color.WHITE);
                freeText.setBackgroundResource(R.color.appColor);
                paidText.setTextColor(getResources().getColor(R.color.appColor));
                paidText.setBackgroundColor(Color.WHITE);
                price.setText("$0");
            }
        });

        maleMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                maleNum--;
                maleCount.setText(maleNum+"");
            }
        });

        malePlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                maleNum++;
                maleCount.setText(maleNum+"");
            }
        });

        femaleMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                femaleNum--;
                femaleCount.setText(femaleNum+"");
            }
        });

        femalePlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                femaleNum++;
                femaleCount.setText(femaleNum+"");
            }
        });
    }

    private void setupActionBar()
    {
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event1);
        ((CreateEventActivity)getActivity()).getSupportActionBar().setTitle("");
        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView forwardButton = (ImageView) getActivity().findViewById(R.id.actionbar_createEvent1_image);
        forwardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ( startingD.getText().toString().isEmpty() ||
                        startingT.getText().toString().isEmpty() ||
                        endingD.getText().toString().isEmpty() ||
                        endingT.getText().toString().isEmpty() ||
                        location.getText().toString().isEmpty() ||
                        price.getText().toString().isEmpty() )
                {
                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(getActivity());
                    fieldAlert.setMessage("Please enter all the party information.")
                            .setCancelable(true)
                            .show();
                }
                else
                {
                    saveData();
                    FragmentManager fragM = getActivity().getSupportFragmentManager();
                    fragM.beginTransaction()
                            .replace(R.id.createEvent_fragment_container, new CreateEvent2Fragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    private void saveData()
    {
        ((CreateEventActivity)getActivity()).saveData(startingD.getText().toString(),
                startingT.getText().toString(), endingD.getText().toString(),
                endingT.getText().toString(), location.getText().toString(),
                price.getText().toString());
    }
}
