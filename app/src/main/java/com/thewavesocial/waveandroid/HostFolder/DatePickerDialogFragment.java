package com.thewavesocial.waveandroid.HostFolder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatePickerDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "day";
    private static final String ARG_PARAM2 = "month";
    private static final String ARG_PARAM3 = "year";
    private static final String ARG_PARAM4 = "dateDisplayID";
    private static final String ARG_PARAM5 = "dateFormatString";
    private static final String ARG_PARAM6 = "alertDialogStyle";
    private static final String ARG_PARAM7 = "callingClass";
    private static final String ARG_SECONDS_SINCE_EPOCH = "secondsSinceEpoch";


    // TODO: Rename and change types of parameters
    private int day;
    private int month;
    private int year;
    //private int dateDisplayInt;
    private TextView dateDisplay = null; //Use SetTextView in ParentActivity
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private int alertDialogStyle;
    private int callingClass;
    private long secondsSinceEpoch;

    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment DatePickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatePickerDialogFragment newInstance(long secondsSinceEpoch, String dateFormat, int alertDialogStyle, String callingClass) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM1, day);
//        args.putInt(ARG_PARAM2, month);
//        args.putInt(ARG_PARAM3, year);
        //args.putInt(ARG_PARAM4, dateDisplay);
        args.putLong(ARG_SECONDS_SINCE_EPOCH, secondsSinceEpoch);
        args.putString(ARG_PARAM5, dateFormat);
        args.putInt(ARG_PARAM6, alertDialogStyle);
        args.putString(ARG_PARAM7, callingClass);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            day = getArguments().getInt(ARG_PARAM1);
//            month = getArguments().getInt(ARG_PARAM2);
//            year = getArguments().getInt(ARG_PARAM3);
            //dateDisplayInt = getArguments().getInt(ARG_PARAM4);
            secondsSinceEpoch = getArguments().getLong(ARG_SECONDS_SINCE_EPOCH);
            dateFormat = new SimpleDateFormat(getArguments().getString(ARG_PARAM5));
            alertDialogStyle = getArguments().getInt(ARG_PARAM6);
            calendar = UtilityClass.epochToCalendar(secondsSinceEpoch);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            String theClass = getArguments().getString(ARG_PARAM7);
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
        //View view = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);
        //dateDisplay = (TextView) view.findViewById(dateDisplayInt);
        return super.onCreateView(inflater, container, savedInstanceState);//view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = month + 1;
        this.year = year;
        calendar = UtilityClass.epochToCalendar(secondsSinceEpoch);
        calendar.set(year, month, day);
        dateDisplay.setText(dateFormat.format(calendar.getTime()));
        //Intent intent = new Intent(getActivity(), CreateAnEventActivity.class);
        //if(CreateAnEventActivity.class.isInstance(t)){

        switch (callingClass) {
            case 1:
                if (dateDisplay.equals(getActivity().findViewById(R.id.startDateTextView))) {
                    CreateAnEventActivity.CreateEventPage1.startCalendar = UtilityClass.calendarToEpoch(calendar);
//                    set(calendar.get(Calendar.YEAR),
//                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                            CreateAnEventActivity.CreateEventPage1.startCalendar.get(Calendar.HOUR),
//                            CreateAnEventActivity.CreateEventPage1.startCalendar.get(Calendar.MINUTE));
                } else if (dateDisplay.equals(getActivity().findViewById((R.id.endDateTextView)))) {
                    CreateAnEventActivity.CreateEventPage1.endCalendar = UtilityClass.calendarToEpoch(calendar);
//                            set(calendar.get(Calendar.YEAR),
//                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                            CreateAnEventActivity.CreateEventPage1.endCalendar.get(Calendar.HOUR),
//                            CreateAnEventActivity.CreateEventPage1.endCalendar.get(Calendar.MINUTE));
                }
                break;
            case 2:
                if (dateDisplay.equals(getActivity().findViewById(R.id.editEventStartDateTextView))) {
                    EditStatsActivity.startCalendar = UtilityClass.calendarToEpoch(calendar);
//                    set(calendar.get(Calendar.YEAR),
//                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                            EditStatsActivity.startCalendar.get(Calendar.HOUR),
//                            EditStatsActivity.startCalendar.get(Calendar.MINUTE));
                } else if (dateDisplay.equals(getActivity().findViewById((R.id.editEventEndDateTextView)))) {
                    EditStatsActivity.endCalendar = UtilityClass.calendarToEpoch(calendar);
//                    set(calendar.get(Calendar.YEAR),
//                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                            EditStatsActivity.endCalendar.get(Calendar.HOUR),
//                            EditStatsActivity.endCalendar.get(Calendar.MINUTE));
                }
                break;
            default:
                Log.d("V", "Neither");
                break;
        }

            /*if(calendar.compareTo(CreateAnEventActivity.CreateEventPage1.endCalendar) >= 0){
                CreateAnEventActivity.CreateEventPage1.endCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        CreateAnEventActivity.CreateEventPage1.startCalendar.get(Calendar.HOUR)+1,
                        CreateAnEventActivity.CreateEventPage1.startCalendar.get(Calendar.MINUTE));//set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }*/
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), alertDialogStyle, this, year, month, day);
        //View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_date_picker_dialog, mContainer, false);
        //datePickerDialog.setView(view);
        return datePickerDialog;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public TextView getDateDisplay() {
        return dateDisplay;
    }

    public void setDateDisplay(TextView dateDisplay) {
        this.dateDisplay = dateDisplay;
    }

}
