package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class EditStatsActivity extends AppCompatActivity {
    private Activity mainActivity;
    private TextView deleteButton;
    TextView startDateTextView, startTimeTextView, endDateTextView, endTimeTextView;
    EditText titleEditText, locationEditText;
    EmojiconEditText emojiconEditText;
    EmojiconsPopup popup;
    SwitchCompat privateSwitch;
    boolean privateParty = false;
    RangeSeekBar<Integer> rangeSeekBar;
    Integer RANGE_AGE_MIN = 17;
    Integer RANGE_AGE_MAX = 40;
    Integer RANGE_AGE_SELECTED_MIN = 17;
    Integer RANGE_AGE_SELECTED_MAX = 30;

    //Activity thisActivity = this;
    static Calendar startCalendar = Calendar.getInstance();
    static Calendar endCalendar = Calendar.getInstance();
    String DATE_FORMAT = "MMM d, yyyy";
    String TIME_FORMAT = "h:mm a";
    String CALLING_CLASS = "EditEvent";

    RecyclerView invitedRecyclerView, bouncingRecylcerView;

    static Party party;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats_edit);
        mainActivity = this;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.editEventEmojiRelativeLayout);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                UtilityClass.hideKeyboard(getActivity());
                popup.dismiss();
                return true;
            }
        });
        Intent intent = getIntent();
        party = intent.getExtras().getParcelable("partyObject");
        NewPartyInfo.initialize();
        startCalendar = NewPartyInfo.startingDateTime;
        endCalendar = NewPartyInfo.endingDateTime;

        setupActionbar();
        setupReference();
        setUpEmojicon();
        setupFunctionality();
    }

    private void setupFunctionality() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertMessage = new AlertDialog.Builder(mainActivity);
                alertMessage.setTitle("Warning")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mainActivity, "Todo: Delete this party from all attendees.", Toast.LENGTH_LONG).show();
                            }})
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .setCancelable(true)
                        .show();
                // TODO: 04/20/2017 Remove party from server 
                // TODO: 04/20/2017 Notify all users 
                // TODO: 04/20/2017 Back to hostFragment
            }
        });
    }

    private void setupReference() {
        deleteButton = (TextView) findViewById(R.id.delete_button);

        titleEditText = (EditText)findViewById(R.id.editEventEventTitleEditText);
        titleEditText.setText(party.getName());
        titleEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popup.isShowing()){
                    popup.dismiss();
                }
                return false;
            }
        });

        locationEditText = (EditText)findViewById(R.id.editEventLocationEditText);
        locationEditText.setText(party.getMapAddress().getAddress_string());
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popup.isShowing()){
                    popup.dismiss();
                }
                return false;
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

        startDateTextView = (TextView) findViewById(R.id.editEventStartDateTextView);
        startDateTextView.setText(dateFormat.format(party.getStartingDateTime().getTime()));
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                dialogFragment.setDateDisplay(startDateTextView);
                dialogFragment.show(mainActivity.getFragmentManager(), "datePicker");
            }
        });

        startTimeTextView = (TextView) findViewById(R.id.editEventStartTimeTextView);
        startTimeTextView.setText(timeFormat.format(party.getStartingDateTime().getTime()));
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(startCalendar.get(Calendar.HOUR),
                        startCalendar.get(Calendar.MINUTE), TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                timePickerDialogFragment.setTimeTextView(startTimeTextView);
                timePickerDialogFragment.show(mainActivity.getFragmentManager(), "timePicker");
            }
        });

        endDateTextView = (TextView) findViewById(R.id.editEventEndDateTextView);
        endDateTextView.setText(dateFormat.format(party.getEndingDateTime().getTime()));
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                dialogFragment.setDateDisplay(endDateTextView);
                dialogFragment.show(mainActivity.getFragmentManager(), "datePicker");
            }
        });

        endTimeTextView = (TextView) findViewById(R.id.editEventEndTimeTextView);
        endTimeTextView.setText(timeFormat.format(party.getEndingDateTime().getTime()));
        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(endCalendar.get(Calendar.HOUR),
                        endCalendar.get(Calendar.MINUTE), TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                timePickerDialogFragment.setTimeTextView(endTimeTextView);
                timePickerDialogFragment.show(mainActivity.getFragmentManager(), "timePicker");
            }
        });

        emojiconEditText = (EmojiconEditText) findViewById(R.id.editEventEmojiconEditText);
        emojiconEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emojiconEditText.setText(party.getPartyEmoji());
        emojiconEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!popup.isShowing()) {
                    if(popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                    }
                    else {

                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                return true;
            }
        });


        emojiconEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && popup.isShowing()){
                    popup.dismiss();
                }
            }
        });

        setUpEmojicon();


        privateSwitch = (SwitchCompat) findViewById(R.id.editEventPrivateSwitch);
        boolean isPrivate = !party.getIsPublic();
        if(isPrivate){
            privateSwitch.setChecked(true);
        }
        else{
            privateSwitch.setChecked(false);
        }
        rangeSeekBar = (RangeSeekBar<Integer>) findViewById(R.id.editEventAgeRestrictionSeekBar);
        rangeSeekBar.setRangeValues(RANGE_AGE_MIN, RANGE_AGE_MAX);
        rangeSeekBar.setSelectedMinValue(party.getMinAge());
        rangeSeekBar.setSelectedMaxValue(party.getMaxAge());

        invitedRecyclerView = (RecyclerView) findViewById(R.id.invite_list);
        bouncingRecylcerView = (RecyclerView) findViewById(R.id.bouncing_list);

        LinearLayoutManager invitedListManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        invitedRecyclerView.setLayoutManager(invitedListManager);

        LinearLayoutManager bouncingListManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        bouncingRecylcerView.setLayoutManager(bouncingListManager);

        //TODO: Call Server function instead
        List<User> invitedUsers = new ArrayList<User>();
        invitedUsers.addAll(CurrentUser.getUsersListObjects(party.getAttendingUsers()));
        invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, invitedUsers));

        List<User> bouncingUsers = new ArrayList<User>();
        bouncingUsers.addAll(CurrentUser.getUsersListObjects(party.getBouncingUsers()));
        bouncingRecylcerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, bouncingUsers));
    }

    private void setUpEmojicon(){
        final View rootView = findViewById(R.id.scrollViewEditEvent);
        popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }
                else{
                    emojiconEditText.setText(emojicon.getEmoji());
                }
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });
    }

    private void setupActionbar() {
        getSupportActionBar().setCustomView(R.layout.actionbar_hoststats_edit);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        TextView cancelButton = (TextView) findViewById(R.id.actionbar_hoststats_edit_cancel);
        TextView doneButton = (TextView) findViewById(R.id.actionbar_hoststats_edit_done);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmMessage = new AlertDialog.Builder(mainActivity);
                confirmMessage.setTitle("Unsaved Data")
                        .setMessage("Are you sure you want to discard the changes?")
                        .setCancelable(false)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 04/20/2017 Update server: update party object
                if (checkInfo()) {
                    savePage();
                    NewPartyInfo.composeParty();
                    onBackPressed();
                }
            }
        });
    }

    private boolean checkInfo(){
        //Log.d("Address", locationEditText.getText().toString());
        //Log.d("Address", UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) + "");
        if(emojiconEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please select an Emoji for the event ", true);
            return false;
        }
        else if(titleEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please enter an Event Title", true);
            return false;
        }
        else if(locationEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please select an Event Location", true);
            return false;
        }
        else if(startCalendar.compareTo(endCalendar) >= 0){
            Log.d("Date", startCalendar.get(Calendar.DATE) + ", " + endCalendar.get(Calendar.DATE) + "");
            UtilityClass.printAlertMessage(getActivity(), "The event start date must be before the end date", true);
            return false;
        }
        else if(UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) == null){
            UtilityClass.printAlertMessage(getActivity(), "Please enter a valid address", true);
            return false;
        }
        else{
            return true;
        }
    }




    //ONLY called if all checks are passed
    public void savePage() {
        NewPartyInfo.name = titleEditText.getText().toString();
        NewPartyInfo.price = 0;
        NewPartyInfo.hostName = CurrentUser.theUser.getFullName();
        NewPartyInfo.startingDateTime = startCalendar;
        NewPartyInfo.endingDateTime = endCalendar;
        String partyAddress = locationEditText.getText().toString();
        NewPartyInfo.mapAddress = new MapAddress(partyAddress,
                UtilityClass.getLocationFromAddress(getActivity(), partyAddress));
        NewPartyInfo.isPublic = !privateParty;
        NewPartyInfo.partyEmoji = emojiconEditText.getText().toString(); //TODO: 4/22/17 Replace with actual chose emoji
        NewPartyInfo.minAge = rangeSeekBar.getSelectedMinValue();
        NewPartyInfo.maxAge = rangeSeekBar.getSelectedMaxValue();
        //getActivity().finish();
    }

    private Activity getActivity() {
        return this;
    }

    private static class NewPartyInfo {
        static String name;
        static double price;
        static String hostName;
        static Calendar startingDateTime;
        static Calendar endingDateTime;
        static MapAddress mapAddress;
        static List<Long> hostingUsers;
        static List<Long> bouncingUsers;
        static List<Long> attendingUsers;
        static boolean isPublic;
        static String partyEmoji;
        static int minAge;
        static int maxAge;
        public static void initialize() {
            name = party.getName();
            price = party.getPrice();
            hostName = party.getHostName();
            startingDateTime = party.getStartingDateTime();
            endingDateTime = party.getEndingDateTime();
            hostingUsers = party.getHostingUsers();
            bouncingUsers = party.getBouncingUsers();
            attendingUsers = party.getAttendingUsers();
            isPublic = party.getIsPublic();
            partyEmoji = party.getPartyEmoji();
            minAge = party.getMinAge();
            maxAge = party.getMaxAge();
        }
        public static void composeParty(){
//            Party party = new Party(
//                    0, name, price, hostName, startingDateTime, endingDateTime, mapAddress,
//                    hostingUsers, bouncingUsers, attendingUsers, isPublic, partyEmoji, minAge, maxAge);
            // TODO: 03/31/2017 Send to database and create new party with new ID
            // TODO: 04/23/2017 Refresh hosting event list
        }
    }
}