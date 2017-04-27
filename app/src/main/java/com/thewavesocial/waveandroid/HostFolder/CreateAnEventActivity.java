package com.thewavesocial.waveandroid.HostFolder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Range;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import io.github.rockerhieu.emojiconize.Emojiconize;

public class CreateAnEventActivity extends AppCompatActivity {
    private TextView cancel, title;
    private ImageView back, forward;
    private FragmentManager fragmentM;
    private CreateAnEventActivity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_layout);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        fragmentM = getSupportFragmentManager();
        openFirstPage();
        NewPartyInfo.initialize();
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
                AlertDialog.Builder confirmMessage = new AlertDialog.Builder(thisActivity);
                confirmMessage.setTitle("In Progress") //TODO: 4/25/17 Make description better
                        .setMessage("Are you sure you want to discard your progress?")
                        .setCancelable(false)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                                NewPartyInfo.initialize();
                                finish();
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
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage2()).commit();

        //actionbar settings
        getSupportActionBar().setCustomView(R.layout.actionbar_create_event_invite);
        back = (ImageView) findViewById(R.id.actionbar_createEvent_invite_back);
        title = (TextView) findViewById(R.id.actionbar_createEvent_invite_title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventPage2.savePage2();
                openFirstPage();
            }
        });
        title.setText("Invite Friends");
    }

    private void openThirdPage() {
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage3()).commit();

        //actionbar view is already set up when opening second page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventPage3.savePage3();
                openSecondPage();
            }
        });
        title.setText("Invite Bouncers");
    }

    public ImageView getForward() {
        return forward;
    }

    public static class CreateEventPage1 extends Fragment{
        TextView cancelTextView, startDateTextView, startTimeTextView, endDateTextView, endTimeTextView;
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
        String CALLING_CLASS = "CreateAnEvent";

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
            try {
                RelativeLayout relativeLayout = (RelativeLayout)(view.findViewById(R.id.emojiRelativeLayout));
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        UtilityClass.hideKeyboard(getActivity());
                        popup.dismiss();
                        return true;
                    }
                });
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                if (NewPartyInfo.startingDateTime != null) {
                    startCalendar = NewPartyInfo.startingDateTime;
                }
                if (NewPartyInfo.endingDateTime != null) {
                    endCalendar = NewPartyInfo.endingDateTime;
                }
                setUpTextViews(view);
                setupEditText(view);
                setupSwitch(view);
                setUpRangeSeekBar(view);
                setupEmojiconEditText(view);
                setUpEmojicon(view);
                ImageView forwardImageView = ((CreateAnEventActivity) (getActivity())).getForward();
                forwardImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkInfo()) {
                            savePage1();
                            ((CreateAnEventActivity) (getActivity())).openSecondPage();
                        }
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilityClass.hideKeyboard(getActivity());
                    }
                });


                Log.d("V", "OnViewCreated");
            }catch(Exception e){
                UtilityClass.printAlertMessage(getActivity(), e.getMessage(), true);
            }
        }

        private void setUpEmojicon(View v){
            final View rootView = v.findViewById(R.id.scrollViewCreateAnEvent);
            popup = new EmojiconsPopup(rootView, ((CreateAnEventActivity)(getActivity())));
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

        private void setUpTextViews(View v){
            startDateTextView = (TextView) v.findViewById(R.id.startDateTextView);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            startDateTextView.setText(dateFormat.format(startCalendar.getTime()));
            startDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilityClass.hideKeyboard(getActivity());
                    popup.dismiss();
                    DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(startCalendar.get(Calendar.DAY_OF_MONTH),
                            startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                            CALLING_CLASS);
                    dialogFragment.setDateDisplay(startDateTextView);
                    dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            startTimeTextView = (TextView) v.findViewById(R.id.startTimeTextView);
            final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
            startTimeTextView.setText(timeFormat.format(startCalendar.getTime()));
            startTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilityClass.hideKeyboard(getActivity());
                    popup.dismiss();
                    TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(startCalendar.get(Calendar.HOUR),
                            startCalendar.get(Calendar.MINUTE), TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                            CALLING_CLASS);
                    timePickerDialogFragment.setTimeTextView(startTimeTextView);
                    timePickerDialogFragment.show(getActivity().getFragmentManager(), "timePicker");
                }
            });

            endDateTextView = (TextView) v.findViewById(R.id.endDateTextView);
            endDateTextView.setText(dateFormat.format(endCalendar.getTime()));
            endDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilityClass.hideKeyboard(getActivity());
                    popup.dismiss();
                    DatePickerDialogFragment dialogFragment = DatePickerDialogFragment.newInstance(endCalendar.get(Calendar.DAY_OF_MONTH),
                            endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.YEAR), DATE_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                            CALLING_CLASS);
                    dialogFragment.setDateDisplay(endDateTextView);
                    dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            endTimeTextView = (TextView) v.findViewById(R.id.endTimeTextView);
