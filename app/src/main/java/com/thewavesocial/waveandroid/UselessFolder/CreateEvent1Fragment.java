//package com.thewavesocial.waveandroid.HostFolder;
//
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.TimePicker;
//
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.UtilityClass;
//
//import java.util.Calendar;
//
//public class CreateEvent1Fragment extends Fragment
//{
//    private TextView privateText, publicText, paidText, freeText, editMaleCount,
//            editFemaleCount, editStartDate, editStartTime, editEndDate, editEndTime, dollarSign;
//    private EditText editLocation, editPrice;
//    private ImageView maleMinus, malePlus, femaleMinus, femalePlus;
//    private int startendDateCheck, startendTimeCheck;
//    private CreateEventActivity sharedPreferencesContext;
//    private View mainView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        return inflater.inflate(R.layout.create_event_fragment1, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
//    {
//        super.onViewCreated(view, savedInstanceState);
//        sharedPreferencesContext = (CreateEventActivity)getActivity();
//        mainView = view;
//
//        setupActionBar();
//        setupReferences();
//        setupOnClicks();
//        setupInitialValues();
//    }
//
//    private void setupInitialValues()
//    {
//
//        editStartDate.setText( UtilityClass.dateToString( sharedPreferencesContext.startCalendar));
//        editEndDate.setText( UtilityClass.dateToString( sharedPreferencesContext.endCalendar));
//        editStartTime.setText( UtilityClass.timeToString( sharedPreferencesContext.startCalendar));
//        editEndTime.setText( UtilityClass.timeToString( sharedPreferencesContext.endCalendar));
//        editMaleCount.setText( sharedPreferencesContext.maleCount + "");
//        editFemaleCount.setText( sharedPreferencesContext.femaleCount + "");
//
//        if ( sharedPreferencesContext.privatePublic.equals("Private") )
//            privateText.performClick();
//        else
//            publicText.performClick();
//
//        if ( sharedPreferencesContext.paidFree.equals("Paid") )
//            paidText.performClick();
//        else
//            freeText.performClick();
//    }
//
//    private void setupReferences()
//    {
//        editStartDate = (TextView)getActivity().findViewById(R.id.createEvent1_startingDate_editText);
//        editStartTime = (TextView)getActivity().findViewById(R.id.createEvent1_startingTime_editText);
//        editEndDate = (TextView)getActivity().findViewById(R.id.createEvent1_endingDate_editText);
//        editEndTime = (TextView)getActivity().findViewById(R.id.createEvent1_endingTime_editText);
//        privateText = (TextView) getActivity().findViewById(R.id.createEvent1_private_text);
//        publicText = (TextView) getActivity().findViewById(R.id.createEvent1_public_text);
//        paidText = (TextView) getActivity().findViewById(R.id.createEvent1_paid_text);
//        freeText = (TextView) getActivity().findViewById(R.id.createEvent1_free_text);
//        dollarSign = (TextView) getActivity().findViewById(R.id.createEvent1_dollarsign);
//        editMaleCount = (TextView)getActivity().findViewById(R.id.createEvent1_maleCount_text);
//        editFemaleCount = (TextView)getActivity().findViewById(R.id.createEvent1_femaleCount_text);
//
//        editLocation = (EditText)getActivity().findViewById(R.id.createEvent1_location_editText);
//        editPrice = (EditText)getActivity().findViewById(R.id.createEvent1_price_editText);
//
//        maleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_maleMinus_image);
//        malePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_malePlus_image);
//        femaleMinus = (ImageView) getActivity().findViewById(R.id.createEvent1_femaleMinus_image);
//        femalePlus = (ImageView) getActivity().findViewById(R.id.createEvent1_femalePlus_image);
//    }
//
//    private void setupOnClicks()
//    {
//        privateText.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                privateText.setTextColor(Color.WHITE);
//                privateText.setBackgroundResource(R.color.appColor);
//                publicText.setTextColor(getResources().getColor(R.color.appColor));
//                publicText.setBackgroundColor(Color.WHITE);
//                sharedPreferencesContext.privatePublic = "Private";
//            }
//        });
//
//        publicText.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                publicText.setTextColor(Color.WHITE);
//                publicText.setBackgroundResource(R.color.appColor);
//                privateText.setTextColor(getResources().getColor(R.color.appColor));
//                privateText.setBackgroundColor(Color.WHITE);
//                sharedPreferencesContext.privatePublic = "Public";
//            }
//        });
//
//        paidText.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                paidText.setTextColor(Color.WHITE);
//                paidText.setBackgroundResource(R.color.appColor);
//                freeText.setTextColor(getResources().getColor(R.color.appColor));
//                freeText.setBackgroundColor(Color.WHITE);
//                editPrice.setText("");
//                editPrice.setFocusableInTouchMode(true);
//                editPrice.setClickable(true);
//                editPrice.setBackgroundColor(Color.WHITE);
//                dollarSign.setBackgroundColor(Color.WHITE);
//                sharedPreferencesContext.paidFree = "Paid";
//            }
//        });
//
//        freeText.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                freeText.setTextColor(Color.WHITE);
//                freeText.setBackgroundResource(R.color.appColor);
//                paidText.setTextColor(getResources().getColor(R.color.appColor));
//                paidText.setBackgroundColor(Color.WHITE);
//                editPrice.setText("0.00");
//                editPrice.setFocusableInTouchMode(false);
//                editPrice.setClickable(false);
//                editPrice.clearFocus();
//                editPrice.setBackgroundColor(Color.LTGRAY);
//                dollarSign.setBackgroundColor(Color.LTGRAY);
//                sharedPreferencesContext.paidFree = "Free";
//            }
//        });
//
//        maleMinus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if (sharedPreferencesContext.maleCount > 0)
//                {
//                    sharedPreferencesContext.maleCount--;
//                    editMaleCount.setText(sharedPreferencesContext.maleCount + "");
//                }
//            }
//        });
//
//        malePlus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                sharedPreferencesContext.maleCount++;
//                editMaleCount.setText(sharedPreferencesContext.maleCount+"");
//            }
//        });
//
//        femaleMinus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if (sharedPreferencesContext.femaleCount > 0)
//                {
//                    sharedPreferencesContext.femaleCount--;
//                    editFemaleCount.setText(sharedPreferencesContext.femaleCount + "");
//                }
//            }
//        });
//
//        femalePlus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                sharedPreferencesContext.femaleCount++;
//                editFemaleCount.setText(sharedPreferencesContext.femaleCount+"");
//            }
//        });
//
//        editStartDate.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startendDateCheck = 1;
//                new DatePickerDialog(getContext(), dateListener,
//                        sharedPreferencesContext.startCalendar.get(Calendar.YEAR),
//                        sharedPreferencesContext.startCalendar.get(Calendar.MONTH),
//                        sharedPreferencesContext.startCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//
//        editEndDate.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startendDateCheck = 2;
//                new DatePickerDialog(getContext(), dateListener,
//                        sharedPreferencesContext.endCalendar.get(Calendar.YEAR),
//                        sharedPreferencesContext.endCalendar.get(Calendar.MONTH),
//                        sharedPreferencesContext.endCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//
//        editStartTime.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startendTimeCheck = 1;
//                new TimePickerDialog(getContext(), timeListener,
//                        sharedPreferencesContext.startCalendar.get(Calendar.HOUR),
//                        sharedPreferencesContext.startCalendar.get(Calendar.MINUTE), true).show();
//            }
//        });
//
//        editEndTime.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startendTimeCheck = 2;
//                new TimePickerDialog(getContext(), timeListener,
//                        sharedPreferencesContext.endCalendar.get(Calendar.HOUR),
//                        sharedPreferencesContext.endCalendar.get(Calendar.MINUTE), true).show();
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
//    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener()
//    {
//        @Override
//        public void onDateSet(DatePicker view, int y, int m, int d)
//        {
//            if (startendDateCheck == 1)
//            {
//                sharedPreferencesContext.startCalendar.set(Calendar.YEAR, y);
//                sharedPreferencesContext.startCalendar.set(Calendar.MONTH, m);
//                sharedPreferencesContext.startCalendar.set(Calendar.DAY_OF_MONTH, d);
//                editStartDate.setText( UtilityClass.dateToString(sharedPreferencesContext.startCalendar) );
//            }
//            else if (startendDateCheck == 2)
//            {
//                sharedPreferencesContext.endCalendar.set(Calendar.YEAR, y);
//                sharedPreferencesContext.endCalendar.set(Calendar.MONTH, m);
//                sharedPreferencesContext.endCalendar.set(Calendar.DAY_OF_MONTH, d);
//                editEndDate.setText( UtilityClass.dateToString(sharedPreferencesContext.endCalendar) );
//            }
//        }
//    };
//
//    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener()
//    {
//        @Override
//        public void onTimeSet(TimePicker timePicker, int hr, int min)
//        {
//            if (startendTimeCheck == 1)
//                {
//                sharedPreferencesContext.startCalendar.set(Calendar.HOUR, hr);
//                sharedPreferencesContext.startCalendar.set(Calendar.MINUTE, min);
//                editStartTime.setText( UtilityClass.timeToString( sharedPreferencesContext.startCalendar ) );
//            }
//            else if (startendTimeCheck == 2)
//            {
//                sharedPreferencesContext.endCalendar.set(Calendar.HOUR, hr);
//                sharedPreferencesContext.endCalendar.set(Calendar.MINUTE, min);
//                editEndTime.setText( UtilityClass.timeToString( sharedPreferencesContext.endCalendar ) );
//            }
//        }
//    };
//
//    private void setupActionBar()
//    {
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_create_event1);
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setTitle("");
//        ((CreateEventActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        ImageView forwardButton = (ImageView) getActivity().findViewById(R.id.actionbar_createEvent1_image);
//        forwardButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                //LatLng latlng = UtilityClass.getLocationFromAddress(getActivity(), editLocation.getText().toString());
//                if ( editStartDate.getText().toString().isEmpty() ||
//                        editStartTime.getText().toString().isEmpty() ||
//                        editEndDate.getText().toString().isEmpty() ||
//                        editEndTime.getText().toString().isEmpty() ||
//                        editLocation.getText().toString().isEmpty() ||
//                        editPrice.getText().toString().isEmpty() )
//                {
//                    UtilityClass.printAlertMessage(sharedPreferencesContext, "Please enter all the party information.", "Error Creating Party", true);
//                }
//                else if ( sharedPreferencesContext.endCalendar.compareTo( sharedPreferencesContext.startCalendar ) < 0 )
//                {
//                    UtilityClass.printAlertMessage(sharedPreferencesContext, "Your ending time should come after your starting time.","Error Creating Party", true);
//                }
//                else if ( sharedPreferencesContext.maleCount + sharedPreferencesContext.femaleCount == 0 )
//                {
//                    UtilityClass.printAlertMessage(sharedPreferencesContext, "Please specify the numbers of males and females", "Error Creating Party",true);
//                }
////                else if ( latlng == null )
////                {
////                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(getActivity());
////                    fieldAlert.setMessage("Please specify a valid address.")
////                            .setCancelable(true)
////                            .show();
////                UtilityClass.printAlertMessage(sharedPreferencesContext, "Please specify a valid address.", true);
////                    Log.d("Nope", "Invalid");
////                }
//                else
//                {
////                    Log.d("Cool", latlng.toString() + " " + editLocation.getText().toString());
////                    sharedPreferencesContext.location = new MapAddress( editLocation.getText().toString(), latlng );
//                    sharedPreferencesContext.price = Double.parseDouble(editPrice.getText().toString());
//                    FragmentManager fragM = sharedPreferencesContext.getSupportFragmentManager();
//                    fragM.beginTransaction()
//                            .replace(R.id.createEvent_fragment_container, new CreateEvent2Fragment())
//                            .addToBackStack(null)
//                            .commit();
//                }
//            }
//        });
//    }
//}
