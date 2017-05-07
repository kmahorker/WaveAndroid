package com.thewavesocial.waveandroid.HostFolder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HOUR = "hour";
    private static final String ARG_MINUTE = "minute";
    private static final String ARG_TIME_FORMAT = "time_format";
    private static final String ARG_STYLE = "style";
    private static final String ARG_CALLING_CLASS = "callingClass";


    private int hour;
    private int minute;
    private SimpleDateFormat timeFormat;
    private TextView timeTextView = null; //Use SetTextView in parent class before calling
    private Calendar calendar;
    private int alertDialogStyle;
    private int callingClass;


    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hour Current hour value in the Calendar
     * @param min Current minute value in the Calendar
     * @param timeFormat Format to display the time in the textView
     * @param alertDialogStyle id of the type of Style to display
     * @return A new instance of fragment TimePickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimePickerDialogFragment newInstance(int hour, int min, String timeFormat, int alertDialogStyle, String callingClass) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR, hour);
        args.putInt(ARG_MINUTE, min);
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
            hour = getArguments().getInt(ARG_HOUR);
            minute = getArguments().getInt(ARG_MINUTE);
            timeFormat = new SimpleDateFormat(getArguments().getString(ARG_TIME_FORMAT));
            alertDialogStyle = getArguments().getInt(ARG_STYLE);
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR,hour);
            calendar.set(Calendar.MINUTE,minute);
            String theClass = getArguments().getString(ARG_CALLING_CLASS);
            if(theClass.equals("CreateAnEvent")){
                callingClass = 1;
            }
            else if(theClass.equals("EditEvent")){
                callingClass = 2;
            }
            else{
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
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        //timeTextView.setText(timeFormat.format(CreateAnEventActivity.CreateEventPage1.startCalendar.getTime()));
        switch (callingClass){
            case 1:
                if(timeTextView.equals(getActivity().findViewById(R.id.startTimeTextView))) {

                    CreateAnEventActivity.CreateEventPage1.startCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    CreateAnEventActivity.CreateEventPage1.startCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                }
                else if(timeTextView.equals(getActivity().findViewById(R.id.endTimeTextView))) {
                    CreateAnEventActivity.CreateEventPage1.endCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    CreateAnEventActivity.CreateEventPage1.endCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                }
                break;
                case 2:
                    if(timeTextView.equals(getActivity().findViewById(R.id.editEventStartTimeTextView))) {
                        EditStatsActivity.startCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                        EditStatsActivity.startCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                    }
                    else if(timeTextView.equals(getActivity().findViewById(R.id.editEventEndTimeTextView))) {
                        EditStatsActivity.endCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                        EditStatsActivity.endCalendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
                    }
                    break;
                default: Log.d("V", "Neither"); break;
        }

        timeTextView.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), alertDialogStyle, this, hour, minute, false);
        return timePickerDialog;
    }

    public void setTimeTextView(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }
}
