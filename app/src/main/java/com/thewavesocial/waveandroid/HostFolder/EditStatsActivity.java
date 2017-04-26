package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    Party party;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_event_stats_edit);
        mainActivity = this;
        Intent intent = getIntent();
        party = intent.getExtras().getParcelable("partyObject");
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

        locationEditText = (EditText)findViewById(R.id.editEventLocationEditText);
        locationEditText.setText(party.getMapAddress().getAddress_string());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

        startDateTextView = (TextView) findViewById(R.id.editEventStartDateTextView);
        startDateTextView.setText(dateFormat.format(party.getStartingDateTime().getTime()));

        startTimeTextView = (TextView) findViewById(R.id.editEventStartTimeTextView);
        startTimeTextView.setText(timeFormat.format(party.getStartingDateTime().getTime()));

        endDateTextView = (TextView) findViewById(R.id.editEventEndDateTextView);
        endDateTextView.setText(dateFormat.format(party.getEndingDateTime().getTime()));

        endTimeTextView = (TextView) findViewById(R.id.editEventEndTimeTextView);
        endTimeTextView.setText(timeFormat.format(party.getEndingDateTime().getTime()));

        emojiconEditText = (EmojiconEditText) findViewById(R.id.editEventEmojiconEditText);
        emojiconEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emojiconEditText.setText(party.getPartyEmoji());

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
                onBackPressed();
            }
        });
    }
}
