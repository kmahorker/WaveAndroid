package com.thewavesocial.waveandroid.HostFolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateAnEventActivity extends AppCompatActivity {
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
        fragmentM.beginTransaction().replace(R.id.createEvent_fragment_container, new CreateEventPage2()).commit();

        //actionbar settings
        getSupportActionBar().setCustomView(R.layout.actionbar_create_event_invite);
        back = (ImageView) findViewById(R.id.actionbar_createEvent_invite_back);
        forward = (ImageView) findViewById(R.id.actionbar_createEvent_invite_forward);
        title = (TextView) findViewById(R.id.actionbar_createEvent_invite_title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventPage2.savePage2();
                openFirstPage();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventPage2.savePage2();
                openThirdPage();
            }
        });
        forward.setVisibility(View.VISIBLE);
        forward.setClickable(true);
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
        forward.setVisibility(View.INVISIBLE);
        forward.setClickable(false);
        title.setText("Invite Bouncers");
    }

    public static class CreateEventPage1 extends Fragment {
        private CreateAnEventActivity mainActivity;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_create_an_event, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mainActivity = (CreateAnEventActivity)getActivity();
        }

        private void savePage1() {
            mainActivity.finish();
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
            create.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    savePage2();
                    mainActivity.finish();
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
                return friends.get(position).getUserID();
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
                holder.profile.setImageDrawable(UtilityClass.toRoundImage(getResources(), getItem(position).getProfilePic().getBitmap()));
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
                holder.imgView.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                        userList.get(position).getProfilePic().getBitmap()));
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
            NewPartyInfo.bouncingUsers.addAll(CurrentUser.getUserIDList(invites));
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
                return friends.get(position).getUserID();
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

                holder.profile.setImageDrawable(UtilityClass.toRoundImage(getResources(), getItem(position).getProfilePic().getBitmap()));
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
                holder.imgView.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                        userList.get(position).getProfilePic().getBitmap()));
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
        static List<Long> hostingUsers;
        static List<Long> bouncingUsers;
        static List<Long> attendingUsers;
        static boolean isPublic;
        static BitmapDrawable partyEmoji;
        public static void initialize() {
            name = "";
            price = 0;
            hostName = "";
            startingDateTime = null;
            endingDateTime = null;
            hostingUsers = new ArrayList<>();
            bouncingUsers = new ArrayList<>();
            attendingUsers = new ArrayList<>();
            isPublic = false;
            partyEmoji = null;
        }
        public static void composeParty(){
            Party party = new Party(
                    0, name, price, hostName, startingDateTime, endingDateTime, mapAddress,
                    hostingUsers, bouncingUsers, attendingUsers, isPublic, partyEmoji);
            // TODO: 03/31/2017 Send to database and create new party with new ID
        }
    }
}
