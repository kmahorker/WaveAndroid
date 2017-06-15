package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IntegerRes;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.Attendee;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;
import static com.thewavesocial.waveandroid.HostFolder.EditListActivity.getUsersFromFollowing;

public class EditStatsActivity extends AppCompatActivity {
    private static EditStatsActivity mainActivity;
    private static int threads_completion = 0;
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
    TextView inviteTextView, bouncingTextView;

    static Party party;

    final int EDIT_BOUNCER_REQUEST = 2;
    final int EDIT_INVITE_REQUEST = 1;


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
        startCalendar = (Calendar)NewPartyInfo.startingDateTime.clone();
        endCalendar = (Calendar)NewPartyInfo.endingDateTime.clone();

        setupActionbar();
        setupReference();
        setUpEmojicon();
        setupFunctionality();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Integer> updatedListUserIds = new ArrayList<>();
        List<User> updatedList  = new ArrayList<>();
        if(resultCode == RESULT_OK) {
            updatedList = data.getExtras().getParcelableArrayList("updatedList");
            for (User user : updatedList) {
                updatedListUserIds.add(Integer.parseInt(user.getUserID()));
            }
        }
        switch (requestCode){
            case EDIT_INVITE_REQUEST:
                if(resultCode == RESULT_OK){
                    NewPartyInfo.invitingUsers = updatedListUserIds;
                    inviteTextView.setText("INVITED (" + updatedList.size() + ")");
                    invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, updatedList));
                }
                else{
                    //Do nothing
                }
                break;
            case EDIT_BOUNCER_REQUEST:
                if(resultCode == RESULT_OK){
                    NewPartyInfo.bouncingUsers = updatedListUserIds;
                    bouncingTextView.setText("BOUNCERS (" + updatedList.size() + ")");
                    bouncingRecylcerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, updatedList));
                }
                else{
                    //Do nothing
                }
                break;
        }
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
                server_deleteParty(party.getPartyID(), new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        // TODO: 04/20/2017 Remove party from server
                        // TODO: 04/20/2017 Notify all users
                        if ( !result.equals("success") ) {
                            Toast.makeText(mainActivity, "Fail to delete party.", Toast.LENGTH_LONG ).show();
                        } else {
                            Intent intent = new Intent(mainActivity, HostControllerFragment.class);
                            startActivity(intent); // TODO: 04/20/2017 Back to hostFragment
                        }
                    }
                });
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
            privateParty = true;
        }
        else{
            privateSwitch.setChecked(false);
            privateParty = false;
        }

        privateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privateParty == false){
                    privateParty = true;
                }
                else{
                    privateParty = false;
                }
                UtilityClass.hideKeyboard(getActivity());
                popup.dismiss();
            }
        });

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
//        final List<User> invitedUsers = new ArrayList<>();
//        List<String> userIds = new ArrayList<>();
//        for(Attendee a : party.getAttendingUsers()) {
//            if(a.getStatus().equals("invited")) {
//                userIds.add(a.getUserId());
//            }
//        }

        server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                List<User> invited = result.get("inviting");
                invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, invited));
                inviteTextView = (TextView)findViewById(R.id.invite_text);

                inviteTextView.setText("INVITED (" + invited.size() + ")");
                inviteTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, EditListActivity.class);
                        intent.putExtra("partyObject", party);
                        intent.putExtra("layout", 1);
                        startActivityForResult(intent, EDIT_INVITE_REQUEST);
                    }
                });
            }
        });

        // TODO: 05/28/2017 Get Invited List
