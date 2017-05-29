package com.thewavesocial.waveandroid.HostFolder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditListActivity extends AppCompatActivity {
    private Party party;
    private SearchView searchbar;
    private TextView create;
    private ListView friend_list;
    private RecyclerView invite_list;
    private EditText editText;
    private static EditListActivity mainActivity;
    private List<User> users;
    private static List<User> invites;
    private View view;
    private int LAYOUT_TYPE; //1: Attending
                             //2: Bouncing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        mainActivity = this;
        initialize();
        setupActionBar();
        setupReferences();
        setupFunctionality();
    }

    private void initialize() {
        party = getIntent().getExtras().getParcelable("partyObj");
        LAYOUT_TYPE = getIntent().getExtras().getInt("layout");

        //Get user followings
        CurrentUser.server_getUsersListObjects(CurrentUser.theUser.getFollowing(), new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(List<User> result) {
                if ( result != null ) {
                    users = result;
                }
            }
        });

        //Get attending or bouncing users
        CurrentUser.server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                if ( result != null ) {
                    switch (LAYOUT_TYPE) {
                        case 1:
                            invites = result.get("attending");
                            break;
                        case 2:
                            invites = result.get("bouncing");
                            break;
                    }
                }
            }
        });

    }

    private void setupActionBar(){
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_edit_event_list);
        TextView title = (TextView)findViewById(R.id.actionbar_editEvent_list_title);
        ImageView backButton = (ImageView)findViewById(R.id.actionbar_editEvent_list_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmMessage = new AlertDialog.Builder(mainActivity);
                confirmMessage.setTitle("Unsaved Changes")
                        .setMessage("Are you sure you want to discard your progress?")
                        .setCancelable(false)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
        switch (LAYOUT_TYPE){
            case 1:
                title.setText("Invite Friends");
                break;
            case 2:
                title.setText("Invite Bouncers");
                break;
        }

    }

    private void setupReferences(){
        searchbar = (SearchView)findViewById(R.id.edit_event_list_search);
        searchbar.setQueryHint("Search Name");
        create = (TextView)findViewById(R.id.edit_event_list_create);
        create.setText("Update List");
        friend_list = (ListView)findViewById(R.id.edit_event_list_list);
        friend_list.setAdapter(new FriendListAdapter(users));
        invite_list = (RecyclerView)findViewById(R.id.edit_event_list_selectedList);
        LinearLayoutManager layoutManager= new LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL, false);
        invite_list.setLayoutManager(layoutManager);
        invite_list.setAdapter(new SelectedAdapter(invites));
    }

    private void setupFunctionality(){
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //TODO: 5/7/2017 Update server with new invites list
                switch (LAYOUT_TYPE){
                    case 1:
                        for ( User user : invites ) {
                            CurrentUser.server_manageUserForParty(user.getUserID(), party.getPartyID(), "attending", "POST", null);
                        }
                        break;
                    case 2:
                        for ( User user : invites ) {
                            CurrentUser.server_manageUserForParty(user.getUserID(), party.getPartyID(), "bouncing", "POST", null);
                        }
                        break;
                }
            }
        });
        searchbar.clearFocus();
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(users, query)));
                searchbar.clearFocus();
                return false;
            }
            @Override public boolean onQueryTextChange(String query) {
                friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(users, query)));
                return false;
            }
        });

        int searchCloseButtonId = searchbar.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchbar.findViewById(searchCloseButtonId);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = searchbar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText editText = (EditText) searchbar.findViewById(id);
                editText.setText("");
                friend_list.setAdapter(new FriendListAdapter(UtilityClass.search(users, "")));
                searchbar.clearFocus();
            }
        });
    }

    private class FriendListAdapter extends BaseAdapter {
        private List<User> users;
        private LayoutInflater inflater;
        private FriendListAdapter(List<User> friends) {
            this.users = friends;
            inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override public int getCount() {
            return users.size();
        }
        @Override public User getItem(int position) {
            return users.get(position);
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
                    intent.putExtra("userObject", getItem(position) );
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
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgView;
            ViewHolder(View itemView) {
                super(itemView);
                imgView = (ImageView) itemView.findViewById(R.id.each_statsfriend_image);
            }

            public ImageView getImgView() {
                return imgView;
            }
        }
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
                    intent.putExtra("userObject", userList.get(position));
                    mainActivity.startActivity(intent);
                }
            });
        }

    }
}