//            Calendar tempCalendar = Calendar.getInstance();
//            tempCalendar.setTime(new Date());
//            tempCalendar.add(Calendar.HOUR, 1);
            if(NewPartyInfo.endingDateTime == null) {
                endCalendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY) + 1);
            }
            endTimeTextView.setText(timeFormat.format(endCalendar.getTime()));
            endTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilityClass.hideKeyboard(getActivity());
                    popup.dismiss();
                    TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(endCalendar.get(Calendar.HOUR),
                            endCalendar.get(Calendar.MINUTE), TIME_FORMAT, android.R.style.Theme_Material_Light_Dialog_Alert,
                            CALLING_CLASS);
                    timePickerDialogFragment.setTimeTextView(endTimeTextView);
                    timePickerDialogFragment.show(getActivity().getFragmentManager(), "timePicker");
                }
            });
        }

        private void setupEditText(View v) {
            titleEditText = (EditText) v.findViewById(R.id.eventTitleEditText);
            if(!NewPartyInfo.name.isEmpty()) {
                titleEditText.setText(NewPartyInfo.name);
            }
            titleEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(popup.isShowing()){
                        popup.dismiss();
                    }
                    return false;
                }
            });
            locationEditText = (EditText) v.findViewById(R.id.locationEditText);
            MapAddress address = NewPartyInfo.mapAddress;
            if(address != null){
                locationEditText.setText(NewPartyInfo.mapAddress.getAddress_string());
            }
            locationEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(popup.isShowing()){
                        popup.dismiss();
                    }
                    return false;
                }
            });

        }

        private void setupSwitch(View v){
            privateSwitch = (SwitchCompat) v.findViewById(R.id.privateSwitch);
            if(NewPartyInfo.isPublic == true) {
                privateSwitch.setChecked(false);
                privateParty = false;
            }
            else if(NewPartyInfo.isPublic == false){
                privateSwitch.setChecked(true);
                privateParty = true;
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
        }

        private void setUpRangeSeekBar(View v){
            //rangeSeekBar = new RangeSeekBar<>(getActivity());
            rangeSeekBar =  (RangeSeekBar<Integer>) v.findViewById(R.id.ageRestrictionSeekBar);
            rangeSeekBar.setRangeValues(RANGE_AGE_MIN, RANGE_AGE_MAX);
            if(NewPartyInfo.minAge == -1 || NewPartyInfo.maxAge == -1) {
                rangeSeekBar.setSelectedMinValue(RANGE_AGE_SELECTED_MIN);
                rangeSeekBar.setSelectedMaxValue(RANGE_AGE_SELECTED_MAX);
            }
            else{
                rangeSeekBar.setSelectedMinValue(NewPartyInfo.minAge);
                rangeSeekBar.setSelectedMaxValue(NewPartyInfo.maxAge);
            }
        }

        private void setupEmojiconEditText(View v){
            emojiconEditText = (EmojiconEditText) v.findViewById(R.id.emojiconEditText);
            if(!NewPartyInfo.partyEmoji.isEmpty()){
                emojiconEditText.setText(NewPartyInfo.partyEmoji);
            }
            //emojiconEditText.setEmojiconSize(150);
            emojiconEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
                            final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        public void savePage1() {
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
    }

    public static class CreateEventPage2 extends Fragment {
        private SearchView searchbar;
        private TextView create;
        private ListView friend_list;
        private RecyclerView invite_list;
        private static CreateAnEventActivity mainActivity;
        private List<User> friends;
        private static List<User> invites;
        private View view;

        @Nullable
        @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.create_event_invite, container, false);
            return view;
        }

        @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            mainActivity = (CreateAnEventActivity) getActivity();
            setupReferences();
            setupFunctionality();
        }

        private void setupReferences() {
            searchbar = (SearchView) view.findViewById(R.id.create_event_invite_search);
            create = (TextView) view.findViewById(R.id.create_event_invite_create);
            friend_list = (ListView) view.findViewById(R.id.create_event_invite_list);
            invite_list = (RecyclerView) view.findViewById(R.id.create_event_invite_selectedList);
        }

        private void setupFunctionality() {
            create.setText("Next");
            create.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    savePage2();
                    mainActivity.openThirdPage();
                }
            });
            friends = CurrentUser.getUsersListObjects(CurrentUser.theUser.getFollowing());
            friend_list.setAdapter(new FriendListAdapter(friends));

            LinearLayoutManager layoutManager= new LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL, false);
            invites = CurrentUser.getUsersListObjects(NewPartyInfo.attendingUsers);
            invite_list.setLayoutManager(layoutManager);
            invite_list.setAdapter(new SelectedAdapter(invites));

            searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(friends, query)));
                    return false;
                }
                @Override public boolean onQueryTextChange(String query) {
                    friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(friends, query)));
                    return false;
                }
            });
        }

        private static void savePage2() {
            NewPartyInfo.attendingUsers = CurrentUser.getUserIDList(invites);
        }

        private class FriendListAdapter extends BaseAdapter {
            private List<User> friends;
            private LayoutInflater inflater;
            private FriendListAdapter(List<User> friends) {
                this.friends = friends;
                inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override public int getCount() {
                return friends.size();
            }
            @Override public User getItem(int position) {
                return friends.get(position);
            }
            @Override public long getItemId(int position) {
                return position;
            }
            @Override public View getView(final int position, View convertView, ViewGroup parent) {
                final Holder holder;
                View layoutView = convertView;
                if (convertView == null) {
                    layoutView = inflater.inflate(R.layout.each_createevent_invite_item, null);
                    holder = new Holder();
                    layoutView.setTag(holder);
                } else {
                    holder = (Holder) layoutView.getTag();
                }
                holder.profile = (ImageView) layoutView.findViewById(R.id.eachCreateEvent_invite_profile);
                holder.name = (TextView) layoutView.findViewById(R.id.eachCreateEvent_invite_name);
                holder.select = (ImageView) layoutView.findViewById(R.id.eachCreateEvent_invite_button);
                //TODO: Get image from URL
                //holder.profile.setImageDrawable(UtilityClass.toRoundImage(getResources(), getItem(position).getProfilePic().getBitmap()));
                holder.name.setText(getItem(position).getFullName());
                if ( invites.contains( getItem(position) ) )
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                else
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                holder.select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( !invites.contains( getItem(position) ) ) {
                            invites.add(getItem(position));
                            invite_list.setAdapter(new SelectedAdapter(invites));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                        } else {
                            invites.remove(getItem(position));
                            invite_list.setAdapter(new SelectedAdapter(invites));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                        }
                    }
                });
                layoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                        intent.putExtra("userIDLong", getItem(position).getUserID() );
                        startActivity(intent);
                    }
                });
                return layoutView;
            }
            private class Holder {
                ImageView profile;
                TextView name;
                ImageView select;
            }
        }

        private class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.ViewHolder> {
            private List<User> userList;
            private LayoutInflater inflater;
            public SelectedAdapter(List<User> userList) {
                super();
                this.userList = userList;
                inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View rowView = inflater.inflate(R.layout.each_statsfriend_item, null);
                ViewHolder viewHolder = new ViewHolder(rowView);
                return viewHolder;
            }
            @Override public long getItemId(int position) {
                return position;
            }
            @Override public int getItemCount() {
                return userList.size();
            }
            @Override public void onBindViewHolder(ViewHolder holder, final int position) {
                //TODO: Get image from URL
//                holder.imgView.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
//                        userList.get(position).getProfilePic().getBitmap()));
                holder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                        intent.putExtra("userIDLong", userList.get(position).getUserID());
                        mainActivity.startActivity(intent);
                    }
                });
            }
            public class ViewHolder extends RecyclerView.ViewHolder {
                ImageView imgView;
                ViewHolder(View itemView) {
                    super(itemView);
                    imgView = (ImageView) itemView.findViewById(R.id.each_statsfriend_image);
                }
            }
        }
    }

    public static class CreateEventPage3 extends Fragment {
        private SearchView searchbar;
        private TextView create;
        private ListView friend_list;
        private RecyclerView invite_list;
        private static CreateAnEventActivity mainActivity;
        private List<User> friends;
        private static List<User> invites;

        @Nullable
        @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.create_event_invite, container, false);
        }

        @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mainActivity = (CreateAnEventActivity) getActivity();
            setupReferences();
            setupFunctionality();
        }

        private void setupReferences() {
            searchbar = (SearchView) mainActivity.findViewById(R.id.create_event_invite_search);
            create = (TextView) mainActivity.findViewById(R.id.create_event_invite_create);
            friend_list = (ListView) mainActivity.findViewById(R.id.create_event_invite_list);
            invite_list = (RecyclerView) mainActivity.findViewById(R.id.create_event_invite_selectedList);
        }

        private void setupFunctionality() {
            create.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    savePage3();
                    mainActivity.finish();
                }
            });
            friends = CurrentUser.getUsersListObjects(CurrentUser.theUser.getFollowing());
            friend_list.setAdapter(new FriendListAdapter(friends));

            LinearLayoutManager layoutManager= new LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL, false);
            invites = CurrentUser.getUsersListObjects(NewPartyInfo.bouncingUsers);
            invite_list.setLayoutManager( layoutManager );
            invite_list.setAdapter(new SelectedAdapter(invites));

            searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(friends, query)));
                    return false;
                }
                @Override public boolean onQueryTextChange(String query) {
                    friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(friends, query)));
                    return false;
                }
            });
        }

        private static void savePage3() {
            NewPartyInfo.bouncingUsers = CurrentUser.getUserIDList(invites);
            NewPartyInfo.composeParty();
        }

        private class FriendListAdapter extends BaseAdapter {
            private List<User> friends;
            private LayoutInflater inflater;
            private FriendListAdapter(List<User> friends) {
                this.friends = friends;
                inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override public int getCount() {
                return friends.size();
            }
            @Override public User getItem(int position) {
                return friends.get(position);
            }
            @Override public long getItemId(int position) {
                return position;
            }
            @Override public View getView(final int position, View convertView, ViewGroup parent) {
                final Holder holder;
                View layoutView = convertView;
                if (convertView == null) {
                    layoutView = inflater.inflate(R.layout.each_createevent_invite_item, null);
                    holder = new Holder();
                    layoutView.setTag(holder);
                } else {
                    holder = (Holder) layoutView.getTag();
                }
                holder.profile = (ImageView) layoutView.findViewById(R.id.eachCreateEvent_invite_profile);
                holder.name = (TextView) layoutView.findViewById(R.id.eachCreateEvent_invite_name);
                holder.select = (ImageView) layoutView.findViewById(R.id.eachCreateEvent_invite_button);

                //TODO: Get image from URL
                //holder.profile.setImageDrawable(UtilityClass.toRoundImage(getResources(), getItem(position).getProfilePic().getBitmap()));
                holder.name.setText(getItem(position).getFullName());

                if ( invites.contains( getItem(position) ) )
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                else
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                holder.select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( !invites.contains( getItem(position) ) ) {
                            invites.add(getItem(position));
                            invite_list.setAdapter(new SelectedAdapter(invites));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                        } else {
                            invites.remove(getItem(position));
                            invite_list.setAdapter(new SelectedAdapter(invites));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                        }
                    }
                });

                layoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                        intent.putExtra("userIDLong", getItem(position).getUserID() );
                        startActivity(intent);
                    }
                });
                return layoutView;
            }
            private class Holder {
                ImageView profile;
                TextView name;
                ImageView select;
            }
        }

        private class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.ViewHolder> {
            private List<User> userList;
            private LayoutInflater inflater;
            public SelectedAdapter(List<User> userList) {
                super();
                this.userList = userList;
                inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View rowView = inflater.inflate(R.layout.each_statsfriend_item, null);
                ViewHolder viewHolder = new ViewHolder(rowView);
                return viewHolder;
            }
            @Override public long getItemId(int position) {
                return position;
            }
            @Override public int getItemCount() {
                return userList.size();
            }
            @Override public void onBindViewHolder(ViewHolder holder, final int position) {
//                holder.imgView.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
//                        userList.get(position).getProfilePic().getBitmap()));
                holder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                        intent.putExtra("userIDLong", userList.get(position).getUserID());
                        mainActivity.startActivity(intent);
                    }
                });
            }
            public class ViewHolder extends RecyclerView.ViewHolder {
                ImageView imgView;
                ViewHolder(View itemView) {
                    super(itemView);
                    imgView = (ImageView) itemView.findViewById(R.id.each_statsfriend_image);
                }
            }
        }
    }

    private static class NewPartyInfo {
        static String name;
        static double price;
        static String hostName;
        static Calendar startingDateTime;
        static Calendar endingDateTime;
        static MapAddress mapAddress;
        static List<String> hostingUsers;
        static List<String> bouncingUsers;
        static List<String> attendingUsers;
        static boolean isPublic;
        static String partyEmoji;
        static int minAge;
        static int maxAge;
        public static void initialize() {
            name = "";
            price = 0;
            hostName = "";
            startingDateTime = null;
            endingDateTime = null;
            hostingUsers = new ArrayList<>();
            bouncingUsers = new ArrayList<>();
            attendingUsers = new ArrayList<>();
            isPublic = true;
            partyEmoji = "";
            minAge = -1;
            maxAge = -1;
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
