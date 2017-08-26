//package com.thewavesocial.waveandroid.HostFolder;
//
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.UtilityClass;
//
//public class CreateEvent2Fragment extends Fragment
//{
//    private EditText partyname;
//    private TextView startingDate, startingTime, endingDate, endingTime,
//            location, paidfree, privatePublic, maleCount, femaleCount, finish;
//    private CreateEventActivity sharedPreferencesContext;
//    private View mainView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        return inflater.inflate(R.layout.create_event_fragment2, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
//    {
//        super.onViewCreated(view, savedInstanceState);
//        sharedPreferencesContext = (CreateEventActivity)getActivity();
//        mainView = view;
//
//        setupActionbar();
//        setupReferences();
//        setupInitializeValues();
//        setupOnClicks();
//    }
//
//    private void setupInitializeValues()
//    {
//        startingDate.setText( UtilityClass.dateToString( sharedPreferencesContext.startCalendar ));
//        startingTime.setText( UtilityClass.timeToString( sharedPreferencesContext.startCalendar ));
//        endingDate.setText( UtilityClass.dateToString( sharedPreferencesContext.endCalendar ));
//        endingTime.setText( UtilityClass.timeToString( sharedPreferencesContext.endCalendar ));
//        location.setText( sharedPreferencesContext.location.getAddress_string() );
//        privatePublic.setText( sharedPreferencesContext.privatePublic );
//        paidfree.setText( sharedPreferencesContext.paidFree + " - $" + sharedPreferencesContext.price);
//        maleCount.setText( sharedPreferencesContext.maleCount + " Males" );
//        femaleCount.setText( sharedPreferencesContext.femaleCount + " Females");
//    }
//
//    private void setupReferences()
//    {
//        partyname = (EditText) getActivity().findViewById(R.id.createEvent2_partyname);
//        startingDate = (TextView) getActivity().findViewById(R.id.createEvent2_startingDate);
//        startingTime = (TextView) getActivity().findViewById(R.id.createEvent2_startingTime);
//        endingDate = (TextView) getActivity().findViewById(R.id.createEvent2_endingDate);
//        endingTime = (TextView) getActivity().findViewById(R.id.createEvent2_endingTime);
//        location = (TextView) getActivity().findViewById(R.id.createEvent2_location);
//        privatePublic = (TextView) getActivity().findViewById(R.id.createEvent2_private_public);
//        paidfree = (TextView) getActivity().findViewById(R.id.createEvent2_paid_free);
//        maleCount = (TextView) getActivity().findViewById(R.id.createEvent2_male_count);
//        femaleCount = (TextView) getActivity().findViewById(R.id.createEvent2_female_count);
//        finish = (TextView) getActivity().findViewById(R.id.actionbar_createEvent2_finish);
//    }
//
//    private void setupOnClicks()
//    {
//        partyname.setOnFocusChangeListener(new View.OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus)
//            {
//                if (!hasFocus)
//                {
//                    UtilityClass.hideKeyboard( sharedPreferencesContext );
//                }
//            }
//        });
//
//        finish.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if (partyname.getText().toString().isEmpty())
//                {
//                    UtilityClass .printAlertMessage(sharedPreferencesContext, "Please specify your party name.", "Error Creating Party", true);
//                }
//                else
//                {
//                    sharedPreferencesContext.name = partyname.getText().toString();
//                    sharedPreferencesContext.saveToUser();
//                    sharedPreferencesContext.getSupportFragmentManager().popBackStack();
//                    sharedPreferencesContext.getSupportFragmentManager().popBackStack();
//                    sharedPreferencesContext.onBackPressed();
//                }
//            }
//        });
//
//        mainView.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent)
//            {
//                UtilityClass.hideKeyboard(sharedPreferencesContext);
//                return true;
//            }
//        });
//    }
//
//    private void setupActionbar()
//    {
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event2);
//    }
//}
