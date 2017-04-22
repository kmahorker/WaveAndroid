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


    private int hour;
    private int minute;
    private SimpleDateFormat timeFormat;
    private TextView timeTextView = null; //Use SetTextView in parent class before calling
    private Calendar calendar;
    private int alertDialogStyle;


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
    public static TimePickerDialogFragment newInstance(int hour, int min, String timeFormat, int alertDialogStyle) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR, hour);
        args.putInt(ARG_MINUTE, min);
        args.putString(ARG_TIME_FORMAT, timeFormat);
        args.putInt(ARG_STYLE, alertDialogStyle);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        calendar.set(Calendar.HOUR,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        timeTextView.setText(timeFormat.format(CreateAnEventActivity.CreateEventPage1.startCalendar.getTime()));
        if(timeTextView.equals(getActivity().findViewById(R.id.startTimeTextView))){
            CreateAnEventActivity.CreateEventPage1.startCalendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
            CreateAnEventActivity.CreateEventPage1.startCalendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
        }
        else if(timeTextView.equals(getActivity().findViewById(R.id.endTimeTextView))){
            CreateAnEventActivity.CreateEventPage1.endCalendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
            CreateAnEventActivity.CreateEventPage1.endCalendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
        }
        else{
            Log.d("V", "Neither");
        }

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
