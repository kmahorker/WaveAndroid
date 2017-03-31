package com.thewavesocial.waveandroid.HostFolder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

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

    // TODO: Rename and change types of parameters
    private int day;
    private int month;
    private int year;
    //private int dateDisplayInt;
    private TextView dateDisplay = null; //Use SetTextView in ParentActivity
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private int alertDialogStyle;


    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param day Parameter 1.
     * @param month Parameter 2.
     * @param year Parameter 3
     * @return A new instance of fragment DatePickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatePickerDialogFragment newInstance(int day, int month, int year, String dateFormat, int alertDialogStyle) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, day);
        args.putInt(ARG_PARAM2, month);
        args.putInt(ARG_PARAM3, year);
        //args.putInt(ARG_PARAM4, dateDisplay);
        args.putString(ARG_PARAM5, dateFormat);
        args.putInt(ARG_PARAM6, alertDialogStyle);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getInt(ARG_PARAM1);
            month = getArguments().getInt(ARG_PARAM2);
            year = getArguments().getInt(ARG_PARAM3);
            //dateDisplayInt = getArguments().getInt(ARG_PARAM4);
            dateFormat = new SimpleDateFormat(getArguments().getString(ARG_PARAM5));
            alertDialogStyle = getArguments().getInt(ARG_PARAM6);
            calendar = Calendar.getInstance();
            calendar.set(year, month, day);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);
        //dateDisplay = (TextView) view.findViewById(dateDisplayInt);
        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = month+1;
        this.year = year;
        calendar= Calendar.getInstance();
        calendar.set(year, month, day);
        dateDisplay.setText(dateFormat.format(calendar.getTime()));
        //Intent intent = new Intent(getActivity(), CreateAnEventActivity.class);
        //if(CreateAnEventActivity.class.isInstance(t)){
        if(dateDisplay.equals(getActivity().findViewById(R.id.startDateTextView))) {
            ((CreateAnEventActivity) getActivity()).setStartCalendar(calendar);
        }
        else if(dateDisplay.equals(getActivity().findViewById((R.id.endDateTextView)))){
            ((CreateAnEventActivity) getActivity()).setEndCalendar(calendar);
        }
        else{
            Log.d("V", "Neither");
        }
        //}

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), alertDialogStyle, this, year, month, day);
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
