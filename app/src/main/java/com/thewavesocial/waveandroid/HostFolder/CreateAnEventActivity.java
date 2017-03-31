package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

import java.util.Date;

public class CreateAnEventActivity extends AppCompatActivity {

    TextView cancelTextView, startDateTextView, startTimeTextView, endDateTextView, endTimeTextView;
    EditText titleEditText, locationEditText;
    Switch privateSwitch;
    org.florescu.android.rangeseekbar.RangeSeekBar rangeSeekBar;
    Activity thisActivity = this;
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();
    private static final String DATE_FORMAT = "MMM d, YYYY";
    private static final String TIME_FORMAT = "h:mm a";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_event);
        setUpActionBar();
        setUpTextViews();
        setupEditText();

    }

    private void setUpActionBar(){
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_create_an_event);
    }

    private void setUpTextViews(){
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity,  HostControllerFragment.class);
                startActivity(intent);
            }
        });

        startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        startDateTextView.setText(dateFormat.format(startCalendar.getTime()));
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Holo_Light_Dialog);
                dialogFragment.setDateDisplay(startDateTextView);
                dialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        startTimeTextView.setText(timeFormat.format(startCalendar.getTime()));

        endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        endDateTextView.setText(dateFormat.format(endCalendar.getTime()));
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Holo_Light_Dialog);
                dialogFragment.setDateDisplay(endDateTextView);
                dialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(new Date());
        tempCalendar.add(Calendar.HOUR, 1);
        endTimeTextView.setText(timeFormat.format(tempCalendar.getTime()));


    }

    private void setupEditText(){
        titleEditText = (EditText) findViewById(R.id.eventTitleEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }
}
