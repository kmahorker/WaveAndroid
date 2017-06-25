package com.thewavesocial.waveandroid.LoginFolder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

import java.util.Calendar;

public class BirthdayDatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView dateDisplay;
    int day, month, year;

    //static BirthdayDatePickerDialogFragment fragment;
    public BirthdayDatePickerDialogFragment() {
        dateDisplay = null;
        day = 0;
        month = 0;
        year = 0;
    }

    public static BirthdayDatePickerDialogFragment newInstance(TextView text)

    {
        BirthdayDatePickerDialogFragment fragment = new BirthdayDatePickerDialogFragment();
        //Bundle args = new Bundle();
        //args.putParcelable("editText", (Parcelable) text);
        //fragment.setArguments(args);
        fragment.setDateDisplay(text);
        final Calendar c = Calendar.getInstance();
        fragment.setYear(c.get(Calendar.YEAR));
        fragment.setMonth(c.get(Calendar.MONTH));
        fragment.setDay(c.get(Calendar.DAY_OF_MONTH));

        //dateDisplay = (fragment.getArguments().getParcelable("editText"));
        fragment.getDateDisplay().setText("" + (fragment.getMonth() + 1) + "/" + fragment.getDay() + "/" + fragment.getYear());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the current date as the default date in the picker

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.BirthdayDatePickerDialogTheme, this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.day = day;
        this.month = month;
        this.year = year;
        dateDisplay.setText("" + (month + 1) + "/" + day + "/" + year);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_birthday_date_picker_dialog, container, false);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public TextView getDateDisplay() {
        return dateDisplay;
    }

    public void setDateDisplay(TextView text) {
        dateDisplay = text;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
