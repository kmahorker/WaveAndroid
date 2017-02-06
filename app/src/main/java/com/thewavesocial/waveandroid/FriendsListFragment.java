package com.thewavesocial.waveandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {FriendsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

   // private OnFragmentInteractionListener mListener;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList List of Friends of current user.
     * @return A new instance of fragment FriendsListFragment.
     */
    // DONE: Rename and change types and number of parameters
    public static FriendsListFragment newInstance(ArrayList<User> friendsList) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, friendsList);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            //mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        //ListView friendsList = (ListView) findViewById(R.id.friendsList);
        //List<User> users = generateTestUserList(); //TODO testing
        //friendsList.setAdapter(new CustomAdapter(this, users));
        return rootView; //inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_friends);

        //Should take Sorted List as argument
        final ListView friendsList = (ListView) getActivity().findViewById(R.id.friendsList);
        final List<User> friendsUsers = generateTestUserList(); //TODO testing need to replace with actual List
        final CustomAdapter adapt = new CustomAdapter(getActivity(), this, friendsUsers);
        friendsList.setAdapter(adapt);
        ImageButton inviteFriends = (ImageButton) getActivity().findViewById(R.id.addFriendButton);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showInviteFriendsActivity(v);
            }
        });
        final SearchView searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        searchView.setQueryHint("Search Friends Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<User> refinedUserList = search(friendsUsers, query);
                //friendsList.setAdapter(null);
                adapt.setUserList(refinedUserList);
                adapt.notifyDataSetChanged();
                friendsList.setAdapter(new CustomAdapter(getActivity(), (FriendsListFragment)getParentFragment(), refinedUserList));
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<User> refinedUserList = search(friendsUsers, newText);
                adapt.setUserList(refinedUserList);
                adapt.notifyDataSetChanged();
                friendsList.setAdapter(new CustomAdapter(getActivity(), (FriendsListFragment)getParentFragment(), refinedUserList));
                return true;
            }
        });
        searchView.setOnFocusChangeListener(new SearchView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    UtilityClass.hideKeyboard(getActivity());
                }
            }
        });
    }

    //Supporting Methods

    public void showInviteFriendsActivity(View view){
        Intent f_intent = new Intent(getActivity(), InviteFriendsActivity.class);
        startActivity(f_intent);

    }

    public void showFriendProfileActivity(View view, User clickedUser){
        Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
        intent.putExtra("userObj", clickedUser);
        startActivity(intent);
    }

    public List<User> generateTestUserList(){
        List<User> userList = new ArrayList<User>();
        //User a = new User("Bob", "Jones", "bj@k.com", "password", "UCSB", "Male", new Date(51215), userList, userList, userList);
        User a = new User();
        User b = new User();
        User c = new User();
        User d = new User();
        User e = new User();

        a.setFirstName("Bobasdfasdf");
        a.setLastName("Jone");
        b.setFirstName("John");
        b.setLastName("Smith");
        c.setFirstName("Jone");
        c.setLastName("Dude");
        d.setFirstName("Turn");
        d.setLastName("Up");
        e.setFirstName("Kau");
        e.setLastName("Mahlkasdjfalsk;dfjasdf");

        userList.add(a);
        userList.add(b);
        userList.add(c);
        userList.add(d);
        userList.add(e);

        return userList;

    }

    public List<User> search(List<User> us, String query){
        //System.out.print("query: " + query);
        if(query == ""){
            return us;
        }
        List<User> users = new ArrayList<User>();
        for(User u : us){

            if((u.getFirstName().matches("(?i:" + query + ".*)"))){
                //System.out.print("  firstName: " + u.getFirstName());
                //System.out.print("    bool: " + u.getFirstName().contains(query));
                users.add(u);
            }

            else if(u.getLastName().matches("(?i:" + query + ".*)")){
                //System.out.print("  lastName: " + u.getLastName());
                //System.out.print("    bool: " + u.getLastName().contains(query));
                users.add(u);
            }
            //System.out.println("    " + users);
        }

        //System.out.println("    " + users);
        return users;
    }
}
