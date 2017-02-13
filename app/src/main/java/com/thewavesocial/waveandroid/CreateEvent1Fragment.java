package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateEvent1Fragment extends Fragment
{
    private int maleNum, femaleNum, startendDateCheck, startendTimeCheck;
    private Calendar startCalendar, endCalendar;
    private TextView privateText, publicText, paidText, freeText, maleCount,
            femaleCount, editStartDate, editStartTime, editEndDate, editEndTime;
    private EditText editLocation, editPrice;
    private ImageView maleMinus, malePlus, femaleMinus, femalePlus;
    private Activity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.create_event_fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = this.getActivity();

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        setupActionBar();
        setupReferences();
        setupOnClicks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1)
                getActivity().getSupportFragmentManager().popBackStack();
            else
                askToSave();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupReferences()
    {
        editStartDate = (TextView)getActivity().findViewById(R.id.createEvent1_startingDate_editText);
        editStartTime = (TextView)getActivity().findViewById(R.id.createEvent1_startingTime_editText);
        editEndDate = (TextView)getActivity().findViewById(R.id.createEvent1_endingDate_editText);
        editEndTime = (TextView)getActivity().findViewById(R.id.createEvent1_endingTime_editText);
        editLocation = (EditText)getActivity().findViewById(R.id.createEvent1_location_editText);
        editPrice = (EditText)getActivity().findViewById(R.id.createEvent1_price_editText);

        privateText = (TextView) getActivity().findViewById(R.id.createEvent1_private_text);
        publicText = (TextView) getActivity().findViewById(R.id.createEvent1_public_text);
        paidText = (TextView) getActivity().findViewById(R.id.createEvent1_paid_text);
        freeText = (TextView) getActivity().findViewById(R.id.createEvent1_free_text);

        maleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_maleMinus_image);
        malePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_malePlus_image);
        femaleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_femaleMinus_image);
        femalePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_femalePlus_image);

        maleCount = (TextView)getActivity().findViewById(R.id.createEvent1_maleCount_text);
        femaleCount = (TextView)getActivity().findViewById(R.id.createEvent1_femaleCount_text);
        maleNum = Integer.parseInt(maleCount.getText().toString());
        femaleNum = Integer.parseInt(femaleCount.getText().toString());
    }

    private void setupOnClicks()
    {
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
                editPrice.setText("");
                editPrice.setFocusableInTouchMode(true);
                editPrice.setClickable(true);
                editPrice.setBackgroundColor(Color.WHITE);
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
                editPrice.setText("0.00");
                editPrice.setFocusableInTouchMode(false);
                editPrice.setClickable(false);
                editPrice.setBackgroundColor(Color.LTGRAY);
            }
        });

        maleMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (maleNum > 0)
                {
                    maleNum--;
                    maleCount.setText(maleNum + "");
                }
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
                if (femaleNum > 0)
                {
                    femaleNum--;
                    femaleCount.setText(femaleNum + "");
                }
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

        editStartDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startendDateCheck = 1;
                new DatePickerDialog(getContext(), dateListener,
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editEndDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startendDateCheck = 2;
                new DatePickerDialog(getContext(), dateListener,
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startendTimeCheck = 1;
                new TimePickerDialog(getContext(), timeListener,
                        startCalendar.get(Calendar.HOUR), startCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startendTimeCheck = 2;
                new TimePickerDialog(getContext(), timeListener,
                        endCalendar.get(Calendar.HOUR), endCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        editLocation.setOnFocusChangeListener(new View.OnFocusChangeListener()
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

        editPrice.setOnFocusChangeListener(new View.OnFocusChangeListener()
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
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d)
        {
            if (startendDateCheck == 1)
            {
                startCalendar.set(Calendar.YEAR, y);
                startCalendar.set(Calendar.MONTH, m);
                startCalendar.set(Calendar.DAY_OF_MONTH, d);
                editStartDate.setText( UtilityClass.dateToString(startCalendar) );
            }
            else if (startendDateCheck == 2)
            {
                endCalendar.set(Calendar.YEAR, y);
                endCalendar.set(Calendar.MONTH, m);
                endCalendar.set(Calendar.DAY_OF_MONTH, d);
                editEndDate.setText( UtilityClass.dateToString(endCalendar) );
            }
        }
    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker timePicker, int hr, int min)
        {
            if (startendTimeCheck == 1)
            {
                startCalendar.set(Calendar.HOUR, hr);
                startCalendar.set(Calendar.MINUTE, min);
                editStartTime.setText( UtilityClass.timeToString( startCalendar ) );
            }
            else if (startendTimeCheck == 2)
            {
                endCalendar.set(Calendar.HOUR, hr);
                endCalendar.set(Calendar.MINUTE, min);
                editEndTime.setText( UtilityClass.timeToString( endCalendar ) );
            }
        }
    };

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
                if ( editStartDate.getText().toString().isEmpty() ||
                        editStartTime.getText().toString().isEmpty() ||
                        editEndDate.getText().toString().isEmpty() ||
                        editEndTime.getText().toString().isEmpty() ||
                        editLocation.getText().toString().isEmpty() ||
                        editPrice.getText().toString().isEmpty() )
                {
                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(getActivity());
                    fieldAlert.setMessage("Please enter all the party information.")
                            .setCancelable(true)
                            .setIcon(R.drawable.alert_symbol)
                            .show();
                }
                else if ( endCalendar.compareTo( startCalendar ) < 0 )
                {
                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(getActivity());
                    fieldAlert.setMessage("Your ending time should come after your starting time.")
                            .setCancelable(true)
                            .setIcon(R.drawable.alert_symbol)
                            .show();
                }
                else
                {
                    ((CreateEventActivity)getActivity())
                            .savePartyInfo(startCalendar,
                                    endCalendar,
                                    editLocation.getText().toString(),
                                    privateText.getTextColors().equals(Color.WHITE),
                                    paidText.getTextColors().equals(Color.WHITE),
                                    Double.parseDouble(editPrice.getText().toString()),
                                    maleNum,
                                    femaleNum );
                    FragmentManager fragM = getActivity().getSupportFragmentManager();
                    fragM.beginTransaction()
                            .replace(R.id.createEvent_fragment_container, new CreateEvent2Fragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    private void askToSave()
    {
        AlertDialog.Builder confirmMessage = new AlertDialog.Builder(getActivity());
        confirmMessage.setTitle("Unsaved Data")
                .setMessage("Are you sure you want to discard the changes?")
                .setCancelable(false)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1)
                        {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing
                    }
                })
                .show();
    }
}