//        server_getUsersListObjects(userIds, new OnResultReadyListener<List<User>>() {
//            @Override
//            public void onResultReady(List<User> result) {
//                if ( result != null ) {
//                    invitedUsers.addAll(result);
//                    invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, invitedUsers));
//                }
//            }
//        });

        server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                if ( result != null ) {

                    final List<User> bouncing = result.get("bouncing");
                    bouncingRecylcerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, bouncing));
                    bouncingTextView = (TextView)findViewById(R.id.bouncing_text);
                    bouncingTextView.setText("BOUNCERS (" + bouncing.size() + ")");
                    bouncingTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mainActivity, EditListActivity.class);
                            intent.putExtra("partyObject", party);
                            intent.putExtra("layout", 2);
                            startActivityForResult(intent, EDIT_BOUNCER_REQUEST);
                        }
                    });

                }
            }
        });
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
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private boolean checkInfo(){
        //Log.d("Address", locationEditText.getText().toString());
        //Log.d("Address", UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) + "");
        if(emojiconEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please select an Emoji for the event ", "Error Editing Party", true);
            return false;
        }
        else if(titleEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please enter an Event Title", "Error Editing Party", true);
            return false;
        }
        else if(locationEditText.getText().toString().isEmpty()){
            UtilityClass.printAlertMessage(getActivity(), "Please select an Event Location", "Error Editing Party", true);
            return false;
        }
        else if(startCalendar.compareTo(endCalendar) >= 0){
            Log.d("Date", startCalendar.get(Calendar.DATE) + ", " + endCalendar.get(Calendar.DATE) + "");
            UtilityClass.printAlertMessage(getActivity(), "The event start date must be before the end date", "Error Editing Party", true);
            return false;
        }
        else if(UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) == null){
            UtilityClass.printAlertMessage(getActivity(), "Please enter a valid address", "Error Editing Party", true);
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
        static List<String> hostingUsers;
        static List<Integer> bouncingUsers;
        static List<Integer> invitingUsers;
        static boolean isPublic;
        static String partyEmoji;
        static int minAge;
        static int maxAge;

        static List<String> originalHosting;
        static List<Integer> originalBouncing;
        static List<Integer> originalInviting;

        //Initialize party information
        public static void initialize() {
            name = party.getName();
            price = party.getPrice();
            hostName = party.getHostName();
            startingDateTime = party.getStartingDateTime();
            endingDateTime = party.getEndingDateTime();
            server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
                @Override
                public void onResultReady(HashMap<String, ArrayList<User>> result) {
                    if(result != null){
                        hostingUsers = UtilityClass.userObjectToStringId(result.get("hosting"));
                        originalHosting = hostingUsers;
                        bouncingUsers = UtilityClass.userObjectToIntegerId(result.get("bouncing"));
                        originalBouncing = bouncingUsers;
                        invitingUsers = UtilityClass.userObjectToIntegerId(result.get("inviting"));
                        originalInviting = invitingUsers;
                    }
                }
            });
