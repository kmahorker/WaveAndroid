package com.thewavesocial.waveandroid.SocialFolder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.DummyUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.AdaptersFolder.CustomAdapter;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private FragmentActivity mainActivity;
    private FriendsListFragment mainFragment;

    public FriendsListFragment() {

    }

    public static FriendsListFragment newInstance(ArrayList<User> friendsList)

    {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(ARG_PARAM1, friendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_friends, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FriendsListFragment fragment = this;
        mainFragment = this;
        mainActivity = getActivity();
        DummyUser dummy = new DummyUser(getActivity()); //TODO: FOR TESTING ONLY

        ((HomeSwipeActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeSwipeActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_friends);

        final ListView friendsListView = (ListView) getActivity().findViewById(R.id.friendsList);

        final List<User> friendsUsers = new ArrayList<>();
        CurrentUser.server_getUsersListObjects(dummy.getFollowers(), new OnResultReadyListener<List<User>>() {
            @Override
            public void onResultReady(List<User> result) {
                if ( result != null ) {
                    friendsUsers.addAll(result);
                    final CustomAdapter adapt = new CustomAdapter(mainActivity, mainFragment, friendsUsers);
                    friendsListView.setAdapter(adapt);
                }
            }
        });

        ImageView inviteFriends = (ImageView) getActivity().findViewById(R.id.addFriendButton);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInviteFriendsActivity(v);
            }
        });

        final SearchView searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        searchView.setQueryHint("Search Friends Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<User> refinedUserList = new ArrayList<>();
                refinedUserList.addAll(search(friendsUsers, query));
                friendsListView.setAdapter(new CustomAdapter(getActivity(), fragment, refinedUserList));
                searchView.clearFocus(); //Hide Keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<User> refinedUserList = search(friendsUsers, newText);
                friendsListView.setAdapter(new CustomAdapter(getActivity(), fragment, refinedUserList));

                return true;
            }
        });

        //Hide Keyboard
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                UtilityClass.hideKeyboard(fragment.getActivity());
                return true;
            }
        });

        friendsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                searchView.clearFocus();
                return false;
            }
        });
    }

    public void showInviteFriendsActivity(View view) {
        Intent f_intent = new Intent(getActivity(), InviteFriendsActivity.class);
        UtilityClass.hideKeyboard(getActivity());
        startActivity(f_intent);
    }

    public void showFriendProfileActivity(View view, User clickedUser) {
        Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
        intent.putExtra("userIDLong", clickedUser.getUserID());
        startActivity(intent);
    }

    public List<User> search(List<User> us, String query) {
        if (query == "") {
            return us;
        }
        List<User> users = new ArrayList<>();
        for (User u : us) {
            if ((u.getFirstName().matches("(?i:" + query + ".*)"))) {
                Log.d("V", "  firstName: " + u.getFirstName());
                Log.d("V", "    bool: " + u.getFirstName().contains(query));
                users.add(u);
            } else if (u.getLastName().matches("(?i:" + query + ".*)")) {
                Log.d("V", "  lastName: " + u.getLastName());
                Log.d("V", "    bool: " + u.getLastName().contains(query));
                users.add(u);
            } else if (u.getFullName().matches("(?i:" + query + ".*)")) {
                users.add(u);
            }
            for (User check : users) {
                Log.d("V", "    " + check.getFirstName());
            }
        }
        return users;
    }
}
