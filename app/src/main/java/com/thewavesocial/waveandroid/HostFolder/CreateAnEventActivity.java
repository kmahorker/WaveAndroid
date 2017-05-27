package com.thewavesocial.waveandroid.HostFolder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class CreateAnEventActivity extends AppCompatActivity {
    private TextView cancel, title;
    private ImageView back, forward;
    private FragmentManager fragmentM;
    public static CreateAnEventActivity thisActivity;
    public static ArrayList<User> followings;
    private static int threads_completion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_layout);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        fragmentM = getSupportFragmentManager();
        thisActivity = this;

        openFirstPage();
        NewPartyInfo.initialize();

        CurrentUser.server_getUsersListObjects(CurrentUser.theUser.getFollowing(), new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(List<User> result) {
                followings = new ArrayList<>();
                if ( result != null )
                    followings.addAll(result);
            }
        });
    }

    //Open page 1
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
                                NewPartyInfo.initialize();
                                onBackPressed();
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

    //Open page 2
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

    //Open page 3
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

    //Return Forward Button for page 1
    public ImageView getForward() {
        return forward;
    }

    //Handle create event page 1
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
                return true;
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

    //Handle create event page 2
    public static class CreateEventPage2 extends Fragment {
        private SearchView searchbar;
        private TextView create;
        private ListView friend_list;
        private RecyclerView invite_list;
        private static CreateAnEventActivity mainActivity;
        private List<User> friends;
        private static List<Integer> invite_index = new ArrayList<>();
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

            friends = followings;
            friend_list.setAdapter(new FriendListAdapter(friends));

            invite_index = NewPartyInfo.attendingUsers;
            LinearLayoutManager layoutManager= new LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL, false);
            invite_list.setLayoutManager(layoutManager);
            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));

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
            NewPartyInfo.attendingUsers = invite_index;
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
                if ( invite_index.contains( position ))
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                else
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                holder.select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( !invite_index.contains( position ) ) {
                            invite_index.add(position);
                            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                        } else {
                            invite_index.remove(position);
                            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));
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

    //Handle create event page 3
    public static class CreateEventPage3 extends Fragment {
        private SearchView searchbar;
        private TextView create;
        private ListView friend_list;
        private RecyclerView invite_list;
        private static CreateAnEventActivity mainActivity;
        private List<User> friends;
        private static List<Integer> invite_index = new ArrayList<>();

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
                    NewPartyInfo.composeParty();
                    mainActivity.finish();
                }
            });

            friends = followings;
            friend_list.setAdapter(new FriendListAdapter(friends));

            invite_index = NewPartyInfo.bouncingUsers;
            LinearLayoutManager layoutManager= new LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL, false);
            invite_list.setLayoutManager(layoutManager);
            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));
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
            NewPartyInfo.bouncingUsers = invite_index;
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

                Log.d("Contains?", invite_index.toString() );
                if ( invite_index.contains( position ) )
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                else
                    holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                holder.select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Position", position+"");
                        if ( !invite_index.contains( position ) ) {
                            invite_index.add(position);
                            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.checkmark));
                            Log.d("Contains?", invite_index.toString() );
                        } else {
                            invite_index.remove(invite_index.indexOf(position));
                            invite_list.setAdapter(new SelectedAdapter(getUsersFromFollowing(invite_index)));
                            holder.select.setImageDrawable(mainActivity.getDrawable(R.drawable.plus_button));
                            Log.d("Contains?", invite_index.toString() );
                        }
                    }
                });

                layoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                        intent.putExtra("userIDLong", followings.get(position) );
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
                        intent.putExtra("userIDLong", userList.get(position).getUserID()); //TODO: Change to User Object
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

    public static List<User> getUsersFromFollowing(List<Integer> indexes) {
        ArrayList<User> users = new ArrayList<>();
        for ( int i = 0; i < indexes.size(); i++ ) {
            users.add(followings.get(indexes.get(i)));
        }
        return users;
    }

    //Store all new party information
    private static class NewPartyInfo {
        static String name;
        static double price;
        static String hostName;
        static Calendar startingDateTime;
        static Calendar endingDateTime;
        static MapAddress mapAddress;
        static List<String> hostingUsers;
        static List<Integer> bouncingUsers;
        static List<Integer> attendingUsers;
        static boolean isPublic;
        static String partyEmoji;
        static int minAge;
        static int maxAge;

        //Initialize party information
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
            mapAddress = new MapAddress();

            hostingUsers.add("10");
        }

        //Compose all party information
        public static void composeParty(){
            Log.d("Compose Party", "EEntered");
            if ( mapAddress.getAddress_latlng() == null )
                mapAddress.setAddress_latlng(new LatLng(0,0));

            try {
                CurrentUser.server_createNewParty(name, partyEmoji, price, mapAddress.getAddress_string(),
                        mapAddress.getAddress_latlng().latitude, mapAddress.getAddress_latlng().longitude,
                        isPublic, startingDateTime.getTimeInMillis() / 1000L, endingDateTime.getTimeInMillis() / 1000L,
                        minAge, maxAge, new OnResultReadyListener<String>() {

                            @Override
                            public void onResultReady(String result) {
                                int commaIndex = result.indexOf(',');
                                if (commaIndex == -1 || !result.substring(0, commaIndex).equals("success")) {
                                    Log.d("Compose Party", "Unsuccessful");
                                    return;
                                }

                                final String eventId = result.substring(commaIndex + 1);

                                for(User user : getUsersFromFollowing(attendingUsers)){
                                    CurrentUser.server_manageUserForParty(user.getUserID(), eventId, "invited"/*TODO: change this method to new call*/ , "POST", new OnResultReadyListener<String>() {
                                        @Override
                                        public void onResultReady(String result) {
                                            Log.d("addInvitedUserError", result + "");
                                            if(result.equals("success")){
                                                completeThreads();
                                            }
                                        }
                                    });
                                }

                                for(User user : getUsersFromFollowing(bouncingUsers)){
                                    CurrentUser.server_manageUserForParty(user.getUserID(), eventId, "bouncing", "POST", new OnResultReadyListener<String>() {
                                        @Override
                                        public void onResultReady(String result) {
                                            Log.d("addInvitedUserError", result + "");
                                            if(result.equals("success")){
                                                completeThreads();
                                            }
                                        }
                                    });
                                }

                                for(String hostingId : hostingUsers){
                                    CurrentUser.server_manageUserForParty(hostingId, eventId, "hosting", "POST", new OnResultReadyListener<String>() {
                                        @Override
                                        public void onResultReady(String result) {
                                            Log.d("addInvitedUserError", result + "");
                                            if(result != null || result.equals("success")){
                                                //TODO: UpdateUser Hosting field in user object with server
                                                //TODO: Resume (Attach) HostControllerFragment --> add where compose was called
                                                completeThreads();
                                            }
                                        }
                                    });
                                }

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //Count attendingUsers, bouncingUsers, and hostingUsers threads completion
    public static void completeThreads() {
        threads_completion ++;
        if ( threads_completion >= (NewPartyInfo.attendingUsers.size() + NewPartyInfo.bouncingUsers.size() + NewPartyInfo.hostingUsers.size()) ) {
            //Finish task

            CurrentUser.setContext(thisActivity, null);
            thisActivity.onBackPressed();
        }
    }
}