//            hostingUsers = party.getHostingUsers();
//            bouncingUsers = null;
//            invitingUsers = new ArrayList<>();
            isPublic = party.getIsPublic();
            partyEmoji = party.getPartyEmoji();
            minAge = party.getMinAge();
            maxAge = party.getMaxAge();
            mapAddress = party.getMapAddress();

            //hostingUsers.add(DatabaseAccess.getTokenFromLocal(mainActivity).get("id"));
        }

        //Compose all party information
        public static void composeParty(){
            Log.d("Compose Party", "EEntered");
            if ( mapAddress.getAddress_latlng() == null )
                mapAddress.setAddress_latlng(new LatLng(0,0));
            Log.d("Both_List", bouncingUsers.toString() + "\n" + invitingUsers.toString());
            for ( int bouncer_index : bouncingUsers ){
                if ( invitingUsers.contains(bouncer_index) ) {
                    invitingUsers.remove(Integer.valueOf(bouncer_index));
                }
            }
            Log.d("Both_List", bouncingUsers.toString() + "\n" + invitingUsers.toString());

            try {

                HashMap<String, String> newParty = new HashMap<>();
                newParty.put("name", name);
                newParty.put("emoji", partyEmoji);
                newParty.put("price", price + "");
                newParty.put("address", mapAddress.getAddress_string());
                newParty.put("lat", mapAddress.getAddress_latlng().latitude + "");
                newParty.put("long", mapAddress.getAddress_latlng().longitude + "");
                newParty.put("is_public", isPublic ? 1 + "" : 0 + "");
                newParty.put("start_timestamp", startingDateTime.getTimeInMillis() / 1000L + "");
                newParty.put("end_timestamp", endingDateTime.getTimeInMillis() / 1000L + "");
                newParty.put("min_age", minAge + "");
                newParty.put("max_age", maxAge + "");

                final String eventId = party.getPartyID();

                server_updateParty(eventId, newParty, new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        if(result.equals("success")){
                            //TODO: uninvite previous users
                            List<String> hostingDuplicates = UtilityClass.findDuplicates(hostingUsers, originalHosting);
                            List<Integer> bouncingDuplicates = UtilityClass.findDuplicates(bouncingUsers, originalBouncing);
                            List<Integer> invitingDuplicates = UtilityClass.findDuplicates(invitingUsers, originalInviting);

                            originalHosting.removeAll(hostingDuplicates);
                            originalBouncing.removeAll(bouncingDuplicates);
                            originalInviting.removeAll(invitingDuplicates);

                            hostingUsers.removeAll(hostingDuplicates);
                            bouncingUsers.removeAll(bouncingDuplicates);
                            invitingUsers.removeAll(invitingDuplicates);
                            

                            for(final String id : originalHosting){
                                server_manageUserForParty(id, party.getPartyID(), "hosting", "DELETE", new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        if(result.equals("success")){
                                            server_getNotificationsOfUser(id, new OnResultReadyListener<ArrayList<Notification>>() {
                                                @Override
                                                public void onResultReady(ArrayList<Notification> result) {
                                                    if(result != null) {
                                                        for(Notification notif : result){
                                                            if(notif.getSenderID().equals(party.getPartyID())){
                                                                server_deleteNotification(id, notif.getNotificationID(), new OnResultReadyListener<String>() {
                                                                    @Override
                                                                    public void onResultReady(String result) {
                                                                        if (result.equals("success")) {
                                                                            //Nothing to do
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }



                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }

                            for(final int id : originalBouncing){
                                server_manageUserForParty(id + "", party.getPartyID(), "bouncing", "DELETE", new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        if(result.equals("success")){
                                            server_getNotificationsOfUser(id + "", new OnResultReadyListener<ArrayList<Notification>>() {
                                                @Override
                                                public void onResultReady(ArrayList<Notification> result) {
                                                    if(result != null) {
                                                        for (Notification notif : result) {
                                                            if (notif.getSenderID().equals(party.getPartyID())) {
                                                                server_deleteNotification(id + "", notif.getNotificationID(), new OnResultReadyListener<String>() {
                                                                    @Override
                                                                    public void onResultReady(String result) {
                                                                        if (result.equals("success")) {
                                                                            //Do nothing
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            for(final int id : originalInviting){
                                server_uninviteUser(id + "", party.getPartyID(), new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        if(result.equals("success")){
                                            server_getNotificationsOfUser(id + "", new OnResultReadyListener<ArrayList<Notification>>() {
                                                @Override
                                                public void onResultReady(ArrayList<Notification> result) {
                                                    if(result!= null) {
                                                        for(Notification notif : result){
                                                            if(notif.getSenderID().equals(party.getPartyID())){
                                                                server_deleteNotification(id + "", notif.getNotificationID(), new OnResultReadyListener<String>() {
                                                                    @Override
                                                                    public void onResultReady(String result) {
                                                                        if(result.equals("success")){
                                                                            //Do nothing
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                            for(final int userID : invitingUsers){
                                server_inviteUserToEvent(userID + "", eventId, new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        Log.d("addInvitedUser", result + "");
                                        DatabaseAccess.server_createNotification(userID+ "", "", eventId, "invite_going", null);
                                        completeThreads();
                                    }
                                });
                            }

                            for(final int userID: bouncingUsers){
                                DatabaseAccess.server_createNotification(userID + "", "", eventId, "invite_bouncing", null);
                                completeThreads();
                            }

                            for(final String hostingId : hostingUsers){
                                server_manageUserForParty(hostingId, eventId, "hosting", "POST", new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        Log.d("addHostingUser", result + "");
                                        DatabaseAccess.server_createNotification(hostingId, "", eventId, "hosting", null);
                                        completeThreads();
                                    }
                                });
                            }
                        }
                        else{

                        }
                        Log.d("updateParty", result + "");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //Count invitingUsers, bouncingUsers, and hostingUsers threads completion
    public static void completeThreads() {
        threads_completion ++;
        Log.d("Threads Complete", threads_completion + " out of " + (EditStatsActivity.NewPartyInfo.invitingUsers.size() + EditStatsActivity.NewPartyInfo.bouncingUsers.size() + EditStatsActivity.NewPartyInfo.hostingUsers.size()));
        if ( threads_completion >= (EditStatsActivity.NewPartyInfo.invitingUsers.size() + EditStatsActivity.NewPartyInfo.bouncingUsers.size() + EditStatsActivity.NewPartyInfo.hostingUsers.size()) ) {
            //Finish task
            CurrentUser.setContext(mainActivity, new OnResultReadyListener<Boolean>() {
                @Override
                public void onResultReady(Boolean result) {
                    mainActivity.finish();
                    //mainActivity.onBackPressed();
                    //mainActivity.finish();
                }
            });
        }
    }


}
