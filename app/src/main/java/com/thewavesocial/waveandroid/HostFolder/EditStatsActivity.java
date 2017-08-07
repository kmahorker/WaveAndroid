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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.AdaptersFolder.PartyAttendeesCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.florescu.android.rangeseekbar.RangeSeekBar;

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
    static long startCalendar = 0; //Calendar.getInstance();
    static long endCalendar = 0; //Calendar.getInstance();
    String DATE_FORMAT = "MMM d, yyyy";
    String TIME_FORMAT = "h:mm a";
    String CALLING_CLASS = "EditEvent";

    RecyclerView invitedRecyclerView, bouncingRecylcerView;
    TextView inviteTextView, bouncingTextView;

    static Party party;

    final int EDIT_BOUNCER_REQUEST = 2;
    final int EDIT_INVITE_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private Place locationPlace;

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

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        setupActionbar();
        setupReference();
        setUpEmojicon();
        setupFunctionality();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_INVITE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<Integer> updatedListUserIds = new ArrayList<>();
                    List<User> updatedList = new ArrayList<>();
                    updatedList = data.getExtras().getParcelableArrayList("updatedList");
                    for (User user : updatedList) {
                        updatedListUserIds.add(Integer.parseInt(user.getUserID()));
                    }
                    NewPartyInfo.invitingUsers = updatedListUserIds;
                    inviteTextView.setText("INVITED (" + updatedList.size() + ")");
                    invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, updatedList));
                    NewPartyInfo.updateInvites();
                } else {
                    //Do nothing
                }
                break;
            case EDIT_BOUNCER_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<Integer> updatedListUserIds = new ArrayList<>();
                    List<User> updatedList = new ArrayList<>();
                    updatedList = data.getExtras().getParcelableArrayList("updatedList");
                    for (User user : updatedList) {
                        updatedListUserIds.add(Integer.parseInt(user.getUserID()));
                    }
                    NewPartyInfo.bouncingUsers = updatedListUserIds;
                    bouncingTextView.setText("BOUNCERS (" + updatedList.size() + ")");
                    bouncingRecylcerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, updatedList));
                    NewPartyInfo.updateBouncers();

                } else {
                    //Do nothing
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    locationPlace = place;
                    locationEditText.getText().clear();
                    locationEditText.append(place.getName());
                }
                else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    // TODO: Handle the error.
                    Log.d("placesAutoCompError", status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
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
                            }
                        })
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
                        if (!result.equals("success")) {
                            Toast.makeText(mainActivity, "Fail to delete party.", Toast.LENGTH_LONG).show();
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

        titleEditText = (EditText) findViewById(R.id.editEventEventTitleEditText);
        titleEditText.setText(party.getName());
        titleEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popup.isShowing()) {
                    popup.dismiss();
                }
                return false;
            }
        });

        locationEditText = (EditText) findViewById(R.id.editEventLocationEditText);
        locationEditText.setText(party.getAddress());
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (popup.isShowing()) {
                        popup.dismiss();
                    }
                    UtilityClass.hideKeyboard(getActivity());
                    try {

                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    }
                }
                return true;

            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

        startDateTextView = (TextView) findViewById(R.id.editEventStartDateTextView);
        startDateTextView.setText(dateFormat.format(UtilityClass.epochToCalendar(party.getDate()).getTime()));
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(startCalendar/*UtilityClass.epochToCalendar(startCalendar).get(Calendar.DAY_OF_MONTH),
                        UtilityClass.epochToCalendar(startCalendar).get(Calendar.MONTH), UtilityClass.epochToCalendar(startCalendar).get(Calendar.YEAR)*/, DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                dialogFragment.setDateDisplay(startDateTextView);
                dialogFragment.show(mainActivity.getFragmentManager(), "datePicker");
            }
        });

        startTimeTextView = (TextView) findViewById(R.id.editEventStartTimeTextView);
        startTimeTextView.setText(timeFormat.format(UtilityClass.epochToCalendar(party.getDate()).getTime()));
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(startCalendar/*UtilityClass.epochToCalendar(startCalendar).get(Calendar.HOUR),
                        UtilityClass.epochToCalendar(startCalendar).get(Calendar.MINUTE)*/, TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                timePickerDialogFragment.setTimeTextView(startTimeTextView);
                timePickerDialogFragment.show(mainActivity.getFragmentManager(), "timePicker");
            }
        });

        endDateTextView = (TextView) findViewById(R.id.editEventEndDateTextView);
        endDateTextView.setText(dateFormat.format(UtilityClass.epochToCalendar( party.getDate() + party.getDuration() ).getTime()));
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(endCalendar/*UtilityClass.epochToCalendar(endCalendar).get(Calendar.DAY_OF_MONTH),
                        UtilityClass.epochToCalendar(endCalendar).get(Calendar.MONTH), UtilityClass.epochToCalendar(endCalendar).get(Calendar.YEAR)*/, DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                dialogFragment.setDateDisplay(endDateTextView);
                dialogFragment.show(mainActivity.getFragmentManager(), "datePicker");
            }
        });

        endTimeTextView = (TextView) findViewById(R.id.editEventEndTimeTextView);
        endTimeTextView.setText(timeFormat.format(UtilityClass.epochToCalendar( party.getDate() + party.getDuration() ).getTime()));
        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
                popup.dismiss();
                TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(endCalendar/*UtilityClass.epochToCalendar(endCalendar).get(Calendar.HOUR),
                        UtilityClass.epochToCalendar(endCalendar).get(Calendar.MINUTE)*/, TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                        CALLING_CLASS);
                timePickerDialogFragment.setTimeTextView(endTimeTextView);
                timePickerDialogFragment.show(mainActivity.getFragmentManager(), "timePicker");
            }
        });

        emojiconEditText = (EmojiconEditText) findViewById(R.id.editEventEmojiconEditText);
        emojiconEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emojiconEditText.setText(party.getEmoji());
        emojiconEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!popup.isShowing()) {
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                    } else {

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
                if (!hasFocus && popup.isShowing()) {
                    popup.dismiss();
                }
            }
        });

        setUpEmojicon();


        privateSwitch = (SwitchCompat) findViewById(R.id.editEventPrivateSwitch);
        boolean isPrivate = !party.is_public();
        if (isPrivate) {
            privateSwitch.setChecked(true);
            privateParty = true;
        } else {
            privateSwitch.setChecked(false);
            privateParty = false;
        }

        privateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privateParty == false) {
                    privateParty = true;
                } else {
                    privateParty = false;
                }
                UtilityClass.hideKeyboard(getActivity());
                popup.dismiss();
            }
        });

        rangeSeekBar = (RangeSeekBar<Integer>) findViewById(R.id.editEventAgeRestrictionSeekBar);
        rangeSeekBar.setRangeValues(RANGE_AGE_MIN, RANGE_AGE_MAX);
        rangeSeekBar.setSelectedMinValue(party.getMin_age());
        rangeSeekBar.setSelectedMaxValue(party.getMax_age());

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
                inviteTextView = (TextView) findViewById(R.id.invite_text);

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
                if (result != null) {

                    final List<User> bouncing = result.get("bouncing");
                    bouncingRecylcerView.setAdapter(new PartyAttendeesCustomAdapter(mainActivity, bouncing));
                    bouncingTextView = (TextView) findViewById(R.id.bouncing_text);
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

    private void setUpEmojicon() {
        final View rootView = findViewById(R.id.scrollViewEditEvent);
        popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                } else {
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
                    mainActivity.finish();
                }
            }
        });
    }

    private boolean checkInfo() {
        //Log.d("Address", locationEditText.getText().toString());
        //Log.d("Address", UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) + "");
        if (emojiconEditText.getText().toString().isEmpty()) {
            UtilityClass.printAlertMessage(getActivity(), "Please select an Emoji for the event ", "Error Editing Party", true);
            return false;
        } else if (titleEditText.getText().toString().isEmpty()) {
            UtilityClass.printAlertMessage(getActivity(), "Please enter an Event Title", "Error Editing Party", true);
            return false;
        } else if (locationEditText.getText().toString().isEmpty()) {
            UtilityClass.printAlertMessage(getActivity(), "Please select an Event Location", "Error Editing Party", true);
            return false;
        } else if (startCalendar >= endCalendar) {
            Log.d("Date", UtilityClass.epochToCalendar(startCalendar).get(Calendar.DATE) + ", " + UtilityClass.epochToCalendar(endCalendar).get(Calendar.DATE) + "");
            UtilityClass.printAlertMessage(getActivity(), "The event start date must be before the end date", "Error Editing Party", true);
            return false;
        } else if (UtilityClass.getLocationFromAddress(getActivity(), locationEditText.getText().toString()) == null) {
            UtilityClass.printAlertMessage(getActivity(), "Please enter a valid address", "Error Editing Party", true);
            return false;
        } else {
            return true;
        }
    }


    //ONLY called if all checks are passed
    public void savePage() {
        NewPartyInfo.name = titleEditText.getText().toString();
        NewPartyInfo.price = 0;
        NewPartyInfo.hostName = CurrentUser.theUser.getFull_name();
        NewPartyInfo.startingDateTime = startCalendar;
        NewPartyInfo.endingDateTime = endCalendar;
        NewPartyInfo.address = locationPlace.getAddress().toString();
        NewPartyInfo.lat = locationPlace.getLatLng().latitude;
        NewPartyInfo.lng = locationPlace.getLatLng().longitude;
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
        static long startingDateTime;
        static long endingDateTime;
        static String address;
        static double lat;
        static double lng;
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
            hostName = party.getHost_name();
            startingDateTime = party.getDate();
            endingDateTime = party.getDate() + party.getDuration();
            server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
                @Override
                public void onResultReady(HashMap<String, ArrayList<User>> result) {
                    if (result != null) {
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
            isPublic = party.is_public();
            partyEmoji = party.getEmoji();
            minAge = party.getMin_age();
            maxAge = party.getMax_age();
            address = party.getAddress();

            //hostingUsers.add(DatabaseAccess.getTokenFromLocal(mainActivity).get("id"));
        }

        //Compose all party information
        public static void composeParty() {
            Log.d("Compose Party", "Entered");

            try {

                HashMap<String, String> newParty = new HashMap<>();
                newParty.put("name", name);
                newParty.put("emoji", partyEmoji);
                newParty.put("price", price + "");
                newParty.put("address", address);
                newParty.put("lat", lat + "");
                newParty.put("long", lng + "");
                newParty.put("is_public", isPublic ? 1 + "" : 0 + "");
                newParty.put("start_timestamp", startingDateTime + "");
                newParty.put("end_timestamp", endingDateTime + "");
                newParty.put("min_age", minAge + "");
                newParty.put("max_age", maxAge + "");

                final String eventId = party.getPartyID();

                server_updateParty(eventId, newParty, new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        if(result.equals("success")){
                            updateHosting();
                        }
                        else{
                            //Error Here
                        }
                        Log.d("updateParty", result + "");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void updateInvites(){
            final String eventId = party.getPartyID();

            List<Integer> invitingDuplicates = UtilityClass.findDuplicates(invitingUsers, originalInviting);
            originalInviting.removeAll(invitingDuplicates);
            invitingUsers.removeAll(invitingDuplicates);
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
                        //completeThreads();
                    }
                });
            }


        }

        public static void updateBouncers(){
            final String eventId = party.getPartyID();
            List<Integer> conflicts = UtilityClass.findDuplicates(bouncingUsers, invitingUsers);
            invitingUsers.removeAll(conflicts);
            Log.d("conflicts", conflicts + "");

            TextView invitedTextView = (TextView) EditStatsActivity.mainActivity.findViewById(R.id.invite_text);


            class UpdateInviteAdapter implements Runnable{
                public UpdateInviteAdapter(){
                }
                @Override
                public void run() {
                    final List<User> updatedInvites = UtilityClass.IntegerIdtoUserObject(invitingUsers);
                    final RecyclerView invitedRecyclerView = (RecyclerView) EditStatsActivity.mainActivity.findViewById(R.id.invite_list);
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invitedRecyclerView.setAdapter(new PartyAttendeesCustomAdapter(EditStatsActivity.mainActivity, updatedInvites));
                        }
                    });

                }
            }

            UpdateInviteAdapter inv = new UpdateInviteAdapter();
            Thread thread = new Thread(inv);
            thread.start();

            invitedTextView.setText("INVITED (" + invitingUsers.size() + ")");

            //updateInvites();
            for(final int id : conflicts){
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


            List<Integer> bouncingDuplicates = UtilityClass.findDuplicates(bouncingUsers, originalBouncing);
            originalBouncing.removeAll(bouncingDuplicates);
            bouncingUsers.removeAll(bouncingDuplicates);


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

            for(final int userID: bouncingUsers){
                server_manageUserForParty(userID + "", eventId, "bouncing", "POST", new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        DatabaseAccess.server_createNotification(userID + "", "", eventId, "invite_bouncing", null);
                        //completeThreads();
                    }
                });

            }

        }

        public static void updateHosting(){
            final String eventId = party.getPartyID();

            List<String> hostingDuplicates = UtilityClass.findDuplicates(hostingUsers, originalHosting);
            originalHosting.removeAll(hostingDuplicates);
            hostingUsers.removeAll(hostingDuplicates);


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



            for(final String hostingId : hostingUsers){
                server_manageUserForParty(hostingId, eventId, "hosting", "POST", new OnResultReadyListener<String>() {
                    @Override
                    public void onResultReady(String result) {
                        Log.d("addHostingUser", result + "");
                        DatabaseAccess.server_createNotification(hostingId, "", eventId, "hosting", null);
                        //completeThreads();
                    }
                });
            }
        }
    }




}
