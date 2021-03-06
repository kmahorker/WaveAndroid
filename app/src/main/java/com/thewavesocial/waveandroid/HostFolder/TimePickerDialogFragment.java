package com.thewavesocial.waveandroid.HostFolder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimePickerDialogFragment extends DialogFragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HOUR = "hour";
    private static final String ARG_MINUTE = "minute";
    private static final String ARG_TIME_FORMAT = "time_format";
    private static final String ARG_STYLE = "style";
    private static final String ARG_CALLING_CLASS = "callingClass";
    private static final String ARG_SECONDS_SINCE_EPOCH = "secondsSinceEpoch";


    private int hour;
    private int minute;
    private SimpleDateFormat timeFormat;
    private TextView timeTextView = null; //Use SetTextView in parent class before calling
    private Calendar calendar;
    private int alertDialogStyle;
    private int callingClass;
    private long secondsSinceEpoch;


    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param timeFormat       Format to display the time in the textView
     * @param alertDialogStyle id of the type of Style to display
     * @return A new instance of fragment TimePickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimePickerDialogFragment newInstance(long secondsSinceEpoch, String timeFormat, int alertDialogStyle, String callingClass) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_HOUR, hour);
        //args.putInt(ARG_MINUTE, min);
        args.putLong(ARG_SECONDS_SINCE_EPOCH, secondsSinceEpoch);
        args.putString(ARG_TIME_FORMAT, timeFormat);
        args.putInt(ARG_STYLE, alertDialogStyle);
        args.putString(ARG_CALLING_CLASS, callingClass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //hour = getArguments().getInt(ARG_HOUR);
            //minute = getArguments().getInt(ARG_MINUTE);
            secondsSinceEpoch = getArguments().getLong(ARG_SECONDS_SINCE_EPOCH);
            timeFormat = new SimpleDateFormat(getArguments().getString(ARG_TIME_FORMAT));
            alertDialogStyle = getArguments().getInt(ARG_STYLE);
            calendar = UtilityClass.epochToCalendar(secondsSinceEpoch);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            //calendar.setTimeInMillis(secondsSinceEpoch);
            //calendar.set(Calendar.HOUR, hour);
            //calendar.set(Calendar.MINUTE, minute);
            String theClass = getArguments().getString(ARG_CALLING_CLASS);
            if (theClass.equals("CreateAnEvent")) {
                callingClass = 1;
            } else if (theClass.equals("EditEvent")) {
                callingClass = 2;
            } else {
                callingClass = -1;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
        //inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        //timeTextView.setText(timeFormat.format(CreateAnEventActivity.CreateEventPage1.startCalendar.getTime()));
        switch (callingClass) {
            case 1:
                if (timeTextView.equals(getActivity().findViewById(R.id.startTimeTextView))) {

                    CreateAnEventActivity.CreateEventPage1.startCalendar = UtilityClass.calendarToEpoch(calendar); //set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    //CreateAnEventActivity.CreateEventPage1.startCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                } else if (timeTextView.equals(getActivity().findViewById(R.id.endTimeTextView))) {
                    CreateAnEventActivity.CreateEventPage1.endCalendar = UtilityClass.calendarToEpoch(calendar); //.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    //CreateAnEventActivity.CreateEventPage1.endCalendar = .set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                }
                break;
            case 2:
                if (timeTextView.equals(getActivity().findViewById(R.id.editEventStartTimeTextView))) {
                    EditStatsActivity.startCalendar = UtilityClass.calendarToEpoch(calendar); //.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    //EditStatsActivity.startCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                } else if (timeTextView.equals(getActivity().findViewById(R.id.editEventEndTimeTextView))) {
                    EditStatsActivity.endCalendar = UtilityClass.calendarToEpoch(calendar); //set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    //EditStatsActivity.endCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                }
                break;
            default:
                Log.d("V", "Neither");
                break;
        }

        timeTextView.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener(){
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                onTimeSet(view, hourOfDay, minute);
//            }
//        };
//        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), listener, hour, minute, false);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_time_picker_dialog, null, false);
        final TimePicker picker = (TimePicker) view.findViewById(R.id.timePicker);
        picker.setCurrentHour(hour);
        picker.setCurrentMinute(minute);
        //timePickerDialog.setView(view);
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(UtilityClass.timeToString(calendar))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onTimeSet(picker, picker.getCurrentHour(), picker.getCurrentMinute());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public void setTimeTextView(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }
}
