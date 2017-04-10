package com.thewavesocial.waveandroid.HostFolder;


import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import java.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;

import java.util.Date;

public class CreateAnEventActivity extends AppCompatActivity {
    private static final int currentPage = 1; //1, 2, 3
    private TextView cancel, title;
    private ImageView back, forward;
    private FragmentManager fragmentM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_layout);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        fragmentM = getSupportFragmentManager();
        openFirstPage();

    }

    private void openFirstPage() {
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage1()).commit();

        //actionbar settings
        getSupportActionBar().setCustomView(R.layout.actionbar_create_an_event);
        forward = (ImageView) findViewById(R.id.imageView);
        cancel = (TextView) findViewById(R.id.cancelTextView);
        title = (TextView) findViewById(R.id.newEvent);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondPage();
            }
        });
        forward.setVisibility(View.VISIBLE);
        forward.setClickable(true);
        title.setText("New Event");
    }

    private void openSecondPage() {
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage2());

        //actionbar settings
        getSupportActionBar().setCustomView(R.layout.actionbar_create_event_invite);
        back = (ImageView) findViewById(R.id.actionbar_createEvent_invite_back);
        forward = (ImageView) findViewById(R.id.actionbar_createEvent_invite_forward);
        title = (TextView) findViewById(R.id.actionbar_createEvent_invite_title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFirstPage();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThirdPage();
            }
        });
        forward.setVisibility(View.VISIBLE);
        forward.setClickable(true);
        title.setText("Invite Friends");
    }

    private void openThirdPage() {
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage3());

        //actionbar view is already set up when opening second page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondPage();
            }
        });
        forward.setVisibility(View.INVISIBLE);
        forward.setClickable(false);
        title.setText("Invite Bouncers");
    }



    public static class CreateEventPage1 extends Fragment {
        TextView cancelTextView, startDateTextView, startTimeTextView, endDateTextView, endTimeTextView;
        EditText titleEditText, locationEditText;
        Switch privateSwitch;
        org.florescu.android.rangeseekbar.RangeSeekBar rangeSeekBar;
        //Activity thisActivity = this;
        static Calendar startCalendar = Calendar.getInstance();
        static Calendar endCalendar = Calendar.getInstance();
        String DATE_FORMAT = "MMM d, YYYY";
        String TIME_FORMAT = "h:mm a";


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_create_an_event, container, false);
            Log.d("V", "OncreateView");
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setUpTextViews(view);
            setupEditText(view);
            Log.d("V", "OnViewCreated");
        }
        private void setUpTextViews(View v){
            startDateTextView = (TextView) v.findViewById(R.id.startDateTextView);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            startDateTextView.setText(dateFormat.format(startCalendar.getTime()));
            startDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(startCalendar.get(Calendar.DAY_OF_MONTH),
                            startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Holo_Light_Dialog);
                    dialogFragment.setDateDisplay(startDateTextView);
                    dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            startTimeTextView = (TextView) v.findViewById(R.id.startTimeTextView);
            SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
            startTimeTextView.setText(timeFormat.format(startCalendar.getTime()));

            endDateTextView = (TextView) v.findViewById(R.id.endDateTextView);
            endDateTextView.setText(dateFormat.format(endCalendar.getTime()));
            endDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(endCalendar.get(Calendar.DAY_OF_MONTH),
                            endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Holo_Light_Dialog);
                    dialogFragment.setDateDisplay(endDateTextView);
                    dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            endTimeTextView = (TextView) v.findViewById(R.id.endTimeTextView);
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(new Date());
            tempCalendar.add(Calendar.HOUR, 1);
            endTimeTextView.setText(timeFormat.format(tempCalendar.getTime()));
        }

        private void setupEditText(View v){
            titleEditText = (EditText) v.findViewById(R.id.eventTitleEditText);
            locationEditText = (EditText) v.findViewById(R.id.locationEditText);

        }



    }

    public static class CreateEventPage2 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.create_event_invite, container, false);

        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }
    }

    public static class CreateEventPage3 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.create_event_invite, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }
    }
}
